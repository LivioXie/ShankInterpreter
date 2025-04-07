package Shanks;

public class VariableNode extends Node {
    private String name;
    private String type;
    private boolean isVar;
    private Node value; // For constants
    
    // Range constraints
    private Integer integerFrom;
    private Integer integerTo;
    private Float realFrom;
    private Float realTo;
    private Integer stringLengthFrom;
    private Integer stringLengthTo;
    
    // Constructor for parameters and variables
    public VariableNode(String name, String type, boolean isVar) {
        this.name = name;
        this.type = type;
        this.isVar = isVar;
        this.value = null;
    }
    
    // Constructor for constants
    public VariableNode(String name, String type, Node value) {
        this.name = name;
        this.type = type;
        this.isVar = false;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public boolean isVar() {
        return isVar;
    }
    
    public Node getValue() {
        return value;
    }
    
    public void setValue(Node value) {
        this.value = value;
    }
    
    // Range constraints for integers
    public void setIntegerRange(Integer from, Integer to) {
        this.integerFrom = from;
        this.integerTo = to;
    }
    
    public Integer getIntegerFrom() {
        return integerFrom;
    }
    
    public Integer getIntegerTo() {
        return integerTo;
    }
    
    // Range constraints for reals
    public void setRealRange(Float from, Float to) {
        this.realFrom = from;
        this.realTo = to;
    }
    
    public Float getRealFrom() {
        return realFrom;
    }
    
    public Float getRealTo() {
        return realTo;
    }
    
    // Range constraints for strings (length)
    public void setStringRange(Integer from, Integer to) {
        this.stringLengthFrom = from;
        this.stringLengthTo = to;
    }
    
    public Integer getStringLengthFrom() {
        return stringLengthFrom;
    }
    
    public Integer getStringLengthTo() {
        return stringLengthTo;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        if (value != null) {
            // This is a constant
            sb.append(name).append(" = ").append(value);
        } else {
            // This is a parameter or variable
            if (isVar) {
                sb.append("var ");
            }
            
            sb.append(name).append(" : ").append(type);
            
            // Add range constraints if applicable
            if (type.equals("integer") && integerFrom != null && integerTo != null) {
                sb.append(" from ").append(integerFrom).append(" to ").append(integerTo);
            } else if (type.equals("real") && realFrom != null && realTo != null) {
                sb.append(" from ").append(realFrom).append(" to ").append(realTo);
            } else if (type.equals("string") && stringLengthFrom != null && stringLengthTo != null) {
                sb.append(" from ").append(stringLengthFrom).append(" to ").append(stringLengthTo);
            }
        }
        
        return sb.toString();
    }
}