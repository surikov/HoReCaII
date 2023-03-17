package tee.binding;

import tee.binding.task.Task;
import tee.binding.it.It;

/**
 * 
 * @author User
 * @param <Kind>
 */
public class Calculation<Kind> {
    private boolean lockFirst = false;
    private boolean lockSecond = false;
    private It<Kind> _first = new It<Kind>().afterChange(new Task() {
	@Override public void doTask() {
	    if (lockFirst) {
		return;
	    }
	    lockFirst = true;
	    if (!lockSecond) {
		if (second() != null) {
		    second().value(calculateSecond());
		}
	    }
	    lockFirst = false;
	}
    });
    private It<Kind> _second = new It<Kind>().afterChange(new Task() {
	@Override public void doTask() {
	    if (lockSecond) {
		return;
	    }
	    lockSecond = true;
	    if (!lockFirst) {
		if (first() != null) {
		    first().value(calculateFirst());
		}
	    }
	    lockSecond = false;
	}
    });
    /**
     * 
     * @param f
     * @param s
     */
    public Calculation(It<Kind> f, It<Kind> s) {
	first().bind(f);
	second().bind(s);
    }
    /**
     * 
     * @return
     */
    public Kind calculateFirst() {
	return second().value();
    }
    /**
     * 
     * @return
     */
    public Kind calculateSecond() {
	return first().value();
    }
    /**
     * 
     * @return
     */
    public It<Kind> first() {
	return _first;
    }
    /**
     * 
     * @return
     */
    public It<Kind> second() {
	return _second;
    }
    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
	System.out.println("\nCalculation\n");
	final It<Double> tFahrenheit = new It<Double>().value(0.0);
	final It<Double> tCelsius = new It<Double>().value(0.0);
	new Calculation(tFahrenheit, tCelsius) {
	    @Override public Double calculateFirst() {
		return tCelsius.value() * 9.0 / 5.0 + 32.0;
	    }
	    @Override public Double calculateSecond() {
		return (tFahrenheit.value() - 32) * 5.0 / 9.0;
	    }
	};
	System.out.println("let tFahrenheit=100");
	tFahrenheit.value(100.0);
	System.out.println("now tFahrenheit: " + tFahrenheit.value() + ", tCelsius: " + tCelsius.value());
	System.out.println("let tCelsius=100");
	tCelsius.value(100.0);
	System.out.println("now tFahrenheit: " + tFahrenheit.value() + ", tCelsius: " + tCelsius.value());
    }
}
