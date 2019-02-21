/*
package lab4;

import main.ATaskExecutor;
import models.FirstTask;
import models.FourthTask;
import models.SecondTask;
import models.ThirdTask;

import java.util.List;
import java.util.concurrent.Callable;

public class LockTaskExecutor extends ATaskExecutor {
    private Integer firstBarrier = 0;
    private Integer secondBarrier = 0;
    private Integer thirdBarrier = 0;
    private Integer fourthBarrier = 0;

    @Override
    protected void syncFirst() {

    }

    @Override
    protected void syncSecond() {

    }

    @Override
    protected void syncThird() {

    }

    @Override
    protected void syncFourth() {

    }


    protected class FirstTaskExecutorCallable implements Callable<List<Float>> {

        private FirstTask firstTask;
        private int chunkNumber;


        public FirstTaskExecutorCallable(FirstTask firstTask, int chunkNumber) {
            this.firstTask = firstTask;
            this.chunkNumber = chunkNumber;
        }

        //A = B*MC+D*MM*a-B*MM
        @Override
        public List<Float> call() {
            new FirstTaskExecutor(firstTask, chunkNumber).run();
            return firstResult;
        }
    }

    protected class SecondTaskExecutorCallable implements Callable<List<Float>> {
        private SecondTask task;
        private int chunkNumber;

        public SecondTaskExecutorCallable(SecondTask task, int chunkNumber) {
            this.task = task;
            this.chunkNumber = chunkNumber;
        }

        //D=B*MZ+D*MX*a
        @Override
        public List<Float> call() {
            new SecondTaskExecutor(task, chunkNumber).run();
            return secondResult;
        }
    }

    protected class ThirdTaskExecutorCallable implements Callable<List<List<Float>>> {
        private ThirdTask task;
        private int chunkNumber;

        public ThirdTaskExecutorCallable(ThirdTask thirdTask, int chunkNumber) {
            this.task = thirdTask;
            this.chunkNumber = chunkNumber;
        }

        //MG=MB*MK+MC*(MX*MT+MM)
        @Override
        public List<List<Float>> call() {
            new ThirdTaskExecutor(task, chunkSize).run();
            return thirdResult;
        }
    }

    protected class FourthTaskExecutorCallable implements Callable<List<List<Float>>> {
        private FourthTask task;
        private int chunkNumber;

        public FourthTaskExecutorCallable(FourthTask fourthTask, int chunkCount) {
            this.task = fourthTask;
            this.chunkNumber = chunkCount;
        }

        //MA = max(B-D)*MD*MT-MZ*(ME+MM)
        @Override
        public List<List<Float>> call() {
            new FourthTaskExecutor(task, chunkCount).run();
            return thirdResult;
        }
    }
}
*/
