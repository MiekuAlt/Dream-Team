/**
 * Hexiwear application is used to pair with Hexiwear BLE devices
 * and send sensor readings to WolkSense sensor data cloud
 * <p>
 * Copyright (C) 2016 WolkAbout Technology s.r.o.
 * <p>
 * Hexiwear is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Hexiwear is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.wolkabout.hexiwear.activity;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.wolkabout.hexiwear.R;
import com.wolkabout.hexiwear.util.Dialog;
import com.wolkabout.hexiwear.view.Input;
import com.wolkabout.wolkrestandroid.Credentials_;
import com.wolkabout.wolkrestandroid.service.AuthenticationService;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.rest.spring.annotations.RestService;

@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    @ViewById
    Input emailField;

    @ViewById
    Input passwordField;

    @ViewById
    View signInElements;

    @ViewById
    TextView signingInLabel;

    @ViewById
    View signingInElements;

    @ViewById
    Button signInButton;

    @Pref
    Credentials_ credentials;

    @SystemService
    InputMethodManager inputMethodManager;

    @Bean
    Dialog dialog;

    @RestService
    AuthenticationService authenticationService;

    @Override
    protected void onResume() {
        super.onResume();
        MainActivity_.intent(this).start();
        finish();
    }
}
