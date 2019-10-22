"""
Run this file
"""
######### 
#May require depending on the version of tensorflow and GPU Version
import tensorflow as  tf 
config = tf.ConfigProto()
config.gpu_options.allow_growth = True
tf.Session(config=config)
#########
import argparse
import preprocessing
import model
import testing
import constants
import pandas as pd
def convert_to_tensorflowLite():
  converter = tf.lite.TFLiteConverter.from_keras_model_file(constants.BEST_MODEL_MOBILENET)
  tfliteModel = converter.convert()
  f = open("mobilenet_tflite", "wb")
  f.write(tfliteModel)

def get_all_classes_file():
  shortLabels = pd.read_csv(constants.TRAIN_FOLDER + "labelspd_final.csv")
  fullLabels = pd.read_csv(constants.TRAIN_FOLDER + "restaurants.csv")
  outputLabels = pd.merge(shortLabels, fullLabels, on='Id', how='outer')
  outputLabels.to_csv(constants.TRAIN_FOLDER + "final_labels.csv")

if __name__ == "__main__":
  parser = argparse.ArgumentParser()
  parser.add_argument("-pre", "--preprocessingImages", help="", action="store_true")
  parser.add_argument("-train", "--training", help="", action="store_true")
  parser.add_argument("-test", "--runtesting", help="", action="store_true")
  parser.add_argument("-conv", "--convert", help="", action="store_true")
  
  args = parser.parse_args()
  if args.preprocessingImages:
    # preprocessing.image_augmentation()
    preprocessing.create_training_data()
  if args.training:
    # model.run_mobilenet()
    model.save_model()
  if args.runtesting:
    # testing.create_test_data()
    testing.testing()
  if args.convert:
    convert_to_tensorflowLite()
  # get_all_classes_file()

    
