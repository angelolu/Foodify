package com.foodify.foodify;

/**
 * Created by Joey Sun on 2017-12-02.
 *
 * Beverage class for beverage info.
 */

class Beverage {
    private String drinkName;
    private String image;

    Beverage(String drinkName, String image){
        this.drinkName = drinkName;
        this.image = image;
    }

    String getName(){
        return drinkName;
    }

    String getImageURL(){
        return image;
    }
}
