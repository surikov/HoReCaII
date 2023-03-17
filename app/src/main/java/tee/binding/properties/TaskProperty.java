package tee.binding.properties;

import tee.binding.task.*;
import tee.binding.it.*;

public class TaskProperty<Owner> {
    final public It<Task> property = new It<Task>();
    private Owner owner;
    public TaskProperty(Owner owner) {
	this.owner = owner;
    }
    public Owner is(Task it) {
	property.value(it);
	return owner;
    }
}
