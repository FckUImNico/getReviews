/**
 * Created by pfeiffen on 06.10.2016.
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class GetSHiet {
    public static void main(String[] args) {
        Document doc;
        Scanner sc = new Scanner(System.in);
        try {

            System.out.println("Name der App: ");
            String appname = sc.nextLine();
            System.out.println("Url der App im Googleplaystore: ");
            doc = Jsoup.connect(sc.nextLine()).userAgent("Mozilla").get();
            //String url = "https://play.google.com/store/apps/details?id=com.kakarod.bighunter"; // or whatever goes here

            //Element els = doc.getEflementsByClass("review-title").last();
            //titles = doc.select("span.review-title");
            //reviews = doc.getElementsByClass("review-text").get(1).child(0); Titel
            //reviews = doc.getElementsByClass("review-text").get(1);
            /*String els = doc.getElementsByClass("review-body").get(1).child(0).text();
            String els2 = doc.getElementsByClass("author-name").get(1).text();
            System.out.println(els + "\n" +els2);


            */
            System.out.println("progress has been started...");
             Elements els = doc.getElementsByClass("review-body");
            ArrayList<Element> elements = new ArrayList<>();
            for (Element e : els) {
                elements.add(e);
            }
            Element[] elementArr = elements.toArray(new Element[]{});
            PrintWriter pw = new PrintWriter(new File("Corpora/GPS_"+appname+"_corpus.csv"));
            StringBuilder sb = new StringBuilder();
            String tz = "#"; //trennzeichen in der csv
            sb.append("id"+tz);
            sb.append("appname"+tz);
            sb.append("rating"+tz);
            sb.append("date"+tz);
            sb.append("name"+tz);
            sb.append("title"+tz);
            sb.append("review \n");
            for (int i = 4; i<elementArr.length;i++) {
                String titles_var = doc.getElementsByClass("review-body").get(i).child(0).text();
                String var1 = doc.getElementsByClass("review-body").get(i).text().substring(titles_var.length());
                String reviews_var = var1.substring(0, var1.length()-23);
                String names_var = doc.getElementsByClass("author-name").get(i).text();
                String rating_var = doc.getElementsByClass("review-info-star-rating").get(i).toString().substring(114).substring(0, 1);
                String dates_var = doc.getElementsByClass("review-date").get(i).text();

                sb.append(i-4+tz + appname +tz + rating_var +tz + dates_var +tz + names_var +tz + titles_var +tz + reviews_var + "\n");
            }
            pw.write(sb.toString());
            pw.close();

        } catch (IOException e){
            e.printStackTrace();
        }
       System.out.println("done!!");


    }

    public void makeItReady(){

    }

}

