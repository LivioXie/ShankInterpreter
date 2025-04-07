package Shanks;

import java.util.HashMap;
import java.util.Map;

public class ProgramNode extends Node {
    private Map<String, FunctionNode> functions;
    
    public ProgramNode() {
        this.functions = new HashMap<>();
    }
    
    public void addFunction(FunctionNode function) {
        functions.put(function.getName(), function);
    }
    
    public FunctionNode getFunction(String name) {
        return functions.get(name);
    }
    
    public Map<String, FunctionNode> getFunctions() {
        return functions;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Program:\n");
        
        for (FunctionNode function : functions.values()) {
            sb.append(function).append("\n");
        }
        
        return sb.toString();
    }
}