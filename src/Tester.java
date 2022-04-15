import java.util.Scanner;

public class Tester{
    public static void main(String[] args){
        boolean running = true;
        System.out.println("ENTER MATH EXPRESSION");
        System.out.println("OPERATORS: +, -, *, /, ^, sqrt");
        System.out.println("ENTER \"EXIT\" TO END");
        Scanner scanner = new Scanner(System.in);
        while(running){
            System.out.print("ENTER HERE: ");
            String in = scanner.nextLine().toLowerCase();
            if(in.equals("exit")) running = false;
            else Calculator.calculate(in);
        }
        System.out.println("DONE");

    }
}
