package Shanks;
import java.util.List;

public class BuiltInLeft extends Shanks.FunctionNode {
    
    public BuiltInLeft() {
        super("left");
    }
    
    @Override
    public void execute(List<InterpreterDataType> parameters) {
        if (parameters.size() != 3) {
            throw new RuntimeException("Left function requires exactly 3 parameters");
        }
        
        // First parameter is the source string (read-only)
        if (!(parameters.get(0) instanceof StringDataType)) {
            throw new RuntimeException("First parameter of Left must be a string");
        }
        StringDataType source = (StringDataType) parameters.get(0);
        
        // Second parameter is the length (read-only)
        if (!(parameters.get(1) instanceof IntegerDataType)) {
            throw new RuntimeException("Second parameter of Left must be an integer");
        }
        IntegerDataType length = (IntegerDataType) parameters.get(1);
        
        // Third parameter is the result (var parameter)
        if (!(parameters.get(2) instanceof StringDataType)) {
            throw new RuntimeException("Third parameter of Left must be a string");
        }
        StringDataType result = (StringDataType) parameters.get(2);
        
        // Get the left substring
        String sourceStr = source.getValue();
        int lengthVal = length.getValue();
        
        if (lengthVal <= 0) {
            result.setValue("");
        } else {
            result.setValue(sourceStr.substring(0, Math.min(lengthVal, sourceStr.length())));
        }
    }
}