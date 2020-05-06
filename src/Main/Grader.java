package Main;

import java.io.Serializable;
import java.util.HashMap;
import java.util.function.BiFunction;

public class Grader implements Serializable {

    private HashMap<GradingStrategy, BiFunction<Integer, Integer, Float>> grader;

    public Grader(HashMap<GradingStrategy, BiFunction<Integer, Integer, Float>> stratToFunction) {
        this.grader = stratToFunction;
    }

    public float apply(int id1, int id2, GradingStrategy gradingStrategy) {
        return this.grader.get(gradingStrategy).apply(id1, id2);
    }
}
