package tee.binding;

import tee.binding.task.Task;
import tee.binding.it.Note;
import tee.binding.it.It;
import java.util.*;

/**
 * 
 * @author User
 * @param <Kind>
 */
public class Fit<Kind> {

    Hashtable<String, It<Kind>> currentSet = new Hashtable<String, It<Kind>>();
    Hashtable<String, Hashtable<String, It<Kind>>> sets = new Hashtable<String, Hashtable<String, It<Kind>>>();
    private Vector<Fit<Kind>> _binded = new Vector<Fit<Kind>>();
    Note _selector = new Note().afterChange(new Task() {

	@Override public void doTask() {
	    refreshSet();
	}
    }).value("");

    /**
     * 
     * @return
     */
    public Note selector() {
	return _selector;
    }
/*
    private Hashtable<String, It<Kind>>  addGroup(String groupName) {
	Hashtable<String, It<Kind>> s = new Hashtable<String, It<Kind>>();
	sets.put(groupName, s);
	return s;
    }
*/
    private void bindChild(String groupFrom, String keyFrom, It<Kind> valueFrom
	    //, Hashtable<String, Hashtable<String, It<Kind>>> to
	    ,Fit<Kind> to
	    ) {
	Hashtable<String, It<Kind>> toGroup = to.findSet(groupFrom);
		//to.get(groupFrom);
	/*if (toGroup == null) {
	    toGroup = new Hashtable<String, It<Kind>>();
	    to.put(groupFrom, toGroup);
	}*/
	It<Kind> v = toGroup.get(keyFrom);
	if (v == null) {
	    v = new It<Kind>();
	    toGroup.put(keyFrom, v);
	}
	valueFrom.bind(v);
    }

    private void bindChildren(Fit<Kind> fromFit, Fit<Kind> toFit) {
	Enumeration<String> groups = fromFit.sets.keys();
	while (groups.hasMoreElements()) {
	    String groupFrom = groups.nextElement();
	    Enumeration<String> keys = fromFit.sets.get(groupFrom).keys();
	    while (keys.hasMoreElements()) {
		String keyFrom = keys.nextElement();
		It<Kind> valueFrom = fromFit.sets.get(groupFrom).get(keyFrom);
		bindChild(groupFrom, keyFrom, valueFrom, toFit);
	    }
	}
    }

    /**
     * 
     * @param to
     * @return
     */
    public Fit<Kind> bind(Fit<Kind> to) {
	if (to == null) {
	    return this;
	}
	if (!this._binded.contains(to)) {
	    this._binded.add(to);
	}
	if (!to._binded.contains(this)) {
	    to._binded.add(this);
	}
	bindChildren(this, to);
	bindChildren(to, this);
	_selector.bind(to._selector);
	return this;
    }

    /**
     * 
     * @param key
     * @return
     */
    public It<Kind> find(String key) {
	It<Kind> r;
	It<Kind> v = currentSet.get(key);
	if (v == null) {
	    v = new It<Kind>();
	    currentSet.put(key, v);
	}
	r = v;
	return r;
    }

    private void refreshSet() {
	if (_selector == null) {
	    return;
	}
	Hashtable<String, It<Kind>> choosenSet = findSet(_selector.value());
	for (Enumeration<String> e = choosenSet.keys(); e.hasMoreElements();) {
	    String k = e.nextElement();
	    find(k).value(choosenSet.get(k).value());
	}
    }

    private Hashtable<String, It<Kind>> findSet(String group) {
	Hashtable<String, It<Kind>> s = sets.get(group);
	if (s == null) {
	    s = new Hashtable<String, It<Kind>>();
	    sets.put(group, s);
	    //System.out.println("add group "+group);
	}
	return s;
    }

    private void itemEach(It<Kind> it, String group, String key, Vector<Fit<Kind>> cashe) {
	cashe.add(this);
	for (int i = 0; i < _binded.size(); i++) {
	    if (!cashe.contains(_binded.get(i))) {
		Fit<Kind> b = _binded.get(i);
		Hashtable<String, It<Kind>> s = b.findSet(group);
		It<Kind> v = s.get(key);
		if (v == null) {
		    v = new It< Kind>();
		    s.put(key, v);
		}
		v.bind(it);
		b.refreshSet();
		b.itemEach(v, group, key, cashe);
	    }
	}
	cashe.remove(this);
    }

    /**
     * 
     * @param group
     * @param key
     * @param value
     * @return
     */
    public Fit<Kind> item(String group, String key, Kind value) {
	Hashtable<String, It<Kind>> s = findSet(group);
	It<Kind> v = s.get(key);
	if (v == null) {
	    v = new It< Kind>();
	    s.put(key, v);
	}
	v.value(value);
	refreshSet();
	itemEach(v, group, key, new Vector<Fit<Kind>>());
	return this;
    }

    /**
     * 
     * @param a
     */
    public static void main(String[] a) {
	System.out.println("\nFit\n");
	Fit<String> g = new Fit<String>();
	Fit<String> data = new Fit<String>();

	Note s1 = new Note().bind(g.find("w1"));
	Note s2 = new Note().bind(g.find("w2"));
	Note s3 = new Note().bind(g.find("w3"));
	Note s4 = new Note().bind(g.find("w4"));

	g.item("English", "w1", "First").item("English", "w2", "Second").item("English", "w3", "Third").item("Spain", "w1", "Primero").item("Spain", "w2", "Segundo").item("Spain", "w3", "Tercera");
	data.item("English", "w1", "First2").item("English", "w2", "Second").item("English", "w3", "Third").item("Spain", "w1", "Primero").item("Spain", "w2", "Segundo").item("Spain", "w3", "Tercera");

	System.out.println("/set English");
	g.selector().value("English");
	System.out.println(s1.value() + ", " + s2.value() + ", " + s3.value() + ", " + s4.value());
	System.out.println("/set Spain");
	g.selector().value("Spain");
	System.out.println(s1.value() + ", " + s2.value() + ", " + s3.value() + ", " + s4.value());

	System.out.println("--bind--");
	g.bind(data);
	//data.bind(g);
	System.out.println(s1.value() + ", " + s2.value() + ", " + s3.value() + ", " + s4.value());

	System.out.println("/set English");
	g.selector().value("English");
	System.out.println(s1.value() + ", " + s2.value() + ", " + s3.value() + ", " + s4.value());

	System.out.println("/insert");
	g.selector().value("Spain");
	g.selector().value("English");
	data.item("English", "w4", "Forth");
	System.out.println(s1.value() + ", " + s2.value() + ", " + s3.value() + ", " + s4.value());

    }
}
