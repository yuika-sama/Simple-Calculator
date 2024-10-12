package com.mycompany.simplecalculator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;


public class Calclate extends JFrame{
    private final JTextField display;
    private boolean isOp = false;
    public Calclate() {
        JFrame frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 550);

        display = new JTextField();
        display.setEditable(false);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setFont(new Font("Arial", Font.BOLD, 30));
        display.setBackground(Color.WHITE);
        display.setPreferredSize(new Dimension(400, 70));

        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 10 ,10));
        String[] buttons = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "C", "CE", "%"
        };

        for (String button:buttons){
            JButton btn = new JButton(button);
            btn.setFont(new Font("Arial", Font.BOLD, 30));
            btn.setBackground(Color.LIGHT_GRAY);
            btn.addActionListener(new ButtonClickListener());
            buttonPanel.add(btn);
        }

        frame.setLayout(new BorderLayout());
        frame.add(display, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            try {
                if (isNumeric(command) || command.equals(".")){
                    if (isOp){
                        display.setText(display.getText() + command);
                        isOp = false;
                    } else {
                        display.setText(display.getText() + command);
                    }
                } else {
                    switch (command){
                        case "C":
                            resetCalclate();
                            break;
                        case "CE":
                            display.setText("");
                            break;
                        case "=":
                            String fullExpression = display.getText();
                            double res = evalExpression(fullExpression);
                            display.setText(formatResult(res));
                            break;
                        case "+": case "-": case "*": case "/": case "%":
                            display.setText(display.getText() + " " + command + " ");
                            isOp = true;
                            break;
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    public double evalExpression(String expression) {
        try {
            return evaluate(expression);
        } catch (Exception e) {
            System.err.println("Error evaluating expression: " + e.getMessage());
            return 0;
        }
    }

    private double evaluate(String expression) {
        char[] tokens = expression.toCharArray();
        Stack<Double> values = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i] == ' ')
                continue;

            if (tokens[i] >= '0' && tokens[i] <= '9') {
                StringBuilder sbuf = new StringBuilder();
                while (i < tokens.length && (tokens[i] >= '0' && tokens[i] <= '9' || tokens[i] == '.'))
                    sbuf.append(tokens[i++]);
                values.push(Double.parseDouble(sbuf.toString()));
                i--;
            } else if (tokens[i] == '(') {
                ops.push(tokens[i]);
            } else if (tokens[i] == ')') {
                while (ops.peek() != '(')
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                ops.pop();
            } else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/') {
                while (!ops.isEmpty() && hasPrecedence(tokens[i], ops.peek()))
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                ops.push(tokens[i]);
            }
        }

        while (!ops.isEmpty())
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));

        return values.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')')
            return false;
        return (op1 != '*' && op1 != '/') || (op2 != '+' && op2 != '-');
    }

    private double applyOp(char op, double b, double a) {
        return switch (op) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0)
                    throw new UnsupportedOperationException("Cannot divide by zero");
                yield a / b;
            }
            default -> 0;
        };
    }
    private String formatResult(double result) {
        if (result == (int) result) {
            return String.valueOf((int) result);
        } else {
            return String.valueOf(result);
        }
    }
    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
    private void resetCalclate(){
        display.setText("");
        isOp = false;
    }
    public static void main(String[] args) {
        new Calclate();
    }
}
