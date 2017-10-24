package info.jackjia.apkanalysis.installer;

/**
 * Created by jackjia on 11/12/15.
 */
public class Installer extends info.jackjia.apkanalysis.Engine {
    private static final String TAG = Installer.class.getSimpleName();

    private String name;

    public Installer(String name) {
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
