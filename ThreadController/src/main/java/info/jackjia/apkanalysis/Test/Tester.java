package info.jackjia.apkanalysis.Test;

import info.jackjia.apkanalysis.Engine;

/**
 * Created by jackjia on 11/11/15.
 */
public class Tester extends Engine {
    private static final String TAG = Tester.class.getSimpleName();

    private String name;

    public Tester(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
    public void run() {
        System.out.println("Hello");
    }

    @Override
    public void addToList(Object o) {
        mTaskList.add(o);
    }
}
