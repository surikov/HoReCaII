package tee.binding;

import tee.binding.task.Task;
import tee.binding.it.Numeric;
import tee.binding.it.Toggle;
import tee.binding.it.Note;
import tee.binding.it.It;

/**
 *
 * @author User
 * @param <Kind>
 */
public class Fork<Kind> extends It<Kind> {
	It<Kind> then = new It<Kind>().afterChange(new Task() {
		@Override
		public void doTask() {
			decide();
		}
	});
	It<Kind> otherwise = new It<Kind>().afterChange(new Task() {
		@Override
		public void doTask() {
			decide();
		}
	});
	Toggle condition = new Toggle().afterChange(new Task() {
		@Override
		public void doTask() {
			decide();
		}
	}).value(true);

	private void decide() {
		if (condition == null) {
			return;
		}
		if (condition.value()) {
			unbind(otherwise);
			bind(then);
		} else {
			unbind(then);
			bind(otherwise);
		}
	}

	/**
	 *
	 * @param it
	 * @return
	 */
	public Fork<Kind> when(Toggle it) {
		condition.bind(it);
		return this;
	}

	/**
	 *
	 * @param it
	 * @return
	 */
	public Fork<Kind> then(It<Kind> it) {
		then.bind(it);
		return this;
	}

	/**
	 *
	 * @param it
	 * @return
	 */
	public Fork<Kind> then(Kind it) {
		then.value(it);
		return this;
	}

	/**
	 *
	 * @param it
	 * @return
	 */
	public Fork<Kind> otherwise(It<Kind> it) {
		otherwise.bind(it);
		return this;
	}

	/**
	 *
	 * @param it
	 * @return
	 */
	public Fork<Kind> otherwise(Kind it) {
		otherwise.value(it);
		return this;
	}

	/**
	 *
	 * @param a
	 */
	public static void main(String a[]) {
		System.out.println("\nFork\n");
		System.out.println("/n = -10");
		Numeric n = new Numeric().value(-10);
		Note r = new Note().bind(new Fork<String>()
				.when(n.less(-5))
				.then("Frost")
				.otherwise(new Fork<String>()
						.when(n.less(+15))
						.then("Cold")
						.otherwise(new Fork<String>()
								.when(n
										.less(+30)).then("Warm")
								.otherwise("Hot"))));
		System.out.println(r.value());
		System.out.println("/let n = +10");
		n.value(10);
		System.out.println(r.value());
		System.out.println("/let n = +20");
		n.value(20);
		System.out.println(r.value());
		System.out.println("/let n = +40");
		n.value(40);
		System.out.println(r.value());

		System.out.println("\nbidirectional Fork\n");
		Note dialect = new Note().value("MS SQL");
		Note forMSSQL = new Note().value("select top 10 * from table1");
		Note forPostgreSQL = new Note().value("select * from table1 limit 10");
		System.out.println("forMSSQL: " + forMSSQL.value());
		System.out.println("forPostgreSQL: " + forPostgreSQL.value());
		Note command = new Note();
        /*command.bind(Note
                .iF(dialect.same("MS SQL"))
                .then(forMSSQL)
                .otherwise(forPostgreSQL));*/
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
}
