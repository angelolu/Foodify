package com.foodify.foodify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joey Sun on 2017-12-02.
 *
 * Used to get drink choice given a food item.
 */
public final class DrinkPairer {
    static final String FOOD_JSON_FILE = "food.json";
    static final String DRINK_JSON_FILE = "drink.json";
    static final String PAIRING_NAME = "Pairings";

    private final JSONObject foodStuff;
    private final JSONObject drinkStuff;

    // constructor for drinkPairer
    public DrinkPairer(String foodJSONFile, String drinkJSONFile) throws NullPointerException{
        try{
            this.foodStuff = new JSONObject(foodJSONFile);
            this.drinkStuff = new JSONObject(drinkJSONFile);
        }
        catch (JSONException e) {
            System.err.println(e.getStackTrace());
            throw new NullPointerException("Something failed while reading JSON.");
        }

    }

    // gets drink info for a given food name
    public Beverage [] getDrink(String foodName){
        String [] drinkIDs = drinkIDGivenFood(foodName);
        Beverage [] drinkInfos = drinkInfoGivenDrinkID(drinkIDs);

        return drinkInfos;
    }

    // gets drink IDs given food object
    private String [] drinkIDGivenFood(String foodName){
        try {
            JSONObject food = foodStuff.getJSONObject(foodName);
            JSONArray drinkPairings = food.getJSONArray(PAIRING_NAME);

            String[] drinkPairs = new String[drinkPairings.length()];
            for (int i = 0; i < drinkPairs.length; i++) {
                drinkPairs[i] = (String) drinkPairings.get(i);
            }

            return drinkPairs;
        }
        catch (JSONException e){
            System.err.println(e.getStackTrace());
            return null;
        }

    }

    // for multiple drink ID
    private Beverage [] drinkInfoGivenDrinkID(String [] drinkID) {
        Beverage[] beverages = new Beverage[drinkID.length];

        for (int i = 0; i < drinkID.length; i++) {
            beverages[i] = drinkInfoGivenDrinkID(drinkID[i]);
        }

        return beverages;
    }

    /* for one drink ID */
    private Beverage drinkInfoGivenDrinkID(String drinkID){
        try {
            JSONObject drink = drinkStuff.getJSONObject(drinkID);
            String drinkName = (String) drink.get("name");
            String image = (String) drink.get("URL");

            return new Beverage(drinkName, image);
        }
        catch (JSONException e){
            System.err.println(e.getStackTrace());
            return null;
        }

    }
}