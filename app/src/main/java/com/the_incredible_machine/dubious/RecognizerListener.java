package com.the_incredible_machine.dubious;

import java.util.ArrayList;

public interface RecognizerListener {
    public void onRecognizerResult(ArrayList<String> results );
    public void onRecognizerInitStarted();
    public void onRecognizerInitDone();
    public void onRecognizerSpeechActivity(float volume);
    public void onRecognizerSpeechBegin();
}
