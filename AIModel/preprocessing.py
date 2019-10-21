from keras.preprocessing.image import load_img, img_to_array, ImageDataGenerator
import constants
import os
import shutil
import numpy as np 
import pandas as pd 

def preprocess_image(imagePath):
  image = load_img(imagePath, target_size=(constants.IMG_HEIGHT, constants.IMG_WIDTH))
  image = img_to_array(image)
  image = image.reshape((1,) + image.shape)
  return image

def image_augmentation():
  """
  Increase the number of images in folder by image augmentation technique
  """
  restaurantsIds = [folder[1] for folder in os.walk(constants.IMAGES_PATH)]
  restaurantsIds = restaurantsIds[0]
  imageGen = ImageDataGenerator(rotation_range=20, width_shift_range=0.2, height_shift_range=0.2, zoom_range=0.2, \
                                shear_range=0.2, horizontal_flip=True)
  for restaurantsId in restaurantsIds:
    print (restaurantsId)
    restaurantPath = constants.IMAGES_PATH + restaurantsId + '/'
    for res in os.walk(restaurantPath):
      imageFiles = res[2]
      if len(imageFiles) == 0:
        continue
      batchSize = constants.MAX_IMAGES // len(imageFiles)
      remainderSize = constants.MAX_IMAGES % len(imageFiles)
      saveDir = constants.AUGMENTATION_PATH + restaurantsId
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

def create_training_data():
  restaurantsIds = [folder[1] for folder in os.walk(constants.AUGMENTATION_PATH)]
  restaurantsIds = restaurantsIds[0]
  labelDict = {}
  print (constants.TOTAL_IMAGES)
  x_train = np.ndarray((constants.TOTAL_IMAGES, constants.IMG_HEIGHT, constants.IMG_WIDTH, 3), dtype=np.float32)
  y_train = np.ndarray((constants.TOTAL_IMAGES, ), dtype=np.uint8)
  i = 0
  count = 0
  for restaurantsId in restaurantsIds:
    print (restaurantsId)
    labelDict[restaurantsId] = count
    restaurantPath = constants.AUGMENTATION_PATH + restaurantsId + '/'
    for res in os.walk(restaurantPath):
      imageFiles = res[2]
      for imageFile in imageFiles:
        image = load_img(restaurantPath + imageFile, target_size=(constants.IMG_HEIGHT, constants.IMG_WIDTH))
        image = img_to_array(image) / 255
        x_train[i] = image
        y_train[i] = count
        i += 1
    count += 1
  labelspd = pd.DataFrame(list(labelDict.items()), columns=['Id', 'Label'])
  labelspd.to_csv("../data/training/labelspd.csv")
  np.save(constants.X_TRAIN_FILE, x_train)
  np.save(constants.Y_TRAIN_FILE, y_train)