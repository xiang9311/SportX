package com.xiang.model;

import java.io.Serializable;

/**
 * Created by 祥祥 on 2016/5/14.
 */
public class ChoosedGym implements Serializable {

    private int gymId;
    private String gymName;

    public ChoosedGym(int gymId, String gymName) {
        this.gymId = gymId;
        this.gymName = gymName;
    }

    public int getGymId() {
        return gymId;
    }

    public void setGymId(int gymId) {
        this.gymId = gymId;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    public String getGymName() {
        return gymName;
    }
}
