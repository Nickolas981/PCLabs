package main;

import fileio.TaskFileManager;
import inputgenerator.InputProvider;
import inputgenerator.RandomInputGenerator;
import lab1.ThreadAltTaskExecutor;
import lab1.ThreadAltTaskExecutor_;
import lab1.ThreadTaskExecutor;
import lab2.CyclicBarrierTaskExecutor;
import lab2.CyclicBarrierTaskExecutor_;
import lab3.ExecutorsTaskExecutor;
import lab3.ExecutorsTaskExecutor_;
import models.TaskResults;
import models.Tasks;
import test.BlockingTaskExecutor;

public class Main {

    private static final boolean DEBUG = true;

    public static void main(String[] args) {

        TaskFileManager fileManager = new TaskFileManager();
        generateFile(fileManager);
        Tasks tasks = fileManager.load();

        executeBlocking(tasks);
        executeThreads(tasks);
        executeThreadsAlts(tasks);
        executeCyclicBarrier(tasks);
        executeExecutors(tasks);
//        MatrixHelper.test(tasks.getFirstTask());
    }

    private static void executeBlocking(Tasks tasks) {
        executeTasks(tasks, new BlockingTaskExecutor(), "Blocking");
    }

    private static void executeThreads(Tasks tasks) {
        executeTasks(tasks, new ThreadTaskExecutor(), "Thread");
    }
    private static void executeThreadsAlts(Tasks tasks) {
        executeTasks(tasks, new ThreadAltTaskExecutor_(), "ThreadAlt");
    }

    private static void executeExecutors(Tasks tasks) {
        executeTasks(tasks, new ExecutorsTaskExecutor_(), "Executors");
    }

    private static void executeCyclicBarrier(Tasks tasks) {
        executeTasks(tasks, new CyclicBarrierTaskExecutor_(), "CyclicBarrier");
    }

    private static void executeTasks(Tasks tasks, TaskExecutor executor, String tag) {
        System.out.println(tag);
        long start = System.currentTimeMillis();
        TaskResults execute = executor.execute(tasks);
        if (!DEBUG) {
            execute.print();
        } else {
            System.out.println("Time:" + (System.currentTimeMillis() - start));
        }
    }

    private static void generateFile(TaskFileManager fileManager) {
        InputProvider generator = new RandomInputGenerator();
        fileManager.save(generator.generate(3));
    }
}
