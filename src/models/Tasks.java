package models;

import java.io.Serializable;

public class Tasks implements Serializable {
    private FirstTask firstTask;
    private SecondTask secondTask;
    private ThirdTask thirdTask;
    private FourthTask fourthTask;

    public ThirdTask getThirdTask() {
        return thirdTask;
    }

    public FourthTask getFourthTask() {
        return fourthTask;
    }

    public Tasks(FirstTask firstTask, SecondTask secondTask, ThirdTask thirdTask, FourthTask fourthTask) {
        this.firstTask = firstTask;
        this.secondTask = secondTask;
        this.thirdTask = thirdTask;
        this.fourthTask = fourthTask;
    }

    public FirstTask getFirstTask() {
        return firstTask;
    }

    public SecondTask getSecondTask() {
        return secondTask;
    }
}
