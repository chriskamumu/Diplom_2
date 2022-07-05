package ru.yandex.burgers.model;

import lombok.Data;

import java.util.List;

@Data
public class Order {
    private final List<String> ingredients;

    public void addIngredient(String ingredient){
        ingredients.add(ingredient);
    }

}
