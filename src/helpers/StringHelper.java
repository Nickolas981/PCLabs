package helpers;

import java.util.Arrays;
import java.util.List;

public class StringHelper {

    public static String getListString(List<Float> list) {
        return Arrays.toString(list.stream().map(Object::toString).toArray());
    }

    public static String getMatrixString(List<List<Float>> list) {
        return String.join("\n", ListHelper.map(list, StringHelper::getListString));
    }
}
