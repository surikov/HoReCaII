package tee.binding.properties;

import tee.binding.it.*;

public class ToggleProperty<Owner> {
	final public Toggle property;
	private Owner owner;

	public ToggleProperty(Owner owner) {
		property = new Toggle();
		this.owner = owner;
	}

	public Owner is(boolean it) {
		property.value(it);
		return owner;
	}

	public Owner is(Toggle it) {
		property.bind(it);
		return owner;
	}

	public Owner is(It<Boolean> it) {
		property.bind(it);
		return owner;
	}
}
