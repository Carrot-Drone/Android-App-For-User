package com.lchpatners.shadal.restaurant;

import com.google.gson.annotations.SerializedName;
import com.lchpatners.shadal.restaurant.flyer.Flyer;
import com.lchpatners.shadal.restaurant.menu.RestaurantMenu;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by YoungKim on 2015. 8. 23..
 */
public class Restaurant extends RealmObject {
    @PrimaryKey
    private int id;

    private String name;
    private String phone_number;
    private boolean is_new;
    private boolean has_coupon;
    private String notice;
    private boolean has_flyer;
    private float opening_hours;
    private float closing_hours;
    private int minimum_price;
    private float retention;
    private int number_of_my_calls;
    private int total_number_of_calls;
    private int total_number_of_goods;
    private int total_number_of_bads;
    private int my_preference;

    @SerializedName("flyers_url")
    private RealmList<Flyer> flyers;

    private RealmList<RestaurantMenu> menus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean is_new() {
        return is_new;
    }

    public void setIs_new(boolean is_new) {
        this.is_new = is_new;
    }

    public boolean isHas_coupon() {
        return has_coupon;
    }

    public void setHas_coupon(boolean has_coupon) {
        this.has_coupon = has_coupon;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public boolean isHas_flyer() {
        return has_flyer;
    }

    public void setHas_flyer(boolean has_flyer) {
        this.has_flyer = has_flyer;
    }

    public float getOpening_hours() {
        return opening_hours;
    }

    public void setOpening_hours(float opening_hours) {
        this.opening_hours = opening_hours;
    }

    public float getClosing_hours() {
        return closing_hours;
    }

    public void setClosing_hours(float closing_hours) {
        this.closing_hours = closing_hours;
    }

    public int getMinimum_price() {
        return minimum_price;
    }

    public void setMinimum_price(int minimum_price) {
        this.minimum_price = minimum_price;
    }

    public float getRetention() {
        return retention;
    }

    public void setRetention(float retention) {
        this.retention = retention;
    }

    public int getNumber_of_my_calls() {
        return number_of_my_calls;
    }

    public void setNumber_of_my_calls(int number_of_my_calls) {
        this.number_of_my_calls = number_of_my_calls;
    }

    public int getTotal_number_of_calls() {
        return total_number_of_calls;
    }

    public void setTotal_number_of_calls(int total_number_of_calls) {
        this.total_number_of_calls = total_number_of_calls;
    }

    public int getTotal_number_of_goods() {
        return total_number_of_goods;
    }

    public void setTotal_number_of_goods(int total_number_of_goods) {
        this.total_number_of_goods = total_number_of_goods;
    }

    public int getTotal_number_of_bads() {
        return total_number_of_bads;
    }

    public void setTotal_number_of_bads(int total_number_of_bads) {
        this.total_number_of_bads = total_number_of_bads;
    }

    public int getMy_preference() {
        return my_preference;
    }

    public void setMy_preference(int my_preference) {
        this.my_preference = my_preference;
    }

    public RealmList<Flyer> getFlyers() {
        return flyers;
    }

    public void setFlyers(RealmList<Flyer> flyers) {
        this.flyers = flyers;
    }

    public RealmList<RestaurantMenu> getMenus() {
        return menus;
    }

    public void setMenus(RealmList<RestaurantMenu> menus) {
        this.menus = menus;
    }
}
