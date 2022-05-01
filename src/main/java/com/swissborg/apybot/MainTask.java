package com.swissborg.apybot;

import com.swissborg.apybot.enums.AccountType;
import com.swissborg.apybot.exception.HttpClientException;
import com.swissborg.apybot.exception.HttpServerException;
import com.swissborg.apybot.objects.CryptoCurrency;
import com.swissborg.apybot.utils.FileUtils;
import fr.florian.telegramcannalapi.TelegramCannalAPI;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainTask implements Runnable {

    private TelegramCannalAPI telegramCannalAPI;
    private SwissBorgAPI swissBorgAPI;

    public MainTask(SwissBorgAPI api, TelegramCannalAPI telegramCannalAPI) {
        this.telegramCannalAPI = telegramCannalAPI;
        this.swissBorgAPI = api;
    }

    @Override
    public void run() {
        System.out.println(new Date().toInstant().toString() + " - Started new refresh task");
        checkYield();
        checkCryptoCurrency();
    }

    /*
     $      Crypto Currency
     */

    private void checkCryptoCurrency() {
        try {
            JSONObject coin = new JSONObject(FileUtils.readFileAsString("coin.json"));
            ArrayList<CryptoCurrency> cryptoCurrencies = swissBorgAPI.getAllCryptoCurrencies();
            for (CryptoCurrency cryptoCurrency : cryptoCurrencies) {
                if (!coin.has(cryptoCurrency.getName())) {
                    if (cryptoCurrency.isInListingList())
                        sendNewCoinInListingAlert(cryptoCurrency);
                    else
                        sendNewCoinAlert(cryptoCurrency);
                    coin.put(cryptoCurrency.getName(), !cryptoCurrency.isInListingList());
                    continue;
                }
                if (!cryptoCurrency.isInListingList() && !coin.getBoolean(cryptoCurrency.getName()))
                    sendNewCoinAlert(cryptoCurrency);
            }
            FileUtils.writeFileAsString("coin.json", coin.toString());
        } catch (IOException | HttpClientException | HttpServerException e) {
            e.printStackTrace();
        }
    }

    private void sendNewCoinInListingAlert(CryptoCurrency cryptoCurrency) {
        telegramCannalAPI.addMessageToQueue("⏳ New Crypto Currency in listing list: " + cryptoCurrency.getName());
        System.out.println(new Date().toInstant().toString() + " - New Crypto Currency in listing list: " + cryptoCurrency.getName());
    }

    private void sendNewCoinAlert(CryptoCurrency cryptoCurrency) {
        telegramCannalAPI.addMessageToQueue("🚀 New Crypto Currency listed: " + cryptoCurrency.getName());
        System.out.println(new Date().toInstant().toString() + " - New Crypto Currency listed: " + cryptoCurrency.getName());
    }

    /*
     $      YIELD
     */

    private void checkYield() {
        try {
            JSONObject lastApy = new JSONObject(FileUtils.readFileAsString("apy.json"));
            HashMap<String, Float> result = swissBorgAPI.getAllAPY();
            for (String key : result.keySet()) {
                if (!lastApy.has(key)) {
                    sendNewAPYAlert(key, result.get(key));
                    lastApy.put(key, result.get(key));
                    continue;
                }
                if (lastApy.getFloat(key) != result.get(key)) {
                    sendChangedAPYAlert(key, lastApy.getFloat(key), result.get(key));
                    lastApy.put(key, result.get(key));
                }
            }
            FileUtils.writeFileAsString("apy.json", lastApy.toString());
        } catch (IOException | HttpClientException | HttpServerException e) {
            e.printStackTrace();
        }
    }

    private void sendNewAPYAlert(String coin, float apy) {
        StringBuilder message = new StringBuilder("🆕 New APY for " + coin + "\n\n");
        for (AccountType accountType : AccountType.values())
            message.append(accountType.getSymbol())
                    .append(" ")
                    .append(accountType.getName())
                    .append(": ")
                    .append(String.format("%.2f", apy / 2 * accountType.getMultiplier()))
                    .append("%\n");
        telegramCannalAPI.addMessageToQueue(message.toString());
        System.out.println(new Date().toInstant().toString() + " - New APY for " + coin + ": " + apy + "%");
    }

    private void sendChangedAPYAlert(String coin, float lastApy, float newApy) {
        float standardLastApy = lastApy / 2;
        float standardNewApy = newApy / 2;
        if (lastApy > newApy) {
            StringBuilder message = new StringBuilder("↘️ APY for " + coin + " decreased:\n\n");
            for (AccountType accountType : AccountType.values()) {
                float accountLastApy = standardLastApy * accountType.getMultiplier();
                float accountNewApy = standardNewApy * accountType.getMultiplier();
                message.append(accountType.getSymbol()).append(" ")
                        .append(accountType.getName()).append(": ")
                        .append(String.format("%.2f", accountLastApy)).append(" to ")
                        .append(String.format("%.2f", accountNewApy)).append("% (")
                        .append(String.format("%.2f", accountNewApy - accountLastApy))
                        .append("%)\n");
            }
            telegramCannalAPI.addMessageToQueue(message.toString());
        } else {
            StringBuilder message = new StringBuilder("↗️ APY for " + coin + " increase:\n\n");
            for (AccountType accountType : AccountType.values()) {
                float accountLastApy = standardLastApy * accountType.getMultiplier();
                float accountNewApy = standardNewApy * accountType.getMultiplier();
                message.append(accountType.getSymbol()).append(" ")
                        .append(accountType.getName()).append(": ")
                        .append(String.format("%.2f", accountLastApy)).append(" to ")
                        .append(String.format("%.2f", accountNewApy)).append("% (+")
                        .append(String.format("%.2f", accountNewApy - accountLastApy))
                        .append(")\n");
            }
            telegramCannalAPI.addMessageToQueue(message.toString());
        }
        System.out.println(new Date().toInstant().toString() + " - APY for " + coin + " changed from " + lastApy + "% to " + newApy + "%");
    }

}
