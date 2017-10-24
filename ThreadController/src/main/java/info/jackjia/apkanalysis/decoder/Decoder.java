package info.jackjia.apkanalysis.decoder;

import info.jackjia.apkanalysis.Engine;
import info.jackjia.apkanalysis.utils.Shell;

/**
 * Created by yurushao on 11/4/15.
 */
public class Decoder extends Engine {
    private static final String TAG = Decoder.class.getSimpleName();

    private String name;

    public Decoder(String name) {
       this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void run() {
        for (Object o : mTaskList) {
            String apkPath = o.toString();
            System.out.println(getName() + " decoding " + apkPath);
            decode(apkPath);
        }
    }

    private void decode(String apk) {
        int exitcode = Shell.exec(new String[]{"apktool", "d", apk});
        return;
    }

    @Override
    public void addToList(Object o) {
        mTaskList.add(o);
    }

}
