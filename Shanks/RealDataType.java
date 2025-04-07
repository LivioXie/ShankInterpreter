package Shanks;
public class RealDataType extends InterpreterDataType {
    private float value;
    
    public RealDataType(float value) {
        this.value = value;
    }
    
    public RealDataType() {
        this.value = 0.0f;
    }
    
    public float getValue() {
        return value;
    }
    
    public void setValue(float value) {
        this.value = value;
    }
    
    @Override
    public String ToString() {
        return Float.toString(value);
    }
    
    @Override
    public void FromString(String input) {
        try {
            this.value = Float.parseFloat(input.trim());
        } catch (NumberFormatException e) {
            this.value = 0.0f;
        }
    }
}