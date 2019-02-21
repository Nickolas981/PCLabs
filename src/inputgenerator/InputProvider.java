package inputgenerator;

import models.Tasks;

public interface InputProvider {
    Tasks generate(int capacity);
}
