package Shanks;
import java.util.List;
import java.util.Random;

public class BuiltInGetRandom extends Shanks.FunctionNode {
    private static final Random random = new Random();
    
    public BuiltInGetRandom() {
        super("getrandom");
    }
    
    @Override
    public void execute(List<InterpreterDataType> parameters) {
        if (parameters.size() != 1) {
            throw new RuntimeException("GetRandom function requires exactly 1 parameter");
        }
        
        // Parameter is the result (var parameter)
        if (!(parameters.get(0) instanceof RealDataType)) {
            throw new RuntimeException("Parameter of GetRandom must be a real");
        }
        RealDataType result = (RealDataType) parameters.get(0);
        
        // Generate random number between 0 and 1
        result.setValue(random.nextFloat());
    }
}