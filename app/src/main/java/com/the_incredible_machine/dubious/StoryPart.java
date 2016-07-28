package com.the_incredible_machine.dubious;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;


public class StoryPart {
    private String story = "hi";
    private List<String> triggers = asList("hello", "hey", "hi");
    private List<String> followUps;

    public StoryPart(String story, List<String> triggers, List<String> followUps) {
        this.story = story;
        this.triggers = triggers;
        this.followUps = followUps;
    }

    public Boolean checkTriggers( String input ) {

        for( String trigger : triggers )
           if ( input.contains( trigger ))
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
}
