import os
import shutil
import argparse
import numpy as np 
import pickle
from keras.preprocessing.image import ImageDataGenerator, load_img, img_to_array
from keras.models import Sequential
from keras.layers import Dropout, Flatten, Dense
from keras import applications
from keras.utils.np_utils import to_categorical

IMAGES_PATH = "../data/training/images/"
AUGMENTATION_PATH = "../data/training/augmentation-images/"
CSV_RESTAURANTS = "../data/training/restaurants.csv"
MODEL_FILE = "../data/training/bottleneck_model.h5"
HISTORY_FILE = "../data/training/bottleneck_model_history.json"
BOTTLENECK_FEATURES_FILE = "../data/training/bottleneck_features.npy"

IMG_HEIGHT = 320 #640
IMG_WIDTH = 160 #320
MAX_IMAGES = 300
BATCH_SIZE = 16
EPOCHS = 40

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
  imageGen = ImageDataGenerator(rotation_range=20, width_shift_range=0.2, height_shift_range=0.2, zoom_range=0.2)
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
        image = preprocess_image(restaurantPath + imageFile)
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

def training_model(runBottleneck):
  datagen = ImageDataGenerator()
  if runBottleneck:
    generator = datagen.flow_from_directory(AUGMENTATION_PATH, target_size=(IMG_HEIGHT, IMG_WIDTH), batch_size=BATCH_SIZE, class_mode=None)
    model = applications.VGG16(include_top=False,weights='imagenet')
    bottleneck_features = model.predict(generator)
    np.save(BOTTLENECK_FEATURES_FILE, bottleneck_features)
  else:
    bottleneck_features = np.load(BOTTLENECK_FEATURES_FILE)

  modelTop = datagen.flow_from_directory(AUGMENTATION_PATH, target_size=(IMG_HEIGHT, IMG_WIDTH), batch_size=BATCH_SIZE, class_mode='categorical')
  NUM_CLASSES = len(modelTop.class_indices)
  y_train = to_categorical(modelTop.classes, num_classes=NUM_CLASSES)
  model = Sequential()
  model.add(Flatten(input_shape=bottleneck_features.shape[1:]))
  # model.add(Dense(512, activation='relu'))
  # model.add(Dropout(0.5))
  model.add(Dense(NUM_CLASSES, activation='softmax'))
  model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])
  hist = model.fit(bottleneck_features, y_train, epochs=EPOCHS, batch_size=BATCH_SIZE, validation_split=0.1, verbose=2)
  model.save(MODEL_FILE) 
  historyFile = open(HISTORY_FILE, 'wb')
  pickle.dump(hist.history, historyFile)
  historyFile.close()

if __name__ == "__main__":
  parser = argparse.ArgumentParser()
  parser.add_argument("-a", "--augmentation", help="", action="store_true")
  parser.add_argument("-t", "--training", help="", action="store_true")
  
  args = parser.parse_args()
  if args.augmentation:
    image_augmentation()
  if args.training:
    training_model(False)