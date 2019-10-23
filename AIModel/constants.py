import os 

IMAGES_PATH = "../data/training/images/"
AUGMENTATION_PATH = "../data/training/augmentation-images/"
EVALUATION_FOLDER = "evaluation/"
TEST_FOLDER = "../data/test/"
TRAIN_FOLDER = "../data/training/"

HISTORY_MOBILENET = "../data/training/model_mobile_history"
CHECKPOINT_MOBILE = "../data/training/mobile_checkpoint.h5"
BEST_MODEL_MOBILENET = "../data/training/best_model_mobile.h5"

X_TRAIN_FILE = "../data/training/x_train_small.npy" 
Y_TRAIN_FILE = "../data/training/y_train_small.npy"
X_TEST_FILE = "../data/test/x_test.npy"
Y_TEST_FILE = "../data/test/y_test.npy"

IMG_HEIGHT = 224
IMG_WIDTH = 224
MAX_IMAGES = 600
BATCH_SIZE_TRAIN = 16
BATCH_SIZE_TEST = 2
EPOCHS = 16

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

def getTotalNumberofTestImages():
  total = 0
  restaurantsIds = [folder[1] for folder in os.walk(TEST_FOLDER)]
  restaurantsIds = restaurantsIds[0]
  for restaurantsId in restaurantsIds:
    restaurantPath = TEST_FOLDER + restaurantsId + '/'
    for res in os.walk(restaurantPath):
      imageFiles = res[2]
      total += len(imageFiles)
  return total

TOTAL_IMAGES = getTotalNumberofImages()
NUM_CLASSES = getNumberOfRestaurants()
TOTAL_TEST_IMAGES = getTotalNumberofTestImages()
