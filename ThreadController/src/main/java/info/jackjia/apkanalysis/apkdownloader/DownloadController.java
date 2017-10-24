package info.jackjia.apkanalysis.apkdownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by  on 11/3/15.
 */
public class DownloadController {
    public static final String TAG = DownloadController.class.getSimpleName();
    public static final int MAX_THREADS = 64;

    private static volatile int CURRENT_THREAD_ID = -1;

    private int mNumThreads;
    private ThreadPoolExecutor mExecutor;
    private List<Downloader> mWorkers = new ArrayList<Downloader>();

    private String mInputPath;
    private String mDestDir;

    private int book=0;
    private int business=0;
    private int comics=0;
    private int communication=0;
    private int education=0;
    private int entertainment=0;
    private int finance=0;
    private int game=0;
    private int fitness=0;
    private int lib=0;
    private int lifestyle=0;
    private int media=0;
    private int medical=0;
    private int music=0;
    private int news=0;
    private int personalize=0;
    private int photography=0;
    private int productivity=0;
    private int shopping=0;
    private int social=0;
    private int sports=0;
    private int transportation=0;
    private int travel=0;
    private int weather=0;

    public DownloadController() {
        this(1);
    }

    public DownloadController(int n) {
        mNumThreads = n;
        mExecutor = new ThreadPoolExecutor(mNumThreads, MAX_THREADS, 10,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(MAX_THREADS));
    }

    public void runExecutor() {
        for (Runnable r : mWorkers) {
            try{
                Thread.sleep(1000);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            mExecutor.execute(r);
        }

        mExecutor.shutdown();
    }

    public void setDataFile(String path) {
        mInputPath = path;
    }

    public void setDestDir(String dir) {
        mDestDir = dir;

        File d = new File(mDestDir);
        if (!d.exists()) {
            d.mkdirs();
        }
    }

    private void initializeWorkers() {
        for (int i = 0; i < mNumThreads; ++i) {
            Downloader d = new Downloader(String.format("Thread-%d", getNextId()), mDestDir);
            mWorkers.add(d);
        }
    }

    private int getNextId() {
        return CURRENT_THREAD_ID += 1;
    }

    public void run() {
        initializeWorkers();

        //
        // Assign tasks to workers
        //
        int i = 0;
        /*for (JSONObject o : readFromFile()) {
            mWorkers.get(i % mNumThreads).addToList(o);
            i++;
        }
        */
        assignFromList(mInputPath);

        runExecutor();
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
    /*private List<JSONObject> readMetaData() {
        List<JSONObject> objs = new ArrayList<JSONObject>();

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(mInputPath));
            JSONArray jsonArr = (JSONArray) obj;

            for (int i = 0; i < jsonArr.size(); ++i) {
                JSONObject o = (JSONObject) jsonArr.get(i);
                String category = (String)o.get("category");
                String apk_url = (String)o.get("apk_url");
                if(category.contains("GAME_")){
                    if(game<1000){
                        game++;
                        objs.add(o);
                    }
                }
                else if(category.equals("BUSINESS")){
                    if(business<1000){
                        business++;
                        objs.add(o);
                    }
                }
                else if(category.equals("BOOKS_AND_REFERENCE")){
                    if(book<1000){
                        book++;
                        objs.add(o);
                    }
                }
                else if(category.equals("COMICS")){
                    if(comics<1000){
                        comics++;
                        objs.add(o);
                    }
                }
                else if(category.equals("COMMUNICATION")){
                    if(communication<1000){
                        communication++;
                        objs.add(o);
                    }
                }
                else if(category.equals("EDUCATION")){
                    if(education<1000){
                        education++;
                        objs.add(o);
                    }
                }
                else if(category.equals("ENTERTAINMENT")){
                    if(entertainment<1000){
                        entertainment++;
                        objs.add(o);
                    }
                }
                else if(category.equals("FINANCE")){
                    if(finance<1000){
                        finance++;
                        objs.add(o);
                    }
                }
                else if(category.equals("HEALTH_AND_FITNESS")){
                    if(fitness<1000){
                        fitness++;
                        objs.add(o);
                    }
                }
                else if(category.equals("LIBRARIES_AND_DEMO")){
                    if(lib<1000){
                        lib++;
                        objs.add(o);
                    }
                }
                else if(category.equals("LIFESTYLE")){
                    if(lifestyle<1000){
                        lifestyle++;
                        objs.add(o);
                    }
                }
                else if(category.equals("MEDIA_AND_VIDEO")){
                    if(media<1000){
                        media++;
                        objs.add(o);
                    }
                }
                else if(category.equals("MEDICAL")){
                    if(medical<1000){
                        medical++;
                        objs.add(o);
                    }
                }
                else if(category.equals("MUSIC_AND_AUDIO")){
                    if(music<1000){
                        music++;
                        objs.add(o);
                    }
                }
                else if(category.equals("NEWS_AND_MAGAZINES")){
                    if(news<1000){
                        news++;
                        objs.add(o);
                    }
                }
                else if(category.equals("PERSONALIZATION")){
                    if(personalize<1000){
                        personalize++;
                        objs.add(o);
                    }
                }
                else if(category.equals("PHOTOGRAPHY")){
                    if(photography<1000){
                        photography++;
                        objs.add(o);
                    }
                }
                else if(category.equals("PRODUCTIVITY")){
                    if(productivity<1000){
                        productivity++;
                        objs.add(o);
                    }
                }
                else if(category.equals("SHOPPING")){
                    if(shopping<1000){
                        shopping++;
                        objs.add(o);
                    }
                }
                else if(category.equals("SOCIAL")){
                    if(social<1000){
                        social++;
                        objs.add(o);
                    }
                }
                else if(category.equals("SPORTS")){
                    if(sports<1000){
                        sports++;
                        objs.add(o);
                    }
                }
                else if(category.equals("TRANSPORTATION")){
                    if(transportation<1000){
                        transportation++;
                        objs.add(o);
                    }
                }
                else if(category.equals("TRAVEL_AND_LOCAL")){
                    if(travel<1000){
                        travel++;
                        objs.add(o);
                    }
                }
                else if(category.equals("WEATHER")){
                    if(weather<1000){
                        weather++;
                        objs.add(o);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(objs.size() + " APK info read");

        return objs;
    }*/
    public void OnComplete(){

    }
}
