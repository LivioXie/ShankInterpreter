package Shanks;
import java.util.List;

public class BuiltInStart extends Shanks.FunctionNode {
    
    public BuiltInStart() {
        super("start");
    }
    
    @Override
    public void execute(List<InterpreterDataType> parameters) {
        if (parameters.size() != 2) {
            throw new RuntimeException("Start function requires exactly 2 parameters");
        }
        
        // First parameter is the string (read-only)
        if (!(parameters.get(0) instanceof StringDataType)) {
            throw new RuntimeException("First parameter of Start must be a string");
        }
        StringDataType stringValue = (StringDataType) parameters.get(0);
        
        // Second parameter is the result integer (var parameter)
        if (!(parameters.get(1) instanceof IntegerDataType)) {
            throw new RuntimeException("Second parameter of Start must be an integer");
        }
        IntegerDataType intResult = (IntegerDataType) parameters.get(1);
        
        // Get the start index (1 for non-empty strings, 0 for empty)
        String str = stringValue.getValue();
        intResult.setValue(str.isEmpty() ? 0 : 1);
    }
}