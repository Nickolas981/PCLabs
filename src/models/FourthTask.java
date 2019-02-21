package models;

import java.io.Serializable;
import java.util.List;

//MA = max(B-D)*MD*MT-MZ*(ME+MM)
public class FourthTask implements Serializable {
    private List<List<Float>> MT;
    private List<List<Float>> MZ;
    private List<List<Float>> ME;
    private List<List<Float>> MM;
    private List<List<Float>> MD;
    private List<Float> B;
    private List<Float> D;

    public List<List<Float>> getMD() {
        return MD;
    }

    public List<List<Float>> getMT() {
        return MT;
    }

    public List<List<Float>> getMZ() {
        return MZ;
    }

    public List<List<Float>> getME() {
        return ME;
    }

    public List<List<Float>> getMM() {
        return MM;
    }

    public List<Float> getB() {
        return B;
    }

    public List<Float> getD() {
        return D;
    }

    public FourthTask(List<List<Float>> MT, List<List<Float>> MZ, List<List<Float>> ME, List<List<Float>> MM, List<List<Float>> MD, List<Float> b, List<Float> d) {
        this.MT = MT;
        this.MZ = MZ;
        this.ME = ME;
        this.MM = MM;
        this.MD = MD;
        B = b;
        D = d;
    }
}
