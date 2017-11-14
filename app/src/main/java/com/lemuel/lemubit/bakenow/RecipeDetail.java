package com.lemuel.lemubit.bakenow;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.lemuel.lemubit.bakenow.Fragments.IngredientsFragment;
import com.lemuel.lemubit.bakenow.Fragments.StepDescriptionFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetail extends AppCompatActivity {
    int position;
    @BindView(R.id.ingredientsLBL)
    TextView ingredientsLBL;
    @BindView(R.id.StepsLBL)
    TextView stepsLBL;
@BindView(R.id.ToolBar_Recipe_Title)
TextView ToolBarTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);
        position = getIntent().getExtras().getInt("position");
        ingredientsLBL.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_assignment_black_24dp, 0, 0, 0);
        stepsLBL.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_done_all_black_24dp, 0, 0, 0);
        ToolBarTitle.setText(MainActivity.recipes.get(position).getName());
        IngredientsFragment ingredientsFrag = new IngredientsFragment();
        Bundle b = new Bundle();
        b.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) MainActivity.recipes);
        b.putInt("position", position);
        ingredientsFrag.setArguments(b);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.FRGingredients, ingredientsFrag)
                .commit();

        StepDescriptionFragment stepDescriptionFragment = new StepDescriptionFragment();
        Bundle d = new Bundle();
        d.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) MainActivity.recipes);
        d.putInt("position", position);
        stepDescriptionFragment.setArguments(d);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.FRGdescription, stepDescriptionFragment)
                .commit();
    }
}
