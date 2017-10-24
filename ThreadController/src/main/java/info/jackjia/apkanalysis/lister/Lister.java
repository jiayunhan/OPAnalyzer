package info.jackjia.apkanalysis.lister;

import info.jackjia.apkanalysis.Engine;

import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackjia on 11/24/15.
 */
public class Lister extends Engine{
    private static final String TAG = Lister.class.getSimpleName();
    private BufferedWriter out;
    private String name;
    private List<String> smalis = new ArrayList<String>();
    public Lister(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void aggregate(List<String> list){
        synchronized (this) {
            System.out.println(name+": aggregate :"+smalis.size());
            list.addAll(smalis);
        }
    }
    public void run() {
        try {
            for (Object o : mTaskList) {
                String smaliPath = o.toString();
                list(smaliPath);
            }
            aggregate(ListController.files);
            System.out.println(name+" completed");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void list(String directory) {
        try {
            info.jackjia.apkanalysis.utils.Filewalker walker = new info.jackjia.apkanalysis.utils.Filewalker(".smali",new filter());
            smalis.addAll(walker.listFiles(directory));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void addToList(Object o) {
        mTaskList.add(o);
    }
    public static class filter implements info.jackjia.apkanalysis.utils.Filewalker.Filter {
        //This foo is used for analyzing smali files
        /*public boolean foo(File f) {
            String str = f.getAbsolutePath();
            int count = str.length() - str.replace("/", "").length();
            if (count == 5) {
                if (str.split("/")[5].equals("smali")) return true;
                //if (str.split("/")[5].equals("lib")) return true;
                else return false;
            }
            //The following two condition checks try to filter android libs
            else if (count == 6) {
                if (!str.split("/")[6].equals("android")) return true;
                return false;
            } else if (count == 7) {
                if (!(str.split("/")[7].equals("android") || str.split("/")[7].equals("google"))) return true;
                else return false;
            }
            else return true;
        }*/
        //Used when there is another layer of folder.
        public boolean foo(File f) {
            String str = f.getAbsolutePath();
            int count = str.length() - str.replace("/", "").length();
            if (count == 5) {
                if (str.split("/")[5].startsWith("smali")) return true;
                //if (str.split("/")[5].equals("lib")) return true;
                else return false;
            }
            //The following two condition checks try to filter android libs
            else if (count == 6) {
                if (!str.split("/")[6].equals("android")) return true;
                else return false;
            } else if (count == 7) {
                if (!(str.split("/")[7].equals("android") || str.split("/")[7].equals("google"))) return true;
                else return false;
            }
            else return true;
        }
    }
}
