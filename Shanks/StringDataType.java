package Shanks;
public class StringDataType extends InterpreterDataType {
    private String value;
    
    public StringDataType(String value) {
        this.value = value;
    }
    
    public StringDataType() {
        this.value = "";
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public String ToString() {
        return value;
    }
    
    @Override
    public void FromString(String input) {
        this.value = input;
    }
}