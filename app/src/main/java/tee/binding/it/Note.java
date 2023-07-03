package tee.binding.it;

import tee.binding.Calculation;
import tee.binding.task.Task;
import tee.binding.it.It;

/**
 * @author User
 */
public class Note extends It<String> {
	private Note _otherwise = null;

	public Note() {
		super();
		this.value("");
	}

	/**
	 * @param a
	 */
	public static void main(String a[]) {
		System.out.println("\nCharacters\n");
		Note item = new Note();
		Note s = new Note().bind(new Note().value("A ").append(item).append(" apple."));
		System.out.println(s.value());
		System.out.println("/let item = red");
		item.value("red");
		System.out.println(s.value());
		System.out.println("/let item = green");
		item.value("green");
		System.out.println(s.value());
		System.out.println("/let item = yellow");
		item.value("yellow");
		System.out.println(s.value());
		System.out.println("---");
		Note dialect = new Note().value("MS SQL");
		Note forMSSQL = new Note().value("select top 10 * from table1");
		Note forPostgreSQL = new Note().value("select * from table1 limit 10");
		Note command = new Note();
		//command.bind(Note.iF(dialect.same("MS SQL")).then(forMSSQL).otherwise(forPostgreSQL));
		command.bind(forMSSQL.when(dialect.same("MS SQL")).otherwise(forPostgreSQL));
		System.out.println("dialect: " + dialect.value() + ", command: " + command.value());
		System.out.println("/let dialect isn't MS SQL");
		dialect.value("PostgreSQL");
		System.out.println("dialect: " + dialect.value() + ", command: " + command.value());
		System.out.println("/let command = select field1,filed2 from table1 limit 10");
		command.value("select field1,filed2 from table1 limit 10");
		System.out.println("command: " + command.value());
		System.out.println("forMSSQL: " + forMSSQL.value());
		System.out.println("forPostgreSQL: " + forPostgreSQL.value());
	}

	@Override
	public Note afterChange(Task newValue) {
		super.afterChange(newValue);
		return this;
	}

	@Override
	public Note afterChange(Task newValue, boolean dontFire) {
		super.afterChange(newValue, dontFire);
		return this;
	}

	@Override
	public Note value(String newValue) {
		super.value(newValue);
		return this;
	}

	public Note read() {
		final Note r = new Note().value(value());
		final Note watcher = new Note().bind(this);
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
	 * @param it
	 * @return
	 */
	public Note bind(Note it) {
		super.bind(it);
		return this;
	}

	@Override
	public Note bind(It<String> it) {
		super.bind(it);
		return this;
	}

	/**
	 * @param bb
	 * @return
	 */
	public Toggle same(final Note bb) {
		//return new Toggle().same(this, it);
		final Note aa = this;
		//final Note bb = b;
		final Toggle retvalue = new Toggle().value(aa.value().equals(bb.value()));
		new Note().bind(aa).afterChange(new Task() {
			@Override
			public void doTask() {
				retvalue.value(aa.value().equals(bb.value()));
			}
		});
		new Note().bind(bb).afterChange(new Task() {
			@Override
			public void doTask() {
				retvalue.value(aa.value().equals(bb.value()));
			}
		});
		return retvalue;
	}

	/**
	 * @param it
	 * @return
	 */
	public Toggle same(String it) {
		//return new Toggle().same(this, it);
		return same(new Note().value(it));
	}

	/**
	 * @param bb
	 * @return
	 */
	public Toggle like(final Note bb) {
		//if(this.value()==null)this.value("");
		//return new Toggle().like(this, it);
		final Note aa = this;
		//final Note bb = b;
		//System.out.println(aa);
		//System.out.println(bb);
		//System.out.println(aa.value());
		final Toggle retvalue = new Toggle().value(aa.value().indexOf(bb.value()) > -1);
		new Note().bind(aa).afterChange(new Task() {
			@Override
			public void doTask() {
				retvalue.value(aa.value().indexOf(bb.value()) > -1);
			}
		});
		new Note().bind(bb).afterChange(new Task() {
			@Override
			public void doTask() {
				retvalue.value(aa.value().indexOf(bb.value()) > -1);
			}
		});
		return retvalue;
	}

	/**
	 * @param it
	 * @return
	 */
	public Toggle like(String it) {
		return like(new Note().value(it));
	}

	/**
	 * @param appendString
	 * @return
	 */
	public Note append(final String appendString) {
		//final String fvalue = value;
		Note s = new Note().bind(new Calculation<String>(this, new Note().value(value() + appendString)) {
			@Override
			public String calculateFirst() {
				if (second() == null) {
					return "";
				} else {
					return second().value();
				}
			}

			@Override
			public String calculateSecond() {
				if (first() == null) {
					return "";
				} else {
					return first().value() + appendString;
				}
			}
		}.second());
		return s;
	}

	/**
	 * @param value
	 * @return
	 */
	public Note append(It<String> value) {
		Note n = new Note().bind(value);
		return append(n);
	}
	/*
    public static Fork<String> iF(Toggle it) {
    return new Fork<String>().condition(it);
    }*/

	/**
	 * @param appendNote
	 * @return
	 */
	public Note append(final Note appendNote) {
		//final Note fvalue = value;
		final Note me = this;
		final Note retvalue = new Note().value(value() + appendNote.value());
		new Note().bind(appendNote).afterChange(new Task() {
			@Override
			public void doTask() {
				retvalue.value(me.value() + appendNote.value());
			}
		});
		return retvalue;
	}

	/**
	 * @param it
	 * @return
	 */
	public Note otherwise(Note it) {
		if (_otherwise == null) {
			_otherwise = new Note();
		}
		_otherwise.bind(it);
		return this;
	}

	/**
	 * @param it
	 * @return
	 */
	public Note otherwise(String it) {
		if (_otherwise == null) {
			_otherwise = new Note();
		}
		_otherwise.value(it);
		return this;
	}

	/**
	 * @param it
	 * @return
	 */
	public Note when(final Toggle it) {
		final Note me = this;
		final Note when = new Note();
		new Toggle().bind(it).afterChange(new Task() {
			@Override
			public void doTask() {
				//retvalue.value(me.value() + appendNote.value());
				if (it.value()) {
					when.unbind(when._otherwise);
					when.bind(me);
				} else {
					if (when._otherwise == null) {
						when._otherwise = new Note();
					}
					when.unbind(me);
					when.bind(when._otherwise);
				}
			}
		});
		return when;
	}
}
