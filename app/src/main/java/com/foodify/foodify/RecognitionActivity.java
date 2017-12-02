package com.foodify.foodify;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.internal.ForegroundLinearLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecognitionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // When the activity is first started, determine whether to show the camera fragment or the text entry/ingredients search fragment
        // look at the intent launching this activity

        // Show the appropriate fragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment nextFragment;
        // for now, hardcode to camera fragment for testing
        nextFragment = new CameraFragment();
        fragmentTransaction.add(R.id.vMainFragment, nextFragment, null);
        fragmentTransaction.commit();
    }

    public void interpretPhoto(File photo) {
        toggleAnalyzingUI(true);
        // The camera fragment will call this function when a picture is taken
        // Update UI with "ANALYZING" state
        // Contact class to get composition of photo
        List<WeightedIngredient> myIngredients = null;
        // Once composition of photo is returned call findResults() with the list
        findResults(myIngredients);
    }

    public void interpretList(List<String> enteredIngredients) {
        toggleAnalyzingUI(true);
        // The text entry/search fragment will call this function when the user finishes entering an ingredients list
        List<WeightedIngredient> myIngredients = null;
        // Create a new List<WeightedIngredient> with all ingredients having a weight of 1
        // Call findResults() with the list
        findResults(myIngredients);
    }

    private void toggleAnalyzingUI(boolean analyzing) {
        // Update UI with "ANALYZING" state
        // if analyzing = true then hide banner
    }

    public void findResults(List<WeightedIngredient> myIngredients) {
        // take myIngredients and feed them one by one to the matching class
        // somehow choose the best suggested pairing
        DrinkPairer pairer = new DrinkPairer(foodJSONFile, drinkJSONFile);
        HashMap<String, Float> counter = new HashMap<>();
        for (WeightedIngredient ingredient : myIngredients) {
            for (Beverage drink : pairer.getDrink(ingredient.name())) {
                String name = drink.getName();
                float count = counter.get(name) != null ? counter.get(name) : 0;
                counter.put(name, count + 1 * ingredient.weight());
            }
        }
        List<Map.Entry<String, Float>> drinks = new ArrayList<>(counter.entrySet());
        Collections.sort(drinks, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
        List<Beverage> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            result.add(pairer.getBeverage(drinks.get(i).getKey()));
        }
    }

}
