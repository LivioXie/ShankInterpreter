package Shanks;
import java.util.List;
import java.util.Scanner;


public class BuiltInRead extends Shanks.FunctionNode {
    private static Scanner scanner = new Scanner(System.in);
    
    public BuiltInRead() {
        super("read");
    }
    
    @Override
    public boolean isVariadic() {
        return true;
    }
    
    @Override
    public void execute(List<InterpreterDataType> parameters) {
        // All parameters must be var parameters
        for (InterpreterDataType param : parameters) {
            System.out.print("Enter value: ");
            String input = scanner.nextLine();
            param.FromString(input);
        }
    }
}