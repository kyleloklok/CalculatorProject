import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class Calculator {
    private static final String INPUT_ERROR = "SYNTAX ERROR: INVALID INPUT";
    private static String lastInput = "";
    private static String lastAnswer = "";

    public static void calculate(String in){
        try{
            if(in.equals("") && !lastInput.equals("")){
                System.out.println(lastAnswer);
                return;
            }
            lastInput = in;
            LinkedList<String> inList = addtoList(in);
            ArrayList<String> rpnList = convertToRPN(inList);
            String answer = getResult(rpnList);
            lastAnswer = answer;
            System.out.println(answer);
        } catch(NullPointerException e){
            System.out.println(INPUT_ERROR);
        }
    }

    private static String getResult(ArrayList<String> in){
        Stack<Double> exp = new Stack<>();
        for(String token : in){
            if(isLetterOrDigit(token) && !token.equals("sqrt")) {
                try{
                    Double num = Double.parseDouble(token);
                    exp.push(num);
                } catch(Exception e){
                    return INPUT_ERROR;
                }
            } else {
                exp.push(eval(exp, token));
            }
        }
        Double answer = exp.pop();
        if(answer == null) return INPUT_ERROR;
        Double result = (double) Math.round(answer * 100000d) / 100000d;
        return String.valueOf(result);
    }

    private static Double eval(Stack<Double> in, String op){
        double val = in.pop();
        if(op.equals("sqrt")){
            try{
                return Math.sqrt(val);
            } catch(Exception e){
                return null;
            }
        }
        switch (op) {
            case "+":
                try {
                    return val + in.pop();
                } catch (Exception e) {
                    return null;
                }
            case "-":
                try {
                    return in.pop() - val;
                } catch (Exception e) {
                    return null;
                }
            case "*":
                try {
                    return val * in.pop();
                } catch (Exception e) {
                    return null;
                }
            case "/":
                try {
                    if (val == 0) {
                        return null;
                    }
                    return in.pop() / val;
                } catch (Exception e) {
                    return null;
                }
            case "^":
                try {
                    return Math.pow(in.pop(), val);
                } catch (Exception e) {
                    return null;
                }
            default:
                return null;
        }
    }

    private static LinkedList<String> addtoList(String input){
        if(input.equals("")){
            return null;
        }
        String in = input.replaceAll("\\s+", "");
        String str = in.toLowerCase();
        LinkedList<String> inList = new LinkedList<>();
        int start = 0;
        int end;
        for(int i = 0; i <= str.length(); i++){
            if(i < str.length()){
                char ch = str.charAt(i);
                if(!isLetterOrDigit(ch) && ch != '.'){
                    end = i;
                    addtoList(inList, str, start, end);
                    start = end + 1;
                    inList.add(Character.toString(ch));
                }
            } else if (i == str.length()){
                    addtoList(inList, str, start, i);
            }
        }
        return inList;
    }

    private static ArrayList<String> convertToRPN(LinkedList<String> in){
        ArrayList<String> rpn = new ArrayList<>();
        Stack<String> ops = new Stack<>();
        boolean sign = true;
        boolean rightParantheses = false;
        for(String token : in){
            if(isLetterOrDigit(token) && !token.equals("sqrt")){
                rpn.add(token);
                if(rightParantheses) ops.push("*");
                rightParantheses = false;
                sign = false;
            }
            else if(token.equals("(")){
                if(!sign) ops.push("*");
                else if(rightParantheses) ops.push("*");
                rightParantheses = false;
                ops.push(token);
            }
            else if(token.equals(")")){
                rightParantheses = true;
                sign = false;
                try{
                    while(!ops.peek().equals("(") && !ops.isEmpty()){
                        rpn.add(ops.pop());
                    }
                } catch(Exception e){
                    rpn.clear();
                    rpn.add(INPUT_ERROR);
                    return rpn;
                }
                ops.pop();
            }
            else if(sign && !token.equals("sqrt")){ //check if two operators are in a row -> error
                rpn.clear();
                rpn.add(INPUT_ERROR);
                return rpn;
            }
            else {
                rightParantheses = false;
                if(isValidOp(token)){
                    while(!ops.isEmpty() && getPriority(token) <= getPriority(ops.peek()) && !hasRightAssociativity(token)){
                        rpn.add(ops.pop());
                    }
                    ops.push(token);
                    sign = true;
                } else{
                    rpn.clear();
                    rpn.add(INPUT_ERROR);
                    return rpn;
                }
            }
        }
        while(!ops.isEmpty()){
            if(ops.peek().equals("(")){
                rpn.clear();
                rpn.add(INPUT_ERROR);
                return rpn;
            }
            rpn.add(ops.pop());
        }
        return rpn;
    }

    private static void addtoList(LinkedList<String> list, String str, int start, int end){
        if(start == end) return;
        list.add(str.substring(start, end));
    }

    private static boolean isLetterOrDigit(String str){
        char ch = str.charAt(0);
        return Character.isLetterOrDigit(ch);
    }

    private static boolean isLetterOrDigit(char ch){
        return Character.isLetterOrDigit(ch);
    }

    private static boolean hasRightAssociativity(String str){
        return switch (str) {
            case "+", "-", "*", "/" -> false;
            default -> true;
        };
    }

    private static int getPriority(String str){
        return switch(str){
            case "^", "sqrt"-> 2;
            case "*","/" -> 1;
            case "+","-" -> 0;
            default -> -1;
        };
    }

    private static boolean isValidOp(String str){
        return switch(str){
            case "^","sqrt","*","/","+","-" -> true;
            default -> false;
        };
    }
}
