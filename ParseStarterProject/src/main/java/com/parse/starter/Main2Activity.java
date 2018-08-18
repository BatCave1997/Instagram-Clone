package com.parse.starter;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    ListView lv;
    ArrayList<String> userList;
    ArrayAdapter<String> arrayAdapter;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                GetPhoto();
            }
        }
    }

    void GetPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            try{
                Uri selectedImage = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
               // imageView.setImageBitmap(bitmap);
                Log.i("Image", "Selected");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                ParseFile parseFile = new ParseFile("image.png", byteArray);
                ParseObject parseObject = new ParseObject("Image");

                parseObject.put("image", parseFile);
                parseObject.put("username", ParseUser.getCurrentUser().getUsername());
                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Toast.makeText(Main2Activity.this, "Image is uploaded", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(Main2Activity.this, "Image not saved, try again later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
            catch(Exception e ){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.share_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.item_share){
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }else{
                GetPhoto();
            }
        }
        else if(item.getItemId() == R.id.log_out){
            ParseUser.logOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        lv = (ListView) findViewById(R.id.listView);
        userList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userList);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("userList", ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("userlist");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for(ParseUser parseUser : objects){
                            if(!parseUser.getUsername().equals(ParseUser.getCurrentUser().getUsername())){
                                userList.add(parseUser.getUsername());
                                Log.i("User ", parseUser.getUsername());
                            }
                        }
                        arrayAdapter.notifyDataSetChanged();
                        lv.setAdapter(arrayAdapter);
                    }
                }
                else{
                    e.printStackTrace();
                }
            }
        });

       lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent i = new Intent(getApplicationContext(), UserFeed.class);
               i.putExtra("username", userList.get(position));
               startActivity(i);
           }
       });
    }

    @Override
    public void onBackPressed() {
        Main2Activity.this.moveTaskToBack(true);
        super.onBackPressed();
    }
}
