from keras.models import Sequential
from keras.layers import Dropout, Flatten, Dense, GlobalAveragePooling2D
from keras import applications
from keras.utils.np_utils import to_categorical
from keras.callbacks import EarlyStopping, ModelCheckpoint
from sklearn.model_selection import train_test_split
import numpy as np 
import pickle
import constants

def get_mobilenet():
  baseModel = applications.MobileNet(weights='imagenet', include_top=False, input_shape=(constants.IMG_HEIGHT, constants.IMG_WIDTH, 3))
  mobileModel = Sequential()
  mobileModel.add(baseModel)
  mobileModel.add(GlobalAveragePooling2D())
  mobileModel.add(Dense(512, activation = 'relu'))
  mobileModel.add(Dense(512, activation = 'relu'))    
  mobileModel.add(Dropout(0.5))
  mobileModel.add(Dense(constants.NUM_CLASSES, activation='softmax'))
  mobileModel.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy']) 
  return mobileModel

def run_mobilenet():
  print ("Get the data---------------------------")
  x = np.load(constants.X_TRAIN_FILE)
  y = to_categorical(np.load(constants.Y_TRAIN_FILE), num_classes=constants.NUM_CLASSES)
  x_train, x_val, y_train, y_val = train_test_split(x, y, test_size=0.2)
  print ("Train Final Model-----------------------------------")
  mobileModel = get_mobilenet()
  checkpoint = ModelCheckpoint(constants.CHECKPOINT_MOBILE, monitor='val_loss', verbose=1, save_best_only=True, mode='min')
  early_stopping = EarlyStopping(monitor='val_loss', patience=8)    
  hist = mobileModel.fit(x_train, y_train, epochs=constants.EPOCHS, validation_data=(x_val, y_val), \
              batch_size=constants.BATCH_SIZE_TRAIN, verbose=2, callbacks=[early_stopping, checkpoint])
  mobileModel.load_weights(constants.CHECKPOINT_MOBILE)
  mobileModel.save(constants.BEST_MODEL_MOBILENET)
  historyFile = open(constants.HISTORY_MOBILENET, 'wb')
  pickle.dump(hist.history, historyFile)
  historyFile.close()

