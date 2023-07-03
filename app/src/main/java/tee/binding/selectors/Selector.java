package tee.binding.selectors;

import java.util.*;

import tee.binding.it.*;
import tee.binding.task.*;

public class Selector<Kind> {

	private Vector<It<Kind>> values = new Vector<It<Kind>>();
	private It<Kind> value = new It<Kind>();
	public Numeric index = new Numeric().value(0);

	public Selector() {
		index.afterChange(new Task() {

			@Override
			public void doTask() {
				//throw new UnsupportedOperationException("Not supported yet.");
			}
		});
	}

	public Selector<Kind> item(Kind v) {
		values.add(new It<Kind>().value(v));
		return this;
	}

	public Selector<Kind> item(It<Kind> v) {
		values.add(v);
		return this;
	}

	public It<Kind> value() {
		return value;
	}

	public int count() {
		return values.size();
	}

	public static void main(String[] a) {
	}
}
