import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Calculator {
    private static final String SYNTAX_ERROR = "SYNTAX ERROR: INVALID INPUT";
    private static final String DIVISION_BY_ZERO_ERROR = "ERROR: DIVISION BY ZERO";
    private static final String NONREAL_ANSWER_ERROR = "ERROR: NONREAL ANSWERS";
    private static String lastInput = "";
    private static String lastAnswer = "";

    public static void calculate(String in){
        try{
            if(in.equals("") && !lastInput.equals("")){
                System.out.println(lastAnswer);
                return;
            }
            lastInput = in;
            ArrayList<String> inList = addtoList(in);
            ArrayList<String> rpnList = convertToRPN(inList);
            String answer = getResult(rpnList);
            lastAnswer = answer;
            System.out.println(answer);
        } catch(NullPointerException e){
            System.out.println(SYNTAX_ERROR);
        }
    }
    private static String getResult(ArrayList<String> in){
        Stack<Double> exp = new Stack<>();
        for(String token : in){
            if(isDigit(token)) {
                try{
                    Double num = Double.parseDouble(token);
                    exp.push(num);
                } catch(Exception e){
                    return SYNTAX_ERROR;
                }
            } else {
                try{
                    exp.push(eval(exp, token));
                } catch(Exception e){
                    return SYNTAX_ERROR;
                }
            }
        }
        Double answer = exp.pop();
        if(answer == null) return SYNTAX_ERROR;
        else if(answer.isInfinite()) return DIVISION_BY_ZERO_ERROR;
        else if(answer.isNaN()) return NONREAL_ANSWER_ERROR;
        Double result = (double) Math.round(answer * 1000000000d) / 1000000000d;
        return String.valueOf(result);
    }

    private static Double eval(Stack<Double> in, String op){
        double val = in.pop();
        if(op.equals("sqrt")){
            try{
                if(val < 0) return Double.NaN;
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
                try {/*
                    if (val == 0) {
                        return null;
                    }
                    */
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

    private static ArrayList<String> addtoList(String input){
        if(input.equals("")){
            return null;
        }
        String in = input.replaceAll("\\s+", "");
        String str = in.toLowerCase();
        ArrayList<String> inList = new ArrayList<>();
        int start = 0;
        int end;
        boolean letter = false;
        for(int i = 0; i <= str.length(); i++){
            if(i < str.length()){
                char ch = str.charAt(i);
                if(!isLetterOrDigit(ch) && ch != '.'){
                    letter = false;
                    end = i;
                    addtoList(inList, str, start, end);
                    start = end + 1;
                    inList.add(Character.toString(ch));
                } else if(isLetter(ch) && !letter){
                    letter = true;
                    end = i;
                    addtoList(inList, str, start, end);
                    start = end;
                }
            } else if (i == str.length()){
                    addtoList(inList, str, start, i);
            }
        }
        return inList;
    }

    private static ArrayList<String> convertToRPN(ArrayList<String> in){
        ArrayList<String> rpn = new ArrayList<>();
        Stack<String> ops = new Stack<>();
        boolean sign = true;
        boolean rightParantheses = false;
        boolean num = false;
        for(String token : in){
            if(isDigit(token)){
                rpn.add(token);
                if(rightParantheses){
                    ops.push("*");
                }
                rightParantheses = false;
                sign = false;
                num = true;
            }
            else if(token.equals("(")){
                if(!sign || rightParantheses){
                    while(!ops.isEmpty() && getPriority("*") <= getPriority(ops.peek()) && !hasRightAssociativity("*")){
                        rpn.add(ops.pop());
                    }
                    ops.push("*");
                }
                sign = true;
                rightParantheses = false;
                num = false;
                ops.push(token);
            }
            else if(token.equals(")")){
                rightParantheses = true;
                sign = false;
                num = false;
                try{
                    while(!ops.peek().equals("(") && !ops.isEmpty()){
                        rpn.add(ops.pop());
                    }
                } catch(Exception e){
                    return errorMessage(rpn);
                }
                ops.pop();
            }
            else if(sign && !token.equals("sqrt")){ //check if two operators are in a row -> error
                return errorMessage(rpn);
            }
            else {
                if(isValidOp(token)){
                    while(!ops.isEmpty() && getPriority(token) <= getPriority(ops.peek()) && !hasRightAssociativity(token)){
                        rpn.add(ops.pop());
                    }
                    if(num && token.equals("sqrt") || rightParantheses && token.equals("sqrt")){
                        while(!ops.isEmpty() && getPriority("*") <= getPriority(ops.peek()) && !hasRightAssociativity("*")){
                            rpn.add(ops.pop());
                        }
                        ops.push("*");
                    }
                    ops.push(token);
                    num = false;
                    rightParantheses = false;
                    sign = true;
                } else{
                    return errorMessage(rpn);
                }
            }
        }
        while(!ops.isEmpty()){
            if(ops.peek().equals("(")){
                return errorMessage(rpn);
            }
            rpn.add(ops.pop());
        }
        if(rpn.isEmpty()){
            return errorMessage(rpn);
        }
        return rpn;
    }

    private static ArrayList<String> errorMessage(ArrayList<String> in){
        in.clear();
        in.add(SYNTAX_ERROR);
        return in;
    }

    private static void addtoList(ArrayList<String> list, String str, int start, int end){
        if(start == end) return;
        list.add(str.substring(start, end));
    }

    private static boolean isLetterOrDigit(char ch){
        return Character.isLetterOrDigit(ch);
    }

    private static boolean isLetter(char ch){
        return Character.isLetter(ch);
    }

    private static boolean isDigit(String str){
        char ch = str.charAt(0);
        return Character.isDigit(ch);
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
