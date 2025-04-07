
package Shanks;
public class IntegerDataType extends InterpreterDataType {
    private int value;
    
    public IntegerDataType(int value) {
        this.value = value;
    }
    
    public IntegerDataType() {
        this.value = 0;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
    
    @Override
    public String ToString() {
        return Integer.toString(value);
    }
    
    @Override
    public void FromString(String input) {
        try {
            this.value = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            this.value = 0;
        }
    }
}