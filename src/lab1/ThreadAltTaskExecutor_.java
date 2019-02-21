package lab1;

import main.ATaskExecutor;
import models.*;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static helpers.MatrixHelper.*;

public class ThreadAltTaskExecutor_ extends ATaskExecutor {
    private Integer firstBarrier = 0;
    private Integer secondBarrier = 0;
    private Integer thirdBarrier = 0;
    private Integer fourthBarrier = 0;

    private synchronized void increase(Integer barrier) {
        barrier++;
    }

    private void waitLoop(Integer barrier) {
        while (barrier % chunkCount != 0) {
        }
    }

    @Override
    protected void syncFirst() {
        increase(firstBarrier);
        waitLoop(firstBarrier);
    }

    @Override
    protected void syncSecond() {
        increase(secondBarrier);
        waitLoop(secondBarrier);
    }

    @Override
    protected void syncThird() {
        increase(thirdBarrier);
        waitLoop(thirdBarrier);
    }

    @Override
    protected void syncFourth() {
        increase(fourthBarrier);
        waitLoop(fourthBarrier);
    }
}
