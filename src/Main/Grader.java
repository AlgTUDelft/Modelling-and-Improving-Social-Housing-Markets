package Main;

import java.io.Serializable;
import java.util.function.BiFunction;

public class Grader implements Serializable {

    private BiFunction<Integer, Integer, Float> grader;

    public Grader(BiFunction<Integer, Integer, Float> grader) {
        this.grader = grader;
    }

    public float apply(int id1, int id2) {
        return this.grader.apply(id1, id2);
    }
}
