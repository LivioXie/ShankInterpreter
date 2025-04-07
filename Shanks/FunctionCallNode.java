package Shanks;

import java.util.ArrayList;
import java.util.List;

public class FunctionCallNode extends StatementNode {
    private String name;
    private List<ParameterNode> parameters;
    
    public FunctionCallNode(String name) {
        this.name = name;
        this.parameters = new ArrayList<>();
    }
    
    public String getName() {
        return name;
    }
    
    public List<ParameterNode> getParameters() {
        return parameters;
    }
    
    public void addParameter(ParameterNode parameter) {
        parameters.add(parameter);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("(");
        
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(parameters.get(i));
        }
        
        sb.append(")");
        return sb.toString();
    }
}