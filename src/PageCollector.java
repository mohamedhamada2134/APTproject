/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mostafa
 */
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import static com.sun.org.apache.xalan.internal.lib.ExsltDynamic.map;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.DriverManager;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jdk.nashorn.internal.objects.NativeArray.map;
import static jdk.nashorn.internal.objects.NativeDebug.map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.text.Normalizer.Form;

public class PageCollector implements Runnable {

    private static final String USERNAME = "root";
    private static final String PASSWORD = "hamada";
    private static final String CONN_STRING = "jdbc:mysql://localhost:3306/MY_DB";
    static String seed = "https://en.wikipedia.org";

    private int select = 0 ;

    static Map<String, Integer> CrawlerMap = new TreeMap<String, Integer>();
    static int cnt = 1;
    static int UrlId;
    static String NextUrl;
    static Statement stat2;
    static boolean Check1 ;
    static String Tester;
    static boolean Start;

   // static Document doc ;
    static Vector<String> disallowed = new Vector<String>(3);


    public static void setCrawlerMap(Map<String, Integer> crawlerMap) {
        CrawlerMap = crawlerMap;
    }

    public static void setCnt(int cnt) {
        PageCollector.cnt = cnt;
    }

    public static void setUrlId(int urlId) {
        UrlId = urlId;
    }

    public static Map<String, Integer> getCrawlerMap() {
        return CrawlerMap;
    }

    public static int getCnt() {
        return cnt;
    }

    public static int getUrlId() {
        return UrlId;
    }

    public static String getNextUrl() {
        return NextUrl;
    }

    public static Statement getStat2() {
        return stat2;
    }

    public static void setNextUrl(String nextUrl) {
        NextUrl = nextUrl;
    }

    public static void setStat2(Statement stat2) {
        PageCollector.stat2 = stat2;
    }

   /* public PageCollector (Object parameter) {
        // store parameter for later user


    }


*/

        /* try {
            Is_Normalized("http://www.example.com/bar.html","http://www.example.com/bar.html");
        } catch (URISyntaxException ex) {
            Logger.getLogger(PageCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
         */

    public static void body() {


        Check1 = true ;
        CrawlerMap.put(seed, 1);
        NextUrl=seed;
        Tester = seed;
        Start = true ;
       // Robots();

       /* for(int i=0; i<disallowed.size();i++)
            System.out.println(disallowed.elementAt(i));
        */

        System.out.println(disallowed.size());



        GettingNextUrl();


        Crawling();

        try (Connection connection = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD)) {

            System.out.println("Database connected!");
            Statement stat = connection.createStatement();
            stat.executeUpdate("CREATE TABLE URLS (urlid INT, url VARCHAR(20000) , rank INT, title VARCHAR(200), description VARCHAR(200))");
            PrintWriter out = new PrintWriter("Links.txt");
            for (Map.Entry<String, Integer> entry : CrawlerMap.entrySet()) {
                out.println(entry.getKey());
                String query = "INSERT INTO URLS (urlid,url) VALUES(?,?) ;";

                PreparedStatement preparedStmt = connection.prepareStatement(query);
                preparedStmt.setInt(1, entry.getValue());
                preparedStmt.setString(2, entry.getKey());
                //preparedStmt.setString(3, entry.getKey());
                preparedStmt.execute();
            }


            connection.close();
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(PageCollector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot UPDATE THE TABLE! error occured", e);
        }


    }



    public static void Robots() {


        URL aURL = null;
        try {
            aURL = new URL(NextUrl);
        } catch (MalformedURLException e) {

        }
        String t1 = aURL.getProtocol() + "://" + aURL.getHost();

        if (!Tester.equals(t1) || Start)
        {    Tester = t1;
            Start = false ;

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(new URL(Tester + "/robots.txt").openStream()))) {
            String line = null;
            disallowed.clear();
            while ((line = in.readLine()) != null) {

                if (line.contains("User-agent: *")) {


                    while ((line = in.readLine()) != null && (!line.contains("User-agent: *"))) {


                        if (line.equals("Disallow: /")) {
                            disallowed.addElement(Tester);
                        }


                        if (line.contains("Disallow:")) {

                            String[] parts = line.split(" ");

                            disallowed.addElement(parts[1]);


                        }


                    }
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    }


    public static void Crawling() {
        while (UrlId < 20000) {
            try {

                Document doc = Jsoup.connect(NextUrl).get();
                org.jsoup.select.Elements links = doc.select("a");

                for (Element e : links) {
                    String Url = e.attr("abs:href");
                    UrlId++;

                    Check1 = true;
                    for(int i=0; i<disallowed.size();i++) {

                        if (Url.contains(disallowed.elementAt(i)))
                           Check1=false;
                    }

                    if (Check1)
                        CrawlerMap.putIfAbsent(Url, UrlId);



                   // System.out.println(Thread.currentThread().getName());
                    /*out.println(e.attr("abs:href"));*/
                  //  System.out.println(e.attr("abs:href"));
                }
                cnt++;
                GettingNextUrl();

            } catch (IOException ex) {
                Logger.getLogger(PageCollector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void GettingNextUrl() {
        for (Map.Entry<String, Integer> entry : CrawlerMap.entrySet()) {
            if (entry.getValue() == cnt) {
                NextUrl = entry.getKey();
            }

        }

    }

    public static void Is_Normalized(String FUrl, String SUrl) throws URISyntaxException {

        String url1 = FUrl;
        URI uri1 = new URI(url1);
        System.out.println("First Normalized URI = " + uri1.normalize());

        String url2 = SUrl;
        URI uri2 = new URI(url2);
        System.out.println("Second Normalized URI = " + uri2.normalize());
        if (uri1.normalize().equals(uri2.normalize())) {
            System.out.println("same urls");
        } else {
            System.out.println("different ulrs");
        }
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public int getSelect() {
        return select;
    }

    public void run(){



        body();





    }

}