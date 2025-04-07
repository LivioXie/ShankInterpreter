
package Shanks;
public class BooleanDataType extends InterpreterDataType {
    private boolean value;
    
    public BooleanDataType(boolean value) {
        this.value = value;
    }
    
    public BooleanDataType() {
        this.value = false;
    }
    
    public boolean getValue() {
        return value;
    }
    
    public void setValue(boolean value) {
        this.value = value;
    }
    
    @Override
    public String ToString() {
        return Boolean.toString(value);
    }
    
    @Override
    public void FromString(String input) {
        if (input == null) {
            this.value = false;
            return;
        }
        
        String trimmed = input.trim().toLowerCase();
        this.value = trimmed.equals("true") || trimmed.equals("1") || trimmed.equals("yes");
    }
}