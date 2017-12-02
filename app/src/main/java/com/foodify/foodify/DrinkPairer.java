package com.foodify.foodify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;

/**
 * Created by Joey Sun on 2017-12-02.
 *
 * Used to get drink choice given a food item.
 */
public final class DrinkPairer {
    static final String FOOD_JSON_FILE = "food.json";
    static final String DRINK_JSON_FILE = "drinks.json";
    static final String PAIRING_NAME = "Pairings";

    private final JSONArray foodStuff;
    private final JSONArray drinkStuff;

    // constructor for drinkPairer
    public DrinkPairer(String foodJSONFile, String drinkJSONFile) throws NullPointerException{
        JSONArray tempFoodStuff;
        JSONArray tempDrinkStuff;

        try{
            tempFoodStuff = new JSONArray(foodJSONFile);
            tempDrinkStuff = new JSONArray(drinkJSONFile);
        }
        catch (JSONException e) {
            System.err.println(e.getStackTrace());
            throw new NullPointerException("Something failed while reading JSON.");
        }

        this.foodStuff = tempFoodStuff;
        this.drinkStuff = tempDrinkStuff;
    }

    // gets drink info for a given food name
    public Beverage getDrink(String foodName){
        String drinkID = drinkIDGivenFood(foodName);
        Beverage drinkInfo = drinkInfoGivenDrinkID(drinkID);

        return drinkInfo;
    }

    // gets drink ID given food object
    private String drinkIDGivenFood(String foodName){
        try {
            JSONObject drinkPairing;

            // iterates over the array of JSOn objects because someone didn't
            // consider using multiple attributes
            for (int i = 0; i < foodStuff.length(); i++) {
                if( foodName.equals(foodStuff.getJSONObject(i).get("food")) ) {
                    drinkPairing = foodStuff.getJSONObject(i);
                    return (String) drinkPairing.get("pairing");
                }
            }
        }
        catch (JSONException e){
            System.err.println(e.getStackTrace().toString());
        }
        return null;
    }

    // for multiple drink ID
    private Beverage [] drinkInfoGivenDrinkID(String [] drinkID) {
        Beverage[] beverages = new Beverage[drinkID.length];

        for (int i = 0; i < drinkID.length; i++) {
            beverages[i] = drinkInfoGivenDrinkID(drinkID[i]);
        }

        return beverages;
    }

    /* for one drink name */
    private Beverage drinkInfoGivenDrinkID(String drinkName){
        try {
            String drinkInfo;

            // iterates over the array of JSOn objects because someone didn't
            // consider using multiple attributes
            for (int i = 0; i < drinkStuff.length(); i++) {
                if( drinkName.equals(foodStuff.getJSONObject(i).get("drink")) ) {
                    drinkInfo = (String) foodStuff.getJSONObject(i).get("drinklink");
                    return new Beverage(drinkName, drinkInfo);
                }
            }
        }
        catch (JSONException e){
            System.err.println(e.getStackTrace());
        }

        return null;
    }

    /**
     * Outputs a drink object given a drink name.
     * Iterates over all objects in JSON, because I do
     * not know of more effective method of doing this.
     */
    public Beverage drinkInfoGivenDrinkName(String drinkName){
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
            System.err.println(e.getStackTrace().toString());
        }
        // returns null otherwise
        return null;
    }
}