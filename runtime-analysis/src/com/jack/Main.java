package com.jack;

import java.io.BufferedReader;
import java.io.FileReader;

public class Main {

    public static void main(String[] args) {
	// write your code here
        if(args.length<2){
            System.out.println("Option: <apk list> <output dir>");
            return;
        }
        String apk_path = "";
        try {

            BufferedReader br = new BufferedReader(new FileReader(args[0]));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null){
                apk_path = line;
                Runtime rt = Runtime.getRuntime();
                String path_prefix = "/nfs/rome2/Anzhi10000/";
                String apk_prefix = apk_path.substring(apk_path.lastIndexOf('/')+1,apk_path.length());
                String apk_name = apk_prefix+".apk";
                //String apk_prefix = apk_name.substring(0,apk_name.lastIndexOf('-'));
                apk_path = path_prefix+apk_name;
                //System.out.println(apk_path+" "+apk_name+" "+apk_prefix);
                //This is used to automate both download and install
                String command = "./download_install.sh "+apk_path+" "+apk_name+" "+apk_prefix;
                System.out.println(command);
                Process p = rt.exec(command);
                p.waitFor();
                p.destroy();
                line = br.readLine();

            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
