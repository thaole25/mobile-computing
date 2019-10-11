import os
from keras.preprocessing.image import ImageDataGenerator, load_img, img_to_array
import shutil
import argparse

IMG_HEIGHT = 512
IMG_WIDTH = 512
MAX_IMAGES = 20

IMAGES_PATH = "../data/training/images/"
AUGMENTATION_PATH = "../data/training/augmentation-images/"
CSV_RESTAURANTS = "../data/training/restaurants.csv"

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
    
if __name__ == "__main__":
  parser = argparse.ArgumentParser()
  parser.add_argument("-a", "--augmentation", help="", action="store_true")
  args = parser.parse_args()
  if args.augmentation:
    image_augmentation()