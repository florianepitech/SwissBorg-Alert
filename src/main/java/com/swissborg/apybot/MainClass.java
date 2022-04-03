package com.swissborg.apybot;

import com.swissborg.apybot.exception.HttpClientException;
import com.swissborg.apybot.exception.HttpServerException;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class MainClass {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, HttpClientException, HttpServerException {
        SwissBorgAPI bot = new SwissBorgAPI();
        HashMap<String, Float> result = bot.getAllAPY();
        for (String key : result.keySet()) {
            System.out.println(key + ": " + result.get(key));
        }
    }

}
