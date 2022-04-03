package com.swissborg.apybot.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {

    public static String readFileAsString(String fileName) throws IOException {
        if (!Files.exists(Paths.get(fileName))) writeFileAsString(fileName, "{}");
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    public static void writeFileAsString(String fileName, String content) {
        try {
            Files.write(Paths.get(fileName), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
