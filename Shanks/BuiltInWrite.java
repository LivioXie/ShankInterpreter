package Shanks;
import java.util.List;

public class BuiltInWrite extends Shanks.FunctionNode {
    
    public BuiltInWrite() {
        super("write");
    }
    
    @Override
    public boolean isVariadic() {
        return true;
    }
    
    @Override
    public void execute(List<InterpreterDataType> parameters) {
        for (InterpreterDataType param : parameters) {
            System.out.print(param.ToString() + " ");
        }
        System.out.println();
    }
}