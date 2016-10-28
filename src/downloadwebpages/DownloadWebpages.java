/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloadwebpages;

import com.jaunt.*;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.apache.commons.io.FileUtils;

/**
 * @author bensearle
 */
public class DownloadWebpages {
    JFrame gui;

    static String htmlTemplatePath = "template.html";
    static String webpagesTxtFilePath = "webpages.txt";
    static String dataFolderPath = "data";

    static String indexPageTitle = "bensearle";
    static String indexPageFirstLine = "Developed by Ben Searle <a href=\"https://github.com/bensearle\">https://github.com/bensearle</a>";

    List<Pair<String, String>> webpages;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnsupportedEncodingException {
        // TODO code application logic here
        DownloadWebpages dw = new DownloadWebpages();
        dw.go();
    }

    /**
     * run the program
     */
    private void go() {
        createGUI();
        System.out.println("Starting");

        if (checkFilesExist()) { // check all files exist
            getWebpagesFromFile(); // get the webpages from the txt file
            for (Pair wp : webpages) {
                downloadPage(wp.getL().toString(), wp.getR().toString()); // download all of the webpages
            }
            createIndexPage(); // create an index page to link to all downloaded pages
            System.out.println("Complete");
        } else {
            System.out.println("Cannot complete");
        }

    }

    /**
     * check that the required files and folders exist on the harddrive
     * @return true if all exist
     */
    private static boolean checkFilesExist() {
        boolean exist = true;
        System.out.println("Searching " + new File("").getAbsolutePath());
        
        // check html template
        File h = new File(htmlTemplatePath);
        if (h.isFile()) {
            System.out.println("Found: " + htmlTemplatePath);
        } else {
            System.out.println("ERROR Missing File: " + htmlTemplatePath);
            exist = false;
        }

        // check webpages text file
        File w = new File(webpagesTxtFilePath);
        if (w.isFile()) {
            System.out.println("Found: " + webpagesTxtFilePath);
        } else {
            System.out.println("ERROR Missing File: " + webpagesTxtFilePath);
            exist = false;
        }

        // check data folder
        File d = new File(dataFolderPath);
        if (d.isDirectory()) {
            System.out.println("Found: " + dataFolderPath);
        } else {
            System.out.println("ERROR Missing Folder: " + dataFolderPath);
            exist = false;
        }

        return exist;
    }
    
    /**
     * create a gui to mimic the output console
     */
    private void createGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame();
        frame.add(new JLabel(" Output"), BorderLayout.NORTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // add JTextArea to show console
        JTextArea ta = new JTextArea(30, 50);
        TextAreaOutputStream taos = new TextAreaOutputStream(ta, 60);
        PrintStream ps = new PrintStream(taos);
        System.setOut(ps);
        System.setErr(ps);

        frame.add(new JScrollPane(ta));
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * read the webpages in the text file and save them in the java list
     */
    private void getWebpagesFromFile() {
        try {
            System.out.println("Reading: " + webpagesTxtFilePath);
            webpages = new ArrayList<>(); // initialize list

            Stream<String> stream = Files.lines(Paths.get(webpagesTxtFilePath), Charset.defaultCharset());
            stream.forEach((line) -> {
                System.out.println("Read line: " + line);
                String[] split = line.split("\\t");
                if (split.length > 1) {
                    webpages.add(new Pair(split[0], split[1]));
                }
            });
            //stream.forEach(System.out::println); // print line from file
        } catch (IOException ex) {
            Logger.getLogger(DownloadWebpages.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * download a webpage and save it on the hard drive
     * @param pagename name of the page ("save as")
     * @param url of the webpage to be downloaded
     */
    private void downloadPage(String pagename, String url) {
        try {
            System.out.println("Downloading " + url);
            String filePath = new File("").getAbsolutePath() + "/" + dataFolderPath + "/" + pagename + ".htm"; //location to save webpage
            UserAgent ua = new UserAgent();
            ua.visit(url); // go to url
            ua.doc.saveCompleteWebPage(new File(filePath)); // save webpage locally
        } catch (JauntException j) {
            System.err.println(j);
        } catch (IOException ex) {
            Logger.getLogger(DownloadWebpages.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * use the html template to create an index page, linking to all of the downloaded webpages
     */
    private void createIndexPage() {
        try {
            System.out.println("Creating Index Page");
            File htmlTemplateFile = new File(htmlTemplatePath); // template for index page
            String htmlString;
            htmlString = FileUtils.readFileToString(htmlTemplateFile);
            String title = indexPageTitle;
            String body = "<div>" + indexPageFirstLine + "</div><br/>";

            for (Pair wp : webpages) {
                String filePath = new File("").getAbsolutePath() + "/" + dataFolderPath + "/" + wp.getL() + ".htm"; // local URL of webpage
                String line = "<a href=\"" + filePath + "\">" + wp.getL() + "</a><br/>"; // add hyperlink to index page
                body += line;
            }

            htmlString = htmlString.replace("$title", title); // add title to html string
            htmlString = htmlString.replace("$body", body); // add body to html string
            File newHtmlFile = new File("index.html"); // create index.html
            FileUtils.writeStringToFile(newHtmlFile, htmlString); // write html string to file
            System.out.println("Complete, please check " + newHtmlFile.getAbsolutePath());
        } catch (IOException e) {
            Logger.getLogger(DownloadWebpages.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
