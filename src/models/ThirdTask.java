package models;

import java.io.Serializable;
import java.util.List;

//MG=MB*MK+MC*(MX*MT+MM)
public class ThirdTask implements Serializable {
    private List<List<Float>> MB;
    private List<List<Float>> MK;
    private List<List<Float>> MC;
    private List<List<Float>> MX;
    private List<List<Float>> MT;
    private List<List<Float>> MM;

    public ThirdTask(List<List<Float>> MB, List<List<Float>> MK, List<List<Float>> MC, List<List<Float>> MX, List<List<Float>> MT, List<List<Float>> MM) {
        this.MB = MB;
        this.MK = MK;
        this.MC = MC;
        this.MX = MX;
        this.MT = MT;
        this.MM = MM;
    }

    public List<List<Float>> getMB() {
        return MB;
    }

    public List<List<Float>> getMK() {
        return MK;
    }

    public List<List<Float>> getMC() {
        return MC;
    }

    public List<List<Float>> getMX() {
        return MX;
    }

    public List<List<Float>> getMT() {
        return MT;
    }

    public List<List<Float>> getMM() {
        return MM;
    }
}
