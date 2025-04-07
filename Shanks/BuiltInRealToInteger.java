package Shanks;
import java.util.List;


public class BuiltInRealToInteger extends Shanks.FunctionNode {
    
    public BuiltInRealToInteger() {
        super("realtointeger");
    }
    
    @Override
    public void execute(List<InterpreterDataType> parameters) {
        if (parameters.size() != 2) {
            throw new RuntimeException("RealToInteger function requires exactly 2 parameters");
        }
        
        // First parameter is the real (read-only)
        if (!(parameters.get(0) instanceof RealDataType)) {
            throw new RuntimeException("First parameter of RealToInteger must be a real");
        }
        RealDataType realValue = (RealDataType) parameters.get(0);
        
        // Second parameter is the result integer (var parameter)
        if (!(parameters.get(1) instanceof IntegerDataType)) {
            throw new RuntimeException("Second parameter of RealToInteger must be an integer");
        }
        IntegerDataType intResult = (IntegerDataType) parameters.get(1);
        
        // Convert real to integer (truncate)
        intResult.setValue((int) realValue.getValue());
    }
}