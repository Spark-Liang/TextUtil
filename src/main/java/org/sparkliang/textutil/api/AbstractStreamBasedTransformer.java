package org.sparkliang.textutil.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparkliang.textutil.exception.TextTransformUtilException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Provide the common implementation on method {@link Transformer#apply(String[])}. <br>
 * All the classes that extends this abstract class only need to implement the method {@link Transformer#apply(InputStream, OutputStream)}.
 *
 * @author spark
 * @date 2020-04-07
 * @since 1.0
 */
public abstract class AbstractStreamBasedTransformer implements Transformer {
    private static Logger LOGGER = LoggerFactory.getLogger(AbstractStreamBasedTransformer.class);

    @Override
    public String[] apply(String[] input) {
        try (InputStream in = new ByteArrayInputStream(
                String.join(System.lineSeparator(), Arrays.asList(input)).getBytes(StandardCharsets.UTF_8))
        ) {
            try (OutputStream out = new ByteArrayOutputStream()) {
                this.apply(in, out);
                return out.toString().split(System.lineSeparator());
            }
        } catch (IOException e) {
            LOGGER.warn("An error occur during transform String[] into input stream or output stream.", e);
            throw new TextTransformUtilException(e);
        }
    }
}
