package org.sparkliang.textutil.api;


/**
 * This interface is the abstraction on how we find the files that need to be transformed and how we place the files that after transformed. <br>
 *
 * @author spark
 * @date 2020-04-07
 * @since 1.0
 */
public interface TransformPipe extends Configurable {

    /**
     * The method to control how actually search the files that need to be transform and place the files after being transformed.
     *
     * @param sourcePath  The base source of the files that before transform.
     * @param targetPath  The base target path that we will place the output files.
     * @param transformer The instance of {@link Transformer}. We will use this instance to perform transformation at runtime.
     * @since 1.0
     */
    void transform(String sourcePath, String targetPath, Transformer transformer);
}
