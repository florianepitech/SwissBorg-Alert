package com.swissborg.apybot;

import com.swissborg.apybot.exception.HttpClientException;
import com.swissborg.apybot.exception.HttpServerException;
import com.swissborg.apybot.network.HttpClientSession;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class SwissBorgAPI {

    private static HttpClientSession httpClientSession;
    private static final String API_URL = "https://web-api-proxy.swissborg-stage.com/chsb-v2";

    public SwissBorgAPI() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this.httpClientSession = new HttpClientSession();
    }

    public HashMap<String, Float> getAllAPY() throws IOException, HttpClientException, HttpServerException {
        HashMap<String, Float> allAPY = new HashMap<String, Float>();
        JSONObject jsonObject = getSwissBorgApiResult();
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

    private JSONObject getSwissBorgApiResult() throws IOException, HttpClientException, HttpServerException {
        HttpResponse httpResponse = httpClientSession.sendGet(API_URL);
        String response = EntityUtils.toString(httpResponse.getEntity());
        return new JSONObject(response);
    }

}
