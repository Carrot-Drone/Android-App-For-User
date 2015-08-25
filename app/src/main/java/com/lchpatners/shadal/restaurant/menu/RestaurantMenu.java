package com.lchpatners.shadal.restaurant.menu;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by YoungKim on 2015. 8. 24..
 */
public class RestaurantMenu extends RealmObject {
    @PrimaryKey
    private int id;

    private String section;
    private String name;
    private String description;
    private int price;

    @SerializedName("submenus")
    private RealmList<RestaurantSubMenu> subMenus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public RealmList<RestaurantSubMenu> getSubMenus() {
        return subMenus;
    }

    public void setSubMenus(RealmList<RestaurantSubMenu> subMenus) {
        this.subMenus = subMenus;
    }
}
