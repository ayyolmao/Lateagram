package com.example.lateagram.models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.lateagram.PostDetails;
import com.example.lateagram.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends  RecyclerView.Adapter<PostAdapter.ViewHolder>{

    private List<LatePost> mPosts;

    // Pass in the contact array into the constructor
    public PostAdapter(List<LatePost> posts) {
        mPosts = posts;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.activity_post, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(PostAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final LatePost post = mPosts.get(position);

        // Set item views based on your views and data model
        final String username = post.getKeyUser().getUsername();

        final TextView tvUsername = viewHolder.tvUsername;
        tvUsername.setText(username);

        ImageView ivProfileImage = viewHolder.ivProfileImage;
        Glide.with(viewHolder.itemView.getContext())
                .load(R.drawable.instagram_user_outline_24)
                .transform(new CircleCrop())
                .into(ivProfileImage);

        final TextView tvCaption = viewHolder.tvCaption;
        SpannableStringBuilder str = new SpannableStringBuilder(username + " " + post.getKeyDescription());
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new ForegroundColorSpan(Color.BLACK), 0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvCaption.setText(str);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), PostDetails.class);
                final LatePost postF = post;
                i.putExtra("username", tvUsername.getText());
                i.putExtra("caption", tvCaption.getText().toString());
                i.putExtra("media", postF.getKeyImage().getUrl());
                i.putExtra("timestamp", postF.getKeyTimestamp());
                i.putExtra("objectId", postF.getKeyObjectId());
                i.putExtra("post", postF);
                v.getContext().startActivity(i);
            }
        };
        tvCaption.setOnClickListener(listener);

        ImageView ivMedia = viewHolder.ivMedia;
        Glide.with(viewHolder.itemView.getContext())
                .load(post.getKeyImage().getUrl())
                .into(ivMedia);
        ivMedia.setOnClickListener(listener);

        TextView tvTimestamp = viewHolder.tvTimestamp;
        String timestamp = post.getCreatedAt().toString();
        tvTimestamp.setText(getRelativeTimeAgo(timestamp));

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mPosts.size();
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

    // Clean all elements of the recycler
    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<LatePost> list) {
        mPosts.addAll(list);
        notifyDataSetChanged();
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss zzz yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
