package Shanks;
public class CharacterDataType extends InterpreterDataType {
    private char value;
    
    public CharacterDataType(char value) {
        this.value = value;
    }
    
    public CharacterDataType() {
        this.value = '\0';
    }
    
    public char getValue() {
        return value;
    }
    
    public void setValue(char value) {
        this.value = value;
    }
    
    @Override
    public String ToString() {
        return Character.toString(value);
    }
    
    @Override
    public void FromString(String input) {
        if (input == null || input.isEmpty()) {
            this.value = '\0';
        } else {
            this.value = input.charAt(0);
        }
    }
}