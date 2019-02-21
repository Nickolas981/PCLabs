package models;

import java.io.Serializable;
import java.util.List;


//A = B*MC+D*MM*a-B*MM
public class FirstTask implements Serializable {
    private List<Float> B;
    private List<Float> D;
    private List<List<Float>> MC;
    private List<List<Float>> MM;
    private Float a;

    public Float getA() {
        return a;
    }

    public FirstTask(List<Float> b, List<Float> d, List<List<Float>> MC, List<List<Float>> MM, Float a) {
        B = b;
        D = d;
        this.MC = MC;
        this.MM = MM;
        this.a = a;
    }

    public List<Float> getB() {
        return B;
    }

    public List<Float> getD() {
        return D;
    }

    public List<List<Float>> getMC() {
        return MC;
    }

    public List<List<Float>> getMM() {
        return MM;
    }
}
