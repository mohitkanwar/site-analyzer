/**
 * Created by mohit on 10/10/16.
 */
package com.mohitkanwar.siteanalyzer.analyzer.siteprocessor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting to check files");
        String baseUrl="http://mohitkanwar.com";
        String sourceParentFolder = "/home/mohit/projects/personal/blog/blog-raw-contents";
        Path sourceBasePath = new File(sourceParentFolder).toPath();
        List<String> wordsContaingOtherCharacters = new ArrayList<String>();
        List<String> invalidWords = new ArrayList<String>();
        List<String> validWords = new ArrayList<String>();
        InputStream in = Main.class.getClassLoader()
                .getResourceAsStream("english-dictionary.txt");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            if (in != null) {
                String word;
                while (( word= reader.readLine()) != null) {
                   validWords.add(word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { in.close(); } catch (Throwable ignore) {}
        }
        in = Main.class.getClassLoader()
                .getResourceAsStream("technologies.txt");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            if (in != null) {
                String word;
                while (( word= reader.readLine()) != null) {
                    validWords.add(word.toLowerCase());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { in.close(); } catch (Throwable ignore) {}
        }

        try {
            Predicate<? super Path> validFilesOnly = new Predicate<Path>() {
                @Override
                public boolean test(Path path) {
                    return path.toString().toLowerCase().endsWith(".html")||
                            path.toString().toLowerCase().endsWith(".meta")||
                    path.toString().toLowerCase().endsWith(".md");

                }
            };
            Predicate<? super Path> noOutputFolder = new Predicate<Path>() {
                @Override
                public boolean test(Path path) {
                    return !path.toString().toLowerCase().contains("/output/");
                }
            };
            Files.walk(sourceBasePath).filter(validFilesOnly).filter(noOutputFolder).forEach(path ->{


                try {
                    Files.readAllLines(path).forEach(line->{

                       String[] words = line.split(" ");
                        for(int i=0;i<words.length;i++){
                            String word = words[i];
                            String regex = "[a-zA-Z]+";

                            if(word.matches(regex)){

                                if(!validWords.contains(word.toLowerCase())){
                                    invalidWords.add(word);
                                    System.out.println(path+"--->"+word);
                                }
                            }
                            else{
                                wordsContaingOtherCharacters.add(word);
                            }
                        }

                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
