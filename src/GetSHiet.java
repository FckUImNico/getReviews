import java.io.*;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by pfeiffen on 06.10.2016.
 */


public class GetSHiet {
    //static int i;
    static boolean checkVar = false;
    static int a;//just used to count some stuff
    static String link;// the url of the website we will get the reviews from
    //String id = link.replace("https://play.google.com/store/apps/details?id=","");//the part replaced is equal to every other gps apps, just the id is needed for some operations
    static String delimiter = "%";// delimiter for .csv, also used for separate the review-information after parsing them
    static StringBuilder sg = new StringBuilder();//used to put information of all requests together
    static StringBuilder alexV = new StringBuilder();
    static StringBuilder betterOne = new StringBuilder();


    public static void main(String[] args) throws InterruptedException {

        GetSHiet g = new GetSHiet();
        makePAttern();
        System.out.println("How many different apps do you want?");
        getAppNames apps = new getAppNames(new Scanner(System.in).nextInt());
        System.out.println("How many reviews do you want per app? ");
        int reviewCount = Integer.parseInt(new Scanner(System.in).nextLine());
        for (int i = 0;i<apps.elements.size();i++) {
            doOne(apps.elements.get(i),reviewCount);
        }

        try {
            writeInFile("all",sg,""); //write everything in a file, here: .csv
            writeInFile("all",betterOne,"_review_rating");
            writeInFile("all",alexV,"alexPattern");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void doOne(String gid, int reviewCount) throws InterruptedException {
       // System.out.println("You might receive an error, when the app has less then 40 reviews!\n"+"URL of GOOGLE Play Store: ");
       // link = new Scanner(System.in).nextLine();

        int pageNum = getPageNum(reviewCount);
        String glink = "https://play.google.com/store/apps/details?id="+gid;

        //System.out.println("How many reviews do you want? ");
        //int reviewCount = Integer.parseInt(new Scanner(System.in).nextLine());
        //System.out.println(pageNum);

        String[] splitter = new String[]{
                "<span class=\\\\\"author-name\\\\\">",// Author
                "<span class=\\\\\"review-date\\\\\">",// Date
                "star-rating-non-editable-container\\\\\" aria-label=\\\\\"",// Rating
                "<span class=\\\\\"review-title\\\\\">",// Title + Review
                "<div class=\\\\\"review-link\\\\\"",//Deletable
                "</span>",//Seperator
                "Rezension",//Deletable
        };

        System.out.println("Progress has been started...");
        System.out.println("...sending multiple HTTP requests\n"+"This may takes a few seconds or even minutes, but many requests in a short period of time isn't appreciated that much. ");


        ArrayList<String> elements = new ArrayList<>();//List for separated strings from ONE post request
        for(int p =0;p<=pageNum;p++) {
            String postRequest = executePost(glink, "https://play.google.com/store/getreviews?authuser=0", "reviewType=0&id=" + gid + "&reviewSortOrder=4&xhr=1&pageNum=" + p + "&token=").replaceAll("\\\\u003c", "<").replaceAll("\\\\u003d", "=").replaceAll("\\\\u003e", ">");
            for (String e : postRequest.split(splitter[0] + "|" + splitter[1] + "|" + splitter[2] + "|" + splitter[3] + "|" + splitter[4])) { //split the requested stuff
                String sort = sort(e); //parse the strings, delete unnecessary information
                if (sort != null) {
                    elements.add(sort); //add the string to the list if it is not null - i return null in the sort()-method when its not needed anymore
                }
            }
            /*
            * the pipe (|) can be replaced with every other symbol - its based on the delimiter
            * name|
            * date|
            * rating|
            * title|review
            * name|
            * ....
            */
            for (int i = 0; i < elements.size() / 4; i++) {/*the splitted and parsed list is in the pattern above - every fourth line begins a new set of data*/
                if(p*elements.size()/4+i==reviewCount)break; //stop the loop, when we already reached the given number of reviews the user wanted
                String id = String.valueOf(p*elements.size()/4+i);
                String appstore = "GPS";
                String appname = gid;
                String reviewlang = "deu";
                String reviewdate = elements.get(4 * i + 1); //already has a delimiter
                String reviewrating = elements.get(4 * i + 2);//already has a delimiter
                String reviewtitle = elements.get(4 * i + 3).split("\\"+delimiter)[0];
                String reviewphrase = elements.get(4 * i + 3).split("\\"+delimiter)[1];
                String reviewuserid = String.valueOf(Math.round(Math.random()*10000));
                String reviewusername = elements.get(4 * i).substring(2).replace(" " + delimiter + " ", delimiter); //already has delimiter
                String reviewmetadata = "0";

                sg = addToStringBuilder(new String[]{a + delimiter, reviewusername, reviewdate, reviewrating, reviewtitle+delimiter,reviewphrase},sg);
                alexV= addToStringBuilder(new String[]{a + delimiter,appstore+delimiter,appname+delimiter,reviewlang+delimiter,String.valueOf(timeToMillis(reviewdate.split("\\s+")))+delimiter,reviewrating,reviewtitle+delimiter,reviewphrase+delimiter,reviewuserid+delimiter,reviewusername,reviewmetadata},alexV);
                betterOne = makeItBetter(reviewphrase,a,betterOne);
                //addToStringBuilder(new String[]{String.valueOf(p*elements.size()/4+i) + delimiter, elements.get(4 * i).substring(2).replace(" " + delimiter + " ", delimiter), elements.get(4 * i + 1), elements.get(4 * i + 2), elements.get(4 * i + 3)});
                /*
                * the replacing is just for a clearer .csv - just delete useless whitespaces
                * we add it like that: id, 0 , 1 , 2 , 3
                *                      id, 4 , 5 , 6 , 7
                *                      ....
                */
                a++;
            }
            System.out.println(elements.size()/4+" reviews found.");
            System.out.println(elements.size()/4+" reviews found. | "+gid);
            if(elements.size()<=0)break; //when the list is empty, the request wasnt able to get more reviews, so we might already got all of them - so we can stop it
            elements.clear(); // clear the list for the next request
        }
        System.out.println("Done! You just receive "+a+" reviews!");

    }

    private static String executePost( String link, String targetURL, String urlParameters) throws InterruptedException {
        /*
         * http://stackoverflow.com/questions/1359689/how-to-send-http-request-in-java
         * first answer helped me
         */

        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
                //adding some headers
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("Host","play.google.com");
            connection.setRequestProperty("Accept-Language","de,en-US;q=0.7,en;q=0.3");
            connection.setRequestProperty("Referer",link);
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


            int minDelay = 3000; //3 seconds (minimum delay between http requests)
            int maxDelay = 10000;// 10 seconds (max delay ...)
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

    private static String sort(String els) {
        String[] cases = {"Vollständige Rezension", ")]}'","</span> "," von fünf Sternen","reviews-permalink","single-review"};
        /*
         * we try to filter the unneeded information and delete it later to return a string with just the requested info, like name, review, title, etc
         */
        if(els.contains(cases[5]))return null;
        if(els.contains("developer-reply"))checkVar=true;
        int i;
            for(i = 0; i < cases.length; i++)
            if(els.contains(cases[i])) break;
        /*
         * generate a number for every possible case above
         *
         */
        switch(i) {
            case 0: return null; // we dont need it, cause this line is left from splitting before and i was too dumb to delete it directly
            case 1: return null; // se above
            case 2:
                if(checkVar) {
                    checkVar=false;
                    return null;
                }else if(els.split(cases[2])[1].contains(cases[4]) && !checkVar){
                    return els.split(cases[2])[0]+delimiter; //date
                }else return els.replace(cases[2],delimiter); //title, review, name
            case 3: return els.split(cases[3])[0].split("Mit ")[1]+delimiter; //rating
            default: return els; //
        }

    }
    /*
    private static String sort(String els) {
        String[] cases = {"Vollständige Rezension", ")]}'","</span> "," von fünf Sternen","reviews-permalink"};
    a++;
    System.out.println(a+": "+els);
    int i;
    for(i = 0; i < cases.length; i++)
            if(els.contains(cases[i])) break;

    switch(i) {
        case 0: return null; // we dont need it, cause this line is left from splitting before and i was too dumb to delete it directly
        case 1: return null; // se above
        case 2:
            if(els.split(cases[2])[1].contains(cases[4])){
                return els.split(cases[2])[0]+delimiter; //date
            }else return els.replace(cases[2],delimiter); //title, review, name
        case 3: return els.split(cases[3])[0].split("Mit ")[1]+delimiter; //rating
        default: return els; //
    }

}
    */

    private static void writeInFile(String id, StringBuilder sb, String detail) throws IOException{
        String fileName = "Corpora\\GPS_"+id+"_corpus"+detail+".csv"; //that will be your name of the .csv
        PrintWriter pw = new PrintWriter(new File(fileName));
        pw.write(sb.toString()); //write all of the stringbuilder in the file and close it
        pw.close();
        System.out.println("Here is your .csv stored: "+System.getProperty("user.dir")+"\\"+fileName);
    }

    private static StringBuilder addToStringBuilder(String[] array,StringBuilder sg){
        for(int i = 0;i<array.length;i++){
            sg.append(array[i]);
        }
        sg.append("\n");

        return sg;
        /*
         * adds the id, name, rating, title, review to the stringbuilder
         */
    }

    /*
     private static void addToStringBuilder(String[] array){
        for(int i = 0;i<array.length;i++){
            sg.append(array[i]);
        }
        sg.append("\n");
        }
     */

    private static int getPageNum(int input){
        return Math.round(input/40); //100 would be pageNum 2, pageNum means three http requests(index 0, 1, 2), what means we will get 120 reviews (the 100 fits in)
    }

    private static StringBuilder makeItBetter(String review, int id,StringBuilder betterOne){
        for (String e : review.split("\\s+")){
            betterOne = addToStringBuilder(new String[]{id+delimiter,e+delimiter,getRating(e)}, betterOne);
        }

        return betterOne;
    }

    private static long timeToMillis(String[] date){
        int year = Integer.parseInt(date[2].substring(0,date[2].length()-1));
        int month = getMonth(date[1]);
        int day = Integer.parseInt(date[0].replace(".",""));
        Date d = new Date(year-1900, month, day);
        return d.getTime();
    }

    private static int getMonth(String month){
        String[] months = new String[]{
                "Januar",
                "Februar",
                "März",
                "April",
                "Mai",
                "Juni",
                "Juli",
                "August",
                "September",
                "Oktober",
                "November",
                "Dezember"
        };
        int i;
        for(i = 0; i <= months.length; i++)
            if(month.equals(months[i])) break;
        return i;
    }

    private static void makePAttern(){
        sg = addToStringBuilder(new String[]{"id" + delimiter, "name" + delimiter, "date" + delimiter, "rating" + delimiter, "title" + delimiter, "review"},sg);
        alexV = addToStringBuilder(new String[]{
                "id"+delimiter,
                "appstore"+delimiter,
                "appname"+delimiter,
                "reviewlang"+ delimiter,
                "reviewdate"+ delimiter,
                "reviewrating"+ delimiter,
                "reviewtitle"+ delimiter,
                "reviewphrase"+ delimiter,
                "reviewuserid"+ delimiter,
                "reviewusername"+ delimiter,
                "reviewmetadata"},alexV);
        //addToStringBuilder(new String[]{"id" + delimiter, "name" + delimiter, "date" + delimiter, "rating" + delimiter, "title" + delimiter, "review"});
        betterOne = addToStringBuilder(new String[]{"id"+delimiter,"review-parts"+delimiter,"review-rating"},betterOne);
    }

    private static String getRating(String word){
        String[] cases_pos = {"super","cool","geil","excellent","toll","gelungen","liebe","wunderbar","exzellent","augenweide","genial","genialer","geniale","wunderbare","tolles","gelungenes","geil","toll","gefällt","gut","gute","interessant","süchtig","bock","spaß","spannend","spannende","spannendes","tolle","tolles","gute","gutes","spaßig","geiles"};
        String[] cases_neg = {"scheiße","schade","reinfall","garnicht","schlecht","blöd","doof","kacke","scheiß","nicht","sinnlos","schlechtes","schlechte","doofe","doofes","blödes","sinnloses"};
        for (String cases_po : cases_pos)
            if (word.toLowerCase().equals(cases_po)) {
                return "1";
            }
        for (String aCases_neg : cases_neg)
            if (word.toLowerCase().equals(aCases_neg)) {
                return "-1";
            }
        return "0";

    }
}

