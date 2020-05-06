package Main;

public enum GradingStrategy {
    ExpDistLowLambda,
    ExpDistHighLambda,
    ExpDistLowLambdaConstrained,
    ExpDistHighLambdaConstrained,
    MatchingEvaluatorAVG,
    MatchingEvaluatorMIN,
//    PerfectAgreement,
    NormalDistLowVar,
    NormalDistHighVar,
    NormalDistLowVarConstrained,
    NormalDistHighVarConstrained,
    Random
}
