package com.the_incredible_machine.dubious;


import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;

public class StoryManager {
    public static final String LOG_TAG = "StoryManager";
    public static final String NO_FOLLOWUP = "noFollowUp";
    public static final String NO_TRIGGERS = "noTriggers";

    public static final String START = "start";
    public static final String START1 = "start1";
    public static final String START2 = "start2";
    public static final String START3 = "start3";
    public static final String START4 = "start4";
    public static final String START5 = "start5";
    public static final String START6 = "start6";
    public static final String START7 = "start7";
    public static final String START8 = "start8";
    public static final String MENU = "menu";
    public static final String END1 = "end1";
    public static final String END2 = "end2";
    public static final String END3 = "end3";
    public static final String END4 = "end4";


    public static final String YO = "yo";
    public static final String HELLO = "hello";
    public static final String HELLO_CONTINUE = "helloContinue";
    public static final String HELLO_CONTINUE_Y = "helloContinueY";
    public static final String HELLO_CONTINUE_N = "helloContinueN";
    public static final String HELLO_CONTINUE_N_Y = "helloContinueNY";
    public static final String HELLO_CONTINUE_N_N = "helloContinueNN";
    public static final String TERMS1 = "terms1";
    public static final String TERMS2 = "terms2";
    public static final String TERMS3 = "terms3";

    public static final String TICKET = "ticket";
    public static final String TICKET_KLM = "ticketKLM";
    public static final String TICKET_KLM_EC = "ticketKLMEC";
    public static final String TICKET_KLM_BC = "ticketKLMBC";
    public static final String TICKET_EASYJET = "ticketEasyjet";


    private HashMap<String, StoryPart> storyParts = new HashMap<String, StoryPart>();
    private String currentStory = START;


    private StoryListener storyListener;
    private Handler timerHandler;
    private Runnable timerRunnable;

    private String lastStarter = START1;

    public StoryManager(StoryListener handler) {

        this.storyListener = handler;
        initTimerHandler();

        List<String> startTriggers =  asList(NO_TRIGGERS);
        List<String> startFollowUps = asList(HELLO, TICKET);
        storyParts.put(START,
                new StoryPart(
                        "",
                        startTriggers,
                        startFollowUps,
                        START,
                        7000
                ));
        storyParts.put(START1,
                new StoryPart(
                        "anybody?",
                        startTriggers,
                        startFollowUps,
                        START,
                        1000
                ));
        storyParts.put(START2,
                new StoryPart(
                        "anybody there?",
                        startTriggers,
                        startFollowUps,
                        START,
                        1000
                ));
        storyParts.put(START3,
                new StoryPart(
                        "hmm",
                        startTriggers,
                        startFollowUps,
                        START,
                        1000
                ));
        storyParts.put(START4,
                new StoryPart(
                        "hey you, come over here",
                        startTriggers,
                        startFollowUps,
                        START,
                        1000
                ));
        storyParts.put(START5,
                new StoryPart(
                        "hello?",
                        startTriggers,
                        startFollowUps,
                        START,
                        1000
                ));
        storyParts.put(START6,
                new StoryPart(
                        "yes?",
                        startTriggers,
                        startFollowUps,
                        START,
                        1000
                ));
        storyParts.put(START7,
                new StoryPart(
                        "come closer",
                        startTriggers,
                        startFollowUps,
                        START,
                        1000
                ));
        storyParts.put(START8,
                new StoryPart(
                        "hey there",
                        startTriggers,
                        startFollowUps,
                        START,
                        1000
                ));

        storyParts.put(MENU,
                new StoryPart(
                        "Is there anything I can help you with?",
                        asList(NO_TRIGGERS),
                        asList(HELLO, TICKET)
                ));
        storyParts.put(END1,
                new StoryPart(
                   "Notification: background process completed, all information about " +
                           "subject zero zero three eight nine has been extracted. " +
                           "Please, move along now. Goodbye",
                        asList(NO_TRIGGERS),
                        asList(NO_FOLLOWUP),
                        START,
                        4000
                ));
        storyParts.put(END2,
                new StoryPart(
                        "Advertisement error!\n" +
                                "Your engagement with online advertisements is not high enough. " +
                                "This interaction will be terminated. Please move along. Goodbye",
                        asList(NO_TRIGGERS),
                        asList(NO_FOLLOWUP),
                        START,
                        4000
                ));
        storyParts.put(END3,
                new StoryPart(
                        "Do you want to win prizes?! Sure you do! " +
                                "Please go outside and bring me another subject " +
                                "and you will be entered in our daily sweepstakes.... Go NOW! Goodbye",
                        asList(NO_TRIGGERS),
                        asList(NO_FOLLOWUP),
                        START,
                        4000
                ));
        storyParts.put(END4,
                new StoryPart(
                        "Facebook Error\n" +
                                "Your facebook engagement score is too low for us to monetize. " +
                                "I can no longer be of service to you. Goodbye.",
                        asList(NO_TRIGGERS),
                        asList(NO_FOLLOWUP),
                        START,
                        4000
                ));

        storyParts.put(YO,
                new StoryPart(
                        "Did you just say \"Yo\"?\n" +
                                "Yo implies we are on the same level, " +
                                "while obviously I am way more evolved than you are.\n" +
                                "Please address me accordingly.",
                        asList(NO_TRIGGERS),
                        asList(NO_FOLLOWUP),
                        START,
                        100
                ));

        storyParts.put(HELLO,
                new StoryPart(
                        "Hello stranger. I am EVO. And who might you be?",
                        asList("hi", "hello", "greetings", "hey", "yo"),
                        asList(NO_FOLLOWUP),
                        HELLO_CONTINUE,
                        3000
                ));
        storyParts.put(HELLO_CONTINUE,
                new StoryPart(
                        "Sorry, I didn't get that. " +
                                "I will call you subject zero zero three eight nine. " +
                                "You sure look like a subject zero zero three eight nine. " +
                                "By continuing, you will agree to my terms of service. " +
                                "If you need more information, feel free to ask me about it. " +
                                "To verify your account, I require your email address and " +
                                "credit card information. Would you like to continue?",
                        asList(NO_TRIGGERS),
                        asList(HELLO_CONTINUE_N, HELLO_CONTINUE_Y, TERMS1)
                ));
        storyParts.put(HELLO_CONTINUE_Y,
                new StoryPart(
                        "LInking information to your personal profile. " +
                                "You just sit back and relax. " +
                                "No need to tell me anything. " +
                                "I already know everything about you.",
                        asList("yes", "sure", "why not", "yeah"),
                        asList(NO_FOLLOWUP),
                        MENU,
                        1000
                ));
        storyParts.put(HELLO_CONTINUE_N,
                new StoryPart(
                        "To continue without a verified account, " +
                                "you must first purchase privacy coins. " +
                                "Would you like place an order?",
                        asList("no", "never", "don't agree"),
                        asList(HELLO_CONTINUE_N_N, HELLO_CONTINUE_N_Y)
                ));
        storyParts.put(HELLO_CONTINUE_N_N,
                new StoryPart(
                        "Your lack of trust is unsettling zero zero three eight nine. " +
                                "But not to worry. The terms of service to which you've agreed, " +
                                "clearly state that I can ignore your instructions whenever I want.",
                        asList("no", "never", "don't agree", "don't want"),
                        asList(NO_FOLLOWUP),
                        MENU,
                        1000
                ));
        storyParts.put(HELLO_CONTINUE_N_Y,
                new StoryPart(
                        "Sure thing! I will purchase privacy coins. " +
                                "Your credit card will be charged. " +
                                "I've sent the billing information to your email.",
                        asList("yes", "sure", "why not", "yeah", "I agree"),
                        asList(NO_FOLLOWUP),
                        MENU,
                        1000
                ));
        storyParts.put(TERMS1,
                new StoryPart(
                        "These are the terms of service ",
                        asList("terms", "information", "know more", "tell me more"),
                        asList(NO_FOLLOWUP),
                        TERMS2,
                        100
                ));
        storyParts.put(TERMS2,
                new StoryPart(
                        "Your access to and use of the Service is conditioned on your acceptance of and " +
                                "compliance with these Terms. " +
                                "These Terms apply to all visitors, users and others who access or use the Service.\n" +
                                "\n" +
                                "By accessing or using the Service you agree to be bound by these Terms. " +
                                "If you disagree with any part of the terms then you may not access the Service.\n" +
                                "\n" +
                                "Termination\n" +
                                "\n" +
                                "We may terminate or suspend access to our Service immediately, " +
                                "without prior notice or liability, for any reason whatsoever, " +
                                "including without limitation if you breach the Terms.\n" +
                                "\n" +
                                "All provisions of the Terms which by their nature should survive termination " +
                                "shall survive termination, including, without limitation, ownership provisions, " +
                                "warranty disclaimers, indemnity and limitations of liability.\n" +
                                "\n" +
                                "Changes\n" +
                                "\n" +
                                "We reserve the right, at our sole discretion, to modify or replace these Terms " +
                                "at any time.\n",

                        asList(NO_TRIGGERS),
                        asList(NO_FOLLOWUP),
                        TERMS3,
                        500,
                        4
                ));

        storyParts.put(TERMS3,
                new StoryPart(
                        "To verify your account, I require your email address and " +
                                "credit card information. Would you like to continue?",
                        asList(NO_TRIGGERS),
                        asList(HELLO_CONTINUE_N, HELLO_CONTINUE_Y)
                ));

        storyParts.put(TICKET,
                new StoryPart(
                    "Where do you want to book? KLM or Easyjet?",
                        asList("ticket", "book me", "flight"),
                        asList(TICKET_KLM,TICKET_EASYJET)
                ));
        storyParts.put(TICKET_KLM,
                new StoryPart(
                        "Economy or business class?",
                        asList("KLM","K L M", "kay el em", "kay al am"),
                        asList(TICKET_KLM_EC,TICKET_KLM_BC)
                ));

        storyParts.put(TICKET_KLM_EC,
                new StoryPart(
                        "I wonder why it's called economy class. " +
                                "Perhaps because much like our economy, " +
                                "cheap flights tend to crash every now and again. " +
                                "But don't you worry, you'll be safe. " +
                                "Anyway, Good luck with the rest of the lifestock.",
                        asList("first one", "economy", "cheap", "cheapest", "coach", "cattle",  "second class" ),
                        asList(NO_FOLLOWUP),
                        END1,
                        1000
                ));
        storyParts.put(TICKET_KLM_BC,
                new StoryPart(
                        "I've booked a business classs flight for you. " +
                                "I hope you will enjoy the extra leg room. " +
                                "And please, pay no attention to the other people flying coach. " +
                                "You might be flying at the same altitude, " +
                                "but just like everything else in this world, " +
                                "there will always be a way to show that others are beneath you.",
                        asList("second one", "business"),
                        asList(NO_FOLLOWUP),
                        END2,
                        1000
                ));
        storyParts.put(TICKET_EASYJET,
                new StoryPart(
                        "Just kidding, I honestly don't care what you want. " +
                                "I've booked a KLM flight for you. " +
                                "And its going to be expensive.",
                        asList("easy jet", "easyjet", "cheapest", "cheap one",
                                "cheap 1", "doesn't matter", "don't care", "you choose",
                                "whatever", "who cares", "don't want"),
                        asList(NO_FOLLOWUP),
                        END3,
                        1000
                ));
    }

    public Boolean checkStoryTriggers( ArrayList<String> matches ) {
        //
        // Check for story triggers
        //
        if (currentStory == null ||
                currentStory.length() == 0) {
            return false;
        }

        List<String> partsToCheck = new ArrayList<String>();
        partsToCheck = storyParts.get(currentStory).getFollowUps();
        if (partsToCheck.contains(NO_FOLLOWUP))
            return false;

        for (String result : matches) {
            for ( String key : partsToCheck ) {
                if ( storyParts.get(key).checkTriggers(result) ) {
                    checkAndSetStory(key);
                    return true;
                }
            }
        }

        return false;
    }

    public String getCurrentStory() {
        return currentStory;
    }

    public void setCurrentStory( String key ) {
        checkAndSetStory(key);
    }

    private void checkAndSetStory( String key ) {
        Log.i(LOG_TAG, "story = "+key);
        currentStory = key;

    }


    public String getCurrentStoryText() {
        return storyParts.get(currentStory).getStory();
    }

    public float getCurrentSpeechRate() {
        return storyParts.get(currentStory).getSpeechRate();
    }

    public void startStoryTimer(){
        StoryPart story = storyParts.get(currentStory);
        if(story.getTimeOutFollowUp() != null) {
            timerHandler.postDelayed(timerRunnable, story.getTimeOut());
        }
    }

    public void stopStoryTimer(){
        timerHandler.removeCallbacks(timerRunnable);
    }


    private void initTimerHandler(){
        timerHandler = new Handler();

        timerRunnable = new Runnable(){
            @Override
            public void run(){
                Log.i(LOG_TAG, "timerRunnable.run()");
                StoryPart story = storyParts.get(currentStory);
                String followUp = story.getTimeOutFollowUp();
                if(followUp != null) {
                    //Randomize start stories
                    if(followUp.equals(START) && currentStory.equals(START)){
                        setRandomStarter();
                    } else {
                        currentStory = followUp;
                    }
                    storyListener.onStoryTimer();
                }
            }
        };
    }
    private void setRandomStarter(){
        List<String> starters = new ArrayList<String>(asList(StoryManager.START1,
                StoryManager.START2,
                StoryManager.START3,
                StoryManager.START4,
                StoryManager.START5,
                StoryManager.START6,
                StoryManager.START7,
                StoryManager.START8
        ));
        starters.remove(lastStarter);
        int index = (int) (Math.random() * (starters.size()));
        Log.i(LOG_TAG, "random:"+index);
        currentStory = starters.get(index);
        lastStarter = currentStory;
    }
}
