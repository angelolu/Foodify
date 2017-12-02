package com.foodify.foodify;

/**
 * Created by Angelo on 12/2/2017.
 */

public class WeightedIngredient {
    private String name;
    private float weight;

    public WeightedIngredient(String name, float weight) {
        this.name = name;
        this.weight = weight;
    }

    public String name() {
        return name;
    }

    public float weight() {
        return weight;
    }
}
