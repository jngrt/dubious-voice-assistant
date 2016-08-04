package com.the_incredible_machine.dubious;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;

public class StoryManager {

    public static final String STORY_HELLO = "hello";
    public static final String STORY_NEXT = "next";
    public static final String STORY_OTHER = "other";
    private HashMap<String, StoryPart> storyParts = new HashMap<String, StoryPart>();
    private String currentStory = STORY_HELLO;




    public StoryManager() {

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

    public Boolean checkStoryTriggers( ArrayList<String> matches ){
        //
        // Check for story triggers
        //
        List<String> partsToCheck = new ArrayList<String>();
        if ( currentStory != null && currentStory.length() > 0 ) {
            partsToCheck = storyParts.get(currentStory).getFollowUps();
        }
        for (String result : matches) {
            for ( String key : partsToCheck ) {
                if ( storyParts.get(key).checkTriggers(result) ) {
                    currentStory = key;
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
        currentStory = key;
    }

    public String getCurrentStoryText() {
        return storyParts.get(currentStory).getStory();
    }
}
