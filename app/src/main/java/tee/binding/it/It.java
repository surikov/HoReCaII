package tee.binding.it;

import java.util.*;

import tee.binding.task.Task;

/**
 * <p>Base class for binding</p>
 *
 * @param <Kind> type of the object
 */
public class It<Kind> {
	/**

	 */
	protected Kind _value = null;
	private Vector<It<Kind>> _binded = new Vector<It<Kind>>();
	private Task afterChange = null;

	/**

	 */
	public It() {
	}

	/**
	 * for testing
	 *
	 * @param args fake args
	 */
	public static void main(String[] args) {
		System.out.println("\ntest It\n");
		final It<String> a = new It<String>().value("A");
		final It<String> b = new It<String>().value("B");
		final It<String> c = new It<String>().value("C");
		a.afterChange(new Task() {
			@Override
			public void doTask() {
				System.out.println("[a]: someone changed value to " + a.value());
			}
		});
		b.afterChange(new Task() {
			@Override
			public void doTask() {
				System.out.println("[b]: someone changed value to " + b.value());
			}
		});
		a.afterChange(new Task() {
			@Override
			public void doTask() {
				System.out.println("[c]: someone changed value to " + c.value());
			}
		});
		System.out.println("a: " + a.value() + ", b: " + b.value() + ", c: " + c.value());
		System.out.println("#bind variables");
		a.bind(b);
		b.bind(c);
		c.bind(a);
		System.out.println("a: " + a.value() + ", b: " + b.value() + ", c: " + c.value());
		System.out.println("#let a=D");
		a.value("D");
		System.out.println("a: " + a.value() + ", b: " + b.value() + ", c: " + c.value());
		System.out.println("#a unbind b");
		a.unbind(b);
		System.out.println("#let a=E");
		a.value("E");
		System.out.println("a: " + a.value() + ", b: " + b.value() + ", c: " + c.value());
		System.out.println("#let b=F");
		b.value("F");
		System.out.println("a: " + a.value() + ", b: " + b.value() + ", c: " + c.value());
		System.out.println("#a unbind c");
		a.unbind(c);
		System.out.println("#let a=G");
		a.value("G");
		System.out.println("a: " + a.value() + ", b: " + b.value() + ", c: " + c.value());
		System.out.println("#let b=H");
		b.value("H");
		System.out.println("a: " + a.value() + ", b: " + b.value() + ", c: " + c.value());
	}

	/**

	 */
	protected void adjust() {
	}

	/**
	 * read value
	 *
	 * @return current value
	 */
	public Kind value() {
		return _value;
	}

	/**
	 * assign new value
	 *
	 * @param newValue new value
	 * @return object itself
	 */
	public It<Kind> value(Kind newValue) {
		setForEachBindedItem(newValue, new Vector<It>());
		return this;
	}

	private void setForEachBindedItem(Kind newValue, Vector<It> cashe) {
		if (value() == null && newValue == null) {
			return;
		}
		if (value() != null && value().equals(newValue)) {
			return;
		}
		this._value = newValue;
		adjust();
		cashe.add(this);
		for (int i = 0; i < _binded.size(); i++) {
			if (!cashe.contains(_binded.get(i))) {
				_binded.get(i).setForEachBindedItem(newValue, cashe);
			}
		}
		cashe.remove(this);
		doAfterChange();
	}

	/**
	 * assign trigger
	 *
	 * @param it trigger
	 * @return object itself
	 */
	public It<Kind> afterChange(Task it) {
	/*this.afterChange = it;
	doAfterChange();
	return this;*/
		return afterChange(it, false);
	}

	/**
	 * assign trigger
	 *
	 * @param it trigger
	 * @return object itself
	 */
	public It<Kind> afterChange(Task it, boolean dontFire) {
		this.afterChange = it;
		if (!dontFire) {
			doAfterChange();
		}
		return this;
	}

	private void doAfterChange() {
		if (this.afterChange != null) {
			afterChange.start();
		}
	}

	/**
	 * create readonly clone
	 *
	 * @return object itself
	 */
	public It<Kind> read() {
		final It<Kind> r = new It<Kind>().value(value());
		final It<Kind> watcher = new It<Kind>().bind(this);
		watcher.afterChange(new Task() {
			@Override
			public void doTask() {
				r.value(watcher.value());
			}
		});
		r.afterChange(new Task() {
			@Override
			public void doTask() {
				r.value(watcher.value());
			}
		});
		return r;
	}

	/**
	 * bind value to other object
	 *
	 * @param to bindable object
	 * @return object itself
	 */
	public It<Kind> bind(It<Kind> to) {
		if (to == null) {
			return this;
		}
		if (!this._binded.contains(to)) {
			this._binded.add(to);
		}
		if (!to._binded.contains(this)) {
			to._binded.add(this);
		}
		this.value(to.value());
		return this;
	}

	/**
	 * unbind value from other object
	 *
	 * @param to binded object
	 */
	public void unbind(It<Kind> to) {
		if (to == null) {
			return;
		}
		this._binded.remove(to);
		to._binded.remove(this);
	}

	/**
	 * unbind value froma all objects
	 */
	public void unbind() {
		for (int i = 0; i < _binded.size(); i++) {
			_binded.get(i).
					unbind(this);
		}
	}
}
