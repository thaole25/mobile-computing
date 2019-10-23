import pickle
import numpy as np 
import matplotlib.pyplot as plt
from keras.preprocessing.image import load_img, img_to_array, ImageDataGenerator
from keras.models import load_model
import os 
from keras.utils.np_utils import to_categorical
import constants

def read_history():
  output = open(constants.HISTORY_MOBILENET, 'rb')
  history = pickle.load(output)
  epoches = np.arange(len(history['acc']))
  accuracy = history['acc']
  valAcc = history['val_acc']
  plt.xlabel("Epoch")
  plt.plot(epoches, accuracy)
  plt.plot(epoches, valAcc)
  plt.legend(["acc", "val_acc"])
  plt.savefig(constants.EVALUATION_FOLDER + "accuracy.png")

def create_test_data():
  restaurantsIds = [folder[1] for folder in os.walk(constants.TEST_FOLDER)]
  restaurantsIds = restaurantsIds[0]
  x_test = np.ndarray((constants.TOTAL_TEST_IMAGES, constants.IMG_HEIGHT, constants.IMG_WIDTH, 3), dtype=np.float32)
  y_test = np.ndarray((constants.TOTAL_TEST_IMAGES, ), dtype=np.uint8)
  i = 0
  for restaurantsId in restaurantsIds:
    print (restaurantsId)
    restaurantPath = constants.TEST_FOLDER + restaurantsId + '/'
    for res in os.walk(restaurantPath):
      imageFiles = res[2]
      for imageFile in imageFiles:
        image = load_img(restaurantPath + imageFile, target_size=(constants.IMG_HEIGHT, constants.IMG_WIDTH))
        image = img_to_array(image) / 255
        print (image)
        x_test[i] = image
        y_test[i] = restaurantsId
        i += 1
  np.save(constants.X_TEST_FILE, x_test)
  np.save(constants.Y_TEST_FILE, y_test)

def testing():
  x_test = np.load(constants.X_TEST_FILE)
  y_test = np.load(constants.Y_TEST_FILE)
  y_test = to_categorical(y_test, num_classes=constants.NUM_CLASSES)
  model = load_model(constants.BEST_MODEL_MOBILENET)
  prediction = model.evaluate(x_test, y_test, batch_size=constants.BATCH_SIZE_TEST, verbose=1)
  
  # Return the probabilty distribution of all classes for each input image
  # prediction = model.predict(x_test)
  print (prediction)

    