package ru.yandex.burgers.model;

import java.util.List;

public class Order {
    private final List<String> ingredients;

    public List<String> getIngredients() {
        return ingredients;
    }

    public Order(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void addIngredient(String ingredient){
        ingredients.add(ingredient);
    }

    @Override
    public String toString() {
        return "Order{" +
                "ingredients=" + ingredients +
                '}';
    }
}
