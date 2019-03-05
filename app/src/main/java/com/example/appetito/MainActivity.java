package com.example.appetito;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    static final int REQUEST_IMAGE_CAPTURE = 0;
    static final int REQUEST_IMAGE_PICK = 1;
    ImageView ivPicture;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.navigation_camara:
                    //mTextMessage.setText(R.string.title_camara);
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
                    return true;
                case R.id.navigation_gallery:
                    //mTextMessage.setText(R.string.title_gallery);
                    intent = new Intent();
                    intent.setType("image/");
                    intent.setAction(intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), REQUEST_IMAGE_PICK);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mTextMessage = (TextView) findViewById(R.id.message);
        ivPicture = (ImageView) findViewById(R.id.ivpicture);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        switch (requestCode){
            case REQUEST_IMAGE_CAPTURE:
                bitmap = (Bitmap) data.getExtras().get("data");
                ivPicture.setImageBitmap(bitmap);
                break;
            case REQUEST_IMAGE_PICK:
                System.out.print("ENTRANDO AL CASE");
                /*Uri selectedImageUri = data.getData();
                final String path = getPathFromURI(selectedImageUri);
                if (path != null) {
                    File f = new File(path);
                    selectedImageUri = Uri.fromFile(f);
                }
                // Set the image in ImageView
                ivPicture.setImageURI(selectedImageUri);*/
                bitmap = (Bitmap) data.getExtras().get("data");
                ivPicture.setImageBitmap(bitmap);
                break;
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}
