package info.jackjia.apkanalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by yurushao on 11/3/15.
 */
public abstract class ThreadController {
    public static final String TAG = ThreadController.class.getSimpleName();
    public static final int MAX_THREADS = 64;

    private static volatile int CURRENT_THREAD_ID = -1;

    protected int mNumThreads;
    protected ThreadPoolExecutor mExecutor;
    protected List<info.jackjia.apkanalysis.Engine> mWorkers = new ArrayList<info.jackjia.apkanalysis.Engine>();

    public ThreadController() {
        this(1);
    }

    public ThreadController(int n) {
        mNumThreads = n;
        mExecutor = new ThreadPoolExecutor(mNumThreads, MAX_THREADS, 10,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(MAX_THREADS));
    }

    protected void runExecutor() {
        for (Runnable r : mWorkers) {
            mExecutor.execute(r);
        }
        System.out.println("All thread started");
    }
    protected void shutdownExecutor(){
        mExecutor.shutdown();
    }

    public static int getNextId() {
        return CURRENT_THREAD_ID += 1;
    }

    /**
     * Do initialization
     */
    protected abstract void init();

    /**
     * Assign tasks to workers
     */
    protected abstract void assign();

    /**
     * Start working
     */
    public abstract void start();
    /**
     * Monitor threads status
     */
    public class monitor implements Runnable{
        public void run(){
            while(true){
                //System.out.println("ActiveCount="+mExecutor.getActiveCount());
                if(mExecutor.getActiveCount()==0){
                    OnComplete();
                    break;
                }
                try{
                    Thread.sleep(5000);
                }
                catch (Exception e){
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
    /**
     * OnComplete Callback actions
     */
    protected abstract void OnComplete ();
}
