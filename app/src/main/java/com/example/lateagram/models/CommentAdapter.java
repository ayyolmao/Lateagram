package com.example.lateagram.models;

import android.content.Context;
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
import com.example.lateagram.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends  RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private List<LateComment> mPosts;

    // Pass in the contact array into the constructor
    public CommentAdapter(List<LateComment> posts) {
        mPosts = posts;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_comment, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(CommentAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final LateComment post = mPosts.get(position);

        // Set item views based on your views and data model
        final String username = post.getKeyUser().getUsername();


        ImageView ivProfileImage = viewHolder.ivProfileImage;
        Glide.with(viewHolder.itemView.getContext())
                .load(R.drawable.instagram_user_outline_24)
                .transform(new CircleCrop())
                .into(ivProfileImage);

        final TextView tvCaption = viewHolder.tvCaption;
        SpannableStringBuilder str = new SpannableStringBuilder(username + " " + post.getKeyText());
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new ForegroundColorSpan(Color.BLACK), 0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvCaption.setText(str);



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
        public ImageView ivProfileImage;
        public TextView tvCaption;
        public TextView tvTimestamp;
        public TextView tvLikeCount;

        public ViewHolder(View itemView) {
            super(itemView);

            tvCaption = itemView.findViewById(R.id.tvComment);
            ivProfileImage = itemView.findViewById(R.id.ivProfile);
            tvTimestamp = itemView.findViewById(R.id.tvCreatedAt);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<LateComment> list) {
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