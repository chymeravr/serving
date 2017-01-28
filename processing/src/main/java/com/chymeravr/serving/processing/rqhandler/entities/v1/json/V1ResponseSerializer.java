package com.chymeravr.serving.processing.rqhandler.entities.v1.json;

import com.chymeravr.serving.processing.rqhandler.entities.response.AdResponse;
import com.chymeravr.serving.processing.rqhandler.iface.ResponseSerializer;
import com.google.gson.Gson;

/**
 * Created by rubbal on 19/1/17.
 */
public class V1ResponseSerializer implements ResponseSerializer {

    @Override
    public byte[] serialize(AdResponse response) {
        return new Gson().toJson(response).getBytes();
    }
}
