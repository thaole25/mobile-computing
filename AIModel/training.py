import os
from keras.preprocessing.image import ImageDataGenerator, load_img, img_to_array
import shutil
import argparse
import numpy as np 
import pandas as pd 

IMG_HEIGHT = 320 #640
IMG_WIDTH = 160 #320
MAX_IMAGES = 50

IMAGES_PATH = "../data/training/images/"
AUGMENTATION_PATH = "../data/training/augmentation-images/"
CSV_RESTAURANTS = "../data/training/restaurants.csv"
X_TRAIN_FILE = "../data/training/x_train.npy"
Y_TRAIN_FILE = "../data/training/y_train.npy"

def preprocess_image(imagePath):
  image = load_img(imagePath, target_size=(IMG_HEIGHT, IMG_WIDTH))
  image = img_to_array(image)
  image = image.reshape((1,) + image.shape)
  return image

def image_augmentation():
  """
  Increase the number of images in folder by image augmentation technique
  """
  imageGen = ImageDataGenerator(rotation_range=20, width_shift_range=0.2, height_shift_range=0.2, zoom_range=0.2)
  restaurantsIds = [folder[1] for folder in os.walk(IMAGES_PATH)]
  restaurantsIds = restaurantsIds[0]
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

def create_training_data():
  restaurantspd = pd.read_csv(CSV_RESTAURANTS, names=['Id','Name','Latitude','Longitude','Address'])
  restaurantspd['Label'] = restaurantspd['Id'].astype('category')
  restaurantspd = restaurantspd.set_index('Id')
  restaurantsDict = restaurantspd.to_dict()
  restaurantsLabels = restaurantsDict['Label']

  restaurantsIds = [folder[1] for folder in os.walk(IMAGES_PATH)]
  restaurantsIds = restaurantsIds[0]
  totalInstances = len(restaurantsIds) * MAX_IMAGES
  x_train = np.ndarray((totalInstances, IMG_HEIGHT, IMG_WIDTH, 3), dtype=np.float32)
  y_train = np.ndarray((totalInstances, ), dtype=np.uint8)
  i = 0
  for restaurantsId in restaurantsIds:
    restaurantPath = IMAGES_PATH + restaurantsId + '/'
    for res in os.walk(restaurantPath):
      imageFiles = res[2]
      for imageFile in imageFiles:
        image = load_img(restaurantPath + imageFile, target_size=(IMG_HEIGHT, IMG_WIDTH))
        image = img_to_array(image)
        x_train[i] = image
        y_train[i] = restaurantsLabels[int(restaurantsId)]
        i += 1
  np.save(X_TRAIN_FILE, x_train)
  np.save(Y_TRAIN_FILE, y_train)


if __name__ == "__main__":
  parser = argparse.ArgumentParser()
  parser.add_argument("-a", "--augmentation", help="", action="store_true")
  parser.add_argument("-c", "--create_training", help="", action="store_true")
  
  args = parser.parse_args()
  if args.augmentation:
    image_augmentation()
  if args.create_training:
    create_training_data()