package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class UserFeed extends AppCompatActivity {

    LinearLayout linearLayout ;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        Intent i =  getIntent();
        String s = i.getStringExtra("username");

        linearLayout = (LinearLayout) findViewById(R.id.linearLay);


       // imageView.setImageDrawable(getResources().getDrawable(R.drawable.insta));

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Image");

        query.whereEqualTo("username", s);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    for(ParseObject user : objects){
                        ParseFile file = (ParseFile)user.get("image");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if(e == null && data != null){
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    imageView = new ImageView(getApplicationContext());

                                    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    imageView.setImageBitmap(bitmap);
                                    linearLayout.addView(imageView);
                                }
                                else{
                                    Log.i("Error", "Something went wrong");
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
