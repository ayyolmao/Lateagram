package com.example.lateagram.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lateagram.R;
import com.example.lateagram.models.LatePost;
import com.example.lateagram.models.PostAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class LateHomeFragment extends Fragment {
    ArrayList<LatePost> posts;
    PostAdapter adapter;
    RecyclerView rvPosts;
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        rvPosts = view.findViewById(R.id.rvPosts);

        posts = new ArrayList<>();
        adapter = new PostAdapter(posts);
        rvPosts.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvPosts.setAdapter(adapter);


        loadTopPosts();

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        super.onViewCreated(view, savedInstanceState);
    }

    protected void loadTopPosts() {
        final LatePost.Query postQuery = new LatePost.Query();
        postQuery.getTop().withUser();
        postQuery.addAscendingOrder("createdAt");
        postQuery.findInBackground(new FindCallback<LatePost>() {
            @Override
            public void done(List<LatePost> objects, ParseException e) {
                if(e == null){
                    for(int i = 0; i < objects.size(); ++i) {
                        posts.add(objects.get(i));
                        adapter.notifyItemInserted(i);
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

    public void fetchTimelineAsync(int page) {
        final LatePost.Query postQuery = new LatePost.Query();
        postQuery.getTop().withUser();
        postQuery.findInBackground(new FindCallback<LatePost>() {
            @Override
            public void done(List<LatePost> objects, ParseException e) {
                if(e == null){
                    adapter.clear();
                    for(int i = 0; i < objects.size(); ++i) {
                        posts.add(objects.get(i));
                        adapter.notifyItemInserted(i);
                        Log.d("HomeActivity", "Post[" + i + "] = " +
                                objects.get(i).getKeyDescription()
                                + "\nusername = " + objects.get(i).getKeyUser().getUsername());
                    }
                    swipeContainer.setRefreshing(false);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
