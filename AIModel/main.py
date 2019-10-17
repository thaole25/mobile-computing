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

def convert_to_tensorflowLite():
  converter = tf.lite.TFLiteConverter.from_keras_model_file(constants.BEST_MODEL_MOBILENET)
  tfliteModel = converter.convert()
  f = open("mobilenet_tflite", "wb")
  f.write(tfliteModel)

if __name__ == "__main__":
  parser = argparse.ArgumentParser()
  parser.add_argument("-pre", "--preprocessingImages", help="", action="store_true")
  parser.add_argument("-train", "--training", help="", action="store_true")
  parser.add_argument("-test", "--runtesting", help="", action="store_true")
  parser.add_argument("-conv", "--convert", help="", action="store_true")
  
  args = parser.parse_args()
  if args.preprocessingImages:
    preprocessing.image_augmentation()
    preprocessing.create_training_data()
  if args.training:
    model.run_mobilenet()
  if args.runtesting:
    testing.testing()
  if args.convert:
    convert_to_tensorflowLite()

    
