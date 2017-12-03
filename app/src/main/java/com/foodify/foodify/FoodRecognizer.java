package com.foodify.foodify;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public class FoodRecognizer {

    private ClarifaiClient client;

    public FoodRecognizer() {
        this.client = new ClarifaiBuilder("c9d4e612a5a2455fbfe92e201335d352").buildSync();
    }

    public List<WeightedIngredient> recognize(byte[] imageFile) {
        List<WeightedIngredient> resultList = new ArrayList<WeightedIngredient>();
        for (ClarifaiOutput<Concept> output : client.getDefaultModels().foodModel().predict()
                .withInputs(ClarifaiInput.forImage(imageFile))
                .executeSync()
                .get()) {
            for (Concept result : output.data()) {
                if (result.value() < 0.7) {
                    continue;
                }
                resultList.add(new WeightedIngredient(result.name(), result.value()));
            }
        }
        return resultList;
    }
}