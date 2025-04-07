package Shanks;
import java.util.List;

public class BuiltInRight extends Shanks.FunctionNode {
    
    public BuiltInRight() {
        super("right");
    }
    
    @Override
    public void execute(List<InterpreterDataType> parameters) {
        if (parameters.size() != 3) {
            throw new RuntimeException("Right function requires exactly 3 parameters");
        }
        
        // First parameter is the source string (read-only)
        if (!(parameters.get(0) instanceof StringDataType)) {
            throw new RuntimeException("First parameter of Right must be a string");
        }
        StringDataType source = (StringDataType) parameters.get(0);
        
        // Second parameter is the length (read-only)
        if (!(parameters.get(1) instanceof IntegerDataType)) {
            throw new RuntimeException("Second parameter of Right must be an integer");
        }
        IntegerDataType length = (IntegerDataType) parameters.get(1);
        
        // Third parameter is the result (var parameter)
        if (!(parameters.get(2) instanceof StringDataType)) {
            throw new RuntimeException("Third parameter of Right must be a string");
        }
        StringDataType result = (StringDataType) parameters.get(2);
        
        // Get the right substring
        String sourceStr = source.getValue();
        int lengthVal = length.getValue();
        
        if (lengthVal <= 0) {
            result.setValue("");
        } else {
            int startIndex = Math.max(0, sourceStr.length() - lengthVal);
            result.setValue(sourceStr.substring(startIndex));
        }
    }
}