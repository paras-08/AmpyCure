from audio_analyzer import AudioAnalyze
from sklearn import preprocessing
import pickle
import numpy as np
with open('model.pkl', 'rb') as f:
    classifier = pickle.load(f)

from sklearn.preprocessing import StandardScaler
sc = StandardScaler()

lab_enc = preprocessing.LabelEncoder()

analyzed_audio = AudioAnalyze("sample.wav")
print (analyzed_audio)
print("Audio Analysis Complete..")

check = analyzed_audio.slice_audio_parameters()
check = sc.fit_transform(check)
check = check.ravel()
check = lab_enc.fit_transform(check)
#y_train = lab_enc.fit_transform(y_train)
check = np.reshape(check, (-1, 19))
print(classifier.predict(check))