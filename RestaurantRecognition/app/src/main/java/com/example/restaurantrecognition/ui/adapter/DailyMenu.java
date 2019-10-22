package com.example.restaurantrecognition.ui.adapter;

import java.util.List;

public class DailyMenu {

    String daily_id;
    String name;
    String start_date;
    String end_date;
    List<Food> dishes;

    public DailyMenu(String daily_id, String name, String start_date,
                     String end_date, List<Food> dishes) {

        this.daily_id = daily_id;
        this.name = name;
        this.start_date = start_date;
        this.end_date = end_date;
        this.dishes = dishes;
    }

    public String getDaily_id() {
        return daily_id;
    }

    public String getName() {
        return name;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public List<Food> getDishes() {
        return dishes;
    }
}
