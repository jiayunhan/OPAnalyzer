package info.jackjia.apkanalysis;

import java.util.ArrayList;

/**
 * Created by yurushao on 11/4/15.
 */
public abstract class Engine implements Runnable {
    protected ArrayList<Object> mTaskList = new ArrayList<Object>();

    public abstract void addToList(Object o);

}
