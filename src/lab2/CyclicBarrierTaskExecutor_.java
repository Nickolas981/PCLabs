package lab2;

import main.ATaskExecutor;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierTaskExecutor_ extends ATaskExecutor {

    private CyclicBarrier firstBarrier;
    private CyclicBarrier secondBarrier;
    private CyclicBarrier thirdBarrier;
    private CyclicBarrier fourthBarrier;

    @Override
    protected void initData() {
        super.initData();
        firstBarrier = new CyclicBarrier(chunkCount);
        secondBarrier = new CyclicBarrier(chunkCount);
        thirdBarrier = new CyclicBarrier(chunkCount);
        fourthBarrier = new CyclicBarrier(chunkCount);
    }

    @Override
    protected void syncFirst() {
        awaitBarrier(firstBarrier);
    }

    @Override
    protected void syncSecond() {
        awaitBarrier(secondBarrier);
    }

    @Override
    protected void syncThird() {
        awaitBarrier(thirdBarrier);
    }

    @Override
    protected void syncFourth() {
        awaitBarrier(fourthBarrier);
    }

    private void awaitBarrier(CyclicBarrier secondBarrier) {
        try {
            secondBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
