package main;

import models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static helpers.GCD.gcd;
import static helpers.ListHelper.list;
import static helpers.MatrixHelper.*;

public abstract class ATaskExecutor implements TaskExecutor {

    protected int chunkSize;
    protected int chunkCount;
    protected int size;

    private List<Float> firstBMC;
    private List<Float> firstDMM;
    private List<Float> firstBMM;
    private List<Float> firstResult;

    private List<Float> secondBMZ;
    private List<Float> secondDMXa;
    private List<Float> secondResult;

    private List<List<Float>> thirdMBMK;
    private List<List<Float>> thirdMXMTMM;
    private List<List<Float>> thirdResult;

    private List<Float> fourthBD;
    private List<List<Float>> fourthMZMEMM;
    private List<List<Float>> fourthMEMM;
    private List<List<Float>> fourthResult;

    protected abstract void syncFirst();

    protected abstract void syncSecond();

    protected abstract void syncThird();

    protected abstract void syncFourth();

    protected void initData() {
        firstBMC = list(size, integer -> 0f);
        firstDMM = list(size, integer -> 0f);
        firstBMM = list(size, integer -> 0f);
        firstResult = list(size, integer -> 0f);

        secondBMZ = list(size, integer -> 0f);
        secondDMXa = list(size, integer -> 0f);
        secondResult = list(size, integer -> 0f);

        thirdMBMK = list(size, integer -> list(size, integer1 -> 0f));
        thirdMXMTMM = list(size, integer -> list(size, integer1 -> 0f));
        thirdResult = list(size, integer -> list(size, integer1 -> 0f));

        fourthBD = list(size, integer -> 0f);
        fourthMZMEMM = list(size, integer -> list(size, integer1 -> 0f));
        fourthMEMM = list(size, integer -> list(size, integer1 -> 0f));
        fourthResult = list(size, integer -> list(size, integer1 -> 0f));
    }

    protected void initAndSyncThreads(Tasks tasks, TaskResults results) {
        List<Thread> threads = new ArrayList<>();

        for (int i = 1; i <= chunkCount; i++) {
            threads.add(new Thread(new FirstTaskExecutor(tasks.getFirstTask(), i)));
            threads.add(new Thread(new SecondTaskExecutor(tasks.getSecondTask(), i)));
            threads.add(new Thread(new ThirdTaskExecutor(tasks.getThirdTask(), i)));
            threads.add(new Thread(new FourthTaskExecutor(tasks.getFourthTask(), i)));
        }

        executeAndJoin(threads);
    }

    @Override
    public TaskResults execute(Tasks tasks) {
        TaskResults results = new TaskResults();

        size = tasks.getFirstTask().getB().size();

        chunkCount = gcd(size, Runtime.getRuntime().availableProcessors());
        chunkSize = tasks.getFirstTask().getB().size() / chunkCount;

        initData();

        initAndSyncThreads(tasks, results);

        results.setFirst(firstResult);
        results.setSecond(secondResult);
        results.setThird(thirdResult);
        results.setFourth(fourthResult);
        return results;
    }


    private void executeAndJoin(List<Thread> threads) {
        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected class FirstTaskExecutor implements Runnable {

        private FirstTask firstTask;
        private int chunkNumber;


        public FirstTaskExecutor(FirstTask firstTask, int chunkNumber) {
            this.firstTask = firstTask;
            this.chunkNumber = chunkNumber;
        }

        //A = B*MC+D*MM*a-B*MM
        @Override
        public void run() {
            multiplyVectorOnMatrix(firstTask.getB(), firstTask.getMC(), firstBMC, chunkSize, chunkNumber);
            multiplyVectorOnMatrix(firstTask.getD(), firstTask.getMM(), firstDMM, chunkSize, chunkNumber, firstTask.getA());
            multiplyVectorOnMatrix(firstTask.getB(), firstTask.getMM(), firstBMM, chunkSize, chunkNumber);

            syncFirst();

            addVectors(firstBMC, firstDMM, firstResult, chunkSize, chunkNumber);
            div(firstResult, firstBMM, firstResult, chunkSize, chunkNumber);

            syncFirst();
        }
    }

    protected class SecondTaskExecutor implements Runnable {
        private SecondTask task;
        private int chunkNumber;

        public SecondTaskExecutor(SecondTask task, int chunkNumber) {
            this.task = task;
            this.chunkNumber = chunkNumber;
        }

        //D=B*MZ+D*MX*a
        @Override
        public void run() {
            multiplyVectorOnMatrix(task.getB(), task.getMZ(), secondBMZ, chunkSize, chunkNumber);
            multiplyVectorOnMatrix(task.getD(), task.getMX(), secondDMXa, chunkSize, chunkNumber, task.getA());

            syncSecond();

            addVectors(secondBMZ, secondDMXa, secondResult, chunkSize, chunkNumber);

            syncSecond();
        }
    }


    protected class ThirdTaskExecutor implements Runnable {
        private ThirdTask task;
        private int chunkNumber;

        public ThirdTaskExecutor(ThirdTask thirdTask, int chunkNumber) {
            this.task = thirdTask;
            this.chunkNumber = chunkNumber;
        }

        //MG=MB*MK+MC*(MX*MT+MM)
        @Override
        public void run() {
            multiplyMatrix(task.getMB(), task.getMK(), thirdMBMK, chunkSize, chunkNumber);
            multiplyMatrix(task.getMX(), task.getMT(), thirdMXMTMM, chunkSize, chunkNumber, task.getMM());
            syncThird();
            multiplyMatrix(task.getMC(), thirdMXMTMM, thirdResult, chunkSize, chunkNumber, thirdMBMK);
            syncThird();
        }
    }

    protected class FourthTaskExecutor implements Runnable {
        private FourthTask task;
        private int chunkNumber;

        public FourthTaskExecutor(FourthTask fourthTask, int chunkCount) {
            this.task = fourthTask;
            this.chunkNumber = chunkCount;
        }

        //MA = max(B-D)*MD*MT-MZ*(ME+MM)
        @Override
        public void run() {
            div(task.getB(), task.getD(), fourthBD, chunkSize, chunkNumber);
            add(task.getME(), task.getMM(), fourthMEMM, chunkSize, chunkNumber);
            multiplyMatrix(task.getMZ(), fourthMEMM, fourthMZMEMM, chunkSize, chunkNumber);
            syncFourth();

            Float max = max(fourthBD);

            multiplyMatrix(task.getMD(), max, fourthResult, chunkSize, chunkNumber);
            syncFourth();

            multiplyMatrix(fourthResult, task.getMT(), fourthResult, chunkSize, chunkNumber);

            syncFourth();

            divMatrix(fourthResult, fourthMZMEMM, fourthResult, chunkSize, chunkNumber);

            syncFourth();
        }
    }


}
