package com.swissborg.apybot;

import com.swissborg.apybot.exception.HttpClientException;
import com.swissborg.apybot.exception.HttpServerException;
import com.swissborg.apybot.utils.FileUtils;
import fr.florian.telegramcannalapi.TelegramCannalAPI;
import org.json.JSONObject;

import java.io.IOException;
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
        try {
            System.out.println(new Date().toInstant().toString() + " - Started new refresh task");
            JSONObject lastApy = new JSONObject(FileUtils.readFileAsString(MainClass.fileName));
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
            FileUtils.writeFileAsString(MainClass.fileName, lastApy.toString());
        } catch (IOException | HttpClientException | HttpServerException e) {
            e.printStackTrace();
        }
    }

    private void sendNewAPYAlert(String coin, float apy) {
        telegramCannalAPI.addMessageToQueue("🆕 New APY for " + coin + ": " + apy + "%");
        System.out.println(new Date().toInstant().toString() + " - New APY for " + coin + ": " + apy + "%");
    }

    private void sendChangedAPYAlert(String coin, float lastApy, float newApy) {
        if (lastApy > newApy) {
            telegramCannalAPI.addMessageToQueue("📉 APY for " + coin + " decreased from " + lastApy + "% to " + newApy + "% (" + String.format("%.2f", newApy - lastApy) + "%)");
        } else {
            telegramCannalAPI.addMessageToQueue("📈 APY for " + coin + " increased from " + lastApy + "% to " + newApy + "% (" + String.format("%.2f", newApy - lastApy) + "%)");
        }
        System.out.println(new Date().toInstant().toString() + " - APY for " + coin + " changed from " + lastApy + "% to " + newApy + "%");
    }

}
