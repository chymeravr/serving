package com.chymeravr.serving.rqhandler.iface;

import com.chymeravr.serving.rqhandler.entities.response.AdResponse;

/**
 * Created by rubbal on 19/1/17.
 */
public interface ResponseSerializer {
    byte[] serialize(AdResponse response);
}
