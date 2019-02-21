package helpers;

import models.FirstTask;

import java.util.List;
import java.util.stream.IntStream;

import static helpers.ListHelper.list;
import static helpers.ListHelper.map;

public class MatrixHelper {
    public static List<Float> multiply(List<Float> vector, List<List<Float>> matrix) {
        return map(matrix, row -> (float) (IntStream.range(0, row.size())
                .mapToDouble(col -> row.get(col) * vector.get(col))
                .sum()));
    }

    public static List<List<Float>> multiplyMatrix(List<List<Float>> first, List<List<Float>> second) {
        List<List<Float>> result = list(first.size(), i -> list(first.size(), j -> {
            float sum = 0;
            for (int k = 0; k < first.size(); k++) {
                sum += first.get(i).get(k) * second.get(k).get(j);
            }
            return sum;
        }));
        return result;
    }

    public static List<List<Float>> add(List<List<Float>> first, List<List<Float>> second) {
        return list(first.size(), i -> list(first.size(), j -> first.get(i).get(j) + second.get(i).get(j)));
    }

    public static List<Float> div(List<Float> first, List<Float> second) {
        return list(first.size(), i -> first.get(i) - second.get(i));
    }

    public static List<List<Float>> divMatrix(List<List<Float>> first, List<List<Float>> second) {
        return list(first.size(), i -> list(first.size(), j -> first.get(i).get(j) - second.get(i).get(j)));
    }

    public static List<Float> addVectors(List<Float> first, List<Float> second) {
        return list(first.size(), i -> first.get(i) + second.get(i));
    }

    public static void add(List<List<Float>> first, List<List<Float>> second, List<List<Float>> result, int size, int part) {
        int start = size * (part - 1);
        int end = size * part;
        for (int i = start; i < end; i++) {
            for (int j = 0; j < first.size(); j++) {
                result.get(i).set(j, first.get(i).get(j) + second.get(i).get(j));
            }
        }
    }

    public static void div(List<Float> first, List<Float> second, List<Float> result, int size, int part) {
        int start = size * (part - 1);
        int end = size * part;
        for (int i = start; i < end; i++) {
            result.set(i, first.get(i) - second.get(i));
        }
    }

    public static void divMatrix(List<List<Float>> first, List<List<Float>> second, List<List<Float>> result, int size, int part) {
        int start = size * (part - 1);
        int end = size * part;
        for (int i = start; i < end; i++) {
            for (int j = 0; j < first.size(); j++) {
                result.get(i).set(j, first.get(i).get(j) - second.get(i).get(j));
            }
        }
    }

    public static void addVectors(List<Float> first, List<Float> second, List<Float> result, int size, int part) {
        int start = size * (part - 1);
        int end = size * part;
        for (int i = start; i < end; i++) {
            result.set(i, first.get(i) + second.get(i));
        }
    }

    public static void add(List<Float> first, Float second, List<Float> result, int size, int part) {
        int start = size * (part - 1);
        int end = size * part;
        for (int i = start; i < end; i++) {
            result.set(i, first.get(i) + second);
        }
    }

    public static List<Float> add(List<Float> first, Float second) {
        return list(first.size(), integer -> {
            return first.get(integer) + second;
        });
    }

    public static List<Float> multiply(List<Float> first, Float second) {
        return list(first.size(), integer -> first.get(integer) * second);
    }

    public static List<List<Float>> multiplyMatrix(List<List<Float>> first, Float second) {
        return list(first.size(), integer -> list(first.size(), integer1 -> first.get(integer).get(integer1) * second));
    }

    public static void multiplyMatrix(List<List<Float>> first, List<List<Float>> second, List<List<Float>> result, int size, int part) {
        int end = part * size;
        int start = size * (part - 1);
        int fSize = first.size();

        for (int i = start; i < end; i++) {
            List<Float> firstRow = first.get(i);
            List<Float> resultRow = result.get(i);
            for (int j = 0; j < fSize; j++) {
                float sum = 0;
                for (int k = 0; k < fSize; k++) {
                    sum += firstRow.get(k) * second.get(k).get(j);
                }
                resultRow.set(j, sum);
            }
        }
    }

    public static void multiplyMatrix(List<List<Float>> first, Float second, List<List<Float>> result, int size, int part) {
        int end = part * size;
        int start = size * (part - 1);
        int fSize = first.size();

        for (int i = start; i < end; i++) {
            List<Float> firstRow = first.get(i);
            List<Float> resultRow = result.get(i);
            for (int j = 0; j < fSize; j++) {
                resultRow.set(j, firstRow.get(j) * second);
            }
        }
    }

    public static void multiplyMatrix(List<List<Float>> first, List<List<Float>> second, List<List<Float>> result, int size, int part, List<List<Float>> add) {
        int end = part * size;
        int start = size * (part - 1);
        int fSize = first.size();

        for (int i = start; i < end; i++) {
            List<Float> firstRow = first.get(i);
            List<Float> resultRow = result.get(i);
            List<Float> addRow = add.get(i);
            for (int j = 0; j < fSize; j++) {
                float sum = 0;
                for (int k = 0; k < fSize; k++) {
                    sum += firstRow.get(k) * second.get(k).get(j);
                }
                resultRow.set(j, sum + addRow.get(j));
            }
        }
    }

    public static void addAndMultiplyMatrix(List<List<Float>> first, List<List<Float>> second, List<List<Float>> result, int size, int part, List<List<Float>> add) {
        int end = part * size;
        int start = size * (part - 1);
        int fSize = first.size();

        for (int i = start; i < end; i++) {
            List<Float> firstRow = first.get(i);
            List<Float> secondRow = second.get(i);
            List<Float> resultRow = result.get(i);
            List<Float> addRow = add.get(i);
            for (int j = 0; j < fSize; j++) {
                float sum = 0;
                secondRow.set(j, secondRow.get(j) + addRow.get(j));
                for (int k = 0; k < fSize; k++) {
                    sum += firstRow.get(k) * second.get(k).get(j);
                }
                resultRow.set(j, sum);
            }
        }
    }

    public static void multiplyVectorOnMatrix(List<Float> vector, List<List<Float>> matrix, List<Float> result, int size, int part) {
        for (int i = size * (part - 1); i < part * size; i++) {
            float sum = 0f;
            for (int j = 0; j < matrix.size(); j++) {
                sum += matrix.get(i).get(j) * vector.get(j);
            }
            result.set(i, sum);
        }
    }

    public static void multiplyVectorOnMatrix(List<Float> vector, List<List<Float>> matrix, List<Float> result, int size, int part, Float multiplier) {
        for (int i = size * (part - 1); i < part * size; i++) {
            float sum = 0f;
            for (int j = 0; j < matrix.size(); j++) {
                sum += matrix.get(i).get(j) * vector.get(j);
            }
            result.set(i, sum * multiplier);
        }
    }

    public static Float max(List<Float> list) {
        return list.stream().max((o1, o2) -> o1 < o2 ? -1
                : o1 > o2 ? 1
                : 0).get();
    }

    public static void test(FirstTask firstTask) {
        List<List<Float>> first = list(firstTask.getB().size(), integer -> list(firstTask.getB().size(), integer1 -> 1f));
        List<Float> vector = list(firstTask.getB().size(), integer -> 1f);
        List<Float> vectorResult = list(firstTask.getB().size(), integer -> 0f);
        List<List<Float>> second = list(firstTask.getB().size(), integer -> list(firstTask.getB().size(), integer1 -> 1f));
        List<List<Float>> result = list(firstTask.getB().size(), integer -> list(firstTask.getB().size(), integer1 -> 0f));
/*
        System.out.println(StringHelper.getMatrixString(multiplyMatrix(first, second)));*/

        long start = System.currentTimeMillis();
        multiplyMatrix(first, second, result, 200, 1);
        System.out.println("aTime:" + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        multiplyMatrix(first, second);
        System.out.println("Time:" + (System.currentTimeMillis() - start));

        System.out.println(StringHelper.getListString(vectorResult));
        System.out.println(StringHelper.getListString(multiply(firstTask.getD(), firstTask.getMM())));
    }
}
