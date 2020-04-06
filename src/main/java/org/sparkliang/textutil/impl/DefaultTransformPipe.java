package org.sparkliang.textutil.impl;

import org.apache.commons.io.FileUtils;
import org.sparkliang.textutil.api.TransformPipe;
import org.sparkliang.textutil.api.Transformer;
import org.sparkliang.textutil.exception.TextTransformUtilException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Properties;

public class DefaultTransformPipe implements TransformPipe {

    public static final String EXTENSIONS_CONF_NAME = "pipe.conf.extensions";
    public static final String EXTENSIONS_SEPARATOR = ",";

    private String[] fileExtensions = null;

    @Override
    public void transform(String sourcePath, String targetPath, Transformer transformer) {

        File source = new File(sourcePath), target = new File(targetPath);
        if (!source.exists()) {
            throw new TextTransformUtilException(String.format("The given source path \"%s\"is not exists!", sourcePath));
        }

        if (source.isFile()) {
            try (InputStream in = FileUtils.openInputStream(source)) {
                File targetFile;
                if (target.isFile()) {
                    targetFile = target;
                } else {
                    if (!target.exists() && !target.mkdirs()) {
                        throw new TextTransformUtilException(String.format("The target path \"\" is not exists and we unable to create this path.", targetPath));
                    }
                    targetFile = new File(target, source.getName());
                }
                try (OutputStream out = FileUtils.openOutputStream(targetFile)) {
                    transformer.apply(in, out);
                }
            } catch (IOException e) {
                throw new TextTransformUtilException(e);
            }
        } else {
            String sourceAbsolutePath = source.getAbsolutePath();
            if (!target.exists()) {
                if (!target.mkdirs()) {
                    throw new TextTransformUtilException(String.format("The target path \"\" is not exists and we unable to create this path.", targetPath));
                }
            }

            Collection<File> sourceFilePaths = FileUtils.listFiles(source, fileExtensions, true);

            try {
                for (File sourceFile : sourceFilePaths) {
                    String pathRelativeToSource = sourceFile.getAbsolutePath().substring(sourceAbsolutePath.length() + 1);
                    File targetFile = new File(target, pathRelativeToSource);
                    try (InputStream in = FileUtils.openInputStream(sourceFile)) {
                        try (OutputStream out = FileUtils.openOutputStream(targetFile)) {
                            transformer.apply(in, out);
                        }
                    }
                }
            } catch (IOException e) {
                throw new TextTransformUtilException(e);
            }
        }
    }


    @Override
    public synchronized void set(Properties conf) {
        if (conf.containsKey(EXTENSIONS_CONF_NAME)) {
            String extensionsConf = conf.getProperty(EXTENSIONS_CONF_NAME).trim();
            this.fileExtensions = extensionsConf.split(EXTENSIONS_SEPARATOR);
        }

    }
}
