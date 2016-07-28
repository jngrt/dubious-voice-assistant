package com.the_incredible_machine.dubious;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import static android.widget.Toast.makeText;
import static java.util.Arrays.asList;

public class MainActivity extends Activity implements
		RecognitionListener {


    private TextView returnedText;
	private ToggleButton toggleButton;
	private ProgressBar progressBar;
    private RadioButton radioListening;
    private RadioButton radioSpeaking;
    private RadioButton radioProcessing;
    private RadioGroup radioGroupState;

	private SpeechRecognizer speech = null;
	private Intent recognizerIntent;
	private String LOG_TAG = "VoiceRecognitionActivity";

    private TextToSpeech tts;

    public static String STORY_HELLO = "hello";
    public static String STORY_NEXT = "next";
    public static String STORY_OTHER = "other";

    private HashMap<String, StoryPart> storyParts = new HashMap<String, StoryPart>();
    private String curStory = "";

    private void initStories() {

        storyParts.put(STORY_HELLO,
                new StoryPart(
                        "hello",
                        asList("why", "yes", "hello"),
                        asList(STORY_NEXT, STORY_OTHER)
                ));
        storyParts.put(STORY_NEXT,
                new StoryPart(
                        "the next story",
                        asList("next", "trigger next", "forward"),
                        asList(STORY_OTHER)
                ));
        storyParts.put(STORY_OTHER,
                new StoryPart(
                        "the other story",
                        asList("other", "bitches"),
                        asList(STORY_HELLO)
                ));
    }

    private void switchStory( String key ) {
        Log.i(LOG_TAG, "switchStory: " + key);
        curStory = key;
        convertTextToSpeech( storyParts.get(key).getStory());
    }


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

		returnedText = (TextView) findViewById(R.id.textView1);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
		progressBar.setVisibility(View.INVISIBLE);
        radioListening = (RadioButton) findViewById(R.id.radioListening);
        radioSpeaking = (RadioButton) findViewById(R.id.radioSpeaking);
        radioProcessing = (RadioButton) findViewById(R.id.radioProcessing);
        radioGroupState = (RadioGroup) findViewById(R.id.radioState);


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

		//Turn on this for receiving partial results
		//recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);


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
                        /*
                        * 1. THIS IS WHERE THE FLOW STARTS
                         */
                        switchStory ( STORY_HELLO );
                    }



                } else
                    Log.e("error", "Initilization Failed!");


            }
        });
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                Log.i(LOG_TAG, "tts done");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startListening();
                    }
                });
            }

            @Override
            public void onError(String utteranceId) {

            }
        });

        // Change TTS voice
        tts.setPitch(0.6f);
        tts.setSpeechRate(1.5f);

        initStories();

        //Below doesn't work to mute the speech rec beep,
        // because beep is on stream_music, same as TTS
        //AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        //amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
        //amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
        //amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        //amanager.setStreamMute(AudioManager.STREAM_RING, true);
        //amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);

    }
    private void convertTextToSpeech(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH,null,TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
        radioGroupState.check(R.id.radioSpeaking);

    }

    private void startListening() {
        speech.startListening(recognizerIntent);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        radioGroupState.check(R.id.radioListening);
    }
    private void stopListening() {
        speech.stopListening();

        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
    }


	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (speech != null) {
			speech.destroy();
			Log.i(LOG_TAG, "destroy");
		}

	}

	@Override
	public void onBeginningOfSpeech() {
		Log.i(LOG_TAG, "onBeginningOfSpeech");
		progressBar.setIndeterminate(false);
		progressBar.setMax(10);
	}

	@Override
	public void onBufferReceived(byte[] buffer) {
		Log.i(LOG_TAG, "onBufferReceived: " + buffer);
	}

	@Override
	public void onEndOfSpeech() {
		Log.i(LOG_TAG, "onEndOfSpeech");
		//progressBar.setIndeterminate(true);
		//toggleButton.setChecked(false);
		//startListening();
        if ( tts.isSpeaking() ) {
            convertTextToSpeech("are you still there?");
        }
	}

	@Override
	public void onError(int errorCode) {
		String errorMessage = getErrorText(errorCode);
		Log.d(LOG_TAG, "FAILED " + errorMessage);
		returnedText.setText(errorMessage);
		//toggleButton.setChecked(false);
        if ( !tts.isSpeaking() )
            startListening();


        //if ( Math.random() > 0.5f ) {
          //  convertTextToSpeech("What was that?");
        //} else {
         //   convertTextToSpeech("Could you speak up please?");
        //}
	}

	@Override
	public void onEvent(int arg0, Bundle arg1) {
		Log.i(LOG_TAG, "onEvent");
	}

	//Partial results need to be enabled, see above
	@Override
	public void onPartialResults(Bundle results) {
		Log.i(LOG_TAG, "onPartialResults");
		makeText(getApplicationContext(), "partial", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onReadyForSpeech(Bundle arg0) {
		Log.i(LOG_TAG, "onReadyForSpeech");
	}

	@Override
	public void onResults(Bundle results) {
		Log.i(LOG_TAG, "onResults");

        ArrayList<String> matches = results
				.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);



        //
        // Set text
        //
        String text = "";
        for ( String result: matches ) {
            text += result + "\n";
        }
        returnedText.setText(text);

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
                    return;
                }
            }
		}

        //
        // If no match found
        //
        convertTextToSpeech("Did you say something?");

	}

	@Override
	public void onRmsChanged(float rmsdB) {
		//Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
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

}