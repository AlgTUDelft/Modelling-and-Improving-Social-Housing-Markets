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

    public GraderCreator() {
    }

    public Grader createGrader(Matching matching) {
        HashMap<GradingStrategy, BiFunction<Integer, Integer, Float>> stratToFunction = new HashMap<>();
        double mean = 0.5;

        NormalDistribution normalDistributionLow = new NormalDistribution(mean, 1/18f);
        double[] samplesNormalLow = normalDistributionLow.sample(matching.getHouses().size() * matching.getHouseholds().size());

        NormalDistribution normalDistributionHigh = new NormalDistribution(mean, 1/6f);
        double[] samplesNormalHigh = normalDistributionHigh.sample(matching.getHouses().size() * matching.getHouseholds().size());

        double[] samplesExpLow = createExponentialSamples(matching.getHouses().size() * matching.getHouseholds().size(), 1);
        double[] samplesExpHigh = createExponentialSamples(matching.getHouses().size() * matching.getHouseholds().size(), 5);

        for (GradingStrategy gradingStrategy : GradingStrategy.values()) {
            BiFunction<Integer, Integer, Float> func = null;

            switch (gradingStrategy) {
                case MatchingEvaluatorAVG:
                case MatchingEvaluatorMIN:
                    MatchingEvaluator matchingEvaluator = new MatchingEvaluator(matching, gradingStrategy);
                    func = (BiFunction<Integer, Integer, Float> & Serializable)
                            (Integer id1, Integer id2) -> {
                                float result = 0;
                                try {
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
                    func = samplesToFunc(matching, samplesNormalLow,false);
                    break;
                case NormalDistLowVarConstrained:
                    func = samplesToFunc(matching, samplesNormalLow,true);
                    break;
                case NormalDistHighVar:
                    func = samplesToFunc(matching, samplesNormalHigh, false);
                    break;
                case NormalDistHighVarConstrained:
                    func = samplesToFunc(matching, samplesNormalHigh,true);
                    break;
                case ExpDistLowLambda:
                    func = samplesToFunc(matching, samplesExpLow, false);
                    break;
                case ExpDistLowLambdaConstrained:
                    func = samplesToFunc(matching, samplesExpLow, true);
                    break;
                case ExpDistHighLambdaConstrained:
                    func = samplesToFunc(matching, samplesExpHigh, false);
                    break;
                case ExpDistHighLambda:
                    func = samplesToFunc(matching, samplesExpHigh, true);
                    break;
            }
            stratToFunction.put(gradingStrategy, func);
        }
        return new Grader(stratToFunction);
    }

    public BiFunction<Integer, Integer, Float> samplesToFunc(Matching matching, double[] samples, boolean constrain) {
        ArrayIterator<Double> samplesIterator; samplesIterator = new ArrayIterator(samples);
        HashMap<HouseAndHouseholdIDPair, Float> valuesMap = new HashMap();
        for (House house : matching.getHouses()) {
            for (Household household : matching.getHouseholds()) {
                double val = samplesIterator.next(); // Exists by definition.
                if (constrain) {
                    val = Math.round(val * 3)/3f;
                }
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


    public double[] createExponentialSamples(int count, double lambda) {
        double[] results = new double[count];
        for (int i = 0; i < count; i++) {
            // Exponential formula with lower limit of 0, upper limit of 1.
            results[i] = (- Math.log(1 - (1 - Math.exp(-lambda)) * new Random().nextFloat()) / lambda);
        }
        return results;
    }
}

