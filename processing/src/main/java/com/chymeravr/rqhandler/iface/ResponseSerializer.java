package com.chymeravr.rqhandler.iface;

import com.chymeravr.rqhandler.entities.response.Response;

/**
 * Created by rubbal on 19/1/17.
 */
public interface ResponseSerializer {
    byte[] serialize(Response response);
}
