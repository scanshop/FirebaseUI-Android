package com.firebase.ui.auth.ui.email;

import java.util.HashMap;

public class EmailCustomLayoutTags {


    public static final String SUBMIT_BUTTON = "SUBMIT_BUTTON";

    public static final String PROGRESS_BAR = "PROGRESS_BAR";

    public static final String EMAIL_INPUT_LAYOUT = "EMAIL_INPUT_LAYOUT";

    public static final String EMAIL_EDIT_TEXT = "EMAIL_EDIT_TEXT";

    public static final String EMAIL_TERMS_TEXT = "EMAIL_TERMS_TEXT";

    public static final String HEADER_TEXT = "HEADER_TEXT";

    public static final String FOOTER_TEXT = "FOOTER_TEXT";

    public static final String BACK_VIEW = "BACK_VIEW";


    private static  EmailCustomLayoutTags instance;
    private HashMap<String, Boolean> tags;

    private EmailCustomLayoutTags() {
        registerTags();
    }

    public static EmailCustomLayoutTags getInstance() {
        if(instance == null){
            synchronized (EmailCustomLayoutTags.class) {
                if(instance == null){
                    instance = new EmailCustomLayoutTags();
                }
            }
        }
        return instance;
    }

    private void registerTags() {
        tags = new HashMap<>();
        tags.put(SUBMIT_BUTTON, true);
        tags.put(PROGRESS_BAR, true);
        tags.put(EMAIL_INPUT_LAYOUT, true);
        tags.put(EMAIL_EDIT_TEXT, true);
        tags.put(EMAIL_TERMS_TEXT, true);
        tags.put(HEADER_TEXT, true);
        tags.put(FOOTER_TEXT, true);
        tags.put(BACK_VIEW, true);
    }

    public boolean isTagRegistered(String tag) {
        if (tag == null) return false;

        Boolean isRegistered = tags.get(tag);
        if (isRegistered != null)
            return isRegistered;

        return false;
    }
}
