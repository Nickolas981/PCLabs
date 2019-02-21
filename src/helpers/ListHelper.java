package helpers;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListHelper {

    public static <I> List<I> listOf(I... values) {
        return Arrays.asList(values);
    }

    public static <I> List<I> emptyList() {
        return new ArrayList<>();
    }

    public static <I> List<I> list(int size, Function<Integer, I> fun) {
        List<I> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(fun.apply(i));
        }
        return result;
    }

    public static <I> boolean any(List<I> list, Predicate<I> predicate) {
        for (I i : list) {
            if (predicate.test(i)) {
                return true;
            }
        }
        return false;
    }

    public static <I> boolean none(List<I> list, Predicate<I> predicate) {
        return !any(list, predicate);
    }

    public static <I> I firstOrNull(List<I> list, Predicate<I> predicate) {
        for (I i : list) {
            if (predicate.test(i)) {
                return i;
            }
        }
        return null;
    }

    public static <I> I first(List<I> list, Predicate<I> predicate) {
        for (I i : list) {
            if (predicate.test(i)) {
                return i;
            }
        }
        throw new NoSuchElementException();
    }


    public static <I> List<I> filter(List<I> list, Predicate<I> predicate) {
        return list.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public static <I, R> List<R> map(List<I> list, Function<I, R> function) {
        return list.stream()
                .map(function)
                .collect(Collectors.toList());
    }

    public static <I> void forEach(Iterable<I> list, Consumer<I> function) {
        for (I i : list) {
            function.accept(i);
        }
    }

    public static Double sum(List<Double> list) {
        BigDecimal result = BigDecimal.ZERO;
        for (Double number : list) {
            result = result.add(new BigDecimal(number));
        }
        return result.doubleValue();
    }

    public static int sumInt(List<Integer> list) {
        int result = 0;
        for (Integer number : list) {
            result += number;
        }
        return result;
    }

    public static <T, R> Map<R, List<T>> groupBy(List<T> list, Function<T, R> function) {
        Map<R, List<T>> map = new HashMap<>();
        Set<R> set = new HashSet<>();
        forEach(list, var1 -> set.add(function.apply(var1))); // get All Unique keys
        for (R r : set) {
            map.put(r, filter(list, t -> function.apply(t).equals(r)));
        }
        return map;
    }

}