package com.chymeravr.rqhandler.entities.v1.json;

import com.chymeravr.rqhandler.entities.iface.ResponseSerializer;
import com.chymeravr.rqhandler.entities.response.Response;
import com.google.gson.Gson;

/**
 * Created by rubbal on 19/1/17.
 */
public class V1ResponseSerializer implements ResponseSerializer {

    @Override
    public byte[] serialize(Response response) {
        return new Gson().toJson(response).getBytes();
    }
}
