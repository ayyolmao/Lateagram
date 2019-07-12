package com.example.lateagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lateagram.R;
import com.example.lateagram.models.LatePost;
import com.example.lateagram.models.ProfilePostAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    ArrayList<Object> posts;
    ProfilePostAdapter adapter;
    RecyclerView rvPosts;
    RecyclerView.LayoutManager mLayoutManager;
    public final String APP_TAG = "MyCustomApp";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        rvPosts = view.findViewById(R.id.rvProfile);

        posts = new ArrayList<>();
        adapter = new ProfilePostAdapter(posts);


        mLayoutManager = new GridLayoutManager(this.getContext(), 2);
        ((GridLayoutManager) mLayoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case ProfilePostAdapter.profile:
                        return 2;
                    case ProfilePostAdapter.post:
                        return 1;
                    default:
                        return 1;
                }
            }
        });

        rvPosts.setLayoutManager(mLayoutManager);
        rvPosts.setAdapter(adapter);


        loadTopPosts();




        super.onViewCreated(view, savedInstanceState);
    }

    protected void loadTopPosts() {
        final LatePost.Query postQuery = new LatePost.Query();
        postQuery.getTop().withUser();
        postQuery.addAscendingOrder("createdAt");
        postQuery.whereEqualTo(LatePost.KEY_USER, ParseUser.getCurrentUser());
        postQuery.findInBackground(new FindCallback<LatePost>() {
            @Override
            public void done(List<LatePost> objects, ParseException e) {
                if(e == null){
                    posts.add(ParseUser.getCurrentUser().getUsername());
                    adapter.notifyItemInserted(0);
                    for(int i = 0; i < objects.size(); ++i) {
                        posts.add(objects.get(i));
                        adapter.notifyItemInserted(i+1);
                        Log.d("HomeActivity", "Post[" + i + "] = " +
                                objects.get(i).getKeyDescription()
                                + "\nusername = " + objects.get(i).getKeyUser().getUsername());
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(view.getContext().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri photoUri = data.getData();
            ParseUser user = ParseUser.getCurrentUser();
            user.put("profile_pic", new ParseFile(new File(photoUri.getPath())));
        }
    }
    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("Posting", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


}

