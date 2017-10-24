package info.jackjia.apkanalysis.apkdownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yurushao on 11/3/15.
 */
public class Downloader implements Runnable {
    public static final String TAG = Downloader.class.getSimpleName();
    private List<String> mWorkList = new ArrayList<String>();

    private String name;
    private String mDestDir;

    public Downloader(String name) {
        // Use current dir as destination if not specified
        this(name, ".");
    }

    public Downloader(String name, String dest) {
        this.name = name;
        mDestDir = dest;
    }

    public String getName() {
        return this.name;
    }

    /*public void run() {
        for (JSONObject o : mWorkList) {
            String category = (String) o.get("category");
            if(category.contains("GAME_")){
                category = "GAME";
            }
            String url = (String) o.get("apk_url");

            if (category == null || url == null) {
                continue;
            }

            if (toDownload(category)) {
                prepareSubDir(category);
                if (download(category, url)) {
                    writeToDb();
                }
            }
        }
    }*/
    public void run() {
        for (String s : mWorkList) {
                if (download(s)) {
                    writeToDb();
                }
            else {
                    System.out.println("Download failed!");
                }
        }
    }
    /**
     * TODO:
     * Write apk info to database
     */
    private void writeToDb() {

    }

    /**
     * TODO:
     * Filter out apks that we don't want to download
     * @param category
     * @return
     */
    private boolean toDownload(String category) {
//        if (category.trim().contains("GAME")) {
//            return false;
//        }

        return true;
    }

    private void prepareSubDir(String dir) {
        File d = new File(mDestDir + File.separator + dir);
        if (!d.exists()) {
            d.mkdir();
        }
    }

    public void addToList(String o) {
        mWorkList.add(o);
    }

    /**
     * TODO:
     * Verify the integrity of the previously downloaded file
     * @param f
     * @return
     */
    private boolean verify(File f) {
        return true;
    }

    private boolean download(String url) {
        System.out.println(String.format("%s is downloading %s", getName(), url));

        String apk = url.substring(url.lastIndexOf('/') + 1);
        String p = mDestDir + File.separator + apk;

        File destFile = new File(p);
        if (destFile.exists() && verify(destFile)) {
            //System.out.println(apk + " has been downloaded, skip");
            return true;
        }

        // start downloading
        int exitCode = info.jackjia.apkanalysis.utils.Shell.exec(new String[]{"wget","--tries=4", url, "-O", p});
        if (exitCode != 0) {
            System.err.println("Fail to download " + apk);
            return false;
        }

        return true;
    }
}