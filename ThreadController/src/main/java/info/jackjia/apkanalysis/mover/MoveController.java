package info.jackjia.apkanalysis.mover;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackjia on 1/4/16.
 */
public class MoveController extends info.jackjia.apkanalysis.ThreadController {
    private static final String TAG = MoveController.class.getSimpleName();
    private boolean mRecursiveMode = false;
    private String mSrcDir = ".";
    private String mOutDir = ".";
    public MoveController(int n) {
        super(n);
    }
    public static List<String> files = new ArrayList<String>();
    public BufferedWriter out;
    @Override
    protected void init() {
        for (int i = 0; i < mNumThreads; ++i) {
            String name = String.format("Grepper-%d", info.jackjia.apkanalysis.ThreadController.getNextId());
            mWorkers.add(new info.jackjia.apkanalysis.lister.Lister(name));
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
    @Override
    protected void assign() {
        int i = 0;
        for (String apk : findAllDirectories(mRecursiveMode)) {
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
        assignFromList(mSrcDir);
        runExecutor();
        Runnable runnable = new monitor();
        Thread monitor_thread = new Thread(runnable);
        monitor_thread.start();
    }

    private List<String> findAllDirectories(boolean recursive) {
        int i=0;
        List<String> directories = new ArrayList<String>();

        for (File f : new File(mSrcDir).listFiles()) {
            if (f.isDirectory()) {
                directories.add(f.getAbsolutePath());
                //System.out.println(i++);
            }
        }

        return directories;
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

        }
    }
}
