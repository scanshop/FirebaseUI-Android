package com.firebase.ui.auth.ui.phone;

import java.util.HashMap;

public class PhoneNumberCustomLayoutTags {

    public static final String SUBMIT_BUTTON = "SUBMIT_BUTTON";

    public static final String COUNTRY_LIST_SPINNER = "COUNTRY_LIST_SPINNER";

    public static final String PHONE_INPUT_LAYOUT = "PHONE_INPUT_LAYOUT";

    public static final String PHONE_EDIT_TEXT = "PHONE_EDIT_TEXT";

    public static final String PROGRESS_BAR = "PROGRESS_BAR";

    public static final String PROGRESS_DIALOG = "PROGRESS_DIALOG";

    public static final String SMS_TERMS_TEXT = "SMS_TERMS_TEXT";

    public static final String FOOTER_TEXT = "FOOTER_TEXT";

    public static final String BACK_VIEW = "BACK_VIEW";

    private static  PhoneNumberCustomLayoutTags instance;
    private HashMap<String, Boolean> tags;

    private PhoneNumberCustomLayoutTags() {
        registerTags();
    }

    public static PhoneNumberCustomLayoutTags getInstance() {
        if(instance == null){
            synchronized (PhoneNumberCustomLayoutTags.class) {
                if(instance == null){
                    instance = new PhoneNumberCustomLayoutTags();
                }
            }
        }
        return instance;
    }

    private void registerTags() {
        tags = new HashMap<>();
        tags.put(SUBMIT_BUTTON, true);
        tags.put(COUNTRY_LIST_SPINNER, true);
        tags.put(PHONE_INPUT_LAYOUT, true);
        tags.put(PHONE_EDIT_TEXT, true);
        tags.put(PROGRESS_BAR, true);
        tags.put(PROGRESS_DIALOG, true);
        tags.put(SMS_TERMS_TEXT, true);
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
