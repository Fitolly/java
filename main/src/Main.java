
//import javax.management.BadStringOperationException;
//import java.util.Locale;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final TaylorCalculator calculator = new TaylorCalculator();

    public static void main(String[] args) {
        printHeader();
        boolean continueProgram = true;

        while (continueProgram) {
            try {
                double x = inputXNum();
                int k = inputKNum();
                double epsilon = Math.pow(10, -k);

                printResults(x, epsilon);

            } catch (IllegalArgumentException e) {
                System.out.println("\nerror: " + e.getMessage());
                System.out.println("input again\n");
            } catch (InputMismatchException e) {
                System.out.println("\ninput a number, not a string");
                scanner.next();
                System.out.println("input again\n");
            }
            continueProgram = askToContinue();
        }

        scanner.close();
    }
    private static void printHeader() {
        System.out.println("Taylor's serie: ln(1-x) = -x - x²/2 - x³/3 - x⁴/4 - ...");
        System.out.println(" Diapason: x ∈ [-1, 1)");
        System.out.println();
    }

    private static void printResults(double x, double epsilon) {
        double taylorResult = calculator.calculate(x, epsilon);
        double standardResult = calculator.calculateWithStandardFunction(x);

        System.out.println("\nthe result");
        System.out.printf("Taylor's serie (ε = %.0e): %10.3f%n", epsilon, taylorResult);
        System.out.printf("Standart function: %10.3f%n", standardResult);
        System.out.printf("Difference: %10.3f%n", Math.abs(taylorResult - standardResult)); //форматирование циферок

    }

    private static double inputXNum() {
        while (true) {
            try {
                System.out.print("Write x in diapason of [-1;1): ");
                double x = scanner.nextDouble();
                calculator.validateInput(x, 1.0);
                return x;
            }
            catch (InputMismatchException e) {
                System.out.println("write a num");
                scanner.next();
            } catch (IllegalArgumentException e) {
                System.out.println("error: " + e.getMessage());
            }
        }
    }

    private static int inputKNum() {
        while (true) {
            try {
                System.out.print("input k (must be natural): ");
                int k = scanner.nextInt();

                if (k <= 0) {
                    System.out.println("k must be natural");
                    continue;
                }
                double epsilon = Math.pow(10, -k);
                return k;
            } catch (InputMismatchException e) {
                System.out.println("input integer number");
                scanner.next();
            }
        }
    }

    private static boolean askToContinue() {
        while (true) {
            System.out.print("\nDo you want to continue? (y/n): ");
            String answer = scanner.next().toLowerCase();
            System.out.println();

            if (answer.equals("y") || answer.equals("yes")) {
                return true;
            }

            if (answer.equals("n") || answer.equals("no")) {
                return false;
            }

            System.out.println("Error: please enter 'y' or 'n'");
        }
    }
}