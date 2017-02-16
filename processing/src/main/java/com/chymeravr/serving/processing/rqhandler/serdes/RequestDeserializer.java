package com.chymeravr.serving.processing.rqhandler.serdes;


import com.chymeravr.schemas.serving.ServingRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by rubbal on 19/1/17.
 */
public interface RequestDeserializer {
    ServingRequest deserializeRequest(HttpServletRequest request) throws IOException;
}
