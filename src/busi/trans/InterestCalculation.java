package busi.trans;

/**
 * Created by dell on 2017/4/26.
 */
public class InterestCalculation {
    /*活期、定期年利率*/
    private static final double DEMAND_DEPOSIT_RATE = 0.0035;
    private static final double THREE_MONTH_RATE = 0.011;
    private static final double SIX_MONTH_RATE = 0.013;
    private static final double ONE_YEAR_RATE = 0.015;
    private static final double TWO_YEAR_RATE = 0.021;
    private static final double THREE_YEAR_RATE = 0.0275;
    private static final double SEVEN_DAY_CALL_RATE = 0.0135;

    public static double getDemandDepositRate() {
        return DEMAND_DEPOSIT_RATE;
    }

    public static double getThreeMonthRate() {
        return THREE_MONTH_RATE;
    }

    public static double getSixMonthRate() {
        return SIX_MONTH_RATE;
    }

    public static double getOneYearRate() {
        return ONE_YEAR_RATE;
    }

    public static double getTwoYearRate() {
        return TWO_YEAR_RATE;
    }

    public static double getThreeYearRate() {
        return THREE_YEAR_RATE;
    }

    /*定存销户，活期部分 （定存提前支取按活期利率计算） ：
     利息=按活期计算金额 x（活期年利率/360天）x 按活期天数*/
    public double calcDemandInterPerDay(double amount, int days) {
        double inter = 0;
        inter = amount * (DEMAND_DEPOSIT_RATE / 360) * days;
        return inter;
    }

    /*定存销户结息 三个月*/
    public double calcThreeMonInter(double amount) {
        double inter = 0;
        inter = amount * (THREE_MONTH_RATE / 12) * 3;
        return inter;
    }

    /*定存销户结息 六个月*/
    public double calcSixMonInter(double amount) {
        double inter = 0;
        inter = amount * (SIX_MONTH_RATE / 12) * 6;
        ;
        return inter;
    }

    /*定存销户结息 一年*/
    public double calcOneYearInter(double amount) {
        double inter = 0;
        inter = amount * ONE_YEAR_RATE * 1;
        ;
        return inter;
    }

    /*定存销户结息 两年*/
    public double calcTwoYearInter(double amount) {
        double inter = 0;
        inter = amount * TWO_YEAR_RATE * 2;
        return inter;
    }

    /*定存销户结息 三年*/
    public double calcThreeYearInter(double amount) {
        double inter = 0;
        inter = amount * THREE_YEAR_RATE * 3;
        return inter;
    }

    /*通知存款销户结息*/
    public double calc7DayCallInter(double amount, int periodCount_7) {
        double inter = 0.00;
        double rate = (SEVEN_DAY_CALL_RATE / 360) * 7;
        inter = amount * periodCount_7 * rate;
        return inter;
    }

}
