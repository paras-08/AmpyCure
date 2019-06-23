import csv, glob, os
from pyAudioAnalysis import audioBasicIO, audioFeatureExtraction
import numpy as np

file_list = list()

os.chdir("./dataset")
for file in glob.glob("*.wav"):
    file_list.append(file)

csv_list = list()

for file in file_list:
    [Fs, x] = audioBasicIO.readAudioFile(file)
    F = audioFeatureExtraction.stFeatureExtraction(
        np.mean(x, axis=1), Fs, 0.050 * Fs, 0.025 * Fs)
    input_from_audio = F[0][1]
    value = input_from_audio[:40]
    value = value.tolist()
    value.append(1)
    csv_list.append(value)

# print(csv_list)

with open('person.csv', 'w') as csvFile:
    writer = csv.writer(csvFile)
    writer.writerows(csv_list)

