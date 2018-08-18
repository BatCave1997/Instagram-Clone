/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener{

  int flag = 0;

  Button login;
  EditText u, p;
  TextView signin;

  RelativeLayout lay;
  ImageView logo ;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    u = (EditText) findViewById(R.id.username);
    p = (EditText) findViewById(R.id.password);

    lay = (RelativeLayout)findViewById(R.id.backgroundLayout);
    logo = (ImageView) findViewById(R.id.logoImage);
   if(p!= null){
     p.setOnKeyListener(this);
   }
    lay.setOnClickListener(this);
    logo.setOnClickListener(this);

    signin = (TextView)findViewById(R.id.signin);

    login = (Button)findViewById(R.id.login);

    if(ParseUser.getCurrentUser() != null){
      OpenProfileHome();
    }
    
    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

  public void OpenProfileHome(){
    Intent i = new Intent(this, Main2Activity.class);
    startActivity(i);
  }

  public void SignedIn(View view){
    if(Integer.parseInt((String) view.getTag()) == 1){
      if(flag == 0){
        login.setText("Sign in");
        signin.setText("Log in");
        flag = 1;
      }else{
        login.setText("Log in");
        signin.setText("Sign in");
        flag = 0;
      }
    }
  }

  //PASSWORD : 2YcNbg9ifT2D

  public void Loggedin(View view){
    if(u.getText().toString().matches("") || p.getText().toString().matches("")){
      Toast.makeText(this, "Fill in all the entries", Toast.LENGTH_SHORT).show();
    }else{
      ParseUser parseUser = new ParseUser();
      if(flag == 1){
        parseUser.setUsername(u.getText().toString());
        parseUser.setPassword(p.getText().toString());
        parseUser.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
            if(e == null){
              Toast.makeText(MainActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
            }else{
              Toast.makeText(MainActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
            }
          }
        });
      }else{
        ParseUser.logInInBackground(u.getText().toString(), p.getText().toString(), new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {
            if(e == null && user!= null ){
              OpenProfileHome();
              Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            }else{
              Toast.makeText(MainActivity.this, "username or password incorrect", Toast.LENGTH_SHORT).show();
            }
          }
        });
      }

    }
  }



  @Override
  public void onClick(View v) {
    if(v.getId() == lay.getId() || v.getId() == logo.getId()){
      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
  }

  @Override
  public boolean onKey(View v, int keyCode, KeyEvent event) {
    if(keyCode == event.KEYCODE_ENTER && event.getAction() == event.ACTION_DOWN){
      Loggedin(v);
    }
    return false;
  }
}