package com.chymeravr.rqhandler.iface;

import com.chymeravr.rqhandler.entities.response.AdResponse;
import com.chymeravr.rqhandler.entities.response.InternalAdResponse;

/**
 * Created by rubbal on 19/1/17.
 */
public interface ResponseSerializer {
    byte[] serialize(AdResponse response);
}
