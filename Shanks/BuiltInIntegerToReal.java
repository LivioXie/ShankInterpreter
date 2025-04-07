package Shanks;
import java.util.List;


public class BuiltInIntegerToReal extends Shanks.FunctionNode {
    
    public BuiltInIntegerToReal() {
        super("integertoreal");
    }
    
    @Override
    public void execute(List<InterpreterDataType> parameters) {
        if (parameters.size() != 2) {
            throw new RuntimeException("IntegerToReal function requires exactly 2 parameters");
        }
        
        // First parameter is the integer (read-only)
        if (!(parameters.get(0) instanceof IntegerDataType)) {
            throw new RuntimeException("First parameter of IntegerToReal must be an integer");
        }
        IntegerDataType intValue = (IntegerDataType) parameters.get(0);
        
        // Second parameter is the result real (var parameter)
        if (!(parameters.get(1) instanceof RealDataType)) {
            throw new RuntimeException("Second parameter of IntegerToReal must be a real");
        }
        RealDataType realResult = (RealDataType) parameters.get(1);
        
        // Convert integer to real
        realResult.setValue((float) intValue.getValue());
    }
}