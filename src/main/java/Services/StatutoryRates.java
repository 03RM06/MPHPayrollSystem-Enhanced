package Services;

public final class StatutoryRates {

    private StatutoryRates() {}

    // -- SSS 2024 (SSS Circular 2023-033, effective January 2024) --
    public static final double SSS_EE_RATE     = 0.045;
    public static final double SSS_MSC_FLOOR   = 4000.0;
    public static final double SSS_MSC_CEILING = 30000.0;
    public static final double SSS_MSC_STEP    = 500.0;

    // -- PhilHealth 2024 (PhilHealth Circular 2023-0009) --
    public static final double PH_EE_RATE        = 0.025;
    public static final double PH_SALARY_FLOOR   = 10000.0;
    public static final double PH_SALARY_CEILING = 100000.0;

    // -- Pag-IBIG / HDMF 2024 (HDMF Circular 274) --
    public static final double PAGIBIG_RATE_LOW  = 0.01;
    public static final double PAGIBIG_RATE_HIGH = 0.02;
    public static final double PAGIBIG_THRESHOLD = 1500.0;
    public static final double PAGIBIG_BASE_CAP  = 5000.0;
    public static final double PAGIBIG_MAX_EE    = 100.0;

    // -- Withholding Tax -- TRAIN Law (NIRC as amended, monthly, effective Jan 2023+) --
    // {income floor, base tax, marginal rate}
    private static final double[][] WHT_BRACKETS = {
        {      0.0,        0.0,  0.00},
        {  20833.0,        0.0,  0.15},
        {  33333.0,     2500.0,  0.20},
        {  66667.0,    10833.33, 0.25},
        { 166667.0,    40833.33, 0.30},
        { 666667.0,   200833.33, 0.35}
    };

    public static double computeSSS(double monthlySalary) {
        double msc = Math.round(monthlySalary / SSS_MSC_STEP) * SSS_MSC_STEP;
        msc = Math.max(SSS_MSC_FLOOR, Math.min(SSS_MSC_CEILING, msc));
        return msc * SSS_EE_RATE;
    }

    public static double computePhilHealth(double monthlySalary) {
        double base = Math.max(PH_SALARY_FLOOR, Math.min(PH_SALARY_CEILING, monthlySalary));
        return base * PH_EE_RATE;
    }

    public static double computePagIbig(double monthlySalary) {
        if (monthlySalary <= 0) return 0.0;
        double rate = (monthlySalary <= PAGIBIG_THRESHOLD) ? PAGIBIG_RATE_LOW : PAGIBIG_RATE_HIGH;
        double base = Math.min(monthlySalary, PAGIBIG_BASE_CAP);
        return Math.min(base * rate, PAGIBIG_MAX_EE);
    }

    public static double computeWithholdingTax(double taxableMonthlyIncome) {
        if (taxableMonthlyIncome <= WHT_BRACKETS[1][0]) return 0.0;
        for (int i = WHT_BRACKETS.length - 1; i >= 0; i--) {
            if (taxableMonthlyIncome > WHT_BRACKETS[i][0]) {
                double excess = taxableMonthlyIncome - WHT_BRACKETS[i][0];
                return WHT_BRACKETS[i][1] + excess * WHT_BRACKETS[i][2];
            }
        }
        return 0.0;
    }
}
