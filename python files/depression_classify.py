# Logistic Regression

# Importing the libraries
from audio_analyzer import AudioAnalyze

import numpy as np
import pandas as pd
from sklearn import preprocessing
from sklearn import utils

# Importing the dataset
dataset = pd.read_csv('depression.csv')
X = dataset.iloc[1:, 0:19].values
y = dataset.iloc[1:, 20].values
y = y.astype('int')
print(y)
# Splitting the dataset into the Training set and Test set
from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 0.20, random_state = 0)

# Feature Scaling
from sklearn.preprocessing import StandardScaler
sc = StandardScaler()
X_train = sc.fit_transform(X_train)
X_test = sc.transform(X_test)

lab_enc = preprocessing.LabelEncoder()
X_train = X_train.ravel()
X_train = lab_enc.fit_transform(X_train)
#y_train = lab_enc.fit_transform(y_train)
X_train = np.reshape(X_train, (-1, 19))

# Fitting Decision Tree Classification to the Training set
from sklearn.tree import DecisionTreeClassifier
classifier = DecisionTreeClassifier(criterion = 'entropy', random_state = 0)
classifier.fit(X_train, y_train)

import pickle
# now you can save it to a file
with open('model_depression.pkl', 'wb') as f:
    pickle.dump(classifier, f)



X_test=X_test.ravel()
X_test = lab_enc.fit_transform(X_test)
# Predicting the Test set results
X_test = np.reshape(X_test, (-1, 19))
y_pred = classifier.predict(X_test)
print(y_pred)
print(y_test)
# Making the Confusion Matrix
from sklearn.metrics import confusion_matrix
cm = confusion_matrix(y_test, y_pred)



conf_mat = confusion_matrix(y_pred, y_test)
acc = np.sum(conf_mat.diagonal()) / np.sum(conf_mat)
print('Overall accuracy: {} %'.format(acc*100))


analyzed_audio = AudioAnalyze("aud1.wav")
print (analyzed_audio)
print("Audio Analysis Complete..")

check = analyzed_audio.slice_audio_parameters()
check = sc.fit_transform(check)
check = check.ravel()
check = lab_enc.fit_transform(check)
#y_train = lab_enc.fit_transform(y_train)
check = np.reshape(check, (-1, 19))
print(classifier.predict(check))

TP = 0

FP = 0
TN = 0
FN = 0
y_hat=y_pred
y_actual=y_test
for i in range(len(y_hat)):
        if y_actual[i]==y_hat[i]==1:
           TP += 1
        if y_hat[i]==1 and y_actual[i]!=y_hat[i]:
           FP += 1
        if y_actual[i]==y_hat[i]==0:
           TN += 1
        if y_hat[i]==0 and y_actual[i]!=y_hat[i]:
           FN += 1
print(TP)
print(FP)
print(TN)
print(FN)