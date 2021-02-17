package com.firebase.ui.auth.ui.phone;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;


/**
 * Layout model to customizing layout of the Phone activity screen, where the user should set view
 * controls from public API.
 * <p>
 * To create a new instance, use {@link PhoneNumberCustomLayout.Builder}.
 */
public class PhoneNumberCustomLayout implements Parcelable {

    @LayoutRes
    private int mainLayout;

    private int mIsValid = 0;

    /**
     * PHONE_VIEW_CONTROL_IDS -> IdRes of view controls.
     */
    private Map<String, Integer> mViewControls;

    private PhoneNumberCustomLayout() {}

    protected PhoneNumberCustomLayout(@NonNull Parcel in) {

        mainLayout = in.readInt();
        this.mIsValid = in.readInt();

        Bundle buttonsBundle = in.readBundle(getClass().getClassLoader());
        this.mViewControls = new HashMap<>();
        for (String key : buttonsBundle.keySet()) {
            this.mViewControls.put(key, buttonsBundle.getInt(key));
        }
    }

    public static final Creator<PhoneNumberCustomLayout> CREATOR = new Creator<PhoneNumberCustomLayout>() {

        @Override
        public PhoneNumberCustomLayout createFromParcel(Parcel in) {
            return new PhoneNumberCustomLayout(in);
        }

        @Override
        public PhoneNumberCustomLayout[] newArray(int size) {
            return new PhoneNumberCustomLayout[size];
        }
    };

    /**
     * Builder for {@link PhoneNumberCustomLayout}.
     */
    public static class Builder {

        /**
         * PHONE_VIEW_CONTROL_IDS -> IdRes of view controls.
         */
        private final Map<String, Integer> mViewControls;

        /**
         * Storage for necessary controls constants.
         */
        private List<String> mNecessaryControls;

        private final PhoneNumberCustomLayout instance;

        public Builder(@LayoutRes int mainLayout) {
            instance = new PhoneNumberCustomLayout();
            instance.mainLayout = mainLayout;
            mViewControls = new HashMap<>();
            initNecessaryControlsStorage();
        }

        /**
         * Set the ID of the progress bar in the phone number custom layout. Usage control ID:
         * {@link android.widget.ProgressBar}.
         */
        public PhoneNumberCustomLayout.Builder setProgressBarId(@IdRes int progressBarId) {
            mViewControls.put(PhoneNumberCustomLayoutTags.PROGRESS_BAR, progressBarId);
            return this;
        }

        /**
         * Set the ID of the submit button in the phone number custom layout. Usage control ID:
         * {@link android.widget.Button}.
         */
        public PhoneNumberCustomLayout.Builder setSubmitButtonId(@IdRes int submitButtonId) {
            mViewControls.put(PhoneNumberCustomLayoutTags.SUBMIT_BUTTON, submitButtonId);
            return this;
        }

        /**
         * Set the ID of the country list spinner in the phone number custom layout. Usage control
         * ID: {@link com.firebase.ui.auth.ui.phone.CountryListSpinner}.
         */
        public PhoneNumberCustomLayout.Builder setCountryListSpinnerId(@IdRes int listSpinnerId) {
            mViewControls.put(PhoneNumberCustomLayoutTags.COUNTRY_LIST_SPINNER, listSpinnerId);
            return this;
        }

        /**
         * Set the ID of the phone input layout in the phone number custom layout. Usage control ID:
         * {@link com.google.android.material.textfield.TextInputLayout}
         */
        public PhoneNumberCustomLayout.Builder setPhoneInputLayoutId(@IdRes int phoneInputLayoutId) {
            mViewControls.put(PhoneNumberCustomLayoutTags.PHONE_INPUT_LAYOUT, phoneInputLayoutId);
            return this;
        }

        /**
         * Set the ID of the phone edit text in the phone number custom layout. Usage control ID:
         * {@link android.widget.EditText}
         */
        public PhoneNumberCustomLayout.Builder setPhoneEditTextId(@IdRes int phoneEditTextId) {
            mViewControls.put(PhoneNumberCustomLayoutTags.PHONE_EDIT_TEXT, phoneEditTextId);
            return this;
        }

        /**
         * Set the ID of the sms terms text in the phone number custom layout. Usage control ID:
         * {@link android.widget.TextView}
         */
        public PhoneNumberCustomLayout.Builder setSmsTermsText(@IdRes int smsTermsText) {
            mViewControls.put(PhoneNumberCustomLayoutTags.SMS_TERMS_TEXT, smsTermsText);
            return this;
        }

        /**
         * Set the ID of the footer text in the phone number custom layout. Usage control ID: {@link
         * android.widget.TextView}
         */
        public PhoneNumberCustomLayout.Builder setFooterText(@IdRes int footerText) {
            mViewControls.put(PhoneNumberCustomLayoutTags.FOOTER_TEXT, footerText);
            return this;
        }

        /**
         * Set the ID of the back icon (or any view) in the phone number custom layout.
         * When this view will be clicked Fragment will pop from stack.
         * Usage control ID: {@link android.view.View}
         */
        public PhoneNumberCustomLayout.Builder setBackView(@IdRes int backView) {
            mViewControls.put(PhoneNumberCustomLayoutTags.BACK_VIEW, backView);
            return  this;
        }

        /**
         * @throws IllegalArgumentException if necessary controls for the custom layout are not
         * set or at least one progress control is not set.
         * @return instance of {@link PhoneNumberCustomLayout}
         */
        public PhoneNumberCustomLayout build() {
            instance.mIsValid = 0;
            checkNecessaryControls();
            checkProgressControl();

            instance.mViewControls = mViewControls;
            instance.mIsValid = 1;
            return instance;
        }

        /**
         * Init list with necessary controls constants.
         */
        private void initNecessaryControlsStorage() {
            mNecessaryControls = new ArrayList<>();
            mNecessaryControls.add(PhoneNumberCustomLayoutTags.SUBMIT_BUTTON);
            mNecessaryControls.add(PhoneNumberCustomLayoutTags.COUNTRY_LIST_SPINNER);
            mNecessaryControls.add(PhoneNumberCustomLayoutTags.PHONE_INPUT_LAYOUT);
            mNecessaryControls.add(PhoneNumberCustomLayoutTags.PHONE_EDIT_TEXT);
            mNecessaryControls.add(PhoneNumberCustomLayoutTags.SMS_TERMS_TEXT);
            mNecessaryControls.add(PhoneNumberCustomLayoutTags.FOOTER_TEXT);
        }

        /**
         * Returns progress error message.
         */
        private String getProgressErrorMsg() {
            String progressMsg = "Progress control is necessary.";
            String progressMsgSuffix = "[" + PhoneNumberCustomLayoutTags.PROGRESS_BAR
                    + " or " + PhoneNumberCustomLayoutTags.PROGRESS_DIALOG  + "]";
            return progressMsg + progressMsgSuffix;
        }

        /**
         * Check if all necessary control ids for custom layout are passed.
         * @throws IllegalArgumentException if all necessary controls are not set on builder.
         */
        private void checkNecessaryControls() {
            for (String item : mNecessaryControls) {
                if (mViewControls.get(item) == null) {
                    String necessaryControls = mNecessaryControls.toString();
                    String errorMsg = "Not all necessary controls are set. ";
                    String errorMsgSuffix = "Necessary controls are: " + necessaryControls;
                    throw new IllegalArgumentException(errorMsg + errorMsgSuffix);
                }
            }
        }

        /**
         * Check if progress control id for custom layout is passed.
         * Progress bar or Progress dialog should be set.
         * @throws IllegalArgumentException if progress control id is not set on builder.
         */
        private void checkProgressControl() {
            Integer progressBarId = mViewControls.get(PhoneNumberCustomLayoutTags.PROGRESS_BAR);
            Integer progressDialogId = mViewControls.get(PhoneNumberCustomLayoutTags.PROGRESS_DIALOG);
            if (progressBarId == null)
                if (progressDialogId == null)
                    throw new IllegalArgumentException(getProgressErrorMsg());
        }
    }

    /**
     * @return true if PhoneNumberCustomLayout is valid, otherwise false.
     */
    public boolean getIsValid() { return mIsValid == 1; }

    /**
     * @return main layout resource Id.
     */
    @LayoutRes
    public int getMainLayout() { return mainLayout; }


    /**
     * @param viewControlTag can be from {@link PhoneNumberCustomLayoutTags}
     * @return view control resource Id in custom layout.
     * @throws IllegalArgumentException if wrong viewControlTag is passed.
     */
    @IdRes
    public int getViewControlId(String viewControlTag) {
        if (PhoneNumberCustomLayoutTags.getInstance().isTagRegistered(viewControlTag)) {
            return mViewControls.get(viewControlTag);
        }
        String errMsg = "View control tag: " + viewControlTag + " is not registered.";
        throw new IllegalArgumentException(errMsg);
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mainLayout);
        parcel.writeInt(mIsValid);

        Bundle bundle = new Bundle();
        if (mViewControls != null) {
            for (String key : mViewControls.keySet()) {
                bundle.putInt(key, mViewControls.get(key));
            }
        }
        parcel.writeBundle(bundle);
    }
}
