package com.the_incredible_machine.dubious;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;


public class Recognizer implements RecognitionListener {
    private static String LOG_TAG = "Dubious.Recognizer";

    private enum State {
        INIT, STARTING, LISTENING, STOPPED
    }
    private boolean partialResults = false;

    private State state = State.INIT;

    private SpeechRecognizer speechRecognizer = null;
    private Intent recognizerIntent;
    private Context context;

    private RecognizerListener listener;

    private int resultsLength = 0;

    public Recognizer(Context context, RecognizerListener listener) {
        this.context = context;
        this.listener = listener;
        init();
    }

    public Boolean isListening() {
        return state == State.LISTENING;
    }

    private void init() {
        Log.i(LOG_TAG, "initSpeechRec");


        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                context.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 2000);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
    }

    public void startListening() {
        Log.i(LOG_TAG, "startListening");

        //busyError = false;

        //init
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(this);

        //start
        state = State.STARTING;
        speechRecognizer.startListening(recognizerIntent);
        listener.onRecognizerInitStarted();


        //scheduleStartedCheck();

    }

    public void stopListening() {
        Log.i(LOG_TAG, "stopListening");

        state = State.STOPPED;

        //We now destroy each time and do re-init when starting
        speechRecognizer.destroy();

    }

    public void continueListening() {
        Log.i(LOG_TAG, "continueListening " + partialResults);
        if ( partialResults ) {
            // if partial results are becoming too long it becomes slow
            if (resultsLength > 60) {
                resetListening();
            } else {
                state = State.LISTENING;
            }
        } else {
            //if it was end result we need to start listening again
            startListening();
        }
    }

    public void destroy() {
        Log.i(LOG_TAG, "destroy");

        speechRecognizer.destroy();

    }


    private void resetListening() {
        Log.i(LOG_TAG, "resetListening");
        destroy();
        //init();
        startListening();
    }


    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");

        listener.onRecognizerSpeechBegin();

    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEvent(int eventType, Bundle params) {}

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");

    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.i(LOG_TAG, "onError " + errorMessage);

        if( errorCode == SpeechRecognizer.ERROR_RECOGNIZER_BUSY ){

            resetListening();
        } else if ((errorCode == SpeechRecognizer.ERROR_NO_MATCH)
                || (errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)) {
            resetListening();
        } else {
            Log.i(LOG_TAG, "UNHANDLED ERROR ");
        }
    }


    //Partial results need to be enabled, see above
    @Override
    public void onPartialResults(Bundle results) {
        Log.i(LOG_TAG, "onPartialResults");

        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        resultsLength = matches.get(0).length();
        Log.i(LOG_TAG, "length:"+resultsLength);

        partialResults = true;

        listener.onRecognizerResult(matches);

    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {

        Log.i(LOG_TAG, "onReadyForSpeech");

        state = State.LISTENING;

        listener.onRecognizerInitDone();

    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");

        partialResults = false;

        if ( state == State.LISTENING ) {
            ArrayList<String> matches = results
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            listener.onRecognizerResult(matches);
        } else {
            Log.i(LOG_TAG, "got results, but state is "+state.toString());
        }
    }


    @Override
    public void onRmsChanged(float rmsDb) {

        listener.onRecognizerSpeechActivity(rmsDb);
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speechRecognizer input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }

        return message;
    }
}
