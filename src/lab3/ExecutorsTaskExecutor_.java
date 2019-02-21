package lab3;

import lab2.CyclicBarrierTaskExecutor_;
import models.*;

import java.util.concurrent.*;

public class ExecutorsTaskExecutor_ extends CyclicBarrierTaskExecutor_ {
    @Override
    protected void initAndSyncThreads(Tasks tasks, TaskResults results) {
        ExecutorService service = Executors.newCachedThreadPool();

        for (int i = 1; i <= chunkCount; i++) {
            service.submit(new FirstTaskExecutor(tasks.getFirstTask(), i));
            service.submit(new SecondTaskExecutor(tasks.getSecondTask(), i));
            service.submit(new ThirdTaskExecutor(tasks.getThirdTask(), i));
            service.submit(new FourthTaskExecutor(tasks.getFourthTask(), i));
        }
        service.shutdown();
        try {
            service.awaitTermination(5000, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
