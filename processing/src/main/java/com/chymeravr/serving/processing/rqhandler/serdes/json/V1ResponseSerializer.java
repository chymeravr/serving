package com.chymeravr.serving.processing.rqhandler.serdes.json;

import com.chymeravr.schemas.serving.ServingResponse;
import com.chymeravr.serving.processing.rqhandler.serdes.ResponseSerializer;
import com.google.gson.Gson;

/**
 * Created by rubbal on 19/1/17.
 */
public class V1ResponseSerializer implements ResponseSerializer {

    @Override
    public byte[] serialize(ServingResponse response) {
        return new Gson().toJson(response).getBytes();
    }
}