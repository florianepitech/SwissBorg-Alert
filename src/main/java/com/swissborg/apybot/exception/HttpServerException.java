package com.swissborg.apybot.exception;

import org.apache.http.HttpResponse;

public class HttpServerException extends HttpException {

    public HttpServerException(HttpResponse httpResponse) {
        super(httpResponse);
    }

}
