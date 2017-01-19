package com.chymeravr.rqhandler.entities.v1.json;

import com.chymeravr.rqhandler.iface.RequestDeserializer;
import com.chymeravr.rqhandler.entities.request.Request;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by rubbal on 19/1/17.
 */
public class V1RequestDeserializer implements RequestDeserializer {

    @Override
    public Request deserializeRequest(HttpServletRequest request) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String data = buffer.toString();
        return new Gson().fromJson(data, Request.class);
    }
}
