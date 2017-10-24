package info.jackjia.apkanalysis.utils;

import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by yurushao on 11/4/15.
 */
public class Filewalker {
    String suffix = null;
    private String mPackageList = ".";
    private List<String> packageList;;
    Filter filter;
    public Filewalker() {
    }

    public Filewalker(String suffix) {
        this.suffix = suffix;
        //this.mPackageList = mPackageList;
    }
    public Filewalker(String suffix, Filter f){
        this.suffix = suffix;
        filter = f;
    }
    public static interface Filter{
        public boolean foo(File f);
    }
    /**
     * Recursively list all files with specific suffix in the given directory
     *
     * @param path
     * @return
     */
    public List<String> listFiles(String path) {
        List<String> files = new ArrayList<String>();
        //Temp for Jack
        /*packageList = new ArrayList<String>();
        List<String> selectedApks = new ArrayList<String>();
        try {
            Scanner scanner = new Scanner(new File(mPackageList));

            while(scanner.hasNextLine()){
                packageList.add(scanner.nextLine());
            }
        //System.out.println(packageList.toString());
        String str;*/

            File root = new File(path);
            File[] list = root.listFiles();

            if (list == null)
                return files;

            for (File f : list) {
                if (f.isDirectory()) {
                    if(filter.foo(f)){
                        List<String> tmp = listFiles(f.getAbsolutePath());
                        files.addAll(tmp);
                    }
                } else {
                    if (suffix == null || f.getAbsolutePath().endsWith(suffix)) {
                        //System.out.println(suffix);
                        //Used to find features
                        /*if(f.getAbsolutePath().contains("DPWebServer")) {
                            System.out.println("---------"+f.getAbsolutePath()+"---------");
                            files.add(f.getAbsolutePath());
                        }*/
                        files.add(f.getAbsolutePath());
                    }
                }
            }

        return files;
    }

}
