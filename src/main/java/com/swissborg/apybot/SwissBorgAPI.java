package com.swissborg.apybot;

import com.swissborg.apybot.exception.HttpClientException;
import com.swissborg.apybot.exception.HttpServerException;
import com.swissborg.apybot.network.HttpClientSession;
import com.swissborg.apybot.objects.CryptoCurrency;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class SwissBorgAPI {

    private final HttpClientSession httpClientSession;

    public SwissBorgAPI() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this.httpClientSession = new HttpClientSession();
    }

    public HashMap<String, Float> getAllAPY() throws IOException, HttpClientException, HttpServerException {
        HashMap<String, Float> allAPY = new HashMap<String, Float>();
        JSONObject jsonObject = getSwissBorgApiResult("https://web-api-proxy.swissborg-stage.com/chsb-v2");
        for (Object key : jsonObject.keySet()) {
            String keyString = (String) key;
            if (keyString.contains("CurrentPremiumYieldPercentage")) {
                String symbol = keyString.replace("CurrentPremiumYieldPercentage", "");
                Float value = jsonObject.getFloat(keyString);
                allAPY.put(symbol.toUpperCase(), value);
            }
        }
        return allAPY;
    }

    public ArrayList<CryptoCurrency> getAllCryptoCurrencies() throws IOException, HttpClientException, HttpServerException {
        JSONObject jsonObject = getSwissBorgApiResult("https://web-api-proxy.swissborg-stage.com/tokens");
        ArrayList<CryptoCurrency> cryptoCurrencies = new ArrayList<CryptoCurrency>();

        //add crypto listed
        JSONObject crypto = jsonObject.getJSONObject("crypto");
        for (Object key : crypto.keySet()) {
            String keyString = (String) key;
            JSONObject cryptoCurrency = crypto.getJSONObject(keyString);
            cryptoCurrencies.add(new CryptoCurrency(keyString, cryptoCurrency.getFloat("market_cap"), cryptoCurrency.getFloat("price"), cryptoCurrency.getFloat("percent_change_24h"), false));
        }
        //add crypto not listed yet
        crypto = jsonObject.getJSONObject("listing");
        for (Object key : crypto.keySet()) {
            String keyString = (String) key;
            JSONObject cryptoCurrency = crypto.getJSONObject(keyString);
            cryptoCurrencies.add(new CryptoCurrency(keyString, cryptoCurrency.getFloat("market_cap"), cryptoCurrency.getFloat("price"), cryptoCurrency.getFloat("percent_change_24h"), true));
        }
        return cryptoCurrencies;
    }

    private JSONObject getSwissBorgApiResult(String url) throws IOException, HttpClientException, HttpServerException {
        HttpResponse httpResponse = httpClientSession.sendGet(url);
        String response = EntityUtils.toString(httpResponse.getEntity());
        return new JSONObject(response);
    }

}
