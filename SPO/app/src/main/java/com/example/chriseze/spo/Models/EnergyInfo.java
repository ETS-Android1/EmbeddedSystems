package com.example.chriseze.spo.Models;

/**
 * Created by CHRIS EZE on 6/30/2018.
 */

public class EnergyInfo {
    private String energy_val, timestamp;

    public EnergyInfo(String energy_val, String timestamp){
        this.energy_val = energy_val;
        this.timestamp = timestamp;
    }

    public String getEnergy_val(){
        return energy_val;
    }
    public String getTimestamp(){
        return timestamp;
    }
}
