package com.the_incredible_machine.dubious;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Speaker {
    private static String LOG_TAG = "Dubious.Speaker";
    private AudioManager audioManager;
    private TextToSpeech tts;
    private SpeakerListener listener;
    private Context context;

    private List<Voice> voices;

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
                        voices = new ArrayList<Voice>(tts.getVoices());
                        Log.i(LOG_TAG, "Voices:");
                        if( voices.size() > 0 ) {
                            //Log.i(LOG_TAG, TextUtils.join("\n ", voices));
                            for( int i = 0; i < voices.size(); i++) {
                                if (voices.get(i).getLocale().toString().contains("en_"))
                                    Log.i(LOG_TAG, i + " - " + voices.get(i).toString());
                            }
                        } else {
                            Log.i(LOG_TAG, "!!! NO VOICES");
                        }

                        tts.setVoice(voices.get(117));
                        // male voice: 96
                        // female voice : 117
                        Voice cv = tts.getVoice();
                        Log.i(LOG_TAG, "current voice:" + cv.toString());

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
        //tts.setPitch(0.6f);
        //tts.setSpeechRate(1.5f);


    }

    public void destroy(){
        tts.shutdown();
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
    }


    public void speak( String text, String key, float speechRate, float pitch, int voiceId) {
        tts.setSpeechRate(speechRate);
        tts.setPitch(pitch);
        tts.setVoice(voices.get(voiceId));
        tts.speak(text, TextToSpeech.QUEUE_FLUSH,null, key);

    }



}
