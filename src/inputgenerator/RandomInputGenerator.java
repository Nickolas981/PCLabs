package inputgenerator;

import models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomInputGenerator implements InputProvider {

    private Random random = new Random(System.currentTimeMillis());

    @Override
    public Tasks generate(int capacity) {
        return new Tasks(generateFirstTask(capacity),
                generateSecondTask(capacity),
                generateThirdTask(capacity),
                generateFourthTask(capacity)
        );
    }

    private FirstTask generateFirstTask(int capacity) {
        return new FirstTask(
                generateList(capacity),
                generateList(capacity),
                generateMatrix(capacity),
                generateMatrix(capacity),
                random.nextFloat()
        );
    }

    private SecondTask generateSecondTask(int capacity) {
        return new SecondTask(
                random.nextFloat(),
                generateList(capacity),
                generateList(capacity),
                generateMatrix(capacity),
                generateMatrix(capacity)
        );
    }

    private ThirdTask generateThirdTask(int capacity) {
        return new ThirdTask(
                generateMatrix(capacity),
                generateMatrix(capacity),
                generateMatrix(capacity),
                generateMatrix(capacity),
                generateMatrix(capacity),
                generateMatrix(capacity)
        );
    }

    private FourthTask generateFourthTask(int capacity) {
        return new FourthTask(
                generateMatrix(capacity),
                generateMatrix(capacity),
                generateMatrix(capacity),
                generateMatrix(capacity),
                generateMatrix(capacity),
                generateList(capacity),
                generateList(capacity)
        );
    }

    private List<Float> generateList(int capacity) {
        ArrayList<Float> floats = new ArrayList<>();
        for (int i = 0; i < capacity; i++) floats.add(random.nextFloat());
        return floats;
    }


    private List<List<Float>> generateMatrix(int capacity) {
        ArrayList<List<Float>> floats = new ArrayList<>();
        for (int i = 0; i < capacity; i++) floats.add(generateList(capacity));
        return floats;
    }
}
