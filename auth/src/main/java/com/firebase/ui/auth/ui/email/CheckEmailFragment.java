package com.firebase.ui.auth.ui.email;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.R;
import com.firebase.ui.auth.data.model.FlowParameters;
import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.auth.ui.FragmentBase;
import com.firebase.ui.auth.util.ExtraConstants;
import com.firebase.ui.auth.util.data.PrivacyDisclosureUtils;
import com.firebase.ui.auth.util.ui.ImeHelper;
import com.firebase.ui.auth.util.ui.fieldvalidators.EmailFieldValidator;
import com.firebase.ui.auth.viewmodel.ResourceObserver;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.EmailAuthProvider;

import java.util.HashMap;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import static com.firebase.ui.auth.AuthUI.EMAIL_LINK_PROVIDER;

/**
 * Fragment that shows a form with an email field and checks for existing accounts with that email.
 * <p>
 * Host Activities should implement {@link CheckEmailFragment.CheckEmailListener}.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class CheckEmailFragment extends FragmentBase implements
        View.OnClickListener,
        ImeHelper.DonePressedListener {

    public static final String TAG = "CheckEmailFragment";
    private CheckEmailHandler mHandler;
    private Button mNextButton;
    private ProgressBar mProgressBar;
    private EditText mEmailEditText;
    private TextInputLayout mEmailLayout;
    private EmailFieldValidator mEmailFieldValidator;
    private CheckEmailListener mListener;
    private View mBackView;

    private HashMap<String, Integer> defaultIds;

    public static CheckEmailFragment newInstance(@Nullable String email) {
        CheckEmailFragment fragment = new CheckEmailFragment();
        Bundle args = new Bundle();
        args.putString(ExtraConstants.EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return getEmailLayout(inflater, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        registerDefaultIds();

        mNextButton = getViewControl(view, EmailCustomLayoutTags.SUBMIT_BUTTON);
        mProgressBar = getViewControl(view, EmailCustomLayoutTags.PROGRESS_BAR);

        // Email field and validator
        mEmailLayout = getViewControl(view, EmailCustomLayoutTags.EMAIL_INPUT_LAYOUT);
        mEmailEditText = getViewControl(view, EmailCustomLayoutTags.EMAIL_EDIT_TEXT);
        mEmailFieldValidator = new EmailFieldValidator(mEmailLayout);
        mEmailLayout.setOnClickListener(this);
        mEmailEditText.setOnClickListener(this);

        // Custom back view
        mBackView = getViewControl(view, EmailCustomLayoutTags.BACK_VIEW);

        // Hide header
        TextView headerText = getViewControl(view, EmailCustomLayoutTags.HEADER_TEXT);
        if (headerText != null) {
            headerText.setVisibility(View.GONE);
        }

        ImeHelper.setImeOnDoneListener(mEmailEditText, this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && getFlowParams().enableHints) {
            mEmailEditText.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
        }

        mNextButton.setOnClickListener(this);

        TextView termsText = getViewControl(view, EmailCustomLayoutTags.EMAIL_TERMS_TEXT);
        TextView footerText = getViewControl(view, EmailCustomLayoutTags.FOOTER_TEXT);
        FlowParameters flowParameters = getFlowParams();

        if (!flowParameters.shouldShowProviderChoice()) {
            PrivacyDisclosureUtils.setupTermsOfServiceAndPrivacyPolicyText(requireContext(),
                    flowParameters,
                    termsText);
        } else {
            termsText.setVisibility(View.GONE);
            PrivacyDisclosureUtils.setupTermsOfServiceFooter(requireContext(),
                    flowParameters,
                    footerText);
        }

        setupCustomLayoutListeners();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler = new ViewModelProvider(this).get(CheckEmailHandler.class);
        mHandler.init(getFlowParams());

        FragmentActivity activity = getActivity();
        if (!(activity instanceof CheckEmailListener)) {
            throw new IllegalStateException("Activity must implement CheckEmailListener");
        }
        mListener = (CheckEmailListener) activity;

        mHandler.getOperation().observe(getViewLifecycleOwner(), new ResourceObserver<User>(
                this, R.string.fui_progress_dialog_checking_accounts) {
            @Override
            protected void onSuccess(@NonNull User user) {
                String email = user.getEmail();
                String provider = user.getProviderId();

                mEmailEditText.setText(email);
                //noinspection ConstantConditions new user
                if (provider == null) {
                    mListener.onNewUser(new User.Builder(EmailAuthProvider.PROVIDER_ID, email)
                            .setName(user.getName())
                            .setPhotoUri(user.getPhotoUri())
                            .build());
                } else if (provider.equals(EmailAuthProvider.PROVIDER_ID)
                        || provider.equals(EMAIL_LINK_PROVIDER)) {
                    mListener.onExistingEmailUser(user);
                } else {
                    mListener.onExistingIdpUser(user);
                }
            }

            @Override
            protected void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseUiException
                        && ((FirebaseUiException) e).getErrorCode() == ErrorCodes.DEVELOPER_ERROR) {
                    mListener.onDeveloperFailure(e);
                }

                if (e instanceof FirebaseNetworkException) {
                    Snackbar.make(getView(), getString(R.string.fui_no_internet), Snackbar.LENGTH_SHORT).show();
                }

                // Otherwise just let the user enter their data
            }
        });

        if (savedInstanceState != null) {
            return;
        }

        // Check for email
        String email = getArguments().getString(ExtraConstants.EMAIL);
        if (!TextUtils.isEmpty(email)) {
            mEmailEditText.setText(email);
            validateAndProceed();
        } else if (getFlowParams().enableHints) {
            mHandler.fetchCredential();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mHandler.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.button_next) {
            validateAndProceed();
        } else if (id == R.id.email_layout || id == R.id.email) {
            mEmailLayout.setError(null);
        }
    }

    @Override
    public void onDonePressed() {
        validateAndProceed();
    }

    private void validateAndProceed() {
        String email = mEmailEditText.getText().toString();
        if (mEmailFieldValidator.validate(email)) {
            mHandler.fetchProvider(email);
        }
    }


    /**
     * @return EmailCustomLayout set from public API.
     * @throws IllegalStateException if email custom layout is not set from public API.
     */
    @NonNull
    private EmailCustomLayout getCustomEmailLayout() {
        EmailCustomLayout customLayout = getFlowParams().emailCustomLayout;
        if (customLayout == null)
            throw new IllegalStateException("Email custom layout is not set.");
        return customLayout;
    }

    private @IdRes
    int getDefaultViewControlId(String tag) {
        Integer resourceId = defaultIds.get(tag);
        if (resourceId == null) return 0;
        return resourceId;
    }

    private void registerDefaultIds() {
        defaultIds = new HashMap<>();
        defaultIds.put(EmailCustomLayoutTags.PROGRESS_BAR, R.id.top_progress_bar);
        defaultIds.put(EmailCustomLayoutTags.SUBMIT_BUTTON, R.id.button_next);
        defaultIds.put(EmailCustomLayoutTags.EMAIL_INPUT_LAYOUT, R.id.email_layout);
        defaultIds.put(EmailCustomLayoutTags.EMAIL_EDIT_TEXT, R.id.email);
        defaultIds.put(EmailCustomLayoutTags.EMAIL_TERMS_TEXT, R.id.email_tos_and_pp_text);
        defaultIds.put(EmailCustomLayoutTags.HEADER_TEXT, R.id.header_text);
        defaultIds.put(EmailCustomLayoutTags.FOOTER_TEXT, R.id.email_footer_tos_and_pp_text);
    }

    private void setupCustomLayoutListeners() {
        if (mBackView != null) {
            mBackView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentActivity activity = getActivity();
                    if (activity != null)
                        activity.onBackPressed();
                }
            });
        }
        // Note(istep):
        // SS specific. Remove before PR to FirebaseUI.
        mEmailLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDonePressed();
            }
        });
    }

    private boolean isCustomLayoutEnabled() {
        EmailCustomLayout customLayout = getFlowParams().emailCustomLayout;
        return (customLayout != null) && customLayout.getIsValid();
    }

    private View getEmailLayout(@NonNull LayoutInflater inflater,
                                @Nullable ViewGroup container) {
        if (isCustomLayoutEnabled()) {
            return inflater.inflate(getCustomEmailLayout().getMainLayout(), container, false);
        } else {
            return inflater.inflate(R.layout.fui_check_email_layout, container, false);
        }
    }

    private <T extends View> T getViewControl(@NonNull View view, @NonNull String tag) {
        if (isCustomLayoutEnabled()) {
            return view.findViewById(getCustomEmailLayout().getViewControlId(tag));
        } else {
            return view.findViewById(getDefaultViewControlId(tag));
        }
    }

    @Override
    public void showProgress(int message) {
        mNextButton.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mNextButton.setEnabled(true);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Interface to be implemented by Activities hosting this Fragment.
     */
    interface CheckEmailListener {

        /**
         * Email entered belongs to an existing email user.
         */
        void onExistingEmailUser(User user);

        /**
         * Email entered belongs to an existing IDP user.
         */
        void onExistingIdpUser(User user);

        /**
         * Email entered does not belong to an existing user.
         */
        void onNewUser(User user);

        /**
         * Email entered corresponds to an existing user whose sign in methods we do not support.
         */
        void onDeveloperFailure(Exception e);
    }
}
