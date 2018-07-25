package com.sobelman.bakingapp;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.sobelman.bakingapp.model.RecipeStep;

/**
 * A fragment representing a single RecipeStep detail screen.
 * This fragment is either contained in a {@link RecipeActivity}
 * in two-pane mode (on tablets) or a {@link RecipeStepDetailActivity}
 * on handsets.
 */
public class RecipeStepDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    // saved instance state keys for ExoPlayer
    private static final String VIDEO_POSITION = "video_position";
    private static final String PLAY_WHEN_READY = "play_when_ready";

    /**
     * The recipe step this fragment is presenting.
     */
    private RecipeStep mItem;

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeStepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(ARG_ITEM_ID)) {
            // Load the recipe step specified by the fragment arguments
            mItem = getArguments().getParcelable(ARG_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipestep_detail, container, false);

        TextView descriptionTextView = rootView.findViewById(R.id.recipestep_detail);

        if (mItem != null) {
            descriptionTextView.setText(mItem.getDescription());
        }

        mPlayerView = rootView.findViewById(R.id.recipe_video);
        long videoPosition = 0L;
        boolean playWhenReady = true;
        if (savedInstanceState != null) {
            videoPosition = savedInstanceState.getLong(VIDEO_POSITION, videoPosition);
            playWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY, true);
        }
        if (mItem != null) {
            String videoUrl = mItem.getVideoUrl();
            if (!TextUtils.isEmpty(videoUrl)) {
                initializePlayer(Uri.parse(videoUrl), getActivity(), videoPosition, playWhenReady);
            } else {
                String thumbnailUrl = mItem.getThumbnailUrl();
                if (!TextUtils.isEmpty(thumbnailUrl) && thumbnailUrl.endsWith(".mp4")) {
                    initializePlayer(Uri.parse(thumbnailUrl), getActivity(), videoPosition, playWhenReady);
                } else {
                    mPlayerView.setVisibility(View.GONE);
                    descriptionTextView.setVisibility(View.VISIBLE);
                }
            }
        }

        return rootView;
    }

    private void initializePlayer(Uri uri, Context context, long videoPosition,
                                  boolean playWhenReady) {
        if (mExoPlayer == null) {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);
            mExoPlayer =
                    ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);
            mPlayerView.setPlayer(mExoPlayer);

            String userAgent = Util.getUserAgent(context, "BakingApp");
            DataSource.Factory dataSourceFactory =
                    new DefaultDataSourceFactory(context, userAgent);
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(uri);
            mExoPlayer.prepare(videoSource);
            mExoPlayer.seekTo(videoPosition);
            mExoPlayer.setPlayWhenReady(playWhenReady);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mExoPlayer != null) {
            outState.putLong(VIDEO_POSITION, mExoPlayer.getCurrentPosition());
            outState.putBoolean(PLAY_WHEN_READY, mExoPlayer.getPlayWhenReady());
        }
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mExoPlayer != null) releasePlayer();
    }
}
