package Main;

import HousingMarket.House.House;
import HousingMarket.HouseAndHouseholdIDPair;
import HousingMarket.Household.Household;
import Matching.MatchingEvaluator;
import Matching.Matching;
import org.apache.commons.collections4.iterators.ArrayIterator;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;
import java.util.function.BiFunction;

public class GraderCreator {

    private GradingStrategy gradingStrategy;

    public GraderCreator(GradingStrategy gradingStrategy) {
        this.gradingStrategy = gradingStrategy;
    }

    public Grader createGrader(Matching matching) {
        BiFunction<Integer, Integer, Float> func = null;

        switch (gradingStrategy) {
            case MatchingEvaluatorAVG:
            case MatchingEvaluatorMIN:
                MatchingEvaluator matchingEvaluator = new MatchingEvaluator(matching, gradingStrategy);
                func = (BiFunction<Integer, Integer, Float> & Serializable)
                        (Integer id1, Integer id2) -> {
                            float result = 0;
                            try {
                                // TODO: Isolate ME.
                                result = matchingEvaluator.evaluateIndividualTotalFit(id1, id2);
                            } catch (MatchingEvaluator.HouseholdIncomeTooHighException e) {
                                e.printStackTrace();
                            }
                            return result;
                        };
                break;
            case Random:
                HashMap<HouseAndHouseholdIDPair, Float> randomMap = new HashMap();
                for (House house : matching.getHouses()) {
                    for (Household household : matching.getHouseholds()) {
                        float rand = new Random().nextFloat();
                        randomMap.put(new HouseAndHouseholdIDPair(house.getID(), household.getID()), rand);
                    }
                }
                func = (BiFunction<Integer, Integer, Float> & Serializable)
                        (Integer id1, Integer id2) -> randomMap.get(new HouseAndHouseholdIDPair(id1, id2));
                break;
            case NormalDistLowVar:
            case NormalDistHighVar:
                func = calculateNormalDistributionValues(matching);
                break;
            case ExpDistLowLambda:
            case ExpDistHighLambda:
                func = calculateExponentialDistributionValues(matching);
                break;
        }
        return new Grader(func);
    }

    public BiFunction<Integer, Integer, Float> calculateNormalDistributionValues(Matching matching) {
        double mean = 0.5;
        double var = 1/6f; // Default value, unused.
        switch (gradingStrategy) {
            case NormalDistLowVar:
                var = 1/18f;
                break;
            case NormalDistHighVar:
                var = 1/6f;
                break;
        }
        NormalDistribution normalDistribution = new NormalDistribution(mean, var);
        double[] samples = normalDistribution.sample(matching.getHouses().size() * matching.getHouseholds().size());
        ArrayIterator<Double> samplesIterator = new ArrayIterator(samples);
        HashMap<HouseAndHouseholdIDPair, Float> valuesMap = new HashMap();
        for (House house : matching.getHouses()) {
            for (Household household : matching.getHouseholds()) {
                double val = samplesIterator.next(); // Exists by definition.
                // These two cases should happen very rarely in both gradingStrategies.
                if (val < 0) {
                    val = 0;
                } else if (val > 1) {
                    val = 1;
                }
                valuesMap.put(new HouseAndHouseholdIDPair(house.getID(), household.getID()), (float) val);
            }
        }
        return (BiFunction<Integer, Integer, Float> & Serializable)
                (Integer id1, Integer id2) -> valuesMap.get(new HouseAndHouseholdIDPair(id1, id2));
    }

    public BiFunction<Integer,Integer,Float> calculateExponentialDistributionValues(Matching matching) {
        double lambda = 1; // Default value, unused.
        switch(gradingStrategy) {
            case ExpDistLowLambda:
                lambda = 1;
                break;
            case ExpDistHighLambda:
                lambda = 5;
                break;
        }
//        ExponentialDistribution exponentialDistribution = new ExponentialDistribution(mean);
        double[] samples = createExponentialSamples(matching.getHouses().size() * matching.getHouseholds().size(), lambda); //exponentialDistribution.sample(matching.getHouses().size() * matching.getHouseholds().size());
        ArrayIterator<Double> samplesIterator = new ArrayIterator(samples);
        HashMap<HouseAndHouseholdIDPair, Float> valuesMap = new HashMap();
        for (House house : matching.getHouses()) {
            for (Household household : matching.getHouseholds()) {
                double val = samplesIterator.next(); // Exists by definition.
                valuesMap.put(new HouseAndHouseholdIDPair(house.getID(), household.getID()), (float) val);
            }
        }
        return (BiFunction<Integer, Integer, Float> & Serializable)
                (Integer id1, Integer id2) -> valuesMap.get(new HouseAndHouseholdIDPair(id1, id2));
    }

    public double[] createExponentialSamples(int count, double lambda) {
        double[] results = new double[count];
        for (int i = 0; i < count; i++) {
            // Exponential formula with lower limit of 0, upper limit of 1.
            results[i] = (- Math.log(1 - (1 - Math.exp(-lambda)) * new Random().nextFloat()) / lambda);
        }
        return results;
    }
}

