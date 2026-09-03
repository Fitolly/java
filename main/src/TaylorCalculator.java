public class TaylorCalculator {
    public double calculate(double x, double epsilon) {
        validateInput(x, epsilon);
        if (x == 0) {
            return 0;
        }
        double sum = 0;
        double currentTerm = -x;
        int n = 1;
        while (Math.abs(currentTerm) >= epsilon) {
            sum += currentTerm;
            n++;
            currentTerm = currentTerm * x * (n - 1) / n;
        }

        return sum;
    }

    public void validateInput(double x, double epsilon) {
        if (x < -1.0 || x >= 1.0) {
            throw new IllegalArgumentException(
                    "x must be in diapason of [-1, 1). initial value: " + x);
        }

        if (epsilon <= 0.0) {
            throw new IllegalArgumentException(
                    "epsilon must be positive. initial value: " + epsilon);
        }

        if (Double.isNaN(x) || Double.isNaN(epsilon) ||
                Double.isInfinite(x) || Double.isInfinite(epsilon)) {
            throw new IllegalArgumentException(
                    "x and epsilon must be finite numbers");
        }
    }


    public double calculateWithStandardFunction(double x) {
        validateInput(x, 1.0);
        return Math.log(1 - x);
    }
}