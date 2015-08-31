package com.lchpatners.shadal.restaurant_suggestion;

/**
 * Created by eunhyekim on 2015. 8. 31..
 */
public class RestaurantSuggestion {
    String uuid;
    int campus_id;
    String name;
    String phone_number;
    String office_hours;
    int is_suggested_by_restaurant;
    String files[];

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getCampus_id() {
        return campus_id;
    }

    public void setCampus_id(int campus_id) {
        this.campus_id = campus_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getOffice_hours() {
        return office_hours;
    }

    public void setOffice_hours(String office_hours) {
        this.office_hours = office_hours;
    }

    public int getIs_suggested_by_restaurant() {
        return is_suggested_by_restaurant;
    }

    public void setIs_suggested_by_restaurant(int is_suggested_by_restaurant) {
        this.is_suggested_by_restaurant = is_suggested_by_restaurant;
    }

    public String[] getFiles() {
        return files;
    }

    public void setFiles(String[] files) {
        this.files = files;
    }
}
