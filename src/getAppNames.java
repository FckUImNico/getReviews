/**
 * Created by Nico on 31.10.2016.
 */
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class getAppNames {

    public static void main(String[] args) throws InterruptedException {
        new getAppNames(100);
    }


    public getAppNames(int count) throws InterruptedException {


    String test = getApps("start=0&num="+count+"&numChildren=0&cctcss=square-cover&cllayout=NORMAL&ipf=1&xhr=1");
    int x = 0;
    ArrayList<String> elements = new ArrayList<>();

    for(String e : test.split("href=\\\"/store/apps/details\\?id=")) {
        if (e.startsWith("<div>"))continue;
        if (elements.isEmpty())elements.add(e.split("\\\"")[0]);
        System.out.println(elements.size());
        for (int i = x; i<=elements.size();i++)      {
            if(e.split("\\\"")[0].equals(elements.get(i))){break;}
            else{ elements.add(e.split("\\\"")[0]);
                x++;
                break; }
        }
    }
    System.out.println(elements);
    }


    private static String getApps(String urlParameters) throws InterruptedException {
        HttpURLConnection connection = null;

        try {
            //Create connection
            //URL url = new URL("https://play.google.com/store/apps/collection/promotion_3000957_casualgamemea?authuser=0"); //Casualgames
            URL url = new URL("https://play.google.com/store/apps/collection/promotion_300085a_most_popular_games?authuser=0"); //Popular
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            //adding some headers
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("Host","play.google.com");
            connection.setRequestProperty("Accept-Language","de,en-US;q=0.7,en;q=0.3");
            connection.setRequestProperty("Referer","https://play.google.com/store/apps/collection/promotion_300085a_most_popular_games");
            connection.setRequestProperty("Content-Length",Integer.toString(urlParameters.getBytes().length));


            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            //adding the parameters
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            System.out.println("Get Response...");
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;


            int minDelay = 300; //3 seconds (minimum delay between http requests)
            int maxDelay = 1000;// 10 seconds (max delay ...)
            Thread.sleep((long)(Math.random()*(maxDelay - minDelay) + minDelay));
            /*
            * I often reached a "maximum" of data inquiries so google wanna u to fill a captcha
            * we can't fill that captcha, so better try to not reach it
            */

            while ((line = rd.readLine()) != null) {
                //System.out.println(line);
                response.append(line);
                //System.out.println(response.toString());
                //response.append('\n');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
