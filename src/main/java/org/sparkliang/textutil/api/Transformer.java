package org.sparkliang.textutil.api;

import java.io.InputStream;
import java.io.OutputStream;

public interface Transformer extends Configurable {

    String[] apply(String[] input);

    void apply(InputStream input, OutputStream output);
}
