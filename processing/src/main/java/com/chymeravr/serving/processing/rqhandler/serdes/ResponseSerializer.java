package com.chymeravr.serving.processing.rqhandler.serdes;


import com.chymeravr.schemas.serving.ServingResponse;

/**
 * Created by rubbal on 19/1/17.
 */
public interface ResponseSerializer {
    byte[] serialize(ServingResponse response);
}
