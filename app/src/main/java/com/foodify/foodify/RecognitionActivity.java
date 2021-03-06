package com.foodify.foodify;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecognitionActivity extends AppCompatActivity implements CameraFragment.OnPictureCaptureListener {

    Fragment myResultFragment;
    ResultToFragment resultToFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myResultFragment = new ResultFragment();
        resultToFragment = (ResultToFragment) myResultFragment;
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
            public void run() {
                proceedToResults();
            }
        });
        // The camera fragment will call this function when a picture is taken
        // Update UI with "ANALYZING" state
        // Contact class to get composition of photo
        FoodRecognizer myFood = new FoodRecognizer();
        List<WeightedIngredient> myIngredients = myFood.recognize(photo);

        // Once composition of photo is returned call findResults() with the list
        setRecognition(myIngredients);
        findResults(myIngredients);
    }

    public void interpretList(List<String> enteredIngredients) {
        proceedToResults();
        // The text entry/search fragment will call this function when the user finishes entering an ingredients list
        List<WeightedIngredient> myIngredients = null;
        // Create a new List<WeightedIngredient> with all ingredients having a weight of 1
        // Call findResults() with the list
        findResults(myIngredients);
    }

    private void proceedToResults() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.vMainFragment, myResultFragment);
        fragmentTransaction.commit();
    }

    public void setRecognition(List<WeightedIngredient> myIngredients) {
        String result = "";
        for (WeightedIngredient element : myIngredients) {
            result = result + ((float) Math.round(element.weight() * 1000)) / 10.0 + "% " + element.name() + "\n";
        }
        final String print = result;
        runOnUiThread(new Runnable() {
            public void run() {
                resultToFragment.sendRecognition(print);
            }
        });
    }

    public void findResults(List<WeightedIngredient> myIngredients) {
        // take myIngredients and feed them one by one to the matching class
        // somehow choose the best suggested pairing
        // Read the JSON Files
        String foodJSONFile = "food.json";
        String drinkJSONFile = "drinks.json";
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

        DrinkPairer pairer = new DrinkPairer(foodJSONFile, drinkJSONFile, this);
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
        Log.e("Recommendation", drinks.size() + "");

        String primaryPair = "";
        String primaryPairURL = "";
        String secondPair = "";
        int numRecommendations = 3;
        if (drinks.size() < 3) numRecommendations = drinks.size();
        if (numRecommendations > 0) {
            for (int i = 0; i < numRecommendations; i++) {
                result.add(pairer.drinkInfoGivenDrinkName(drinks.get(i).getKey()));
            }
            primaryPair = result.get(0).getName();
            primaryPairURL = result.get(0).getImageURL();
            for (int i = 1; i < result.size(); i++) {
                secondPair = secondPair + pairer.drinkInfoGivenDrinkName(drinks.get(i).getKey()).getName() + "\n";
            }
            final String pp = primaryPair;
            final String ppurl = primaryPairURL;
            final String psp = secondPair;
            runOnUiThread(new Runnable() {
                public void run() {
                    resultToFragment.sendPairing(pp, ppurl, psp);
                }
            });
        } else {
            Log.e("Recommendation", "No recommendation");
            runOnUiThread(new Runnable() {
                public void run() {
                    resultToFragment.sendPairing("", "", "");
                }
            });
        }
    }

    public interface ResultToFragment {
        void sendRecognition(String data);

        void sendPairing(String primary, String url, String data);
    }

}
