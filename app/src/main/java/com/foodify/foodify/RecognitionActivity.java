package com.foodify.foodify;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecognitionActivity extends AppCompatActivity implements CameraFragment.OnPictureCaptureListener {

    RelativeLayout vAnalyzing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vAnalyzing = findViewById(R.id.vAnalyzing);
        //vAnalyzing.setVisibility(View.VISIBLE);
        // When the activity is first started, determine whether to show the camera fragment or the text entry/ingredients search fragment
        // look at the intent launching this activity

        // Show the appropriate fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment nextFragment;
        // for now, hardcode to camera fragment for testing
        nextFragment = new CameraFragment();
        fragmentTransaction.add(R.id.vMainFragment, nextFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void OnPictureCapture(byte[] photo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toggleAnalyzingUI(true);
            }
        });
        // The camera fragment will call this function when a picture is taken
        // Update UI with "ANALYZING" state
        // Contact class to get composition of photo
        FoodRecognizer myFood = new FoodRecognizer();
        List<WeightedIngredient> myIngredients = myFood.recognize(photo);
        Log.e("Output", "Most likely to be " + myIngredients.get(0).name());
        finish();
        // Once composition of photo is returned call findResults() with the list
        //findResults(myIngredients);
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
        if (analyzing) {
            vAnalyzing.setVisibility(View.VISIBLE);
        } else {
            vAnalyzing.setVisibility(View.GONE);
        }
        // Update UI with "ANALYZING" state
        // if analyzing = true then hide banner
    }

    public void findResults(List<WeightedIngredient> myIngredients) {
        // take myIngredients and feed them one by one to the matching class
        // somehow choose the best suggested pairing
        // Read the JSON Files
        String foodJSONFile = "";
        String drinkJSONFile = "";
        /*BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("food.json")));

            String mLine;
            while ((mLine = reader.readLine()) != null) {
                foodJSONFile = foodJSONFile + mLine;
            }
            Log.e("Food.json", foodJSONFile);
            reader = new BufferedReader(new InputStreamReader(getAssets().open("drinks.json")));

            while ((mLine = reader.readLine()) != null) {
                drinkJSONFile = drinkJSONFile + mLine;
            }
            Log.e("Drinks.json", drinkJSONFile);

        } catch (IOException e) {
            //log the exception
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }*/

        DrinkPairer pairer = new DrinkPairer(foodJSONFile, drinkJSONFile);
        HashMap<String, Float> counter = new HashMap<>();
        for (WeightedIngredient ingredient : myIngredients) {
            for (Beverage drink : pairer.getDrink(ingredient.name())) {
                if (drink == null) {
                    break;
                }
                String name = drink.getName();
                float count = counter.get(name) != null ? counter.get(name) : 0;
                counter.put(name, count + 1 * ingredient.weight());
            }
        }
        List<Map.Entry<String, Float>> drinks = new ArrayList<>(counter.entrySet());
        Collections.sort(drinks, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
        List<Beverage> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            //result.add(pairer.getBeverage(drinks.get(i).getKey()));
        }
    }
}
