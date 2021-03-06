package com.codepath.apps.twitterclient.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.helper.Utils;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjayaram on 5/29/2015.
 */
public class MetionsTimelineFragment extends TweetsListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populateTimeline();
    }

    @Override
    public void populateTimeline()
    {
        if(!Utils.isNetworkAvailable(getActivity()))
        {
            Toast.makeText(getActivity(), "Getting from sql lite", Toast.LENGTH_SHORT).show();

            if(aTweets.getCount()==0) {
                List<Tweet> tweetList = Tweet.getAll();
                aTweets.clear();
                aTweets.addAll(tweetList);
            }
        }
        else {
            showProgressBar();
            client.getMentionsline(maxId, null, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d("DEBUG", response.toString());

                    ArrayList<Tweet> tweetList = Tweet.fromJsonArray(response);

                    if (tweetList.size() > 0) {
                        maxId = tweetList.get(tweetList.size() - 1).getUid() - 1 + "";
                        addAll(tweetList);

                        if (sinceId == null)
                            sinceId = aTweets.getItem(0).getUid() + "";
                    }

                    hideProgressBar();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    hideProgressBar();
                }
            });

        }
    }

    public void refreshTimeline()
    {
        if(!Utils.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_internet_error, Toast.LENGTH_SHORT).show();
        }
        else {
            sinceId = "1";
            client.getMentionsline(null, sinceId, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d("DEBUG", response.toString());
                    ArrayList<Tweet> tweetList = Tweet.fromJsonArray(response);

                    if (tweetList.size() > 0) {
                        sinceId = tweetList.get(0).getUid() + "";
                        maxId = tweetList.get(tweetList.size() - 1).getUid() - 1 + "";
                        aTweets.clear();
                        aTweets.addAll(tweetList);
                    }

                    swipeContainer.setRefreshing(false);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    swipeContainer.setRefreshing(false);
                }
            });
        }
    }

}
