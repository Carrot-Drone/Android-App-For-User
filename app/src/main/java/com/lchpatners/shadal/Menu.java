package com.lchpatners.shadal;

/**
 * Model class representing a menu data.
 */
public class Menu {

    /**
     * An instance-unique integer value.
     */
    private int id;
    /**
     * The price value in Won.
     */
    private int price;
    /**
     * The section this belongs to.
     */
    private String section;
    /**
     * The item name.
     */
    private String item;
    /**
     * Foreign key pointing to the {@link com.lchpatners.shadal.Restaurant
     * Restaurant} this belongs to.
     * @see com.lchpatners.shadal.Restaurant#id
     */

    private String description;

    private long restaurantId;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
