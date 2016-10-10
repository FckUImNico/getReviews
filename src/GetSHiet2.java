/**
 * Created by pfeiffen on 06.10.2016.
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class GetSHiet2 {
    public static void main(String[] args) {
        try {
        Scanner sc = new Scanner(System.in);
        System.out.println("Corpusname: ");
        String appname = sc.nextLine();
        PrintWriter pw = new PrintWriter(new File("GPS_"+appname+"_corpus.csv"));
        StringBuilder sb = new StringBuilder();
        String tz = "#"; //trennzeichen in der cs
        String url;
        sb.append("id"+tz);
        sb.append("appname"+tz);
        sb.append("rating"+tz);
        sb.append("date"+tz);
        sb.append("name"+tz);
        sb.append("title"+tz);
        sb.append("review \n");
        System.out.println("Url der App im Googleplaystore(leeres Enter beendet Eingabe): ");
            ArrayList<String> data = new ArrayList<String>();
            System.out.println(data.isEmpty());
            while(data.isEmpty()||!data.get(data.size()-1).equals("")){
                data.add(sc.nextLine());
            }
        for(int i = 0;i<data.size()-1;i++){
            infinitymode(data.get(i),appname,tz,sb);
        }

        pw.write(sb.toString());
        pw.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    static private String infinitymode(String url, String appname, String tz, StringBuilder sg){
        try {
            Document doc;
            doc = Jsoup.connect(url).userAgent("Mozilla").get();

            System.out.println("progress has been started...");
            Elements els = doc.getElementsByClass("review-body");
            ArrayList<Element> elements = new ArrayList<>();
            for (Element e : els) {
                elements.add(e);
            }
            Element[] elementArr = elements.toArray(new Element[]{});
            for (int i = 4; i<elementArr.length;i++) {
                String titles_var = doc.getElementsByClass("review-body").get(i).child(0).text();
                String var1 = doc.getElementsByClass("review-body").get(i).text().substring(titles_var.length());
                String reviews_var = var1.substring(0, var1.length()-23);
                String names_var = doc.getElementsByClass("author-name").get(i).text();
                String rating_var = doc.getElementsByClass("review-info-star-rating").get(i).toString().substring(114).substring(0, 1);
                String dates_var = doc.getElementsByClass("review-date").get(i).text();

                sg.append(i-4+tz + appname +tz + rating_var +tz + dates_var +tz + names_var +tz + titles_var +tz + reviews_var + "\n");
            }

        } catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("done!!");
        return sg.toString();
    }

}

