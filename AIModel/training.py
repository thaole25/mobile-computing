import os
import shutil
import argparse
import numpy as np 
import pickle
import pandas as pd 
from keras.preprocessing.image import load_img, img_to_array, ImageDataGenerator
from keras.models import Sequential
from keras.layers import Dropout, Flatten, Dense, GlobalAveragePooling2D
from keras import applications
from keras.utils.np_utils import to_categorical
from keras.callbacks import EarlyStopping, ModelCheckpoint
from contextlib import redirect_stdout
from sklearn.model_selection import train_test_split

import tensorflow as  tf 
config = tf.ConfigProto()
config.gpu_options.allow_growth = True
tf.Session(config=config)

IMAGES_PATH = "../data/training/images/"
AUGMENTATION_PATH = "../data/training/augmentation-images/"

####################### mobile net 
HISTORY_MOBILENET = "../data/training/model_mobile_history"
CHECKPOINT_MOBILE = "../data/training/mobile_checkpoint.h5"
BEST_MODEL_MOBILENET = "../data/training/best_model_mobile.h5"

X_TRAIN_FILE = "../data/training/x_train_small.npy" #"../data/training/x_train_2.npy"
Y_TRAIN_FILE = "../data/training/y_train_small.npy" #"../data/training/y_train_2.npy"

IMG_HEIGHT = 224
IMG_WIDTH = 224
MAX_IMAGES = 400
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

NUM_CLASSES = getNumberOfRestaurants()

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

def get_mobilenet():
  baseModel = applications.MobileNet(weights='imagenet', include_top=False, input_shape=(IMG_HEIGHT,IMG_WIDTH,3))
  mobileModel = Sequential()
  mobileModel.add(baseModel)
  mobileModel.add(GlobalAveragePooling2D())
  mobileModel.add(Dense(512, activation = 'relu'))
  mobileModel.add(Dense(512, activation = 'relu'))    
  mobileModel.add(Dropout(0.5))
  mobileModel.add(Dense(NUM_CLASSES, activation='softmax'))
  mobileModel.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy']) 

  return mobileModel

def train_mobilenet_new():
  print ("Get the data---------------------------")
  x = np.load(X_TRAIN_FILE)
  y = to_categorical(np.load(Y_TRAIN_FILE), num_classes=NUM_CLASSES)
  x_train, x_val, y_train, y_val = train_test_split(x, y, test_size=0.2)
  print ("Train Final Model-----------------------------------")
  mobileModel = get_mobilenet()
  checkpoint = ModelCheckpoint(CHECKPOINT_MOBILE, monitor='val_loss', verbose=1, save_best_only=True, mode='min')
  early_stopping = EarlyStopping(monitor='val_loss', patience=8)    
  hist = mobileModel.fit(x_train, y_train, epochs=EPOCHS, validation_data=(x_val, y_val), \
              batch_size=BATCH_SIZE, verbose=2, callbacks=[early_stopping, checkpoint])
  mobileModel.load_weights(CHECKPOINT_MOBILE)
  mobileModel.save(BEST_MODEL_MOBILENET)
  historyFile = open(HISTORY_MOBILENET, 'wb')
  pickle.dump(hist.history, historyFile)
  historyFile.close()

if __name__ == "__main__":
  parser = argparse.ArgumentParser()
  parser.add_argument("-a", "--augmentation", help="", action="store_true")
  parser.add_argument("-t", "--training", help="", action="store_true")
  parser.add_argument("-c", "--createtraining", help="", action="store_true")
  
  args = parser.parse_args()
  if args.augmentation:
    image_augmentation()
  if args.createtraining:
    create_training_data()
  if args.training:
    train_mobilenet_new()
