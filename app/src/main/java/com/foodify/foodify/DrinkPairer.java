package com.foodify.foodify;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Joey Sun on 2017-12-02.
 *
 * Used to get drink choice given a food item.
 */
final class DrinkPairer {
    static final String FOOD_JSON_FILE = "food.json";
    static final String DRINK_JSON_FILE = "drinks.json";

    private final JSONArray foodStuff;
    private final JSONArray drinkStuff;

    // constructor for drinkPairer
    DrinkPairer(String foodJSONFile, String drinkJSONFile, Context c) throws NullPointerException {

        // Uses JSON file name passed to us if not empty/null
        // else uses default JSON file names
        String foodFile = (foodJSONFile != null && !foodJSONFile.isEmpty())
                ? foodJSONFile : FOOD_JSON_FILE;
        String drinkFile = (drinkJSONFile != null && !drinkJSONFile.isEmpty())
                ? drinkJSONFile : DRINK_JSON_FILE;


        // Read the JSON Files
        StringBuilder foodString = new StringBuilder();
        StringBuilder drinkString = new StringBuilder();
        BufferedReader reader = null;
        try {
            // opens a reader to read the food JSON file
            reader = new BufferedReader(new InputStreamReader(c.getAssets().open(foodJSONFile)));

            String mLine;
            while ((mLine = reader.readLine()) != null) {
                foodString = foodString.append(mLine);
            }

            reader = new BufferedReader(new InputStreamReader(c.getAssets().open(drinkJSONFile)));

            while ((mLine = reader.readLine()) != null) {
                drinkString = drinkString.append(mLine);
            }

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
        }

        JSONArray tempFoodStuff;
        JSONArray tempDrinkStuff;

        try{
            tempFoodStuff = new JSONArray(foodString);
            tempDrinkStuff = new JSONArray(drinkString);
        }
        catch (JSONException e) {
            e.printStackTrace();
            throw new NullPointerException("Something failed while parsing JSON.");
        }

        this.foodStuff = tempFoodStuff;
        this.drinkStuff = tempDrinkStuff;
    }

    // gets drink info for a given food name
    Beverage [] getDrink(String foodName){
        String drinkID = drinkIDGivenFood(foodName);

        // returns null if the food item does not exist
        if (drinkID == null) {
            return new Beverage [] {null};
        }
        else {
            Beverage drinkInfo = drinkInfoGivenDrinkID(drinkID);
            return new Beverage[]{drinkInfo};
        }

    }

    // gets drink ID given food object
    private String drinkIDGivenFood(String foodName){
        try {
            JSONObject drinkPairing;

            // iterates over the array of JSON objects, searching for a food
            for (int i = 0; i < foodStuff.length(); i++) {
                if( foodName.equals(foodStuff.getJSONObject(i).get("food")) ) {
                    drinkPairing = foodStuff.getJSONObject(i);
                    return (String) drinkPairing.get("pairing");
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        // returns null otherwise
        return null;
    }

    // gets beverage info for multiple drink ID
    private Beverage [] drinkInfoGivenDrinkID(String [] drinkID) {
        Beverage[] beverages = new Beverage[drinkID.length];

        for (int i = 0; i < drinkID.length; i++) {
            beverages[i] = drinkInfoGivenDrinkID(drinkID[i]);
        }

        return beverages;
    }

    // gets beverage info for one drink ID
    private Beverage drinkInfoGivenDrinkID(String drinkName){
        try {
            // iterates over the array of JSON objects, searching for a drink
            for (int i = 0; i < drinkStuff.length(); i++) {
                if( drinkName.equals(drinkStuff.getJSONObject(i).get("drink")) ) {
                    String drinkInfo = (String) drinkStuff.getJSONObject(i).get("drinklink");
                    String drink = (String) drinkStuff.getJSONObject(i).get("description");
                    return new Beverage(drink, drinkInfo);
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        // returns null otherwise
        return null;
    }

    /**
     * Outputs a drink object given a drink name.
     * Iterates over all objects in JSON, because I do
     * not know of a more effective method of doing this.
     */
    Beverage drinkInfoGivenDrinkName(String drinkName){
        try {
            JSONObject jsonDrink;
            Object drink;

            // iterates over all the drinks
            for(int i=0; i<drinkStuff.length(); i++) {
                if ((drink = drinkStuff.getJSONObject(i)) instanceof JSONObject) {
                    jsonDrink = (JSONObject) drink;
                    String otherDrinkName = (String) jsonDrink.get("name");

                    // returns the drink with the given name and image if we find it
                    if (drinkName.equals(otherDrinkName)) {
                        return new Beverage(drinkName, (String) jsonDrink.get("URL"));
                    }
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        // returns beverage with no image otherwise
        return new Beverage(drinkName, "");
    }
}