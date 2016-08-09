package com.the_incredible_machine.dubious;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

public class Speaker {
    private static String LOG_TAG = "Dubious.Speaker";
    private AudioManager audioManager;
    private TextToSpeech tts;
    private SpeakerListener listener;
    private Context context;

    public Speaker( SpeakerListener sListener, Context appContext ) {
        this.listener = sListener;
        this.context = appContext;

        // Music stream is muted to prevent voice recogn. beeps
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);


        // Set up TTS
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("error", "This Language is not supported");
                    } else {
                        listener.speakerInitDone();
                    }
                } else {
                    Log.e("error", "Initilization Failed!");
                }
            }
        });
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.i(LOG_TAG, "tts.onStart " + utteranceId);

                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);

            }

            @Override
            public void onDone(String utteranceId) {
                Log.i(LOG_TAG, "tts.onDone " + utteranceId);
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                listener.speakingDone();
            }

            @Override
            public void onError(String utteranceId) {
                Log.i(LOG_TAG, "tts.onError " + utteranceId);

                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);

            }
        });

        // Change TTS voice
        tts.setPitch(0.6f);
        tts.setSpeechRate(1.5f);
    }

    public void destroy(){
        tts.shutdown();
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
    }


    public void speak( String text, String key, float speechRate) {
        tts.setSpeechRate(speechRate);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH,null, key);

    }



}
