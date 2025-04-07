package Shanks;
import java.util.List;

public class BuiltInSubstring extends Shanks.FunctionNode {
    
    public BuiltInSubstring() {
        super("substring");
    }
    
    @Override
    public void execute(List<InterpreterDataType> parameters) {
        if (parameters.size() != 4) {
            throw new RuntimeException("Substring function requires exactly 4 parameters");
        }
        
        // First parameter is the source string (read-only)
        if (!(parameters.get(0) instanceof StringDataType)) {
            throw new RuntimeException("First parameter of Substring must be a string");
        }
        StringDataType source = (StringDataType) parameters.get(0);
        
        // Second parameter is the start index (read-only)
        if (!(parameters.get(1) instanceof IntegerDataType)) {
            throw new RuntimeException("Second parameter of Substring must be an integer");
        }
        IntegerDataType start = (IntegerDataType) parameters.get(1);
        
        // Third parameter is the length (read-only)
        if (!(parameters.get(2) instanceof IntegerDataType)) {
            throw new RuntimeException("Third parameter of Substring must be an integer");
        }
        IntegerDataType length = (IntegerDataType) parameters.get(2);
        
        // Fourth parameter is the result (var parameter)
        if (!(parameters.get(3) instanceof StringDataType)) {
            throw new RuntimeException("Fourth parameter of Substring must be a string");
        }
        StringDataType result = (StringDataType) parameters.get(3);
        
        // Get the substring
        String sourceStr = source.getValue();
        int startVal = start.getValue();
        int lengthVal = length.getValue();
        
        if (startVal < 1 || startVal > sourceStr.length() || lengthVal <= 0) {
            result.setValue("");
        } else {
            // Adjust for 1-based indexing
            startVal = startVal - 1;
            
            // Ensure we don't go past the end of the string
            int endVal = Math.min(startVal + lengthVal, sourceStr.length());
            
            result.setValue(sourceStr.substring(startVal, endVal));
        }
    }
}