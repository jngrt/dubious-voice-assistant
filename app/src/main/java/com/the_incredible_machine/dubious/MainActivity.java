package com.the_incredible_machine.dubious;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.makeText;
import static java.util.Arrays.asList;


public class MainActivity extends Activity implements RecognitionListener {
    private String LOG_TAG = "Dubious";

    public enum SpeechRecState {
        STARTED, READY_FOR_SPEECH, SPEECH_DETECTED, ERROR, SPEECH_ENDED
    }

    public static final String STATE_INIT = "init";
    public static final String STATE_LISTENING = "listening";
    public static final String STATE_START_LISTENING = "startListening";
    public static final String STATE_SPEAKING = "speaking";
    public static final String STATE_START_TIMEOUT = "startTimeout";
    public static final String STATE_REC_ERROR = "recError";
    public static final String STATE_NO_RESULTS = "noResults";
    public static final String STATE_LISTENING_TIMEOUT = "listeningTimeout";
    private String state = STATE_INIT;

    private TextView returnedText;
	private ProgressBar progressBar;
    private ArrayList<View> squares = new ArrayList<View>();

	private SpeechRecognizer speech = null;
	private Intent recognizerIntent;

    private TextToSpeech tts;

    public static final String STORY_HELLO = "hello";
    public static final String STORY_NEXT = "next";
    public static final String STORY_OTHER = "other";
    private HashMap<String, StoryPart> storyParts = new HashMap<String, StoryPart>();
    private String curStory = "";

	private AudioManager amanager;

    private Handler handler;
    private Runnable startedRunnable;
    private Runnable listeningRunnable;

    /*********************************
     * INITIALIZATION
     *********************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initAudio();
        initViews();
        initStories();
        initSpeechRec();
        initTTS();
        initHandler();

    }

    private void initAudio() {

        // Music stream is muted to prevent voice recogn. beeps
        amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
    }
    private void initViews() {
        setContentView(R.layout.activity_main);

        returnedText = (TextView) findViewById(R.id.textView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE);

        for( int i = 0; i < 4; i++ ){
            int resId = getResources().getIdentifier("square"+i, "id", getPackageName());
            squares.add(findViewById(resId));
        }

    }
    /*********************************
     * HANDLER / RUNNABLE
     *********************************/
    private void initHandler(){
        handler = new Handler();

        startedRunnable = new Runnable(){
            @Override
            public void run(){
                Log.i(LOG_TAG, "startedRunnable.run()");
                if( state == STATE_START_LISTENING ) {
                    resetSpeechRec( STATE_START_TIMEOUT);
                }
            }
        };

        listeningRunnable = new Runnable() {
            @Override
            public void run() {
                Log.i(LOG_TAG, "listeningRunnable.run()");
                if( state == STATE_LISTENING )
                    resetSpeechRec( STATE_LISTENING_TIMEOUT );
            }
        };

    }

    private void scheduleStartedCheck(){
        handler.removeCallbacks(startedRunnable);
        handler.postDelayed(startedRunnable, 10000);
    }
    private void cancelStartedCheck(){
        handler.removeCallbacks(startedRunnable);
    }

    private void scheduleListeningCheck(){
        handler.removeCallbacks(listeningRunnable);
        handler.postDelayed(listeningRunnable, 10000);
    }

    private void cancelListeningCheck(){
        handler.removeCallbacks(listeningRunnable);
    }


    /*********************************
     * STATE
     *********************************/
    private void setState( String newState ) {
        state = newState;
        Log.i(LOG_TAG, "** STATE: "+newState);
    }
    private void startListening() {

        if( state == STATE_START_LISTENING ) {
            return;
        }

        setState(STATE_START_LISTENING);

        speech.cancel();
        speech.startListening(recognizerIntent);
        showSquares();

        scheduleStartedCheck();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if( state == STATE_START_LISTENING ) {
//                    state = STATE_START_TIMEOUT;
//                }
//            }
//        }, 4*1000);
    }
    private void setIsListening() {
        setState(STATE_LISTENING);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        showSquares();

    }

    private void switchStory( String key ) {
        Log.i(LOG_TAG, "** SWITCH STATE SPEAKING - key: " + key);
        setState(STATE_SPEAKING);

        curStory = key;
        speak( key );

        showSquares();
    }

    private void listenAgain() {
        Log.i(LOG_TAG, "** LISTEN AGAIN");

        speech.cancel();
        speech.startListening(recognizerIntent);
        setState(STATE_START_LISTENING);
        showSquares();
    }

    private void giveFeedback() {

    }


    /*********************************
     * STORIES
     *********************************/

    private void initStories() {

        storyParts.put(STORY_HELLO,
                new StoryPart(
                        "one",
                        asList("one", "hello", "1"),
                        asList(STORY_NEXT, STORY_OTHER)
                ));
        storyParts.put(STORY_NEXT,
                new StoryPart(
                        "two",
                        asList("next", "two", "2"),
                        asList(STORY_OTHER)
                ));
        storyParts.put(STORY_OTHER,
                new StoryPart(
                        "three",
                        asList("three", "3", "tree"),
                        asList(STORY_HELLO)
                ));
    }

    private boolean checkStoryTriggers( ArrayList<String> matches ){
        //
        // Check for story triggers
        //
        List<String> partsToCheck = new ArrayList<String>();
        if ( curStory != null && curStory.length() > 0 ) {
            partsToCheck = storyParts.get(curStory).getFollowUps();
        }
        for (String result : matches) {
            for ( String key : partsToCheck ) {
                if ( storyParts.get(key).checkTriggers(result) ) {
                    switchStory( key );
                    return true;
                }
            }
        }

        return false;
    }



    /*********************************
     * SPEECHRECOGNIZER
     *********************************/

    private void initSpeechRec() {
        Log.i(LOG_TAG, "initSpeechRec");
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 2000);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
    }

    private void resetSpeechRec( String errorCode ){
        Log.i(LOG_TAG,"resetSpeechRec");
        if(state == STATE_LISTENING || state == STATE_START_LISTENING ){
            setState( errorCode );
            speech.destroy();
            initSpeechRec();
            startListening();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speech.destroy();
        tts.shutdown();
        amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
    }

    private void speak(String key) {
        tts.speak(storyParts.get(key).getStory(), TextToSpeech.QUEUE_FLUSH,null,key);

    }

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onBeginningOfSpeech() {
		Log.i(LOG_TAG, "onBeginningOfSpeech");
		progressBar.setIndeterminate(false);
		progressBar.setMax(10);

        //reset check
        scheduleListeningCheck();
	}

	@Override
	public void onBufferReceived(byte[] buffer) {
		Log.i(LOG_TAG, "onBufferReceived: " + buffer);
	}

	@Override
	public void onEndOfSpeech() {
		Log.i(LOG_TAG, "onEndOfSpeech");
	}

	@Override
	public void onError(int errorCode) {
		String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "onError " + errorMessage);

//        if ( errorCode == SpeechRecognizer.ERROR_RECOGNIZER_BUSY ){
//            return;
//        }
        debugText( errorMessage );

        setState(STATE_REC_ERROR);
        startListening();

	}

	@Override
	public void onEvent(int arg0, Bundle arg1) {
		Log.i(LOG_TAG, "onEvent");
	}

	//Partial results need to be enabled, see above
	@Override
	public void onPartialResults(Bundle results) {
		Log.i(LOG_TAG, "onPartialResults");
		//makeText(getApplicationContext(), "partial", Toast.LENGTH_LONG).show();

        scheduleListeningCheck();

        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);


        String text = "Part: [";
        for (String result : matches) {
            text += result + ", ";
        }
        debugText(text);
        Log.i(LOG_TAG, text);

        // If results are found in partial results then cancel current speech rec
        if(checkStoryTriggers(matches)){
            speech.cancel();
        }
	}

	@Override
	public void onReadyForSpeech(Bundle arg0) {

        Log.i(LOG_TAG, "onReadyForSpeech");
        setIsListening();

        cancelStartedCheck();
        scheduleListeningCheck();

    }

	@Override
	public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");

        cancelListeningCheck();

        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);


        String text = "Res: [";
        for (String result : matches) {
            text += result + ", ";
        }
        debugText( text );

        Log.i(LOG_TAG, text);

        // If no triggers, listen again
        if ( !checkStoryTriggers(matches) ) {
            setState(STATE_NO_RESULTS);
            listenAgain();
        }
    }


	@Override
	public void onRmsChanged(float rmsdB) {
		//Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        //System.out.print("("+rmsdB+")");
		progressBar.setProgress((int) rmsdB);
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
				message = "No speech input";
				break;
			default:
				message = "Didn't understand, please try again.";
				break;
		}

		return message;
	}

    /*********************************
     * TEXT TO SPEECH
     *********************************/

    private void initTTS() {
        // Set up TTS
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("error", "This Language is not supported");
                    } else {
                        switchStory(STORY_HELLO);
                    }


                } else
                    Log.e("error", "Initilization Failed!");


            }
        });
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.i(LOG_TAG, "tts.onStart " + utteranceId);

                amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);

            }

            @Override
            public void onDone(String utteranceId) {
                Log.i(LOG_TAG, "tts.onDone " + utteranceId);
                amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startListening();
                    }
                });
            }

            @Override
            public void onError(String utteranceId) {
                Log.i(LOG_TAG, "tts.onError " + utteranceId);

                amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);

            }
        });

        // Change TTS voice
        tts.setPitch(0.6f);
        tts.setSpeechRate(1.5f);
    }


    /*********************************
     * VIEW RELATED
     *********************************/
    private void showSquares() {
        for( View square: squares)
            square.setVisibility(View.INVISIBLE);

        switch( state ){
            case STATE_SPEAKING:
                squares.get(0).setVisibility(View.VISIBLE);
                break;
            case STATE_START_LISTENING:
                squares.get(2).setVisibility(View.VISIBLE);
                break;
            case STATE_LISTENING:
                squares.get(3).setVisibility(View.VISIBLE);
                break;
        }

    }

    private void debugText( String s ) {
        returnedText.setText(s + "\n" + returnedText.getText());

        if( returnedText.getText().length() > 600 )
            returnedText.setText( returnedText.getText().subSequence(0,300));

    }
}