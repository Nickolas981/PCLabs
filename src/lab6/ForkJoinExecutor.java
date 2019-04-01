package lab6;

import lab5.BlockingQueueExecutor;
import models.TaskResults;
import models.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ForkJoinExecutor extends BlockingQueueExecutor {
    @Override
    protected void initAndSyncThreads(Tasks tasks, TaskResults results) {
        FutureTask<List<Float>> first = new FutureTask<>(new FirstTaskExecutor(tasks.getFirstTask()));
        FutureTask<List<Float>> second = new FutureTask<>(new SecondTaskExecutor(tasks.getSecondTask()));
        FutureTask<List<List<Float>>> third = new FutureTask<>(new ThirdTaskExecutor(tasks.getThirdTask()));
        FutureTask<List<List<Float>>> fourth = new FutureTask<>(new FourthTaskExecutor(tasks.getFourthTask()));

        List<RecursiveTask<Object>> queue = new ArrayList<>(4);

        new Thread(first).start();
        new Thread(second).start();
        new Thread(third).start();
        new Thread(fourth).start();

        int i = 4;

        queue.add(new RecursiveTask<Object>() {
            @Override
            protected FirstResult compute() {
                try {
                    return new FirstResult(first.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        queue.add(new RecursiveTask<Object>() {
            @Override
            protected SecondResult compute() {
                try {
                    return new SecondResult(second.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        queue.add(new RecursiveTask<Object>() {
            @Override
            protected ThirdResult compute() {
                try {
                    return new ThirdResult(third.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        queue.add(new RecursiveTask<Object>() {
            @Override
            protected FourthResult compute() {
                try {
                    return new FourthResult(fourth.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        new ForkJoinPool().invoke(new Executor(queue, results));
    }

    private class Executor extends RecursiveAction {


        private List<RecursiveTask<Object>> subtasks;
        private TaskResults results;

        public Executor(List<RecursiveTask<Object>> subtasks, TaskResults results) {
            this.subtasks = subtasks;
            this.results = results;
        }

        @Override
        protected void compute() {
            for (RecursiveTask<Object> task : subtasks) {
                task.fork();
            }
            for (RecursiveTask<Object> task : subtasks) {
                var result = task.join();
                if (result instanceof FirstResult) {
                    results.setFirst(((FirstResult) result).list);
                } else if (result instanceof SecondResult) {
                    results.setSecond(((SecondResult) result).list);
                } else if (result instanceof ThirdResult) {
                    results.setThird(((ThirdResult) result).list);
                } else if (result instanceof FourthResult) {
                    results.setFourth(((FourthResult) result).list);
                }
            }
        }
    }

}
