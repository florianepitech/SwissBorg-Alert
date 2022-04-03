package com.swissborg.apybot.exception;

import org.apache.http.HttpResponse;

public class HttpClientException extends HttpException {

    public HttpClientException(HttpResponse httpResponse) {
        super(httpResponse);
    }
}
