package Shanks;
import java.util.List;


public class BuiltInSquareRoot extends Shanks.FunctionNode {
    
    public BuiltInSquareRoot() {
        super("squareroot");
    }
    
    @Override
    public void execute(List<InterpreterDataType> parameters) {
        if (parameters.size() != 2) {
            throw new RuntimeException("SquareRoot function requires exactly 2 parameters");
        }
        
        // First parameter is the number (read-only)
        InterpreterDataType number = parameters.get(0);
        float value;
        
        if (number instanceof IntegerDataType) {
            value = ((IntegerDataType) number).getValue();
        } else if (number instanceof RealDataType) {
            value = ((RealDataType) number).getValue();
        } else {
            throw new RuntimeException("First parameter of SquareRoot must be a number");
        }
        
        // Second parameter is the result (var parameter)
        if (!(parameters.get(1) instanceof RealDataType)) {
            throw new RuntimeException("Second parameter of SquareRoot must be a real");
        }
        RealDataType result = (RealDataType) parameters.get(1);
        
        // Calculate square root
        if (value < 0) {
            throw new RuntimeException("Cannot calculate square root of a negative number");
        }
        
        result.setValue((float) Math.sqrt(value));
    }
}