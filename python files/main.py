from model import AllistoPredict
from audio_analyzer import AudioAnalyze

print("abc")
analyzed_audio = AudioAnalyze("sample.wav")
print (analyzed_audio)
print("Audio Analysis Complete..")

allisto_prediction = AllistoPredict()
print("comp.....")
print(allisto_prediction.predict(analyzed_audio.slice_audio_parameters()))
print ("comp..")
print(allisto_prediction)
