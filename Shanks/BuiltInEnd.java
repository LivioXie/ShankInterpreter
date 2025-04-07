package Shanks;
import java.util.List;

public class BuiltInEnd extends Shanks.FunctionNode {
    
    public BuiltInEnd() {
        super("end");
    }
    
    @Override
    public void execute(List<InterpreterDataType> parameters) {
        if (parameters.size() != 2) {
            throw new RuntimeException("End function requires exactly 2 parameters");
        }
        
        // First parameter is the string (read-only)
        if (!(parameters.get(0) instanceof StringDataType)) {
            throw new RuntimeException("First parameter of End must be a string");
        }
        StringDataType stringValue = (StringDataType) parameters.get(0);
        
        // Second parameter is the result integer (var parameter)
        if (!(parameters.get(1) instanceof IntegerDataType)) {
            throw new RuntimeException("Second parameter of End must be an integer");
        }
        IntegerDataType intResult = (IntegerDataType) parameters.get(1);
        
        // Get the end index (length of string for non-empty strings, 0 for empty)
        String str = stringValue.getValue();
        intResult.setValue(str.length());
    }
}