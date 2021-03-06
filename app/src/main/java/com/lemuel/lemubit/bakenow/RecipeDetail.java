/*
 *
 *   BakeNow application
 *
 *   @author Lemuel Ogbunude
 *   Copyright (C) 2017 Lemuel Ogbunude (lemuelcco@gmail.com)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *
 */

package com.lemuel.lemubit.bakenow;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;


import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.lemuel.lemubit.bakenow.Adapter.StepDescriptionAdapter;
import com.lemuel.lemubit.bakenow.Fragments.IngredientsFragment;
import com.lemuel.lemubit.bakenow.Fragments.StepDescriptionFragment;
import com.lemuel.lemubit.bakenow.Models.Recipe;
import com.lemuel.lemubit.bakenow.Models.Steps;
import com.lemuel.lemubit.bakenow.Utils.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
* This is the activity that displays the ingredients and steps, but also includes the media player
* and description if it is a tablet
* */

public class RecipeDetail extends AppCompatActivity implements StepDescriptionAdapter.OnStepClickListener {
    int position;
    @BindView(R.id.ingredientsLBL)
    TextView ingredientsLBL;
    @BindView(R.id.StepsLBL)
    TextView stepsLBL;
    @BindView(R.id.ToolBar_Recipe_Title)
    TextView ToolBarTitle;

    SimpleExoPlayerView videoView;
    TextView instructionTXT;
    Snackbar snackbar;
    ImageView imageView;
    private boolean mTwoPane;
    List<Recipe> recipes = new ArrayList<>();
    String description;
    Boolean playWhenReady;
    private SimpleExoPlayer mExoPlayer;
    private BandwidthMeter bandwidthMeter;
    private DataSource.Factory mediaDataSourceFactory;
    StepDescriptionFragment stepDescriptionFragment;
    IngredientsFragment ingredientsFrag;
    private Long currentMediaPlayerPosition;
    private String currentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        //check if it is the Two Fragment layout for Large screens
        if (findViewById(R.id.recipeStepLayout) != null) {
            //the layout for large screens
            mTwoPane = true;
            videoView = findViewById(R.id.video_view);
            instructionTXT = findViewById(R.id.InstructionTXT);
            imageView = findViewById(R.id.imageView);
        } else {
            mTwoPane = false;
        }

        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(this,
                com.google.android.exoplayer2.util.Util.getUserAgent(this, getString(R.string.bakeNow)),
                (TransferListener<? super DataSource>) bandwidthMeter);


        //display if video is not available
        snackbar = Snackbar
                .make(findViewById(R.id.recipeDetailMainLayout), R.string.NoVideo, Snackbar.LENGTH_LONG);


        if (savedInstanceState == null) {
            position = getIntent().getExtras().getInt(getString(R.string.position));
            recipes = MainActivity.recipes;
            playWhenReady = true;
        } else {
            recipes = savedInstanceState.getParcelableArrayList(getString(R.string.list));
            position = savedInstanceState.getInt(getString(R.string.position));
            playWhenReady = savedInstanceState.getBoolean(getString(R.string.playWhenReady));
        }
        ingredientsLBL.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_assignment_black_24dp, 0, 0, 0);
        stepsLBL.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_done_all_black_24dp, 0, 0, 0);


        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            ToolBarTitle.setText(recipes.get(position).getName());
        }


        //Begin fragment transaction if savedInstance state is null
        if (savedInstanceState == null) {
            ingredientsFrag = new IngredientsFragment();
            Bundle b = new Bundle();
            b.putParcelableArrayList(getString(R.string.list), (ArrayList<? extends Parcelable>) recipes);
            b.putInt(getString(R.string.position), position);
            ingredientsFrag.setArguments(b);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.FRGingredients, ingredientsFrag)
                    .commit();

            stepDescriptionFragment = new StepDescriptionFragment();
            Bundle d = new Bundle();
            d.putParcelableArrayList(getString(R.string.list), (ArrayList<? extends Parcelable>) recipes);
            d.putInt(getString(R.string.position), position);
            stepDescriptionFragment.setArguments(d);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.FRGdescription, stepDescriptionFragment)
                    .commit();
        }

        //called once on first run
        if (Util.ObjectisNull(currentUrl) && Util.firstRun == true && mTwoPane && Util.ObjectisNull(savedInstanceState)) {
            currentMediaPlayerPosition = 0L;
            Util.firstRun = false;//set to false so that onStepSelected isn't called again if activity is recreated
            onStepSelected(0, recipes.get(position).getSteps());
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (Util.ObjectisNotNull(outState) && mTwoPane) {
            outState.putParcelableArrayList(getString(R.string.list), (ArrayList<? extends Parcelable>) recipes);
            outState.putInt(getString(R.string.position), position);
            outState.putString(getString(R.string.currentUrl), currentUrl);
            outState.putString(getString(R.string.descriptionKey), description);
            getSupportFragmentManager().putFragment(outState, getString(R.string.stepDescription), stepDescriptionFragment);
            getSupportFragmentManager().putFragment(outState, getString(R.string.ingredientsFragm), ingredientsFrag);
            if (Util.ObjectisNotNull(mExoPlayer)) {
                currentMediaPlayerPosition = mExoPlayer.getCurrentPosition();
                outState.putLong(getString(R.string.currentMediaPosition), currentMediaPlayerPosition);
                outState.putBoolean(getString(R.string.playWhenReady), playWhenReady);
            }
        } else if (Util.ObjectisNotNull(outState) && !mTwoPane) {
            outState.putParcelableArrayList(getString(R.string.list), (ArrayList<? extends Parcelable>) recipes);
            outState.putInt(getString(R.string.position), position);
            getSupportFragmentManager().putFragment(outState, getString(R.string.stepDescription), stepDescriptionFragment);
            getSupportFragmentManager().putFragment(outState, getString(R.string.ingredientsFragm), ingredientsFrag);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            recipes = savedInstanceState.getParcelableArrayList(getString(R.string.list));
            position = savedInstanceState.getInt(getString(R.string.position));
            stepDescriptionFragment = (StepDescriptionFragment)
                    getSupportFragmentManager().getFragment(savedInstanceState, getString(R.string.stepDescription));
            ingredientsFrag = (IngredientsFragment)
                    getSupportFragmentManager().getFragment(savedInstanceState, getString(R.string.ingredientsFragm));
            if (mTwoPane) {
                currentMediaPlayerPosition = savedInstanceState.getLong(getString(R.string.currentMediaPosition));
                currentUrl = savedInstanceState.getString(getString(R.string.currentUrl));
                playWhenReady = savedInstanceState.getBoolean(getString(R.string.playWhenReady));
                description = savedInstanceState.getString(getString(R.string.descriptionKey));
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mTwoPane) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().hide();
                }
            } else {
                //To show the action bar
                if (getSupportActionBar() != null) {
                    getSupportActionBar().show();
                }
            }
        }
    }

    @Override
    public void onStepSelected(int stepPosition, List<Steps> steps) {
        Bundle s = new Bundle();
        if (mTwoPane) {
            description = steps.get(stepPosition).getDescription();
            String videoURL = steps.get(stepPosition).getVideoURL();
            String thumbnailURL = steps.get(stepPosition).getThumbnailURL();

            if (Util.StringNotEmpty(description)) {
                instructionTXT.setText(description);
            }

            if (Util.StringNotEmpty(videoURL)) {
                release();

                //play next video
                playWhenReady = true;

                videoView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                setupVideoView(videoURL);
            } else {
                release();
                snackbar.show();
                videoView.setVisibility(View.INVISIBLE);
                if (Util.StringNotEmpty(thumbnailURL))
                    Picasso.with(this).load(thumbnailURL).placeholder(R.drawable.chef).into(imageView);
            }

        } else {
            //If it is not a Tablet then open the RecipeStepDetail activity
            s.putParcelableArrayList(getString(R.string.step), (ArrayList<? extends Parcelable>) steps);
            s.putInt(getString(R.string.stepPosition), stepPosition);
            startActivity(new Intent(this, RecipeStepDetail.class).putExtras(s));
        }


    }

    private void setupVideoView(String url) {

        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            currentUrl = url;
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            videoView.setPlayer(mExoPlayer);
            // Prepare the MediaSource.
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            // Prepare the MediaSource. :)
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(url),
                    mediaDataSourceFactory, extractorsFactory, null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(playWhenReady);

        }


    }

    private void setupVideoView() {

        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            videoView.setPlayer(mExoPlayer);
            // Prepare the MediaSource.
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            // Prepare the MediaSource
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(currentUrl),
                    mediaDataSourceFactory, extractorsFactory, null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(playWhenReady);
            //after resuming media player, seek to the latest position
            mExoPlayer.seekTo(currentMediaPlayerPosition);

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        //If it is in tablet mode and there was a Current video being played
        if (mTwoPane) {
            if (Util.StringNotEmpty(currentUrl)) {
                setupVideoView();
                instructionTXT.setText(description);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTwoPane) {
            if (mExoPlayer != null) {
                playWhenReady = mExoPlayer.getPlayWhenReady();
                mExoPlayer.setPlayWhenReady(false);
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mTwoPane) {
            //release resource, if Tablet
            release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTwoPane) {
            //reset variable firstRun for Tablet mode
            Util.firstRun = true;
        }
    }

    public void release() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }


}
