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

import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lemuel.lemubit.bakenow.Adapter.RecipeAdapter;
import com.lemuel.lemubit.bakenow.Models.Recipe;
import com.lemuel.lemubit.bakenow.RetrofitRequestInterface.RequestInterface;
import com.lemuel.lemubit.bakenow.Utils.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Loads information from network request into recycler view using Retrofit
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recipe_recycler_view)
    RecyclerView recipeRecyclerV;
    @BindView(R.id.recipe_progress)
    ProgressBar recipeProgress;

    Parcelable mListState;
    RecyclerView.LayoutManager mLayoutManager;
    RecipeAdapter recipeAdapter;
    public static List<Recipe> recipes;
    Bundle savedState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedState = savedInstanceState;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Check if there is internet connection before requesting
        if (Util.isConnected(MainActivity.this) && recipes == null) {
            loadRecipe(savedInstanceState);
        } else if (recipes != null) {
            //to prevent another network request
            // since the recipes downloaded is still available in the static variable
            recipeAdapter = new RecipeAdapter(MainActivity.this, recipes);
            recipeRecyclerV.setAdapter(recipeAdapter);
        } else {
            Toast.makeText(MainActivity.this, R.string.connectionCheck, Toast.LENGTH_SHORT).show();
        }
        loadAppropriateLayout();

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mListState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(getString(R.string.listkey), mListState);
        outState.putParcelableArrayList(getString(R.string.list), (ArrayList<? extends Parcelable>) recipes);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(getString(R.string.listkey));
            recipes = savedInstanceState.getParcelableArrayList(getString(R.string.list));
        }
    }

    private void loadRecipe(Bundle savedInstanceState) {

        if (Util.ObjectisNull(savedInstanceState)) {
            recipeProgress.setVisibility(View.VISIBLE);
        /*build Retrofit using Retrofit.Builder() and convert JSON data
        into accessible data object using GsonConverterFactory*/
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RequestInterface request = retrofit.create(RequestInterface.class);
            Call<List<Recipe>> call = request.getRecipes();//response will be a JSON array not a JSON object
            call.enqueue(new Callback<List<Recipe>>() {
                @Override
                public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                    recipeProgress.setVisibility(View.INVISIBLE);
                    recipes = response.body();
                    recipeAdapter = new RecipeAdapter(MainActivity.this, recipes);
                    recipeRecyclerV.setAdapter(recipeAdapter);

                }

                @Override
                public void onFailure(Call<List<Recipe>> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    recipeProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, R.string.connectionCheck, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            //Load from savedInstanceState if state had been saved :)
            recipes = savedInstanceState.getParcelableArrayList(getString(R.string.list));
            recipeAdapter = new RecipeAdapter(MainActivity.this, recipes);
            recipeRecyclerV.setAdapter(recipeAdapter);
        }
    }


    private void loadAppropriateLayout() {

        //GET ORIENTATION
        int orientation = MainActivity.this.getResources().getConfiguration().orientation;

        //IF SCREEN IS LARGE, SHOW MORE RECIPES PER ROW
        if (Util.isLargeScreen(MainActivity.this)) {
            //LARGE SCREEN
            if (Util.isPortraitMode(orientation)) {
                recipeRecyclerV.setLayoutManager(new GridLayoutManager(MainActivity.this, getResources().getInteger(R.integer.gridSpan)));
                mLayoutManager = recipeRecyclerV.getLayoutManager();
            } else {
                recipeRecyclerV.setLayoutManager(new GridLayoutManager(MainActivity.this, getResources().getInteger(R.integer.gridSpan)));
                mLayoutManager = recipeRecyclerV.getLayoutManager();
            }
        } else if (Util.isMediumScreen(MainActivity.this)) {
            //NOT SO LARGE SCREEN :)
            if (Util.isPortraitMode(orientation)) {
                recipeRecyclerV.setLayoutManager(new GridLayoutManager(MainActivity.this, getResources().getInteger(R.integer.gridSpan)));
                mLayoutManager = recipeRecyclerV.getLayoutManager();
            } else {
                recipeRecyclerV.setLayoutManager(new GridLayoutManager(MainActivity.this, getResources().getInteger(R.integer.gridSpan)));
                mLayoutManager = recipeRecyclerV.getLayoutManager();
            }
        } else {
            //SMALLER SCREENS
            if (Util.isPortraitMode(orientation)) {
                recipeRecyclerV.setLayoutManager(new GridLayoutManager(MainActivity.this, getResources().getInteger(R.integer.gridSpan)));
                mLayoutManager = recipeRecyclerV.getLayoutManager();
            } else {
                recipeRecyclerV.setLayoutManager(new GridLayoutManager(MainActivity.this, getResources().getInteger(R.integer.gridSpan)));
                mLayoutManager = recipeRecyclerV.getLayoutManager();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mListState != null) {
            loadAppropriateLayout();
            mLayoutManager.onRestoreInstanceState(mListState);
        }
    }

    //used to get 'recipes' during testing
    @VisibleForTesting
    public List<Recipe> testrecipes() {
        return recipes;
    }
}
