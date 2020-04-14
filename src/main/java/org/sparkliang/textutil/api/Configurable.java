package org.sparkliang.textutil.api;

import java.util.Properties;

/**
 * The classes which implement this interface means it can be set by the given properties.
 *
 * @author spark
 * @date 2020-04-07
 * @since 1.0
 */
public interface Configurable {

    /**
     * This method is to config the object based on the given properties.
     *
     * @param conf
     */
    void set(Properties conf);
}
