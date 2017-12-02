package com.foodify.foodify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Collection;
import java.util.ArrayList;

import static com.foodify.foodify.DrinkPairer.PAIRING_NAME;

/**
 * Created by Joey Sun on 2017-12-02.
 *
 * This class allows people to add a drink to some food.
 */

public class JSONchanger {
    static final String PAIRING_NAME = "Pairings";

    private final JSONObject foodStuff;
    private final JSONObject drinkStuff;

    // constructor for JSONchanger
    public JSONchanger(String foodJSONFile, String drinkJSONFile) throws NullPointerException{
        try{
            this.foodStuff = new JSONObject(foodJSONFile);
            this.drinkStuff = new JSONObject(drinkJSONFile);
        }
        catch (JSONException e) {
            System.err.println(e.getStackTrace());
            throw new NullPointerException("Something failed while reading JSON.");
        }

    }

    // adds a drink to a food pairing if it exists in the JSON file
    // if it doesn't exist, adds the drink to the drink file, and the
    // food to the food file
    public void addConceptDrinkPairing(String foodName, String drinkName){
        try {
            JSONObject food = getFood(foodName);
            JSONArray drinkPairings = food.getJSONArray(PAIRING_NAME);

            boolean hasDrink = false; // notes if the food already has the drink

            // checks to see if drink is already paired with food
            String[] drinkPairs = new String[drinkPairings.length()];
            for(int i=0; i<drinkPairings.length(); i++){
                if (drinkPairings.get(i).equals(drinkName)){
                    hasDrink = true;
                }
            }

            // if the food does not have this drink paired with it
            if(!hasDrink){
                // add drink to the pairing
                drinkPairings.put(drinkName);
            }
        }
        catch (JSONException e){
            System.err.println(e.getStackTrace());
        }

    }

    /**
     * returns a JSONobject that has the given food.
     * @return JSONobject that has the given food.
     */
    private JSONObject getFood(String foodName){
        // checks to see if food exists
        try {
            JSONObject food = foodStuff.getJSONObject(foodName);
            return food;
        }
        catch (JSONException e){ // if it doesn't, make one
            Collection<JSONObject> items = new ArrayList<JSONObject>();

            JSONObject item1 = new JSONObject();

            try{ // tries adding creating a new food item, with no pairings
                item1.put(PAIRING_NAME, new JSONArray());
                items.add(item1);
                foodStuff.put(foodName, item1);
            }
            catch (JSONException ee){}

            return item1;
        }
    }

    /**
     * returns a JSONobject that has the given drink.
     * @return JSONobject that has its key as the food name.
     */
    /*private JSONObject getDrink(String drinkName){
        // checks to see if food exists
        try {
            JSONObject food = foodStuff.getJSONObject(foodName);
            return food;
        }
        catch (JSONException e){ // if it doesn't, make one
            Collection<JSONObject> items = new ArrayList<JSONObject>();

            JSONObject item1 = new JSONObject();

            try{ // tries adding creating a new food item, with no pairings
                item1.put(PAIRING_NAME, new JSONArray());
                items.add(item1);
                foodStuff.put(foodName, item1);
            }
            catch (JSONException ee){}

            return item1;
        }
    }*/
}
