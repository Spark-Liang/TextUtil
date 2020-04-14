package org.sparkliang.textutil.api;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * This interface is the abstraction on transforming or processing the text content. <br>
 * You can design your own transformer to handle some specific logic, such as locate and replace some specific content in the given text file.
 *
 * @author spark
 * @date 2020-04-07
 * @since 1.0
 */
public interface Transformer extends Configurable {

    /**
     * The method for transforming the text content.
     *
     * @param input The text content before transform.
     * @return return the text content after transformed.
     * @since 1.0
     */
    String[] apply(String[] input);

    /**
     * The method that process the text content in a streaming approach.
     * Based on the streaming approach, we will be able to have the ability on processing the rich text content.
     *
     * @param input  The input stream of the source that need to be transform.
     * @param output The output stream of the content that after transformed.
     * @since 1.0
     */
    void apply(InputStream input, OutputStream output);
}
