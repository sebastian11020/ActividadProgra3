package presenter;
import view.*;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
class ExpressionEvaluatorPresenter {
    private ExpressionEvaluatorView view;

    public ExpressionEvaluatorPresenter(ExpressionEvaluatorView view) {
        this.view = view;
    }

    public void evaluateExpression(String expression) {
        try {
            double result = evaluate(expression);
            view.showResult(result);
        } catch (Exception e) {
            view.showError("Error evaluating expression: " + e.getMessage());
        }
    }

    private double evaluate(String expression) {
        Stack<Double> numbers = new Stack<>();
        Stack<String> operators = new Stack<>();

        Map<String, BinaryOperator<Double>> binaryOperators = new HashMap<>();
        binaryOperators.put("+", (a, b) -> a + b);
        binaryOperators.put("-", (a, b) -> a - b);
        binaryOperators.put("*", (a, b) -> a * b);
        binaryOperators.put("/", (a, b) -> a / b);

        Map<String, Function<Double, Double>> unaryFunctions = new HashMap<>();
        unaryFunctions.put("sin", Math::sin);
        unaryFunctions.put("cos", Math::cos);

        Map<String, Integer> precedence = new HashMap<>();
        precedence.put("+", 1);
        precedence.put("-", 1);
        precedence.put("*", 2);
        precedence.put("/", 2);

        StringTokenizer tokenizer = new StringTokenizer(expression, "+-*/() ", true);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.isEmpty()) {
                continue;
            }

            if (Character.isDigit(token.charAt(0))) {
                numbers.push(Double.parseDouble(token));
            } else if (binaryOperators.containsKey(token)) {
                while (!operators.isEmpty() && binaryOperators.containsKey(operators.peek()) &&
                        precedence.get(operators.peek()) >= precedence.get(token)) {
                    double b = numbers.pop();
                    double a = numbers.pop();
                    numbers.push(binaryOperators.get(operators.pop()).apply(a, b));
                }
                operators.push(token);
            } else if (unaryFunctions.containsKey(token)) {
                operators.push(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.peek().equals("(")) {
                    String operator = operators.pop();
                    if (operator.equals("(")) {
                        break;
                    }
                    if (binaryOperators.containsKey(operator)) {
                        double b = numbers.pop();
                        double a = numbers.pop();
                        numbers.push(binaryOperators.get(operator).apply(a, b));
                    } else if (unaryFunctions.containsKey(operator)) {
                        double a = numbers.pop();
                        numbers.push(unaryFunctions.get(operator).apply(a));
                    }
                }
                operators.pop();
            }
        }

        while (!operators.isEmpty()) {
            String operator = operators.pop();
            if (binaryOperators.containsKey(operator)) {
                double b = numbers.pop();
                double a = numbers.pop();
                numbers.push(binaryOperators.get(operator).apply(a, b));
            } else if (unaryFunctions.containsKey(operator)) {
                double a = numbers.pop();
                numbers.push(unaryFunctions.get(operator).apply(a));
            }
        }

        return numbers.pop();
    }
}
