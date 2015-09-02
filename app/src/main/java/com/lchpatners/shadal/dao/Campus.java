package com.lchpatners.shadal.dao;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by YoungKim on 2015. 8. 20..
 */
public class Campus extends RealmObject {
    @PrimaryKey
    private int id;

    @SerializedName("name_kor")
    private String name;

    private String name_kor_short;
    private String email;
    private String administrator;

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

    public String getName_kor_short() {
        return name_kor_short;
    }

    public void setName_kor_short(String name_kor_short) {
        this.name_kor_short = name_kor_short;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdministrator() {
        return administrator;
    }

    public void setAdministrator(String administrator) {
        this.administrator = administrator;
    }

}
