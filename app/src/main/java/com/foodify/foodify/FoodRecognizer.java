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

    public class FoodRecognizerResult {
        private String name;
        private float weight;

        public FoodRecognizerResult(String name, float weight) {
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

    public FoodRecognizer() {
        this.client = new ClarifaiBuilder("c9d4e612a5a2455fbfe92e201335d352").buildSync();
    }

    private List<FoodRecognizerResult> recognize(File imageFile) {
        List<FoodRecognizerResult> resultList = new ArrayList<FoodRecognizerResult>();
        for (ClarifaiOutput<Concept> output : client.getDefaultModels().foodModel().predict()
                .withInputs(ClarifaiInput.forImage(imageFile))
                .executeSync()
                .get()) {
            for (Concept result : output.data()) {
                resultList.add(new FoodRecognizerResult(result.name(), result.value()));
            }
        }
        return resultList;
    }
}