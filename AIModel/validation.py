import pickle
import numpy as np 
import matplotlib.pyplot as plt

HISTORY_FILE = "../data/training/bottleneck_model_history"
EVALUATION_FOLDER = "evaluation/"

def read_history():
  output = open(HISTORY_FILE, 'rb')
  history = pickle.load(output)
  epoches = np.arange(len(history['accuracy']))
  accuracy = history['accuracy']
  valAcc = history['val_accuracy']
  plt.xlabel("Epoch")
  plt.plot(epoches, accuracy)
  plt.plot(epoches, valAcc)
  plt.legend(["accuracy", "val_accuracy"])
  plt.savefig(EVALUATION_FOLDER + "accuracy.png")

if __name__ == "__main__":
  read_history()
    