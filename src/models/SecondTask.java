package models;

import java.io.Serializable;
import java.util.List;

//D=B*MZ+D*MX*a
public class SecondTask implements Serializable {
    private Float a;
    private List<Float> B;
    private List<Float> D;
    private List<List<Float>> MZ;
    private List<List<Float>> MX;

    public Float getA() {
        return a;
    }

    public List<Float> getB() {
        return B;
    }

    public List<Float> getD() {
        return D;
    }

    public List<List<Float>> getMZ() {
        return MZ;
    }

    public List<List<Float>> getMX() {
        return MX;
    }

    public SecondTask(Float a, List<Float> b, List<Float> d, List<List<Float>> MZ, List<List<Float>> MX) {
        this.a = a;
        B = b;
        D = d;
        this.MZ = MZ;
        this.MX = MX;
    }
}
