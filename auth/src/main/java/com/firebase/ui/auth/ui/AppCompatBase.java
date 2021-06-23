/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.firebase.ui.auth.ui;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUILanguage;
import com.firebase.ui.auth.R;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public abstract class AppCompatBase extends HelperActivityBase {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.FirebaseUI); // Provides default values
        setTheme(getFlowParams().themeId);

        if (getFlowParams().lockOrientation) {
            lockOrientation();
        }

        // Note(istep): SS specific.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Locale locale = new Locale(AuthUILanguage.AppLanguageCode);
            Locale.setDefault(locale);
            Resources resources = getBaseContext().getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }
    }

    protected void switchFragment(@NonNull Fragment fragment,
                                  int fragmentId,
                                  @NonNull String tag,
                                  boolean withTransition,
                                  boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (withTransition) {
            ft.setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left);
        }
        ft.replace(fragmentId, fragment, tag);
        if (addToBackStack) {
            ft.addToBackStack(null).commit();
        } else {
            ft.disallowAddToBackStack().commit();
        }
    }

    protected void switchFragment(@NonNull Fragment fragment, int fragmentId, @NonNull String tag) {
        switchFragment(fragment, fragmentId, tag, false, false);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void lockOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
