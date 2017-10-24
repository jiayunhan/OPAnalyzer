package info.jackjia.apkanalysis.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by yurushao on 10/20/15.
 */
public class Shell {

    public static int exec(String[] cmds) {

        Runtime rt = Runtime.getRuntime();
        Process proc = null;

        int exitCode = 0;

        try {
            proc = rt.exec(cmds);

            StreamGobbler errorGobbler = new
                    StreamGobbler(proc.getErrorStream(), "ERROR");
            StreamGobbler outputGobbler = new
                    StreamGobbler(proc.getInputStream(), "OUTPUT");

            errorGobbler.start();
            outputGobbler.start();

            exitCode = proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return exitCode;
    }
}

class StreamGobbler extends Thread {
    InputStream is;
    String type;

    StreamGobbler(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
//                System.out.println(type + ">" + line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}