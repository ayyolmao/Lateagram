package com.example.lateagram.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

@ParseClassName("Comment")
public class LateComment extends ParseObject {
    public static final String KEY_TEXT = "text";
    public static final String KEY_USER = "user";
    public static final String KEY_TIMESTAMP = "createdAt";
    public static final String KEY_LIKE_COUNT = "like_count";
    public static final String KEY_IN_REPLY_TO = "in_reply_to";

    public void setKeyInReplyTo(LatePost post) {
        put(KEY_IN_REPLY_TO, post);
    }

    public String getKeyText() {
        return getString(KEY_TEXT);
    }

    public void setKeyText(String text) {
        put(KEY_TEXT, text);
    }

    public Long getKeyLikeCount() {
        return getLong(KEY_LIKE_COUNT);
    }

    public void setKeyLikeCount(Long count) {
        put(KEY_LIKE_COUNT, count);
    }

    public ParseUser getKeyUser() {
        return getParseUser(KEY_USER);
    }

    public void setKeyUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getKeyTimestamp() {
        return getString(KEY_TIMESTAMP);
    }

    public void setKeyTimestamp(String timestamp) {
        put(KEY_TIMESTAMP, timestamp);
    }

    public static class Query extends ParseQuery<LateComment> {

        public Query() {
            super(LateComment.class);
        }

        public Query getTop() {
            setLimit(20);
            return this;
        }

        public Query withUser() {
            include("user");
            include("in_reply_to");
            return this;
        }

    }

    private static int lastPostId = 0;


    public static ArrayList<LateComment> createPostsList(int numPosts) {
        ArrayList<LateComment> posts = new ArrayList<LateComment>();

        for (int i = 1; i <= numPosts; i++) {
            posts.add(new LateComment());
        }

        return posts;
    }
}