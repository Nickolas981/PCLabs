package main;

import models.TaskResults;
import models.Tasks;

public interface TaskExecutor {
    TaskResults execute(Tasks tasks);
}
