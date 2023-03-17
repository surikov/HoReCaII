package tee.binding.properties;

import tee.binding.it.*;

public class NumericProperty<Owner> {
	final public Numeric property;
	private Owner owner;

	public NumericProperty(Owner owner) {
		property = new Numeric();
		this.owner = owner;
	}

	public Owner is(double it) {
		property.value(it);
		return owner;
	}

	public Owner is(int it) {
		property.value(it);
		return owner;
	}

	public Owner is(Numeric it) {
		property.bind(it);
		return owner;
	}

	public Owner is(It<Double> it) {
		property.bind(it);
		return owner;
	}
}