package tee.binding.properties;

import tee.binding.it.*;

public class ItProperty<Owner, Kind> {
    final  public It<Kind> property;
    private Owner owner;
    public ItProperty(Owner owner) {
	property = new It<Kind>();
	this.owner = owner;
    }
    public Owner is(Kind it) {
	property.value(it);
	return owner;
    }
    public Owner is(It<Kind> it) {
	property.bind(it);
	return owner;
    }
}
