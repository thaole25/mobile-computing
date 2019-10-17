import pickle
import numpy as np 
import matplotlib.pyplot as plt
from keras.preprocessing.image import load_img, img_to_array, ImageDataGenerator
from keras.models import load_model
import os 
from keras.utils.np_utils import to_categorical

HISTORY_MOBILENET = "../data/training/model_mobile_history"
BEST_MODEL_MOBILENET = "../data/training/best_model_mobile.h5"
EVALUATION_FOLDER = "evaluation/"
TEST_FOLDER = "../data/test/"
IMG_HEIGHT = 224
IMG_WIDTH = 224
BATCH_SIZE = 2
NUM_CLASSES = 11

X_TEST_FILE = "../data/test/x_test.npy"
Y_TEST_FILE = "../data/test/y_test.npy"

def read_history():
  output = open(HISTORY_MOBILENET, 'rb')
  history = pickle.load(output)
  epoches = np.arange(len(history['acc']))
  accuracy = history['acc']
  valAcc = history['val_acc']
  plt.xlabel("Epoch")
  plt.plot(epoches, accuracy)
  plt.plot(epoches, valAcc)
  plt.legend(["acc", "val_acc"])
  plt.savefig(EVALUATION_FOLDER + "accuracy.png")

def getTotalNumberofImages():
  total = 0
  restaurantsIds = [folder[1] for folder in os.walk(TEST_FOLDER)]
  restaurantsIds = restaurantsIds[0]
  for restaurantsId in restaurantsIds:
    restaurantPath = TEST_FOLDER + restaurantsId + '/'
    for res in os.walk(restaurantPath):
      imageFiles = res[2]
      total += len(imageFiles)
  return total

def create_test_data():
  TOTAL_IMAGES = getTotalNumberofImages()
  restaurantsIds = [folder[1] for folder in os.walk(TEST_FOLDER)]
  restaurantsIds = restaurantsIds[0]
  x_test = np.ndarray((TOTAL_IMAGES, IMG_HEIGHT, IMG_WIDTH, 3), dtype=np.float32)
  y_test = np.ndarray((TOTAL_IMAGES, ), dtype=np.uint8)
  i = 0
  for restaurantsId in restaurantsIds:
    print (restaurantsId)
    restaurantPath = TEST_FOLDER + restaurantsId + '/'
    for res in os.walk(restaurantPath):
      imageFiles = res[2]
      for imageFile in imageFiles:
        image = load_img(restaurantPath + imageFile, target_size=(IMG_HEIGHT, IMG_WIDTH))
        image = img_to_array(image) / 255
        x_test[i] = image
        y_test[i] = restaurantsId
        i += 1
  np.save(X_TEST_FILE, x_test)
  np.save(Y_TEST_FILE, y_test)

def testing():
  x_test = np.load(X_TEST_FILE)
  y_test = np.load(Y_TEST_FILE)
  y_test = to_categorical(y_test, num_classes=NUM_CLASSES)
  model = load_model(BEST_MODEL_MOBILENET)
  # print (model.summary())
  prediction = model.evaluate(x_test, y_test, batch_size=BATCH_SIZE, verbose=1)
  print (prediction)

if __name__ == "__main__":
  # read_history()
  # create_test_data()
  testing()
    