package com.firebase.ui.auth.ui.email;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

/**
 * Layout model to customizing layout of the Email activity screen, where the user should set view
 * controls from public API.
 * <p>
 * To create a new instance, use {@link EmailCustomLayout.Builder}.
 */
public class EmailCustomLayout implements Parcelable {

    @LayoutRes
    private int mainLayout;

    private int mIsValid = 0;

    /**
     * EMAIL_VIEW_CONTROL_IDS -> IdRes of view controls.
     */
    private Map<String, Integer> mViewControls;

    private EmailCustomLayout() {}

    protected EmailCustomLayout(Parcel in) {
        mainLayout = in.readInt();
        this.mIsValid = in.readInt();

        Bundle buttonsBundle = in.readBundle(getClass().getClassLoader());
        this.mViewControls = new HashMap<>();
        for (String key : buttonsBundle.keySet()) {
            this.mViewControls.put(key, buttonsBundle.getInt(key));
        }
    }

    public static final Creator<EmailCustomLayout> CREATOR = new Creator<EmailCustomLayout>() {
        @Override
        public EmailCustomLayout createFromParcel(Parcel in) {
            return new EmailCustomLayout(in);
        }

        @Override
        public EmailCustomLayout[] newArray(int size) {
            return new EmailCustomLayout[size];
        }
    };

    /**
     * Builder for {@link EmailCustomLayout}.
     */
    public static class Builder {

        /**
         * EMAIL_VIEW_CONTROL_IDS -> IdRes of view controls.
         */
        private final Map<String, Integer> mViewControls;

        /**
         * Storage for necessary controls constants.
         */
        private List<String> mNecessaryControls;

        private final EmailCustomLayout instance;

        public Builder(@LayoutRes int mainLayout) {
            instance = new EmailCustomLayout();
            instance.mainLayout = mainLayout;
            mViewControls = new HashMap<>();
            initNecessaryControlsStorage();
        }

        /**
         * Set the ID of the progress bar in the email custom layout. Usage control ID:
         * {@link android.widget.ProgressBar}.
         */
        public EmailCustomLayout.Builder setProgressBarId(@IdRes int progressBarId) {
            mViewControls.put(EmailCustomLayoutTags.PROGRESS_BAR, progressBarId);
            return this;
        }

        /**
         * Set the ID of the submit button in the email custom layout. Usage control ID:
         * {@link android.widget.Button}.
         */
        public EmailCustomLayout.Builder setSubmitButtonId(@IdRes int submitButtonId) {
            mViewControls.put(EmailCustomLayoutTags.SUBMIT_BUTTON, submitButtonId);
            return this;
        }

        /**
         * Set the ID of the email input layout in the email custom layout. Usage control ID:
         * {@link com.google.android.material.textfield.TextInputLayout}
         */
        public EmailCustomLayout.Builder setEmailInputLayoutId(@IdRes int emailInputLayoutId) {
            mViewControls.put(EmailCustomLayoutTags.EMAIL_INPUT_LAYOUT, emailInputLayoutId);
            return this;
        }

        /**
         * Set the ID of the email edit text in the email custom layout. Usage control ID:
         * {@link android.widget.EditText}
         */
        public EmailCustomLayout.Builder setEmailEditTextId(@IdRes int emailEditTextId) {
            mViewControls.put(EmailCustomLayoutTags.EMAIL_EDIT_TEXT, emailEditTextId);
            return this;
        }

        /**
         * Set the ID of the email terms text in the email custom layout. Usage control ID:
         * {@link android.widget.TextView}
         */
        public EmailCustomLayout.Builder setEmailTermsText(@IdRes int emailTermsText) {
            mViewControls.put(EmailCustomLayoutTags.EMAIL_TERMS_TEXT, emailTermsText);
            return this;
        }

        /**
         * Set the ID of the footer text in the email custom layout. Usage control ID: {@link
         * android.widget.TextView}
         */
        public EmailCustomLayout.Builder setFooterText(@IdRes int footerText) {
            mViewControls.put(EmailCustomLayoutTags.FOOTER_TEXT, footerText);
            return this;
        }

        /**
         * Set the ID of the back icon (or any view) in the email custom layout.
         * When this view will be clicked Fragment will pop from stack.
         * Usage control ID: {@link android.view.View}
         */
        public EmailCustomLayout.Builder setBackView(@IdRes int backView) {
            mViewControls.put(EmailCustomLayoutTags.BACK_VIEW, backView);
            return  this;
        }

        /**
         * Set the ID of the header text in the email custom layout.
         * Usage control ID: {@link android.widget.TextView}
         */
        public EmailCustomLayout.Builder setHeaderText(@IdRes int headerText) {
            mViewControls.put(EmailCustomLayoutTags.HEADER_TEXT, headerText);
            return  this;
        }

        /**
         * @return instance of {@link EmailCustomLayout}
         * @throws IllegalArgumentException if necessary controls for the custom layout are not set.
         */
        public EmailCustomLayout build() {
            instance.mIsValid = 0;
            checkNecessaryControls();

            instance.mViewControls = mViewControls;
            instance.mIsValid = 1;
            return instance;
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

        private void initNecessaryControlsStorage() {
            mNecessaryControls = new ArrayList<>();
            mNecessaryControls.add(EmailCustomLayoutTags.SUBMIT_BUTTON);
            mNecessaryControls.add(EmailCustomLayoutTags.PROGRESS_BAR);
            mNecessaryControls.add(EmailCustomLayoutTags.EMAIL_INPUT_LAYOUT);
            mNecessaryControls.add(EmailCustomLayoutTags.EMAIL_EDIT_TEXT);
            mNecessaryControls.add(EmailCustomLayoutTags.EMAIL_TERMS_TEXT);
            mNecessaryControls.add(EmailCustomLayoutTags.HEADER_TEXT);
            mNecessaryControls.add(EmailCustomLayoutTags.FOOTER_TEXT);
            mNecessaryControls.add(EmailCustomLayoutTags.BACK_VIEW);
        }
    }

    /**
     * @return true if EmailCustomLayout is valid, otherwise false.
     */
    public boolean getIsValid() { return mIsValid == 1; }

    /**
     * @return main layout resource Id.
     */
    @LayoutRes
    public int getMainLayout() { return mainLayout; }

    /**
     * @param viewControlTag can be from {@link EmailCustomLayoutTags}
     * @return view control resource Id in custom layout.
     * @throws IllegalArgumentException if wrong viewControlTag is passed.
     */
    @IdRes
    public int getViewControlId(String viewControlTag) {
        if (EmailCustomLayoutTags.getInstance().isTagRegistered(viewControlTag)) {
            return mViewControls.get(viewControlTag);
        }
        String errMsg = "View control tag: " + viewControlTag + " is not registered.";
        throw new IllegalArgumentException(errMsg);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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
