package org.sparkliang.textutil.api;

public interface TransformPipe extends Configurable {

    void transform(String sourcePath, String targetPath, Transformer transformer);
}
