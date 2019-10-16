import pickle
import numpy as np 
import matplotlib.pyplot as plt

HISTORY_FILE = "../data/training/fc_hist"
# HISTORY_FILE = "../data/training/model_vgg16_history_1"

EVALUATION_FOLDER = "evaluation/"

def read_history():
  output = open(HISTORY_FILE, 'rb')
  history = pickle.load(output)
  epoches = np.arange(len(history['acc']))
  accuracy = history['acc']
  valAcc = history['val_acc']
  plt.xlabel("Epoch")
  plt.plot(epoches, accuracy)
  plt.plot(epoches, valAcc)
  plt.legend(["acc", "val_acc"])
  plt.savefig(EVALUATION_FOLDER + "accuracy.png")

if __name__ == "__main__":
  read_history()
    