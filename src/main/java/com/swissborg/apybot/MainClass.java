package com.swissborg.apybot;

import com.swissborg.apybot.exception.HttpClientException;
import com.swissborg.apybot.exception.HttpServerException;
import fr.florian.telegramcannalapi.TelegramCannalAPI;
import fr.florian.telegramcannalapi.object.TelegramMessage;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainClass {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, HttpClientException, HttpServerException {
        checkEnvironmentsVariable();
        SwissBorgAPI bot = new SwissBorgAPI();
        TelegramCannalAPI telegram = new TelegramCannalAPI(System.getenv("TELEGRAM_BOT_ID"), System.getenv("TELEGRAM_CANAL_ID"));
        telegram.start();
        startTask(bot, telegram);
    }

    private static void startTask(SwissBorgAPI bot, TelegramCannalAPI telegram) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new MainTask(bot, telegram), 0, 5, TimeUnit.MINUTES);
    }

    private static void checkEnvironmentsVariable() {
        if (System.getenv("TELEGRAM_BOT_ID") == null) {
            System.out.println("Please set the TELEGRAM_BOT_ID environment variable");
            System.exit(1);
        }
        if (System.getenv("TELEGRAM_CANAL_ID") == null) {
            System.out.println("Please set the TELEGRAM_CHAT_ID environment variable");
            System.exit(1);
        }
    }

}
