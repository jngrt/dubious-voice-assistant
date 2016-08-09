package com.the_incredible_machine.dubious;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;


public class StoryPart {
    private String story = "hi";
    private List<String> triggers = asList("hello", "hey", "hi");
    private List<String> followUps;
    private String timeOutFollowUp;
    private int timeOut = 0;
    private float speechRate = 1;

    public StoryPart(String story, List<String> triggers, List<String> followUps) {
        this.story = story;
        this.triggers = triggers;
        this.followUps = followUps;
    }

    public StoryPart(String story, List<String> triggers, List<String> followUps, String timeOutFollowUp, int timeOut) {
        this.story = story;
        this.triggers = triggers;
        this.followUps = followUps;
        this.timeOutFollowUp = timeOutFollowUp;
        this.timeOut = timeOut;
    }

    public StoryPart(String story, List<String> triggers, List<String> followUps, String timeOutFollowUp, int timeOut, float speechRate) {
        this.story = story;
        this.triggers = triggers;
        this.followUps = followUps;
        this.timeOutFollowUp = timeOutFollowUp;
        this.timeOut = timeOut;
        this.speechRate = speechRate;
    }

    public Boolean checkTriggers(String input ) {
        input = input.toLowerCase();
        for( String trigger : triggers )
           if ( input.contains( trigger.toLowerCase() ))
               return true;

        return false;
    }


    public String getStory() {
        return story;
    }

    public List<String> getTriggers() {
        return triggers;
    }

    public List<String> getFollowUps() {
        return followUps;
    }

    public String getTimeOutFollowUp() {
        return timeOutFollowUp;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public float getSpeechRate() {
        return speechRate;
    }
}
