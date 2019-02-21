package test;

import main.TaskExecutor;
import models.*;

import java.util.List;

import static helpers.MatrixHelper.*;

public class BlockingTaskExecutor implements TaskExecutor {
    @Override
    public TaskResults execute(Tasks tasks) {
        TaskResults taskResults = new TaskResults();

        executeFirstTask(taskResults, tasks.getFirstTask());
        executeSecondTask(taskResults, tasks.getSecondTask());
        executeThirdTask(taskResults, tasks.getThirdTask());
        executeFourthTask(taskResults, tasks.getFourthTask());

        return taskResults;
    }

    //A = B*MC+D*MM*a-B*MM
    private void executeFirstTask(TaskResults results, FirstTask firstTask) {

        results.setFirst(div(
                addVectors(
                        multiply(firstTask.getB(), firstTask.getMC()),
                        multiply(multiply(firstTask.getD(), firstTask.getMM()), firstTask.getA())),
                multiply(firstTask.getB(), firstTask.getMM())
                )
        );
    }

    //D=B*MZ+D*MX*a
    private void executeSecondTask(TaskResults results, SecondTask secondTask) {
        results.setSecond(addVectors(
                multiply(secondTask.getB(), secondTask.getMZ()),
                multiply(multiply(secondTask.getD(), secondTask.getMX()), secondTask.getA())
        ));
    }

    //MG=MB*MK+MC*(MX*MT+MM)
    private void executeThirdTask(TaskResults results, ThirdTask task) {
        results.setThird(
                add(
                        multiplyMatrix(task.getMB(), task.getMK()),
                        multiplyMatrix(
                                task.getMC(),
                                add(
                                        multiplyMatrix(task.getMX(), task.getMT()),
                                        task.getMM()
                                )
                        )
                )
        );
    }

    //MA = max(B-D)*MD*MT-MZ*(ME+MM)
    private void executeFourthTask(TaskResults results, FourthTask task) {

        List<Float> BD = div(task.getB(), task.getD());
        Float max = max(BD);


        List<List<Float>> BDMD = multiplyMatrix(
                task.getMD(),
                max
        );
        List<List<Float>> MEMM = add(task.getME(), task.getMM());
        List<List<Float>> MZMEMM = multiplyMatrix(task.getMZ(), MEMM);
        results.setFourth(
                divMatrix(
                        multiplyMatrix(
                                BDMD, task.getMT()
                        ),
                        MZMEMM
                )
        );
    }
}
