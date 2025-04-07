package Shanks;

public class RealNode extends Node {
    private float value;
    
    public RealNode(float value) {
        this.value = value;
    }
    
    public float getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return "RealNode(" + value + ")";
    }
}