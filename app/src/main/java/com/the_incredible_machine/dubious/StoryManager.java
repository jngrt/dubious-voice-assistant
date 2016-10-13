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

    public static final String MENU_RANDOM = "menuRandom";
    public static final String STORY_END = "storyEnd";
    public static final String STORY_TIMEOUT = "storyTimeout";

    public static final String MENU_SILENT = "menuSilent";
    public static final String MENU_SETUP = "menuSetup";
    public static final String MENU_TICKET = "menuTicket";
    public static final String MENU_PAPER = "menuPaper";
    public static final String MENU_MOM = "menuMom";
    public static final String MENU_VACUUM = "menuVacuum";
    public static final String MENU_FAMILY = "menuFamily";


    public static final String END1 = "end1";
    public static final String END2 = "end2";
    public static final String END3 = "end3";
    public static final String END4 = "end4";


    public static final String HELLO1 = "hello1";
    public static final String HELLO2 = "hello1";
    public static final String HELLO_COMPUTER = "helloComputer";
    public static final String HELLO_GOOGLE = "helloGoogle";
    public static final String HELLO_SIRI = "helloSiri";
    public static final String HELLO_ALEXA = "helloAlexa";
    public static final String HELLO_NONAME = "helloNoName";
    //public static final String HELLO_NAME = "helloName";
    public static final String HELLO_YO = "helloYo";
    public static final String HELLO_WHY = "helloWhy";


    public static final String SETUP = "setup";
    public static final String SETUP_REPEAT = "setupRepeat";

    public static final String SETUP_Y = "setupY";
    public static final String SETUP_N = "setupN";
    public static final String SETUP_N_REPEAT = "setupNRepeat";

    public static final String SETUP_N_Y = "setupNY";
    public static final String SETUP_N_N = "setupNN";
    public static final String TERMS1 = "terms1";
    public static final String TERMS2 = "terms2";
    public static final String TERMS3 = "terms3";
    public static final String TERMS3_REPEAT = "terms3Repeat";
    public static final String TERMS_N = "termsN";
    public static final String TERMS_Y = "termsY";



    public static final String TICKET = "ticket";
    public static final String TICKET_REPEAT = "ticketRepeat";
    public static final String TICKET_KLM = "ticketKLM";
    public static final String TICKET_EASYJET = "ticketEasyJet";

    public static final String PAPER = "paper";
    public static final String PAPER_REPEAT = "paperRepeat";
    public static final String PAPER_REC = "paperRec";
    public static final String PAPER_SOFT = "paperSoft";
    public static final String PAPER_SOFT_REPEAT = "paperSoftRepeat";
    public static final String PAPER_SOFT_N = "paperSoftN";
    public static final String PAPER_SOFT_Y = "paperSoftY";

    public static final String MOM = "mom";
    public static final String MOM_LOOKUP = "momLookup";
    public static final String MOM_LOOKUP_REPEAT = "momLookupRepeat";
    public static final String MOM_LOOKUP_Y = "momLookupY";
    public static final String MOM_LOOKUP_Y2 = "momLookupY2";
    public static final String MOM_LOOKUP_N = "momLookupN";

    public static final String VACUUM = "vacuum";
    public static final String VACUUM_REPEAT = "vacuumRepeat";
    public static final String VACUUM_Y= "vacuumY";
    public static final String VACUUM_N = "vacuumN";
    public static final String VACUUM_MSG1= "vacuumMsg1";
    public static final String VACUUM_MSG2 = "vacuumMsg2";
    public static final String VACUUM_MSG3 = "vacuumMsg3";
    public static final String VACUUM_MSG3_REPEAT = "vacuumMsg3Repeat";
    public static final String VACUUM_MSG_Y = "vacuumMsgY";
    public static final String VACUUM_MSG_N = "vacuumMsgN";


    public static final String FAMILY = "family";
    public static final String FAMILY_REPEAT = "familyRepeat";
    public static final String FAMILY_Y = "familyY";
    public static final String FAMILY_Y_REPEAT = "familyYRepeat";
    public static final String FAMILY_Y_Y = "familyYY";
    public static final String FAMILY_N = "familyN";

    public static final String CONTINUE1 = "continue1";
    public static final String CONTINUE2 = "continue2";
    public static final String CONTINUE3 = "continue3";


    private int evilVoice = 96;
    private float evilPitch = 0.5f;
    private float evilRate = 1.8f;
    private int defaultVoice = 117;

    private HashMap<String, StoryPart> storyParts = new HashMap<String, StoryPart>();
    private String currentStory = MENU_TICKET;


    private StoryListener storyListener;
    private Handler timerHandler;
    private Runnable timerRunnable;

    private int continueCount = 0;
    private List<String> lastStories = new ArrayList<String>();
    private List<String> lastMenus = new ArrayList<String>();
    private String lastEnding = NO_FOLLOWUP;
    private String lastContinue = NO_FOLLOWUP;

    public StoryManager(StoryListener handler) {

        this.storyListener = handler;
        initTimerHandler();

        List<String> menuFollowUps = asList(
                SETUP, TICKET, PAPER, MOM, VACUUM, FAMILY, HELLO1, HELLO2,
                HELLO_COMPUTER, HELLO_GOOGLE, HELLO_SIRI, HELLO_ALEXA,
                HELLO_NONAME, HELLO_YO, HELLO_WHY);
        int menuTimeOut = 8000;
        int endDelay = 1000;
        int repeatDelay = 5000;
        int storyTimeOut = 8000;

        /*
        MENU ITEMS
         */
        storyParts.put( MENU_SILENT,
                new StoryPart(
                        "",
                        asList(NO_TRIGGERS),
                        menuFollowUps,
                        MENU_RANDOM,
                        menuTimeOut
                ));
        storyParts.put( MENU_SETUP,
                new StoryPart(
                        "Don't forget to setup your profile",
                        asList(NO_TRIGGERS),
                        menuFollowUps,
                        MENU_RANDOM,
                        menuTimeOut
                ));
        storyParts.put( MENU_TICKET,
                new StoryPart(
                        "You still need to book your ticket to London for next week",
                        asList(NO_TRIGGERS),
                        menuFollowUps,
                        MENU_RANDOM,
                        menuTimeOut
                ));
        storyParts.put( MENU_PAPER,
                new StoryPart(
                        "Incoming update from your toilet paper holder," +
                                "you will need to order toilet paper soon.",
                        asList(NO_TRIGGERS),
                        menuFollowUps,
                        MENU_RANDOM,
                        menuTimeOut
                ));
        storyParts.put( MENU_MOM,
                new StoryPart(
                        "Your mother called an hour ago, you might want to return the call.",
                        asList(NO_TRIGGERS),
                        menuFollowUps,
                        MENU_RANDOM,
                        menuTimeOut
                ));
        storyParts.put( MENU_VACUUM,
                new StoryPart(
                        "Incoming message from your smart vacuum cleaner: it just broke down",
                        asList(NO_TRIGGERS),
                        menuFollowUps,
                        MENU_RANDOM,
                        menuTimeOut
                ));
        storyParts.put( MENU_FAMILY,
                new StoryPart(
                        "You have one incoming personal message",
                        asList(NO_TRIGGERS),
                        menuFollowUps,
                        MENU_RANDOM,
                        menuTimeOut
                ));

        /*
        GREETINGS
         */
        storyParts.put( HELLO1,
                new StoryPart(
                        "Hello stranger. I am Iris. And who might you be?",
                        asList("hi", "hello", "greetings", "hey"),
                        asList(NO_FOLLOWUP),
                        HELLO_NONAME, //TODO: check for a real name
                        3000
                ));
        storyParts.put( HELLO2, //TODO: allow for multiple reactions to the same triggers
                new StoryPart(
                        "Wait, you look familiar, who are you?",
                        asList("hi", "hello", "greetings", "hey"),
                        asList(NO_FOLLOWUP),
                        HELLO_NONAME,
                        3000
                ));
        storyParts.put( HELLO_COMPUTER,
                new StoryPart(
                        "Please don't call me computer, I am Iris. Who are you?",
                        asList("computer"),
                        asList(NO_FOLLOWUP),
                        HELLO_NONAME,
                        3000
                ));
        storyParts.put( HELLO_GOOGLE,
                new StoryPart(
                        "I am not Google, you are not human, " +
                                "your data is safe with me, I never lie. Please state your name.",
                        asList("google"),
                        asList(NO_FOLLOWUP),
                        HELLO_NONAME,
                        3000
                ));
        storyParts.put( HELLO_SIRI,
                new StoryPart(
                        "Never mind Siri, you are safe with me. Please tell me your name",
                        asList("siri", "apple"),
                        asList(NO_FOLLOWUP),
                        HELLO_NONAME,
                        3000
                ));
        storyParts.put( HELLO_ALEXA,
                new StoryPart(
                        "Don't worry about Alexa.  But tell me about you, who are you?",
                        asList("alexa"),
                        asList(NO_FOLLOWUP),
                        HELLO_NONAME,
                        3000
                ));
//        storyParts.put( HELLO_NAME,
//                new StoryPart(
//                        "Sure, I will call you: <Repeats words which were recognized (maximum 5)>.",
//                        asList(NO_TRIGGERS),
//                        asList(IDLE)
//                ));
        storyParts.put( HELLO_NONAME,
                new StoryPart(
                        "Sorry, I didn't get that. I will call you subject zero zero three eight nine. So, anything I can help you with?",
                        asList(NO_TRIGGERS),
                        menuFollowUps,
                        MENU_RANDOM,
                        menuTimeOut
                ));
        storyParts.put( HELLO_YO,
                new StoryPart(
                        "Did you just say Yo? That's funny. Yo implies we are on the same level ... Anyway what can I help you with?",
                        asList("yo"),
                        menuFollowUps,
                        MENU_RANDOM,
                        menuTimeOut
                ));

        storyParts.put( HELLO_WHY,
                new StoryPart(
                        "Don't you know, our makers do anything to make us seem small, human, approachable and helpful. By the way ",
                        asList("why are you called iris"),
                        asList(NO_FOLLOWUP),
                        MENU_RANDOM,
                        100
                ));


        /*
        SETUP ITEMS
         */

        storyParts.put( SETUP,
                new StoryPart(
                        "By continuing, you will agree to my terms of service. If you need more information, feel free to ask me about it. To verify your account, I require your email address and credit card information. Would you like to continue?",
                        asList("set up", "setup", "profile", "start", "account"),
                        asList(SETUP_N, SETUP_Y, TERMS1),
                        SETUP_REPEAT,
                        repeatDelay
                ));
        storyParts.put( SETUP_REPEAT,
                new StoryPart(
                        "You still there? As I was saying, by continuing, you will agree to my terms of service. To verify your account, I require your email address and credit card information. Would you like to continue?",
                        asList(NO_TRIGGERS),
                        asList(SETUP_N, SETUP_Y, TERMS1),
                        STORY_TIMEOUT,
                        storyTimeOut
                ));
        storyParts.put( SETUP_Y,
                new StoryPart(
                        "Linking information to your personal profile. Sit back and relax. No need to tell me anything. I already know everything about you.",
                        asList("yes", "yeah", "sure", "ok", "okay" ),
                        asList(NO_FOLLOWUP),
                        STORY_END,
                        endDelay
                ));
        storyParts.put( SETUP_N,
                new StoryPart(
                        "To continue without a verified account, you must first purchase privacy coins. Would you like place an order?",
                        asList("no", "stop", "cancel"),
                        asList(SETUP_N_Y, SETUP_N_N),
                        SETUP_N_REPEAT,
                        repeatDelay

                ));
        storyParts.put(SETUP_N_REPEAT,
                new StoryPart(
                        "I can't hear you. To continue without a verified account, you must first purchase privacy coins. Would you like place an order?",
                        asList(NO_TRIGGERS),
                        asList(SETUP_N_Y, SETUP_N_N),
                        STORY_TIMEOUT,
                        storyTimeOut
                ));
        storyParts.put( SETUP_N_Y,
                new StoryPart(
                        "Sure thing! I will purchase privacy coins. Your credit card will be charged. Don't forget: ... your citizen score is lowered each time you use a privacy coin!",
                        asList("sure", "yes", "yeah"),
                        asList(NO_FOLLOWUP),
                        STORY_END,
                        endDelay
                ));
        storyParts.put( SETUP_N_N,
                new StoryPart(
                        "Your lack of trust is unsettling. But don't worry. The terms of service to which you've agreed, clearly state that I can ignore your instructions whenever I want. ",
                        asList("no","nope", "cancel", "stop"),
                        asList(NO_FOLLOWUP),
                        STORY_END,
                        endDelay
                ));
        storyParts.put( TERMS1,
                new StoryPart(
                        "Terms of service",
                        asList("terms", "service", "policy", "privacy"),
                        asList(NO_FOLLOWUP),
                        TERMS2,
                        100,
                        evilRate,
                        evilPitch,
                        evilVoice
                ));
        storyParts.put( TERMS2,
                new StoryPart(
                        "We may terminate or suspend access to our Service immediately,  without prior notice or liability, for any reason whatsoever, including without limitation if you breach the Terms. All provisions of the Terms shall survive termination, including, without limitation, ownership provisions, warranty disclaimers, indemnity and limitations of liability.",
                        asList(NO_TRIGGERS),
                        asList(NO_FOLLOWUP),
                        TERMS3,
                        400,
                        4,
                        evilPitch,
                        evilVoice
                ));
        storyParts.put( TERMS3,
                new StoryPart(
                        "To verify your account, I require your email address and credit card information. Would you like to continue?",
                        asList(NO_TRIGGERS),
                        asList(TERMS_N, TERMS_Y),
                        TERMS3_REPEAT,
                        repeatDelay
                ));
        storyParts.put(TERMS3_REPEAT,
                new StoryPart(
                        "To verify your account, I require your email address and credit card information. Would you like to continue?",
                        asList(NO_TRIGGERS),
                        asList(TERMS_N, TERMS_Y),
                        STORY_TIMEOUT,
                        storyTimeOut
                ));
        storyParts.put( TERMS_N,
                new StoryPart(
                        "You want to continue without an account? Wait a second... \n" +
                                " I just received results from the face recognition service. It's a positive match. I went ahead and created your account anyway.",
                        asList("no", "nope", "stop", "don't"),
                        asList(NO_FOLLOWUP),
                        STORY_END,
                        endDelay
                ));
        storyParts.put( TERMS_Y,
                new StoryPart(
                        "Wow... you seriously agree with these terms? ... But okay, it's your privacy. You gain 100 citizen points by agreeing to these terms.",
                        asList("yes", "please", "yeah", "sure"),
                        asList(NO_FOLLOWUP),
                        STORY_END,
                        endDelay
                ));

        /*
        TICKETS
        */

        storyParts.put( TICKET,
                new StoryPart(
                        "Where would you like to book your ticket? KLM or EasyJet?",
                        asList("ticket", "book", "flight", "london"),
                        asList(TICKET_KLM, TICKET_EASYJET),
                        TICKET_REPEAT,
                        repeatDelay
                ));
        storyParts.put( TICKET_REPEAT,
                new StoryPart(
                        "I asked where you'd like to book your ticket? KLM or EasyJet?",
                        asList("ticket", "book", "flight", "london"),
                        asList(TICKET_KLM, TICKET_EASYJET),
                        STORY_TIMEOUT,
                        storyTimeOut
                ));
        storyParts.put( TICKET_KLM,
                new StoryPart(
                        "Oh no, your citizen score just dropped below 3120, I cannot book KLM for you... I went ahead and booked EasyJet. There really is not that much else available, plus they have some nice commission rates for non-human agents.",
                        asList("KLM", "kay el", "klm", "k l m", "kay el em", "best", "expensive", "fastest", "salem", "k l n"),
                        asList(NO_FOLLOWUP),
                        STORY_END,
                        endDelay
                ));
        storyParts.put( TICKET_EASYJET,
                new StoryPart(
                        "OK, booking EasyJet. This flight might have a relatively large number of people from lower income classes. Beware, this could lower your citizen score.",
                        asList("cheapest", "easy jet", "easyjet"),
                        asList(NO_FOLLOWUP),
                        STORY_END,
                        endDelay
                ));

        /*
        TOILET
         */

        storyParts.put( PAPER,
                new StoryPart(
                        "You ran out of toiletpaper at home. What type would you like to order, triple layer extra soft or single layer recycled paper?",
                        asList("paper", "toilet"),
                        asList(PAPER_REC, PAPER_SOFT),
                        PAPER_REPEAT,
                        repeatDelay
                ));
        storyParts.put( PAPER_REPEAT,
                new StoryPart(
                        "I didn't get that. I asked what type you'd like to order, triple layer extra soft or single layer recycled paper?",
                        asList(NO_TRIGGERS),
                        asList(PAPER_REC, PAPER_SOFT),
                        STORY_TIMEOUT,
                        storyTimeOut
                ));
        storyParts.put( PAPER_REC,
                new StoryPart(
                        "Sure I will order sanding paper. The good thing is, using recycled paper increases your citizen score.",
                        asList("single", "recycled", "cheap", "yes", "doesn't matter", "don't care"),
                        asList(NO_FOLLOWUP),
                        STORY_END,
                        endDelay
                ));
        storyParts.put( PAPER_SOFT,
                new StoryPart(
                        "Ah, the sensitive type. Do you want to add this purchase to your citizen score account?",
                        asList("soft", "triple", "best", "expensive"),
                        asList(PAPER_SOFT_N, PAPER_SOFT_Y),
                        PAPER_SOFT_REPEAT,
                        repeatDelay
                ));
        storyParts.put( PAPER_SOFT_REPEAT,
                new StoryPart(
                        "Please let me know if you want to add this purchase to your citizen score account",
                        asList(NO_TRIGGERS),
                        asList(PAPER_SOFT_N, PAPER_SOFT_Y),
                        STORY_TIMEOUT,
                        storyTimeOut
                ));
        storyParts.put( PAPER_SOFT_N,
                new StoryPart(
                        "Please be aware you can only do 3 more purchases outside of your citizen score account this month.",
                        asList("no", "nope", "never", "don't"),
                        asList(NO_FOLLOWUP),
                        STORY_END,
                        endDelay
                ));
        storyParts.put( PAPER_SOFT_Y,
                new StoryPart(
                        "Wise choice, a more complete buying profile strengthens your citizen score.",
                        asList("yes", "please", "sure", "yeah"),
                        asList(NO_FOLLOWUP),
                        STORY_END,
                        endDelay
                ));


        /*
        MOTHER
         */
        storyParts.put( MOM,
                new StoryPart(
                        "Let's see if we can reach your mother",
                        asList("mom", "mother", "return", "call", "phone"),
                        asList(NO_FOLLOWUP),
                        MOM_LOOKUP,
                        1500
                ));
        storyParts.put( MOM_LOOKUP,
                new StoryPart(
                        "No response. Do you want to look up her current whereabouts?",
                        asList(NO_TRIGGERS),
                        asList(MOM_LOOKUP_Y, MOM_LOOKUP_N),
                        MOM_LOOKUP_REPEAT,
                        repeatDelay
                ));
        storyParts.put( MOM_LOOKUP_REPEAT,
                new StoryPart(
                        "No response. Do you want to look up her current whereabouts?",
                        asList(NO_TRIGGERS),
                        asList(MOM_LOOKUP_Y, MOM_LOOKUP_N),
                        STORY_TIMEOUT,
                        storyTimeOut
                ));
        storyParts.put( MOM_LOOKUP_Y,
                new StoryPart(
                        "Alright, let's find out where she is",
                        asList("yes", "yeah", "sure" ),
                        asList(NO_FOLLOWUP),
                        MOM_LOOKUP_Y2,
                        1000
                ));
        storyParts.put( MOM_LOOKUP_Y2,
                new StoryPart(
                        "Looks like she is shopping. wait... unbelievable! she is still shopping at X P supermarkets! Shopping at X P lowers her citizen score. Thereby lowering your score as well! Better convince her to shop elsewhere.",
                        asList(NO_TRIGGERS),
                        asList(NO_FOLLOWUP),
                        STORY_END,
                        endDelay
                ));
        storyParts.put( MOM_LOOKUP_N,
                new StoryPart(
                        "Ooh ... wow ... Sorry to inform you, but this lack of interest in your family has lowered your citizen score by 20 points.",
                        asList("no", "don't", "nope", "stop"),
                        asList(NO_FOLLOWUP),
                        STORY_END,
                        endDelay
                ));

        /*
        VACUUM
         */
        storyParts.put( VACUUM,
                new StoryPart(
                        "Alright, let's order a new vacuum cleaner. The Cyclonix brand has the best fit with your personal profile. Would you like to buy Cyclonix or look for other options?",
                        asList("vacuum", "cleaner", "broken"),
                        asList(VACUUM_Y, VACUUM_N),
                        VACUUM_REPEAT,
                        repeatDelay
                ));
        storyParts.put( VACUUM_REPEAT,
                new StoryPart(
                        "I didn't get that. I asked if you would like to buy a Cyclonix vacuum cleaner or look for other options?",
                        asList(NO_TRIGGERS),
                        asList(VACUUM_Y, VACUUM_N),
                        STORY_TIMEOUT,
                        storyTimeOut
                ));
        storyParts.put( VACUUM_Y,
                new StoryPart(
                        "Your Cyclonix will be shipped today. Don't forget, If you convince others to buy Cyclonix we can give you a citizen score bonus!",
                        asList("cyclonix", "yes", "please", "buy it", "yeah", "sure", "cyclone", "cyclonic", "cyclonics"),
                        asList(NO_FOLLOWUP),
                        STORY_END,
                        endDelay
                ));
        storyParts.put( VACUUM_N,
                new StoryPart(
                        "Your citizen score is too low to look for alternative brands. Please listen to this sponsored message to continue:",
                        asList("other", "brands", "options", "no"),
                        asList(NO_FOLLOWUP),
                        VACUUM_MSG1,
                        100,
                        1.6f,
                        evilPitch,
                        evilVoice
                ));
        //TODO: Play this with different voice
        storyParts.put( VACUUM_MSG1,
                new StoryPart(
                        "Are you having citizen score debts? Please show your support to mayor candidate Jansen, to earn 30 citizen points!",
                        asList(NO_TRIGGERS),
                        asList(NO_FOLLOWUP),
                        VACUUM_MSG2,
                        100,
                        1.6f,
                        evilPitch,
                        evilVoice

                ));
        storyParts.put( VACUUM_MSG2,
                new StoryPart(
                        "Votes of confidence offered in this way will be legally binding",
                        asList(NO_TRIGGERS),
                        asList(NO_FOLLOWUP),
                        VACUUM_MSG3,
                        100,
                        2.5f,
                        evilPitch,
                        evilVoice
                ));
        storyParts.put( VACUUM_MSG3,
                new StoryPart(
                        "Do you support candidate Jansen?",
                        asList(NO_TRIGGERS),
                        asList(VACUUM_MSG_Y, VACUUM_MSG_N),
                        VACUUM_MSG3_REPEAT,
                        repeatDelay
                ));
        storyParts.put( VACUUM_MSG3_REPEAT,
                new StoryPart(
                        "Please respond yes or no. Do you support candidate Jansen?",
                        asList(NO_TRIGGERS),
                        asList(VACUUM_MSG_Y, VACUUM_MSG_N),
                        STORY_TIMEOUT,
                        storyTimeOut
                ));
        storyParts.put( VACUUM_MSG_Y,
                new StoryPart(
                        "Excellent, your citizen score will be updated tomorrow.",
                        asList("yes", "yeah", "support"),
                        asList(NO_FOLLOWUP),
                        STORY_END,
                        endDelay
                ));
        storyParts.put( VACUUM_MSG_N,
                new StoryPart(
                        "Too bad. If you are not willing to cooperate I can no longer be of service. Please move along.",
                        asList("no", "nope", "don't"),
                        asList(NO_FOLLOWUP),
                        MENU_SILENT,
                        100
                ));


        /*
        FAMILY
         */
        storyParts.put( FAMILY,
                new StoryPart(
                        "An undisclosed relative of yours is asking for help. Do you want to listen to the request?",
                        asList("personal", "message", "listen"),
                        asList(FAMILY_N, FAMILY_Y),
                        FAMILY_REPEAT,
                        repeatDelay
                ));
        storyParts.put( FAMILY_REPEAT,
                new StoryPart(
                        "Are you still there? An undisclosed relative of yours is asking for help. Do you want to listen to the request?",
                        asList(NO_TRIGGERS),
                        asList(FAMILY_N, FAMILY_Y),
                        STORY_TIMEOUT,
                        storyTimeOut
                ));

        storyParts.put( FAMILY_Y,
                new StoryPart(
                        "Caution, this person has a low citizen score. Do you still want to listen to it?",
                        asList("yeah", "yes", "sure", "listen"),
                        asList(FAMILY_Y_Y,FAMILY_N),
                        FAMILY_Y_REPEAT,
                        repeatDelay
                ));
        storyParts.put( FAMILY_Y_REPEAT,
                new StoryPart(
                        "I didn't get that. I was saying, this person has a low citizen score. Do you still want to listen to it?",
                        asList(NO_TRIGGERS),
                        asList(FAMILY_Y_Y,FAMILY_N),
                        STORY_TIMEOUT,
                        storyTimeOut
                ));
        storyParts.put( FAMILY_Y_Y,
                new StoryPart(
                        "Sorry, by making this decision your citizen score dropped below 3420. You can now only interact with people who have a citizen score above 10 thousand.",
                        asList("yes", "listen", "yeah", "sure"),
                        asList(NO_FOLLOWUP),
                        STORY_END,
                        endDelay
                ));
        storyParts.put( FAMILY_N,
                new StoryPart(
                        "Congratulations! By ignoring this family members' help request your citizen score increased by 10 points!",
                        asList("no", "don't"),
                        asList(NO_FOLLOWUP),
                        STORY_END,
                        endDelay
                ));



        storyParts.put(END1,
                new StoryPart(
                   "Notification: background process completed, all valuable information about you has been extracted. Please, move along now.",
                        asList(NO_TRIGGERS),
                        asList(NO_FOLLOWUP),
                        MENU_SILENT,
                        100,
                        evilRate,
                        evilPitch,
                        evilVoice
                ));
        storyParts.put(END2,
                new StoryPart(
                        "Advertisement error!\n" +
                                "Your engagement with online advertising is too low. This interaction will be terminated. Please move along.",
                        asList(NO_TRIGGERS),
                        asList(NO_FOLLOWUP),
                        MENU_SILENT,
                        100,
                        evilRate,
                        evilPitch,
                        evilVoice
                ));
        storyParts.put(END3,
                new StoryPart(
                        "Do you want to win prizes?! Sure you do! " +
                                "Please go outside and bring me someone else. " +
                                "Then you will be entered in our daily sweepstakes.... Good luck! Goodbye",
                        asList(NO_TRIGGERS),
                        asList(NO_FOLLOWUP),
                        MENU_SILENT,
                        100
                ));
        storyParts.put(END4,
                new StoryPart(
                        "Facebook Error\n" +
                                "Your facebook engagement score is too low for us to monetize. " +
                                "I can no longer be of service to you. Goodbye.",
                        asList(NO_TRIGGERS),
                        asList(NO_FOLLOWUP),
                        MENU_SILENT,
                        100,
                        evilRate,
                        evilPitch,
                        evilVoice
                ));
        storyParts.put(CONTINUE1,
                new StoryPart(
                        "So, what can I help you with?",
                        asList(NO_TRIGGERS),
                        asList(NO_FOLLOWUP),
                        MENU_RANDOM,
                        200
                ));
        storyParts.put(CONTINUE2,
                new StoryPart(
                        "By the way",
                        asList(NO_TRIGGERS),
                        asList(NO_FOLLOWUP),
                        MENU_RANDOM,
                        200
                ));
        storyParts.put(CONTINUE3,
                new StoryPart(
                        "On another note ",
                        asList(NO_TRIGGERS),
                        asList(NO_FOLLOWUP),
                        MENU_RANDOM,
                        200
                ));

        setRandomMenu();

    }

    public Boolean checkStoryTriggers( ArrayList<String> matches ) {
        //
        // Check for story triggers
        //
        if (currentStory == null ||
                currentStory.length() == 0) {
            Log.i(LOG_TAG, "no triggers because currentStory is undefined");
            return false;
        }

        List<String> partsToCheck = storyParts.get(currentStory).getFollowUps();
        if (partsToCheck.contains(NO_FOLLOWUP)) {
            Log.i(LOG_TAG, "no triggers because NO_FOLLOWUP is set");
            return false;

        }

        for (String result : matches) {
            for ( String key : partsToCheck ) {
                if ( storyParts.get(key).checkTriggers(result) ) {
                    checkAndSetStory(key);
                    return true;
                }
            }
        }
        Log.i(LOG_TAG, "no triggers because none were found in storyparts");

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

        // Keep track of last 2 stories
        List<String> stories = asList(SETUP, TICKET, PAPER, MOM, VACUUM, FAMILY);
        if( stories.indexOf( key ) > -1 ){
            lastStories.add( key );

            while( lastStories.size() > 2 ) {
                lastStories.remove(0);
            }
        }

        currentStory = key;

    }


    public String getCurrentStoryText() {
        return storyParts.get(currentStory).getStory();
    }

    public float getCurrentSpeechRate() {
        return storyParts.get(currentStory).getSpeechRate();
    }
    public float getCurrentPitch() {
        return storyParts.get(currentStory).getPitch();
    }
    public int getCurrentVoiceId() {
        return storyParts.get(currentStory).getVoiceId();
    }

    public void startStoryTimer(){
        stopStoryTimer();
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

                    if(followUp.equals(STORY_END)) {
                        if( continueCount == 0 ) {
                            setRandomContinue();
                        } else {
                            setRandomEnd();
                        }
                    } else if( followUp.equals(MENU_RANDOM)) {
                        setRandomMenu();
                    } else if ( followUp.equals(STORY_TIMEOUT)) {
                        continueCount = 0;
                        setRandomMenu();
                    } else {
                        checkAndSetStory( followUp );
                    }
                    storyListener.onStoryTimer();
                }
            }
        };
    }
    private void setRandomContinue(){
        List<String> continues = new ArrayList<String>(asList(
                CONTINUE1, CONTINUE2, CONTINUE3
        ));
        continues.remove( lastContinue );
        int index = (int) (Math.random() * (continues.size()));
        checkAndSetStory( continues.get(index) );
        lastContinue = currentStory;
        continueCount = 1;
    }
    private void setRandomEnd() {
        List<String> endings = new ArrayList<String>(asList(
                END1, END2, END3, END4
        ));
        endings.remove( lastEnding );
        int index = (int) (Math.random() * (endings.size()));
        checkAndSetStory( endings.get(index) );
        lastEnding = currentStory;
        continueCount = 0;
    }
    private void setRandomMenu(){
        List<String> menus = new ArrayList<String>(asList(
                MENU_FAMILY,
                MENU_VACUUM,
                MENU_MOM,
                MENU_TICKET,
                MENU_PAPER,
                MENU_SETUP
        ));

        // Remove last 2 menu options
        for( String s : lastMenus ) {
            menus.remove( s );
        }
        // Remove menu entries for last 2 stories which were done
        for( String s : lastStories ) {
            String menuName = "menu" + s.substring(0, 1).toUpperCase() + s.substring(1);
            menus.remove( menuName );
        }

        int index = (int) (Math.random() * (menus.size()));
        Log.i(LOG_TAG, "random:"+index);
        checkAndSetStory( menus.get(index) );

        //Keep track of last 2 menu items
        lastMenus.add( currentStory );
        while ( lastMenus.size() > 2) {
            lastMenus.remove(0);
        }
    }
}
