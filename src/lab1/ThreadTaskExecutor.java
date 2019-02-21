package lab1;

import main.TaskExecutor;
import models.*;

import java.util.ArrayList;
import java.util.List;

import static helpers.GCD.gcd;
import static helpers.ListHelper.list;
import static helpers.MatrixHelper.*;

public class ThreadTaskExecutor implements TaskExecutor {

    private int chunkSize;
    private int chunkCount;
    private int size;

    @Override
    public TaskResults execute(Tasks tasks) {
        size = tasks.getFirstTask().getB().size();
//        chunkCount = 1;
//        chunkSize = size;

        chunkCount = gcd(size, Runtime.getRuntime().availableProcessors());
        chunkSize = tasks.getFirstTask().getB().size() / chunkCount;

        TaskResults results = new TaskResults();

        List<Thread> threads = new ArrayList<>();

        threads.add(new Thread(new FirstTaskExecutor(results, tasks.getFirstTask())));
        threads.add(new Thread(new SecondTaskExecutor(results, tasks.getSecondTask())));
        threads.add(new Thread(new ThirdTaskExecutor(results, tasks.getThirdTask())));
        threads.add(new Thread(new FourthTaskExecutor(results, tasks.getFourthTask())));

        executeAndJoin(threads);

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

    private class FirstTaskExecutor implements Runnable {

        private TaskResults taskResults;
        private FirstTask firstTask;

        private List<Float> BMC = list(size, integer -> 0f);
        private List<Float> DMM = list(size, integer -> 0f);
        private List<Float> BMM = list(size, integer -> 0f);


        public FirstTaskExecutor(TaskResults taskResults, FirstTask firstTask) {
            this.taskResults = taskResults;
            this.firstTask = firstTask;
        }

        //A = B*MC+D*MM*a-B*MM
        @Override
        public void run() {
            List<Float> BMC = list(size, integer -> 0f);
            List<Float> DMM = list(size, integer -> 0f);
            List<Float> BMM = list(size, integer -> 0f);

            List<Thread> threads = new ArrayList<>();

            for (int i = 1; i <= chunkCount; i++) {
                int finalI = i;
                threads.add(new Thread(() -> multiplyVectorOnMatrix(firstTask.getB(), firstTask.getMC(), BMC, chunkSize, finalI)));
                threads.add(new Thread(() -> multiplyVectorOnMatrix(firstTask.getD(), firstTask.getMM(), DMM, chunkSize, finalI, firstTask.getA())));
                threads.add(new Thread(() -> multiplyVectorOnMatrix(firstTask.getB(), firstTask.getMM(), BMM, chunkSize, finalI)));
            }

            executeAndJoin(threads);
            threads.clear();

            List<Float> result = list(size, integer -> 0f);

            for (int i = 1; i <= chunkCount; i++) {
                int finalI = i;
                threads.add(new Thread(() -> {
                    addVectors(BMC, DMM, result, chunkSize, finalI);
                    div(result, BMM, result, chunkSize, finalI);
                }));
            }

            executeAndJoin(threads);

            taskResults.setFirst(result);
        }
    }

    private class SecondTaskExecutor implements Runnable {
        private TaskResults taskResults;
        private SecondTask task;

        public SecondTaskExecutor(TaskResults taskResults, SecondTask task) {
            this.taskResults = taskResults;
            this.task = task;
        }

        //D=B*MZ+D*MX*a
        @Override
        public void run() {
            List<Float> BMZ = list(size, integer -> 0f);
            List<Float> DMXa = list(size, integer -> 0f);
            List<Thread> threads = new ArrayList<>();

            for (int i = 1; i <= chunkCount; i++) {
                int finalI = i;
                threads.add(new Thread(() -> multiplyVectorOnMatrix(task.getB(), task.getMZ(), BMZ, chunkSize, finalI)));
                threads.add(new Thread(() -> multiplyVectorOnMatrix(task.getD(), task.getMX(), DMXa, chunkSize, finalI, task.getA())));
            }

            executeAndJoin(threads);

            threads.clear();

            List<Float> result = list(size, integer -> 0f);

            for (int i = 1; i <= chunkCount; i++) {
                int finalI = i;
                threads.add(new Thread(() -> addVectors(BMZ, DMXa, result, chunkSize, finalI)));
            }

            executeAndJoin(threads);

            taskResults.setSecond(result);
        }
    }

    private class ThirdTaskExecutor implements Runnable {
        private TaskResults taskResults;
        private ThirdTask task;

        public ThirdTaskExecutor(TaskResults taskResults, ThirdTask thirdTask) {
            this.taskResults = taskResults;
            this.task = thirdTask;
        }

        //MG=MB*MK+MC*(MX*MT+MM)
        @Override
        public void run() {
            List<List<Float>> MBMK = list(size, integer -> list(size, integer1 -> 0f));
            List<List<Float>> MXMTMM = list(size, integer -> list(size, integer1 -> 0f));

            List<Thread> threads = new ArrayList<>();

            for (int i = 1; i <= chunkCount; i++) {
                int finalI = i;
                threads.add(new Thread(() -> multiplyMatrix(task.getMB(), task.getMK(), MBMK, chunkSize, finalI)));
                threads.add(new Thread(() -> multiplyMatrix(task.getMX(), task.getMT(), MXMTMM, chunkSize, finalI, task.getMM())));
            }
            executeAndJoin(threads);
            threads.clear();
            List<List<Float>> result = list(size, integer -> list(size, integer1 -> 0f));

            for (int i = 1; i <= chunkCount; i++) {
                int finalI = i;
                threads.add(new Thread(() -> multiplyMatrix(task.getMC(), MXMTMM, result, chunkSize, finalI, MBMK)));
            }
            executeAndJoin(threads);
            taskResults.setThird(result);
        }
    }

    private class FourthTaskExecutor implements Runnable {
        private TaskResults taskResults;
        private FourthTask task;

        public FourthTaskExecutor(TaskResults taskResults, FourthTask fourthTask) {
            this.taskResults = taskResults;
            this.task = fourthTask;
        }

        //MA = max(B-D)*MD*MT-MZ*(ME+MM)
        @Override
        public void run() {
            List<Float> BD = list(size, integer -> 0f);
            List<List<Float>> MZMEMM = list(size, integer -> list(size, integer1 -> 0f));

            List<Thread> threads = new ArrayList<>();

            List<List<Float>> MEMM = list(size, integer -> list(size, integer1 -> 0f));

            for (int i = 1; i <= chunkCount; i++) {
                int finalI = i;
                threads.add(new Thread(() -> div(task.getB(), task.getD(), BD, chunkSize, finalI)));
                threads.add(new Thread(() -> {
                    add(task.getME(), task.getMM(), MEMM, chunkSize, finalI);
                    multiplyMatrix(task.getMZ(), MEMM, MZMEMM, chunkSize, finalI);
                }));
            }
            executeAndJoin(threads);
            threads.clear();
            List<List<Float>> result = list(size, integer -> list(size, integer1 -> 0f));

            Float max = max(BD);

            for (int i = 1; i <= chunkCount; i++) {
                int finalI = i;
                threads.add(new Thread(() -> multiplyMatrix(task.getMD(), max, result, chunkSize, finalI)));
            }
            executeAndJoin(threads);
            threads.clear();

            for (int i = 1; i <= chunkCount; i++) {
                int finalI = i;
                threads.add(new Thread(() -> multiplyMatrix(result, task.getMT(), result, chunkSize, finalI)));
            }
            executeAndJoin(threads);
            threads.clear();

            for (int i = 1; i <= chunkCount; i++) {
                int finalI = i;
                threads.add(new Thread(() -> divMatrix(result, MZMEMM, result, chunkSize, finalI)));
            }
            executeAndJoin(threads);
            taskResults.setFourth(result);
        }
    }
}
