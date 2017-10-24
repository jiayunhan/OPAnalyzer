package info.jackjia.apkanalysis.Grepper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackjia on 11/16/15.
 */
public class GrepController extends info.jackjia.apkanalysis.ThreadController {
    private static final String TAG = GrepController.class.getSimpleName();

    private boolean mRecursiveMode = false;
    private String mSrcDir = ".";
    private String mOutDir = ".";
    private String keyword = "ServerSocket";
    private String filename = ".";
    public static List<String> files = new ArrayList<String>();
    public BufferedWriter out;
    public GrepController(int n) {
        super(n);
    }

    @Override
    protected void init() {
        for (int i = 0; i < mNumThreads; ++i) {
            String name = String.format("Grepper-%d", info.jackjia.apkanalysis.ThreadController.getNextId());
            //System.out.println(name);
            mWorkers.add(new info.jackjia.apkanalysis.Grepper.Grepper(name));
        }
    }

    public void recursiveMode(boolean r) {
        mRecursiveMode = r;
    }

    public void setSrcDir(String d) {
        mSrcDir = d;
    }

    public void setOutDir(String d) {
        mOutDir = d;
    }
    public void setFilename(String d){
        filename = d;
    }
    public void setKeyword(String d){ keyword = d;}
    @Override
    protected void assign() {
        int i = 0;
        //for (String apk : findAllSmalis(mRecursiveMode)) {
        for (String apk : findAllApks()) {
            // TODO:
            // Describe the task using Map

            mWorkers.get(i % mNumThreads).addToList(apk);
            ++i;
        }
        System.out.print("Assign Completed!");
    }

    @Override
    public void start() {
        init();
        //assignFromList(mSrcDir);
        assign();
        runExecutor();
        Runnable runnable = new monitor();
        Thread monitor_thread = new Thread(runnable);
        monitor_thread.start();
    }
    private List<String> findAllApks(){
        int i=0;
        List<String> directories = new ArrayList<String>();

        for (File f : new File(mSrcDir).listFiles()) {
            if (f.isDirectory()) {
                directories.add(f.getAbsolutePath());
                //System.out.println(f.getAbsolutePath());
            }
        }

        return directories;
    }
    private List<String> findAllSmalis(boolean recursive) {
        List<String> smalis = new ArrayList<String>();

        if (recursive) {
//
//           Filewalker fw = new Filewalker(".apk");
            info.jackjia.apkanalysis.utils.Filewalker fw = new info.jackjia.apkanalysis.utils.Filewalker(".smali",new filter());
            smalis.addAll(fw.listFiles(mSrcDir));
        } else {
            for (File f : new File(mSrcDir).listFiles()) {
                if (f.getName().endsWith(".smali")) {
                    smalis.add(f.getAbsolutePath());
                    //System.out.println(f.getAbsolutePath());
                }
            }
        }

        return smalis;
    }
    private void assignFromList (String filename){
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = br.readLine();
            int i=0;
            while(line != null){
                mWorkers.get(i%mNumThreads).addToList(line);
                ++i;
                line = br.readLine();
            }
            System.out.println("Assign Completed");
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public void OnComplete(){
        System.out.println("OnComplete");
        System.out.println("Files length="+files.size());
        shutdownExecutor();
        try {
            File outFile = new File(mOutDir);
            if (!outFile.exists()) {
                outFile.createNewFile();
            }
            out = new BufferedWriter(new FileWriter(outFile.getName(), true));
            for (String str:files){
                out.write(str+"\n");
            }
            out.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public static class filter implements info.jackjia.apkanalysis.utils.Filewalker.Filter {
        public boolean foo(File f) {
            String str = f.getAbsolutePath();
            int count = str.length() - str.replace("/", "").length();
            if (count == 6) {
                if (str.split("/")[6].equals("smali")) return true;
                    //if (str.split("/")[5].equals("lib")) return true;
                else return false;
            }
            //The following two condition checks try to filter android libs
            else if (count == 7) {
                if (!str.split("/")[7].equals("android")) return true;
                return false;
            } else if (count == 8) {
                if (!(str.split("/")[8].equals("android") || str.split("/")[8].equals("google"))) return true;
                else return false;
            }
            else return true;
        }
    }
}
