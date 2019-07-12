package com.example.lateagram;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.lateagram.models.CommentAdapter;
import com.example.lateagram.models.LateComment;
import com.example.lateagram.models.LatePost;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class PostDetails extends AppCompatActivity {


    ArrayList<LateComment> posts;
    CommentAdapter adapter;
    RecyclerView rvPosts;
    String objectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_post_details);

        rvPosts = findViewById(R.id.rvComments);

        objectId = getIntent().getStringExtra("objectId");


        posts = new ArrayList<>();
        adapter = new CommentAdapter(posts);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        rvPosts.setAdapter(adapter);

        loadTopComments();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final TextView tvCaption = findViewById(R.id.tvCaption);
        String username = getIntent().getStringExtra("username");
        SpannableStringBuilder str = new SpannableStringBuilder(getIntent()
                .getStringExtra("caption"));
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new ForegroundColorSpan(Color.BLACK), 0, username.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvCaption.setText(str);
        TextView tvUsername = findViewById(R.id.tvUsername);
        tvUsername.setText(getIntent().getStringExtra("username"));

        ImageView ivMedia = findViewById(R.id.ivMedia);
        Glide.with(this)
                .load(getIntent().getStringExtra("media"))
                .into(ivMedia);

        ImageView ivProfileImage = findViewById(R.id.ivProfileImage);
        Glide.with(this)
                .load(R.drawable.instagram_user_outline_24)
                .transform(new CircleCrop())
                .into(ivProfileImage);

        TextView tvTimestamp = findViewById(R.id.tvTimestamp);
        tvTimestamp.setText(getIntent().getStringExtra("timestamp"));

        final EditText etComment = findViewById(R.id.etComment);

        Button button = findViewById(R.id.btnPost);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etComment.getText().equals("")) {
                    LateComment comment = new LateComment();
                    comment.setKeyText(etComment.getText().toString());
                    comment.setKeyUser(ParseUser.getCurrentUser());
                    comment.setKeyLikeCount((long) 0);
                    comment.setKeyInReplyTo((LatePost) getIntent().getParcelableExtra("post"));
                    comment.saveInBackground(new SaveCallback() {
                                                 @Override
                                                 public void done(ParseException e) {
                                                     etComment.setText("");
                                                 }
                                             }
                    );
                }
            }
        });
    }


    protected void loadTopComments() {
        final LateComment.Query postQuery = new LateComment.Query();
        postQuery.getTop().withUser();
        postQuery.addAscendingOrder("createdAt");
        postQuery.include("in_reply_to");
        LatePost.Query inQuery = new LatePost.Query();
        inQuery.whereMatches("objectId", getIntent().getStringExtra("objectId"));
        postQuery.whereMatchesQuery("in_reply_to", inQuery);


        postQuery.findInBackground(new FindCallback<LateComment>() {
            @Override
            public void done(List<LateComment> objects, ParseException e) {
                if(e == null){
                    for(int i = 0; i < objects.size(); ++i) {
                        posts.add((LateComment) objects.get(i));
                        adapter.notifyItemInserted(i);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }


}
