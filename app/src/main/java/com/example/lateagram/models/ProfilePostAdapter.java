package com.example.lateagram.models;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.lateagram.LateHome;
import com.example.lateagram.MainActivity;
import com.example.lateagram.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class ProfilePostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> items;

    public final static int profile = 0, post = 1;


    public ProfilePostAdapter(List<Object> posts) {
        this.items = posts;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return profile;
        } else if (items.get(position) instanceof LatePost) {
            return post;
        } else {
            return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (i) {
            case profile:
                View v1 = inflater.inflate(R.layout.profile_stuff, viewGroup, false);
                viewHolder = new ViewHolder(v1);
                break;
            case post:
                View v2 = inflater.inflate(R.layout.post_image, viewGroup, false);
                viewHolder = new ViewHolder2(v2);
                break;
            default:
                View v = inflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);
                viewHolder = new ViewHolder2(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        switch (viewHolder.getItemViewType()) {
            case profile:
                ViewHolder vh1 = (ViewHolder) viewHolder;
                configureViewHolder(vh1, i);
                break;
            case post:
                ViewHolder2 vh2 = (ViewHolder2) viewHolder;
                configureViewHolder2(vh2, i);
                break;
            default:
                ViewHolder2 vh = (ViewHolder2) viewHolder;
                configureViewHolder2(vh, i);
                break;
        }
    }

    private void configureViewHolder(ViewHolder vh1, int position) {
        vh1.tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        Button btnLogout = vh1.itemView.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.getCurrentUser().put("emailVerified", false);
                ParseUser.getCurrentUser().saveInBackground();
                ParseUser.logOut();
                Intent i = new Intent(v.getContext(), MainActivity.class);
                v.getContext().startActivity(i);

            }
        });

        Button btnImage = vh1.itemView.findViewById(R.id.btnChangePic);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });
        final ViewHolder holder = vh1;
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LateHome) holder.itemView.getContext()).onPickPhoto(holder.itemView);
            }
        });

        ParseUser user = ParseUser.getCurrentUser();
        ParseFile file = user.getParseFile("profile_image");
        if(file != null) {
            Glide.with(vh1.itemView)
                .load(file.getUrl())
                    .transform(new CircleCrop())
                .into(vh1.ivProfileImage);
        }
    }

    private void configureViewHolder2(ViewHolder2 vh2, int position) {


            Glide.with(vh2.itemView)
                    .load(((LatePost) items.get(position)).getKeyImage().getUrl())
                    .into(vh2.ivProfileImage);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUsername;
        public ImageView ivProfileImage;
        public TextView tvCaption;
        public ImageView ivMedia;
        public TextView tvTimestamp;

        public ViewHolder(View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {

        TextView tvUsername;
        ImageView ivProfileImage;

        public ViewHolder2(View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivProfileImage = itemView.findViewById(R.id.ivMedia);
        }

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
            ((Activity) view.getContext()).startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }


}

