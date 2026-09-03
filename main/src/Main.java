
import javax.management.BadStringOperationException;
import java.util.Locale;
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
                System.out.println("\n❌ Ошибка: " + e.getMessage());
                System.out.println("Попробуйте ввести данные заново.\n");
            } catch (InputMismatchException e) {
                System.out.println("\n❌ Ошибка: введите число, а не текст!");
                scanner.next();
                System.out.println("Попробуйте ввести данные заново.\n");
            }

            continueProgram = askToContinue();
        }

        System.out.println("Программа завершена. Спасибо за использование!");
        scanner.close();
    }

    /**
     * Выводит заголовок программы.
     */
    private static void printHeader() {
        System.out.println("=".repeat(60));
        System.out.println("  ВЫЧИСЛЕНИЕ ln(1-x) С ПОМОЩЬЮ РЯДА ТЕЙЛОРА");
        System.out.println("  Ряд: ln(1-x) = -x - x²/2 - x³/3 - x⁴/4 - ...");
        System.out.println("  Область определения: x ∈ [-1, 1)");
        System.out.println("=".repeat(60));
        System.out.println();
    }


    private static void printResults(double x, double epsilon) {
        double taylorResult = calculator.calculate(x, epsilon);
        double standardResult = calculator.calculateWithStandardFunction(x);

        System.out.println("\nthe result");
        System.out.printf("Taylor's serie (ε = %.0e): %10.3f%n", epsilon, taylorResult);
        System.out.printf("Standart function: %10.3f%n", standardResult);
        System.out.printf("Difference ", Math.abs(taylorResult - standardResult));

    }

    private static double inputXNum() {
        while (true) {
            try {
                System.out.print("Write x in diapason of [-1;1): ");
                double x = scanner.nextDouble();
                calculator.validateInput(x, 1.0);

                if (x == -1.0) {
                    System.out.print("⚠️ При x = -1 ряд сходится медленно. Продолжить? (да/нет): ");
                    String answer = scanner.next().toLowerCase();
                    if (!answer.equals("да") && !answer.equals("yes") && !answer.equals("y")) {
                        continue;
                    }
                }

                return x;

            } catch (InputMismatchException e) {
                System.out.println("❌ Ошибка: введите число!");
                scanner.next();
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Ошибка: " + e.getMessage());
            }
        }
    }

    /**
     * Вводит значение k с клавиатуры с проверкой.
     *
     * @return введенное значение k
     */
    private static int inputKNum() {
        while (true) {
            try {
                System.out.print("Введите значение k (натуральное число, k > 0): ");
                int k = scanner.nextInt();

                if (k <= 0) {
                    System.out.println("❌ Ошибка: k должно быть натуральным числом (k > 0)!");
                    continue;
                }

                double epsilon = Math.pow(10, -k);
                if (epsilon < 1e-15) {
                    System.out.println("⚠️ Предупреждение: очень маленькая точность (ε = " + epsilon + ")");
                    System.out.print("   Продолжить? (да/нет): ");
                    String answer = scanner.next().toLowerCase();
                    if (!answer.equals("да") && !answer.equals("yes") && !answer.equals("y")) {
                        continue;
                    }
                }

                return k;

            } catch (InputMismatchException e) {
                System.out.println("❌ Ошибка: введите целое число!");
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