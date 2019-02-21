package lab1;

import main.TaskExecutor;
import models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static helpers.GCD.gcd;
import static helpers.ListHelper.list;
import static helpers.MatrixHelper.*;

public class ThreadAltTaskExecutor implements TaskExecutor {

    private int chunkSize;
    private int chunkCount;
    private int size;
    private Integer firstBarrier;
    private Integer secondBarrier;
    private Integer thirdBarrier;
    private Integer fourthBarrier;

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


    private synchronized void increase(Integer barrier) {
        barrier++;
    }

    private void waitLoop(Integer barrier) {
        while (barrier % chunkCount != 0) {}
    }

    @Override
    public TaskResults execute(Tasks tasks) {
        TaskResults results = new TaskResults();
        List<Thread> threads = new ArrayList<>();

        size = tasks.getFirstTask().getB().size();

        chunkCount = gcd(size, Runtime.getRuntime().availableProcessors());
        chunkSize = tasks.getFirstTask().getB().size() / chunkCount;


        firstBMC = list(size, integer -> 0f);
        firstDMM = list(size, integer -> 0f);
        firstBMM = list(size, integer -> 0f);
        firstResult = list(size, integer -> 0f);
        firstBarrier = 0;

        secondBMZ = list(size, integer -> 0f);
        secondDMXa = list(size, integer -> 0f);
        secondResult = list(size, integer -> 0f);
        secondBarrier = 0;

        thirdMBMK = list(size, integer -> list(size, integer1 -> 0f));
        thirdMXMTMM = list(size, integer -> list(size, integer1 -> 0f));
        thirdResult = list(size, integer -> list(size, integer1 -> 0f));
        thirdBarrier = 0;

        fourthBD = list(size, integer -> 0f);
        fourthMZMEMM = list(size, integer -> list(size, integer1 -> 0f));
        fourthMEMM = list(size, integer -> list(size, integer1 -> 0f));
        fourthResult = list(size, integer -> list(size, integer1 -> 0f));
        fourthBarrier = 0;

        for (int i = 1; i <= chunkCount; i++) {
            threads.add(new Thread(new FirstTaskExecutor(results, tasks.getFirstTask(), i)));
            threads.add(new Thread(new SecondTaskExecutor(results, tasks.getSecondTask(), i)));
            threads.add(new Thread(new ThirdTaskExecutor(tasks.getThirdTask(), i)));
            threads.add(new Thread(new FourthTaskExecutor(results, tasks.getFourthTask(), i)));
        }

        executeAndJoin(threads);

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

    private void awaitBarrier(CyclicBarrier secondBarrier) {
        try {
            secondBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    private class FirstTaskExecutor implements Runnable {

        private TaskResults taskResults;
        private FirstTask firstTask;
        private int chunkNumber;


        public FirstTaskExecutor(TaskResults taskResults, FirstTask firstTask, int chunkNumber) {
            this.taskResults = taskResults;
            this.firstTask = firstTask;
            this.chunkNumber = chunkNumber;
        }

        //A = B*MC+D*MM*a-B*MM
        @Override
        public void run() {
            multiplyVectorOnMatrix(firstTask.getB(), firstTask.getMC(), firstBMC, chunkSize, chunkNumber);
            multiplyVectorOnMatrix(firstTask.getD(), firstTask.getMM(), firstDMM, chunkSize, chunkNumber, firstTask.getA());
            multiplyVectorOnMatrix(firstTask.getB(), firstTask.getMM(), firstBMM, chunkSize, chunkNumber);

            increase(firstBarrier);
            waitLoop(firstBarrier);

            addVectors(firstBMC, firstDMM, firstResult, chunkSize, chunkNumber);
            div(firstResult, firstBMM, firstResult, chunkSize, chunkNumber);

            increase(firstBarrier);
            waitLoop(firstBarrier);
        }
    }

    private class SecondTaskExecutor implements Runnable {
        private TaskResults taskResults;
        private SecondTask task;
        private int chunkNumber;

        public SecondTaskExecutor(TaskResults taskResults, SecondTask task, int chunkNumber) {
            this.taskResults = taskResults;
            this.task = task;
            this.chunkNumber = chunkNumber;
        }

        //D=B*MZ+D*MX*a
        @Override
        public void run() {
            multiplyVectorOnMatrix(task.getB(), task.getMZ(), secondBMZ, chunkSize, chunkNumber);
            multiplyVectorOnMatrix(task.getD(), task.getMX(), secondDMXa, chunkSize, chunkNumber, task.getA());

            increase(secondBarrier);
            waitLoop(secondBarrier);

            addVectors(secondBMZ, secondDMXa, secondResult, chunkSize, chunkNumber);

            increase(secondBarrier);
            waitLoop(secondBarrier);
        }
    }


    private class ThirdTaskExecutor implements Runnable {
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
            increase(thirdBarrier);
            waitLoop(thirdBarrier);
            multiplyMatrix(task.getMC(), thirdMXMTMM, thirdResult, chunkSize, chunkNumber, thirdMBMK);
            increase(thirdBarrier);
            waitLoop(thirdBarrier);
        }
    }

    private class FourthTaskExecutor implements Runnable {
        private TaskResults taskResults;
        private FourthTask task;
        private int chunkNumber;

        public FourthTaskExecutor(TaskResults taskResults, FourthTask fourthTask, int chunkCount) {
            this.taskResults = taskResults;
            this.task = fourthTask;
            this.chunkNumber = chunkCount;
        }

        //MA = max(B-D)*MD*MT-MZ*(ME+MM)
        @Override
        public void run() {

            div(task.getB(), task.getD(), fourthBD, chunkSize, chunkNumber);
            add(task.getME(), task.getMM(), fourthMEMM, chunkSize, chunkNumber);
            multiplyMatrix(task.getMZ(), fourthMEMM, fourthMZMEMM, chunkSize, chunkNumber);
            increase(fourthBarrier);
            waitLoop(fourthBarrier);

            Float max = max(fourthBD);

            multiplyMatrix(task.getMD(), max, fourthResult, chunkSize, chunkNumber);
            increase(fourthBarrier);
            waitLoop(fourthBarrier);

            multiplyMatrix(fourthResult, task.getMT(), fourthResult, chunkSize, chunkNumber);

            increase(fourthBarrier);
            waitLoop(fourthBarrier);

            divMatrix(fourthResult, fourthMZMEMM, fourthResult, chunkSize, chunkNumber);

            increase(fourthBarrier);
            waitLoop(fourthBarrier);
        }
    }
}
