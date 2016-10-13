package com.the_incredible_machine.dubious;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import static java.util.Arrays.asList;


public class MainActivity extends Activity implements SpeakerListener, RecognizerListener, StoryListener {
    private String LOG_TAG = "Dubious.Main";

    private enum State {
        INIT, SPEAKING, LISTENING
    }
    private State state = State.INIT;

    private TextView debugText;
	private ProgressBar progressBar;
    private ArrayList<View> squares = new ArrayList<View>();
    private View listenSquare;
    private View speakingSquare;
    private View inputSquare;
    private View processingSquare;

    private StoryManager storyManager;
    private Speaker speaker;
    private Recognizer recognizer;

    /*********************************
     * INITIALIZATION
     *********************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storyManager = new StoryManager(this);
        speaker = new Speaker(this, this);
        recognizer = new Recognizer(this, this);

        initViews();

    }

    private void initViews() {
        setContentView(R.layout.activity_main);

        debugText = (TextView) findViewById(R.id.textView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE);

        listenSquare = findViewById(R.id.square2);
        inputSquare = findViewById(R.id.square3);
        processingSquare = findViewById(R.id.square0);
        speakingSquare = findViewById(R.id.square1);
        squares.addAll( asList(new View[]{listenSquare, inputSquare, processingSquare, speakingSquare}));

        debugText.setMovementMethod(new ScrollingMovementMethod());
    }


    /*********************************
     * STATE SWITCH
     *********************************/
    private void nextStoryPart() {
        Log.i(LOG_TAG, "nextStoryPart()");
        if(storyManager.getCurrentStory().equals(StoryManager.MENU_SILENT)) {
            listen();
        } else {
            speak();
        }
    }
    private void speak(){
        Log.i(LOG_TAG, "** SPEAKING");
        state = State.SPEAKING;

        storyManager.stopStoryTimer();
        //recognizer.destroy();

        speaker.speak(
                storyManager.getCurrentStoryText(),
                storyManager.getCurrentStory(),
                storyManager.getCurrentSpeechRate(),
                storyManager.getCurrentPitch(),
                storyManager.getCurrentVoiceId()
        );
        updateView();
    }
    private void listen(){
        Log.i(LOG_TAG, "** LISTEN");
        state = State.LISTENING;
        //recognizer.init();
        recognizer.startListening();
        storyManager.startStoryTimer();
        updateView();
    }


    /*********************************
     * VIEW RELATED
     *********************************/
    private void updateView() {
        for( View square: squares)
            square.setVisibility(View.INVISIBLE);

        switch( state ){
            case SPEAKING:
                speakingSquare.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                break;

            case LISTENING:
                progressBar.setVisibility(View.VISIBLE);
                if( recognizer.isListening() ) {
                    listenSquare.setVisibility(View.VISIBLE);
                    inputSquare.setVisibility(View.VISIBLE);
                    inputSquare.setAlpha(0.5f);
                } else {
                    processingSquare.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    private void debugText( String s ) {
        debugText.setText(s + "\n" + debugText.getText());

        String text = debugText.getText().toString();
        String[] ar = text.split("\n");
        if( ar.length > 800 ) {
            ar = Arrays.copyOfRange(ar,0,700);
            text = TextUtils.join("\n", ar);
            debugText.setText(text);
        }
    }

    /*********************************
     * ACTIVITY EVENTS
     *********************************/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        recognizer.destroy();
        speaker.destroy();

    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /*********************************
     * SPEAKER EVENTS
     *********************************/
    @Override
    public void speakerInitDone() {
        nextStoryPart();
    }

    @Override
    public void speakingDone() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listen();
            }
        });
    }


    /*********************************
     * RECOGNIZER EVENTS
     *********************************/

    @Override
    public void onRecognizerResult(ArrayList<String> results) {
        String text = "Res: [";
        for (String result : results) {
            text += result + ", ";
        }
        text+= "]";
        debugText( text );

        Log.i(LOG_TAG, text);

        if ( storyManager.checkStoryTriggers(results) ) {
            Log.i(LOG_TAG, "triggers found");
            recognizer.stopListening();
            storyManager.stopStoryTimer();
            nextStoryPart();
        } else {
            Log.i(LOG_TAG, "no triggers found");
            recognizer.continueListening();
            updateView();
        }
    }

    @Override
    public void onRecognizerInitDone() {
        updateView();
    }

    @Override
    public void onRecognizerInitStarted() {
        progressBar.setIndeterminate(true);
    }

    @Override
    public void onRecognizerSpeechActivity(float level) {
        progressBar.setProgress((int) level);
        inputSquare.setAlpha( level / 100 );
    }

    @Override
    public void onRecognizerSpeechBegin() {
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
        inputSquare.setAlpha( 1f );
    }


    /*********************************
     * STORY EVENTS
     *********************************/
    @Override
    public void onStoryTimer() {
        //if( ! storyManager.getCurrentStory().equals(StoryManager.START))
        recognizer.stopListening();
        //TODO: check what to do
        nextStoryPart();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            this.findViewById(android.R.id.content).setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }
}