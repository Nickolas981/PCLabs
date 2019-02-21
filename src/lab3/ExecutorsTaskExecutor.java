package lab3;

import main.TaskExecutor;
import models.*;

import java.util.List;
import java.util.concurrent.*;

import static helpers.GCD.gcd;
import static helpers.ListHelper.list;
import static helpers.MatrixHelper.*;

public class ExecutorsTaskExecutor implements TaskExecutor {

    private int chunkSize;
    private int chunkCount;
    private int size;
    private CyclicBarrier firstBarrier;
    private CyclicBarrier secondBarrier;
    private CyclicBarrier thirdBarrier;
    private CyclicBarrier fourthBarrier;


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


    @Override
    public TaskResults execute(Tasks tasks) {
        TaskResults results = new TaskResults();

        size = tasks.getFirstTask().getB().size();

        chunkCount = gcd(size, Runtime.getRuntime().availableProcessors());
        chunkSize = tasks.getFirstTask().getB().size() / chunkCount;


        firstBMC = list(size, integer -> 0f);
        firstDMM = list(size, integer -> 0f);
        firstBMM = list(size, integer -> 0f);
        firstResult = list(size, integer -> 0f);
        firstBarrier = new CyclicBarrier(chunkCount);

        secondBMZ = list(size, integer -> 0f);
        secondDMXa = list(size, integer -> 0f);
        secondResult = list(size, integer -> 0f);
        secondBarrier = new CyclicBarrier(chunkCount);

        thirdMBMK = list(size, integer -> list(size, integer1 -> 0f));
        thirdMXMTMM = list(size, integer -> list(size, integer1 -> 0f));
        thirdResult = list(size, integer -> list(size, integer1 -> 0f));
        thirdBarrier = new CyclicBarrier(chunkCount);

        fourthBD = list(size, integer -> 0f);
        fourthMZMEMM = list(size, integer -> list(size, integer1 -> 0f));
        fourthMEMM = list(size, integer -> list(size, integer1 -> 0f));
        fourthResult = list(size, integer -> list(size, integer1 -> 0f));
        fourthBarrier = new CyclicBarrier(chunkCount);

        ExecutorService service = Executors.newCachedThreadPool();

        for (int i = 1; i <= chunkCount; i++) {
            service.submit(new FirstTaskExecutor(tasks.getFirstTask(), i));
            service.submit(new SecondTaskExecutor(results, tasks.getSecondTask(), i));
            service.submit(new ThirdTaskExecutor(tasks.getThirdTask(), i));
            service.submit(new FourthTaskExecutor(tasks.getFourthTask(), i));
        }

        service.shutdown();

        try {
            service.awaitTermination(5000, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        results.setFirst(firstResult);
        results.setSecond(secondResult);
        results.setThird(thirdResult);
        results.setFourth(fourthResult);
        return results;
    }

    private void awaitBarrier(CyclicBarrier secondBarrier) {
        try {
            secondBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    private class FirstTaskExecutor implements Runnable {

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

            awaitBarrier(firstBarrier);

            addVectors(firstBMC, firstDMM, firstResult, chunkSize, chunkNumber);
            div(firstResult, firstBMM, firstResult, chunkSize, chunkNumber);

            awaitBarrier(firstBarrier);
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

            awaitBarrier(secondBarrier);

            addVectors(secondBMZ, secondDMXa, secondResult, chunkSize, chunkNumber);

            awaitBarrier(secondBarrier);
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
            awaitBarrier(thirdBarrier);
            multiplyMatrix(task.getMC(), thirdMXMTMM, thirdResult, chunkSize, chunkNumber, thirdMBMK);
            awaitBarrier(thirdBarrier);
        }
    }

    private class FourthTaskExecutor implements Runnable {
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
            awaitBarrier(fourthBarrier);

            Float max = max(fourthBD);

            multiplyMatrix(task.getMD(), max, fourthResult, chunkSize, chunkNumber);
            awaitBarrier(fourthBarrier);

            multiplyMatrix(fourthResult, task.getMT(), fourthResult, chunkSize, chunkNumber);

            awaitBarrier(fourthBarrier);

            divMatrix(fourthResult, fourthMZMEMM, fourthResult, chunkSize, chunkNumber);

            awaitBarrier(fourthBarrier);
        }
    }
}
