package Shanks;
import java.util.ArrayList;
import java.util.List;

public class ArrayDataType<T extends InterpreterDataType> extends InterpreterDataType {

	private List<T> store;

	public ArrayDataType(List<T> store) {
		this.store = store;
	}
	
	// Add a default constructor
	public ArrayDataType() {
		this.store = new ArrayList<>();
	}

	public List<T> getStore() {
		return store;
	}

	@Override
	public String ToString() {
		return "ArrayDataType [value=" + store + "]";
	}

	@Override
	public void FromString(String input) {
		// This is a placeholder - proper implementation would depend on how you want to parse strings into array elements
		// store.add((T) input); // This cast is unsafe and will likely cause ClassCastException
	}
}