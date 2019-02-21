package models;

import helpers.ListHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static helpers.StringHelper.getListString;
import static helpers.StringHelper.getMatrixString;

public class TaskResults {
    private List<Float> first;
    private List<Float> second;
    private List<List<Float>> third;
    private List<List<Float>> fourth;

    public TaskResults(List<Float> first, List<Float> second, List<List<Float>> third, List<List<Float>> fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public TaskResults() {
        first = new ArrayList<>();
        second = new ArrayList<>();
        third = new ArrayList<>();
        fourth = new ArrayList<>();
    }

    public void setFirst(List<Float> first) {
        this.first = first;
    }

    public void setSecond(List<Float> second) {
        this.second = second;
    }

    public void setThird(List<List<Float>> third) {
        this.third = third;
    }

    public void setFourth(List<List<Float>> fourth) {
        this.fourth = fourth;
    }

    public List<Float> getFirst() {
        return first;
    }

    public List<Float> getSecond() {
        return second;
    }

    public List<List<Float>> getThird() {
        return third;
    }

    public List<List<Float>> getFourth() {
        return fourth;
    }

    public void print() {
        System.out.println("Results:");
        System.out.println("A:" + getListString(first));
        System.out.println("D:" + getListString(second));
        System.out.println("MG:" + getMatrixString(third));
        System.out.println("MA:" + getMatrixString(fourth));
    }

}
