package lab4;

import main.ATaskExecutor;
import models.*;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.ReentrantLock;

import static helpers.MatrixHelper.*;

public class LockTaskExecutor extends ATaskExecutor {

    private Integer firstBarrier = 0;
    private Integer secondBarrier = 0;
    private Integer thirdBarrier = 0;
    private Integer fourthBarrier = 0;
    private ReentrantLock firstLock = new ReentrantLock();
    private ReentrantLock secondLock = new ReentrantLock();
    private ReentrantLock thirdLock = new ReentrantLock();
    private ReentrantLock fourthLock = new ReentrantLock();

    private synchronized void increase(Integer barrier, ReentrantLock lock) {
        lock.lock();
        try {
            barrier++;
        } finally {
            lock.unlock();
        }
    }

    private void waitLoop(Integer barrier) {
        while (barrier % chunkCount != 0) {
        }
    }

    @Override
    protected void syncFirst() {
        increase(firstBarrier, firstLock);
        waitLoop(firstBarrier);
    }

    @Override
    protected void syncSecond() {
        increase(secondBarrier, secondLock);
        waitLoop(secondBarrier);
    }

    @Override
    protected void syncThird() {
        increase(thirdBarrier, thirdLock);
        waitLoop(thirdBarrier);
    }

    @Override
    protected void syncFourth() {
        increase(fourthBarrier, fourthLock);
        waitLoop(fourthBarrier);
    }

    @Override
    public TaskResults execute(Tasks tasks) {
        TaskResults results = new TaskResults();

        switch (size = tasks.getFirstTask().getB().size()) {
        }

        chunkCount = 1;
        chunkSize = tasks.getFirstTask().getB().size();

        initData();

        initAndSyncThreads(tasks, results);

        results.setFirst(firstResult);
        results.setSecond(secondResult);
        results.setThird(thirdResult);
        results.setFourth(fourthResult);
        return results;
    }

    @Override
    protected void initAndSyncThreads(Tasks tasks, TaskResults results) {
        FutureTask<List<Float>> first = new FutureTask<>(new FirstTaskExecutor(tasks.getFirstTask()));
        FutureTask<List<Float>> second = new FutureTask<>(new SecondTaskExecutor(tasks.getSecondTask()));
        FutureTask<List<List<Float>>> third = new FutureTask<>(new ThirdTaskExecutor(tasks.getThirdTask()));
        FutureTask<List<List<Float>>> fourth = new FutureTask<>(new FourthTaskExecutor(tasks.getFourthTask()));

        new Thread(first).start();
        new Thread(second).start();
        new Thread(third).start();
        new Thread(fourth).start();

        try {
            results.setFirst(first.get());
            results.setSecond(second.get());
            results.setThird(third.get());
            results.setFourth(fourth.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    protected class FirstTaskExecutor implements Callable<List<Float>> {

        private FirstTask firstTask;
        private int chunkNumber;


        public FirstTaskExecutor(FirstTask firstTask) {
            this.firstTask = firstTask;
            this.chunkNumber = 1;
        }

        //A = B*MC+D*MM*a-B*MM
        @Override
        public List<Float> call() {
            multiplyVectorOnMatrix(firstTask.getB(), firstTask.getMC(), firstBMC, chunkSize, chunkNumber);
            multiplyVectorOnMatrix(firstTask.getD(), firstTask.getMM(), firstDMM, chunkSize, chunkNumber, firstTask.getA());
            multiplyVectorOnMatrix(firstTask.getB(), firstTask.getMM(), firstBMM, chunkSize, chunkNumber);

            syncFirst();

            addVectors(firstBMC, firstDMM, firstResult, chunkSize, chunkNumber);
            div(firstResult, firstBMM, firstResult, chunkSize, chunkNumber);

            syncFirst();
            return firstResult;
        }
    }

    protected class SecondTaskExecutor implements Callable<List<Float>> {
        private SecondTask task;
        private int chunkNumber;

        public SecondTaskExecutor(SecondTask task) {
            this.task = task;
            this.chunkNumber = 1;
        }

        //D=B*MZ+D*MX*a

        @Override
        public List<Float> call() {
            multiplyVectorOnMatrix(task.getB(), task.getMZ(), secondBMZ, chunkSize, chunkNumber);
            multiplyVectorOnMatrix(task.getD(), task.getMX(), secondDMXa, chunkSize, chunkNumber, task.getA());

            syncSecond();

            addVectors(secondBMZ, secondDMXa, secondResult, chunkSize, chunkNumber);

            syncSecond();
            return secondResult;
        }

    }


    protected class ThirdTaskExecutor implements Callable<List<List<Float>>> {
        private ThirdTask task;
        private int chunkNumber;

        public ThirdTaskExecutor(ThirdTask thirdTask) {
            this.task = thirdTask;
            this.chunkNumber = 1;
        }

        //MG=MB*MK+MC*(MX*MT+MM)

        @Override
        public List<List<Float>> call() {
            multiplyMatrix(task.getMB(), task.getMK(), thirdMBMK, chunkSize, chunkNumber);
            multiplyMatrix(task.getMX(), task.getMT(), thirdMXMTMM, chunkSize, chunkNumber, task.getMM());
            syncThird();
            multiplyMatrix(task.getMC(), thirdMXMTMM, thirdResult, chunkSize, chunkNumber, thirdMBMK);
            syncThird();
            return thirdResult;
        }
    }

    protected class FourthTaskExecutor implements Callable<List<List<Float>>> {
        private FourthTask task;
        private int chunkNumber;

        public FourthTaskExecutor(FourthTask fourthTask) {
            this.task = fourthTask;
            this.chunkNumber = 1;
        }

        //MA = max(B-D)*MD*MT-MZ*(ME+MM)

        @Override
        public List<List<Float>> call() {
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
            return fourthResult;
        }
    }
}
