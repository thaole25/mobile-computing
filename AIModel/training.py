import os
import shutil
import argparse
import numpy as np 
import pickle
import pandas as pd 
from keras.preprocessing.image import load_img, img_to_array, ImageDataGenerator
from keras.models import Sequential
from keras.layers import Dropout, Flatten, Dense
from keras import applications
from keras.utils.np_utils import to_categorical
from keras.models import Model
from keras.callbacks import EarlyStopping, ModelCheckpoint
from contextlib import redirect_stdout

IMAGES_PATH = "../data/training/images/"
AUGMENTATION_PATH = "../data/training/augmentation-images/"

BOTTLENECK_VGG16 = "../data/training/bottleneck_vgg16_1.npy"

CHECKPOINT_FILE = "../data/training/vgg16_checkpoint_1.h5"
FC_HISTORY = "../data/training/fc_hist"
MODEL_FC = "../data/training/model_fc_1.h5"
MODEL_SUMMARY = "../data/training/fc_summary_1"

MODEL_VGG16 = "../data/training/model_vgg16_2.h5"
HISTORY_VGG16 = "../data/training/model_vgg16_history_2"

CSV_RESTAURANTS = "../data/training/restaurants.csv"
X_TRAIN_FILE = "../data/training/x_train_1.npy"
Y_TRAIN_FILE = "../data/training/y_train_1.npy"

IMG_HEIGHT = 224
IMG_WIDTH = 224
MAX_IMAGES = 500
BATCH_SIZE = 16
EPOCHS = 20

def preprocess_image(imagePath):
  image = load_img(imagePath, target_size=(IMG_HEIGHT, IMG_WIDTH))
  image = img_to_array(image)
  image = image.reshape((1,) + image.shape)
  return image

def image_augmentation():
  """
  Increase the number of images in folder by image augmentation technique
  """
  restaurantsIds = [folder[1] for folder in os.walk(IMAGES_PATH)]
  restaurantsIds = restaurantsIds[0]
  imageGen = ImageDataGenerator(rotation_range=20, width_shift_range=0.2, height_shift_range=0.2, zoom_range=0.2, \
                                shear_range=0.2, horizontal_flip=True)
  for restaurantsId in restaurantsIds:
    print (restaurantsId)
    restaurantPath = IMAGES_PATH + restaurantsId + '/'
    for res in os.walk(restaurantPath):
      imageFiles = res[2]
      if len(imageFiles) == 0:
        continue
      batchSize = MAX_IMAGES // len(imageFiles)
      remainderSize = MAX_IMAGES % len(imageFiles)
      saveDir = AUGMENTATION_PATH + restaurantsId
      if os.path.exists(saveDir):
        shutil.rmtree(saveDir)
      os.mkdir(saveDir)
      for imageFile in imageFiles:
        sourceFile = restaurantPath + imageFile
        shutil.copyfile(sourceFile, saveDir + '/' + imageFile)
        image = preprocess_image(sourceFile)
        i = 1
        for _ in imageGen.flow(image, batch_size=1, save_to_dir=saveDir, save_prefix='new'):
          i += 1
          if i > batchSize: break
      if remainderSize > 0:
        image = preprocess_image(restaurantPath + imageFiles[0])
        i = 1
        for _ in imageGen.flow(image, batch_size=1, save_to_dir=saveDir, save_prefix='new'):
          i += 1
          if i > remainderSize: break 

def getTotalNumberofImages():
  total = 0
  restaurantsIds = [folder[1] for folder in os.walk(AUGMENTATION_PATH)]
  restaurantsIds = restaurantsIds[0]
  for restaurantsId in restaurantsIds:
    restaurantPath = AUGMENTATION_PATH + restaurantsId + '/'
    for res in os.walk(restaurantPath):
      imageFiles = res[2]
      total += len(imageFiles)
  return total

def getNumberOfRestaurants():
  restaurantsIds = [folder[1] for folder in os.walk(AUGMENTATION_PATH)]
  restaurantsIds = restaurantsIds[0]
  return len(restaurantsIds)

def create_training_data():
  TOTAL_IMAGES = getTotalNumberofImages()
  restaurantsIds = [folder[1] for folder in os.walk(AUGMENTATION_PATH)]
  restaurantsIds = restaurantsIds[0]
  labelDict = {}
  print (TOTAL_IMAGES)
  x_train = np.ndarray((TOTAL_IMAGES, IMG_HEIGHT, IMG_WIDTH, 3), dtype=np.float32)
  y_train = np.ndarray((TOTAL_IMAGES, ), dtype=np.uint8)
  i = 0
  count = 0
  for restaurantsId in restaurantsIds:
    print (restaurantsId)
    labelDict[restaurantsId] = count
    restaurantPath = AUGMENTATION_PATH + restaurantsId + '/'
    for res in os.walk(restaurantPath):
      imageFiles = res[2]
      for imageFile in imageFiles:
        image = load_img(restaurantPath + imageFile, target_size=(IMG_HEIGHT, IMG_WIDTH))
        image = img_to_array(image) / 255
        x_train[i] = image
        y_train[i] = count
        i += 1
    count += 1
  labelspd = pd.DataFrame(list(labelDict.items()), columns=['Id', 'Label'])
  labelspd.to_csv("../data/training/labelspd.csv")
  np.save(X_TRAIN_FILE, x_train)
  np.save(Y_TRAIN_FILE, y_train)

def preprocess_numpy_images(x_train):
  mean = np.mean(x_train, axis=0)
  std = np.std(x_train, axis=0)
  x_train -= mean
  x_train /= std
  return x_train

def train_vgg16_new(runBottleneck, runFC, runFinalModel):
  """
  Avoid random weights initialisation
  """
  # Get the data
  print ("Get the data---------------------------")
  NUM_CLASSES = getNumberOfRestaurants()
  x_train = np.load(X_TRAIN_FILE)
  x_train = preprocess_numpy_images(x_train)
  y_train = to_categorical(np.load(Y_TRAIN_FILE), num_classes=NUM_CLASSES)
  print (x_train.shape)
  print (y_train.shape)
  # Get the output of VGG16 model
  if runFC:
    print ("Get bottleneck output-----------------------------")
    if runBottleneck:
      vgg16Model = applications.VGG16(include_top=False,weights='imagenet')
      bottleneck_features = vgg16Model.predict(x_train, batch_size=BATCH_SIZE, verbose=2)
      np.save(BOTTLENECK_VGG16, bottleneck_features)
    else:
      bottleneck_features = np.load(BOTTLENECK_VGG16)
    # Train the FC layers to get the best weights (CHECKPOINT FILE)
    print ("Train FC layers----------------------------")
    model = Sequential()
    model.add(Flatten(input_shape=bottleneck_features.shape[1:]))
    model.add(Dense(1024, activation = 'relu'))
    # model.add(Dense(256, activation = 'relu'))
    model.add(Dropout(0.5))
    model.add(Dense(NUM_CLASSES, activation = 'softmax'))
    model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])
    with open(MODEL_SUMMARY, 'w') as f:
      with redirect_stdout(f):
        model.summary()

    checkpoint = ModelCheckpoint(CHECKPOINT_FILE, monitor='val_acc', verbose=1, save_best_only=True, mode='max')
    early_stopping = EarlyStopping(monitor='val_acc', patience=5)
    hist = model.fit(bottleneck_features, y_train, epochs=EPOCHS, validation_split=0.1, \
              batch_size=BATCH_SIZE, callbacks=[checkpoint, early_stopping], verbose=2)
    model.save(MODEL_FC)
    historyFile = open(FC_HISTORY, 'wb')
    pickle.dump(hist.history, historyFile)
    historyFile.close()

  if runFinalModel:
    print ("Train Final Model-----------------------------------")
    base_model = applications.VGG16(weights='imagenet', include_top=False, input_shape=(IMG_HEIGHT,IMG_WIDTH,3))
    top_model = Sequential()
    top_model.add(Flatten(input_shape=base_model.output_shape[1:]))
    top_model.add(Dense(1024, activation = 'relu'))
    # top_model.add(Dense(512, activation = 'relu'))
    # top_model.add(Dense(512, activation = 'relu'))
    top_model.add(Dropout(0.5))
    top_model.add(Dense(NUM_CLASSES, activation='softmax'))
    # top_model.load_weights(CHECKPOINT_FILE)
    finalModel = Model(inputs=base_model.input, outputs=top_model(base_model.output))
    for layer in finalModel.layers[:15]:
      layer.trainable = False
    finalModel.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy']) 
    early_stopping = EarlyStopping(monitor='val_acc', patience=5)    
    hist = finalModel.fit(x_train, y_train, epochs=EPOCHS, validation_split=0.1, \
              batch_size=BATCH_SIZE, verbose=2, callbacks=[early_stopping])
    finalModel.save(MODEL_VGG16)
    historyFile = open(HISTORY_VGG16, 'wb')
    pickle.dump(hist.history, historyFile)
    historyFile.close()

def test():
  x_train = np.load(X_TRAIN_FILE)
  print (x_train[0])

if __name__ == "__main__":
  parser = argparse.ArgumentParser()
  parser.add_argument("-a", "--augmentation", help="", action="store_true")
  parser.add_argument("-t", "--training", help="", action="store_true")
  parser.add_argument("-c", "--createtraining", help="", action="store_true")
  parser.add_argument("-d", "--debug", help="", action="store_true")
  
  args = parser.parse_args()
  if args.augmentation:
    image_augmentation()
  if args.createtraining:
    create_training_data()
  if args.training:
    train_vgg16_new(False, False, True)
  if args.debug:
    test()
