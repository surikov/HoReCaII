package tee.binding;

import java.util.*;
import tee.binding.it.*;
import tee.binding.task.*;

public class They<Kind> {

    public static void main(String[] s) {
        System.out.println("create");
        They<String> a = new They<String>()//
                .value("1")//
                .value("2")//
                .value("3")//
                .value("4")//
                .afterChange(new Task() {

            @Override
            public void doTask() {
                System.out.println("a changed");
            }
        });
        System.out.println("bind");
        They<String> b = new They<String>().bind(a).afterChange(new Task() {

            @Override
            public void doTask() {
                System.out.println("b changed");
            }
        });
        System.out.println("unbind");
        b.unbind();
        System.out.println("do");
        //a.insert(-46, "1+");        
        a.delete(a.at(2));
        System.out.println("result");
        for (int i = 0; i < a.size(); i++) {
            System.out.println(i + ": " + a.at(i));
        }
    }
    private Vector<Kind> values = new Vector<Kind>();
    private They<Kind> bindTo = null;
    private Vector<They<Kind>> binded = new Vector<They<Kind>>();
    private Task afterChange = null;
    public boolean lockDoAfterChange = false;
    private boolean lockSize = false;
    private boolean lockAt = false;
    private boolean lockDelete = false;
    private boolean lockClear = false;
    private boolean lockInsert = false;
    private boolean lockValue = false;

    public They<Kind> afterChange(Task it) {
        //this.afterChange = it;
        //doAfterChange();
        return afterChange(it,false);
    }
public They<Kind> afterChange(Task it,boolean dontFire) {
        this.afterChange = it;
        if(!dontFire){
        doAfterChange();}
        return this;
    }
    public void doAfterChange() {
        if (!lockDoAfterChange) {
            lockDoAfterChange = true;
            if (this.afterChange != null) {
                afterChange.start();
            }
            for (int i = 0; i < binded.size(); i++) {
                binded.get(i).doAfterChange();
            }
            lockDoAfterChange = false;
        }
    }

    public int size() {
        int r = 0;
        if (!lockSize) {
            lockSize = true;
            if (bindTo != null) {
                r = bindTo.size();
            } else {
                r = values.size();
            }
            lockSize = false;
        }
        return r;
    }

    public Kind at(int nn) {
        Kind r = null;
        if (!lockAt) {
            lockAt = true;
            if (bindTo != null) {
                r = bindTo.at(nn);
            } else {
                if (nn >= 0 && nn < values.size()) {
                    r = values.get(nn);
                }
            }
            lockAt = false;
        }
        return r;
    }

    public They<Kind> bind(They<Kind> to) {
        if (to != null) {
            this.bindTo = to;
            to.binded.add(this);
            this.doAfterChange();
        }
        return this;
    }

    public void unbind(They<Kind> from) {
        for (int i = 0; i < binded.size(); i++) {
            They<Kind> b = binded.get(i);
            if (b == from) {
                binded.remove(b);
                b.doAfterChange();
            }
        }
    }

    public void unbind() {
        this.bindTo = null;
        //this.binded.removeAllElements();
        this.doAfterChange();
        /*for (int i = 0; i < binded.size(); i++) {
         binded.get(i).doAfterChange();
         }*/
        while (binded.size() > 0) {
            They<Kind> t = binded.get(0);
            binded.remove(t);
            t.doAfterChange();
        }
    }

    public They<Kind> delete(Kind v) {
        if (!lockDelete) {
            lockDelete = true;
            if (bindTo != null) {
                bindTo.delete(v);
            } else {
                values.remove(v);
                this.doAfterChange();
            }
            lockDelete = false;
        }
        return this;
    }

    public They<Kind> clear() {
        if (!lockClear) {
            lockClear = true;
            if (bindTo != null) {
                bindTo.clear();
            } else {
                values.removeAllElements();
                this.doAfterChange();
            }
            lockClear = false;
        }
        return this;
    }

    public void insert(int nn, Kind v) {
        if (!lockInsert) {
            lockInsert = true;
            if (bindTo != null) {
                bindTo.insert(nn, v);
            } else {
                if (nn > values.size()) {
                    nn = values.size();
                }
                if (nn < 0) {
                    nn = 0;
                }
                values.insertElementAt(v, nn);
                this.doAfterChange();
            }
            lockInsert = false;
        }
    }

    public They<Kind> value(Kind v) {
        if (!lockValue) {
            lockValue = true;
            if (bindTo != null) {
                bindTo.value(v);
            } else {
                values.add(v);
                this.doAfterChange();
            }
            lockValue = false;
        }
        return this;
    }
}
