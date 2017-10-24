package info.jackjia.apkanalysis.Grepper;

import info.jackjia.apkanalysis.Engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by jackjia on 11/16/15.
 */
public class Grepper extends Engine {
    private static final String TAG = Grepper.class.getSimpleName();
    private String name;
    private List<String > smalis = new ArrayList<String>();
    public Grepper(String name) {
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
            System.out.println(name+": tasklist length="+mTaskList.size());
            for (Object o : mTaskList) {
                String[] full_name = o.toString().split("/");
                System.out.println("Grepping apk: "+full_name[full_name.length-1]);
                String smaliPath = o.toString();
                grepApk(smaliPath);
            }
            aggregate(GrepController.files);
            System.out.println(name+" completed");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void grepApk(String directory){
        try{
            info.jackjia.apkanalysis.utils.Filewalker fw = new info.jackjia.apkanalysis.utils.Filewalker(".smali",new filter());
            for (String smaliPath : fw.listFiles(directory)) {
                //System.out.println(smaliPath);
                File file = new File(smaliPath);
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.contains("/ServerSocket;->") || line.contains("/ServerSocketChannel;->")) {
                        //System.out.println(smaliPath);
                        smalis.add(directory);
                        scanner.close();
                        return;
                    }
                }
                scanner.close();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void grepSmali(String smaliPath) {
        try {
            File file = new File(smaliPath);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("/ServerSocket;->accept()") || line.contains("/ServerSocketChannel;->bind()")) {
                    smalis.add(smaliPath);
                    break;
                }
            }
            scanner.close();
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
        public boolean foo(File f) {
            /*
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
            */
            //Following code used for category
            String str = f.getAbsolutePath();
            int count = str.length() - str.replace("/", "").length();
            if (count == 6) {
                if (str.split("/")[6].startsWith("smali")) return true;
                    //if (str.split("/")[5].equals("lib")) return true;
                else return false;
            }
            //The following two condition checks try to filter android libs
            else if (count == 7) {
                if (!str.split("/")[7].equals("android")) return true;
                else return false;
            } else if (count == 8) {
                if (!(str.split("/")[8].equals("android") || str.split("/")[8].equals("google"))) return true;
                else return false;
            }
            else return true;
        }
    }
}
