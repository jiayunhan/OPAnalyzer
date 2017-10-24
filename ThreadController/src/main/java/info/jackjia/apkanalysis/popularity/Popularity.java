package info.jackjia.apkanalysis.popularity;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackjia on 2/3/16.
 */
public class Popularity {
    public static List<String> apks=new ArrayList<String>();
    public static List<JSONObject> readMetaData() {
        List<JSONObject> objs = new ArrayList<JSONObject>();
        String mInputPath = "/home/jackjia/2014-10-31.json";
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(mInputPath));
            JSONArray jsonArr = (JSONArray) obj;

            for (int i = 0; i < jsonArr.size(); ++i) {
                JSONObject o = (JSONObject) jsonArr.get(i);
                String category = (String) o.get("category");
                String apk_url = (String) o.get("apk_url");
                if(apk_url!=null) {
                    String[] parts = apk_url.split("/");
                    String package_name = parts[parts.length-1];
                    //package_name = package_name.substring(0,package_name.length()-4);
                    if(apks.contains(package_name)){
                        Long  downloads = (Long) o.get("downloads");
                        System.out.println(downloads);
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(objs.size() + " APK info read");

        return objs;
    }
    public static void readFile(){
        try{
        BufferedReader br = new BufferedReader(new FileReader("/home/jackjia/grep_result/set.txt"));
            String line = br.readLine();
            while(line != null){
                String[] parts = line.split("/");
                String package_name = parts[parts.length-1];
                apks.add(package_name);
                line = br.readLine();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
