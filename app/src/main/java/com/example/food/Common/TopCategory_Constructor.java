package com.example.food.Common;

import java.util.Map;

public class TopCategory_Constructor {

    String image;
    String name;

    public TopCategory_Constructor(){

    }

    public TopCategory_Constructor(String image, String name) {
        this.name = name;
        this.image = image;
    }

    public static Map.Entry push() {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}
