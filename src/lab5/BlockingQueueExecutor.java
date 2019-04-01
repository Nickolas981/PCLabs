package lab5;

import lab4.LockTaskExecutor;
import models.TaskResults;
import models.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class BlockingQueueExecutor extends LockTaskExecutor {

    public class FirstResult {
        public List<Float> list = new ArrayList<>();

        public FirstResult(List<Float> list) {
            this.list = list;
        }
    }

    public class SecondResult {
        public List<Float> list = new ArrayList<>();

        public SecondResult(List<Float> list) {
            this.list = list;
        }
    }

    public class ThirdResult {
        public List<List<Float>> list = new ArrayList<>();

        public ThirdResult(List<List<Float>> list) {
            this.list = list;
        }
    }

    public class FourthResult {
        public List<List<Float>> list = new ArrayList<>();

        public FourthResult(List<List<Float>> list) {
            this.list = list;
        }
    }

    @Override
    protected void initAndSyncThreads(Tasks tasks, TaskResults results) {
        FutureTask<List<Float>> first = new FutureTask<>(new FirstTaskExecutor(tasks.getFirstTask()));
        FutureTask<List<Float>> second = new FutureTask<>(new SecondTaskExecutor(tasks.getSecondTask()));
        FutureTask<List<List<Float>>> third = new FutureTask<>(new ThirdTaskExecutor(tasks.getThirdTask()));
        FutureTask<List<List<Float>>> fourth = new FutureTask<>(new FourthTaskExecutor(tasks.getFourthTask()));

        BlockingQueue<Object> queue = new ArrayBlockingQueue<>(4);

        new Thread(first).start();
        new Thread(second).start();
        new Thread(third).start();
        new Thread(fourth).start();

        int i = 4;

        new Thread(() -> {
            try {
                queue.put(new FirstResult(first.get()));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                queue.put(new SecondResult(second.get()));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                queue.put(new ThirdResult(third.get()));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                queue.put(new FourthResult(fourth.get()));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();

        while (i != 0) {
            try {
                Object result = queue.take();
                if (result instanceof FirstResult) {
                    results.setFirst(((FirstResult) result).list);
                } else if (result instanceof SecondResult) {
                    results.setSecond(((SecondResult) result).list);
                } else if (result instanceof ThirdResult) {
                    results.setThird(((ThirdResult) result).list);
                } else if (result instanceof FourthResult) {
                    results.setFourth(((FourthResult) result).list);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i--;
        }
    }
}
