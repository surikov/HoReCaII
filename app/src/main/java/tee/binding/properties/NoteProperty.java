package tee.binding.properties;

import tee.binding.it.*;

public class NoteProperty<Owner> {
    final public Note property;
    private Owner owner;
    public NoteProperty(Owner owner) {
	property = new Note();
	this.owner = owner;
    }
    public Owner is(String it) {
	property.value(it);
	return owner;
    }
    public Owner is(Note it) {
	property.bind(it);
	return owner;
    }
    public Owner is(It<String> it) {
	property.bind(it);
	return owner;
    }
}
