package com.example.lateagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.lateagram.models.BitmapScaler;
import com.example.lateagram.models.LatePost;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Posting extends AppCompatActivity {

    ImageView ivMedia;
    EditText etCaption;
    public String photoFileName = "photo.jpg";
    public final String APP_TAG = "MyCustomApp";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        setContentView(R.layout.activity_posting);

        File image = (File) getIntent().getSerializableExtra("image");

        ivMedia = findViewById(R.id.ivMedia);
        try {
            Bitmap origBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
            final Bitmap bitmap = BitmapScaler.scaleToFitWidth(origBitmap, 200);
            // Configure byte output stream
            final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            // Compress the image further
            new Thread(new Runnable() {
                public void run() {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                }
            }).start();
            // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
            File resizedFile = getPhotoFileUri(photoFileName + "_resized");
            resizedFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(resizedFile);
            // Write the bytes of the bitmap to file
            fos.write(bytes.toByteArray());
            fos.close();

            ExifInterface ei = new ExifInterface(image.getAbsolutePath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
            ivMedia.setImageBitmap(rotatedBitmap);
        } catch (IOException e) {
            Log.d("Posting", e.toString());
        }
        etCaption = findViewById(R.id.etCaption);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void sharePost(View v) {
        savePost(etCaption.getText().toString(), ParseUser.getCurrentUser());
        Intent i = new Intent(Posting.this, LateHome.class);
        startActivity(i);
    }

    private void savePost(String description, ParseUser parseUser) {
        LatePost post = new LatePost();
        post.setKeyDescription(description);
        post.setKeyUser(parseUser);
        File photoFile = (File) getIntent().getSerializableExtra("image");
        post.setKeyImage(new ParseFile(photoFile));
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    etCaption.setText("");
                    Log.d("Posting", "Post success!");
                } else {
                    Log.e("Posting", "Couldn't post. Rip");
                    e.printStackTrace();
                }
            }
        });
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("Posting", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }
}
