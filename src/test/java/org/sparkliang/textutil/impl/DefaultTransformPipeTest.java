package org.sparkliang.textutil.impl;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sparkliang.textutil.api.TransformPipe;
import org.sparkliang.textutil.api.Transformer;
import org.sparkliang.textutil.test.util.TestCasesWithExternalData;
import org.xmlunit.assertj.XmlAssert;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.sparkliang.textutil.test.util.XMLTestUtil.getTextContentMatcher;

public class DefaultTransformPipeTest extends TestCasesWithExternalData {

    @ClassRule
    public final static TemporaryFolder TEMPORARY_FOLDER = new TemporaryFolder();


    @Before
    public void setUp() throws Exception {
        TEMPORARY_FOLDER.create();
    }


    @Test
    public void canTransformSingleFile() {
        // given
        // create TextUtil
        Properties conf = new Properties();
        String paramName = "TEST_PARAM", paramValue = "VALUE";
        conf.setProperty(DefaultXMLParameterFileTransformer.PARAM_VAL_PROP_NAME_PREFIX + paramName, paramValue);
        Transformer transformer = new DefaultXMLParameterFileTransformer();
        transformer.set(conf);
        // create the ProcessPipe need to be tested
        Properties pipeConf = new Properties();
        TransformPipe pipToBeTested = new DefaultTransformPipe();
        pipToBeTested.set(conf);
        // method parameter
        String fileName = "param1.xml";
        String sourceFileName = getTestDataRootPath() + File.separator + fileName, targetDir = TEMPORARY_FOLDER.getRoot().getPath();

        // when
        pipToBeTested.transform(sourceFileName, targetDir, transformer);

        // then
        String targetFileName = targetDir + File.separator + fileName;
        File targetFile = new File(targetFileName);
        assertThat(targetFile).exists();

        XmlAssert.assertThat(targetFile)
                .nodesByXPath("/root/project/workflow/parameter[@name='" + paramName + "']")
                .are(getTextContentMatcher(paramValue));
    }

    @Test
    public void canTransformAllTheFilesInTheFolder() {
        // given
        // create TextUtil
        Properties conf = new Properties();
        String paramName = "TEST_PARAM", paramValue = "VALUE";
        conf.setProperty(DefaultXMLParameterFileTransformer.PARAM_VAL_PROP_NAME_PREFIX + paramName, paramValue);
        Transformer transformer = new DefaultXMLParameterFileTransformer();
        transformer.set(conf);
        // create the ProcessPipe need to be tested
        Properties pipeConf = new Properties();
        TransformPipe pipToBeTested = new DefaultTransformPipe();
        pipToBeTested.set(conf);
        // method parameter
        String sourceDir = getTestDataRootPath(), targetDir = TEMPORARY_FOLDER.getRoot().getPath();

        // when
        pipToBeTested.transform(sourceDir, targetDir, transformer);

        // then
        List<String> sourFileNames = Arrays.asList(new File(sourceDir).list());
        File targetDirectory = new File(targetDir);
        assertThat(targetDirectory).isDirectory();
        List<String> targetFileNames = Arrays.asList(targetDirectory.list());
        assertThat(targetFileNames).containsAll(sourFileNames);

        sourFileNames.stream().forEach(sourFileName -> {
            XmlAssert.assertThat(new File(targetDirectory, sourFileName))
                    .nodesByXPath("/root/project/workflow/parameter[@name='" + paramName + "']")
                    .are(getTextContentMatcher(paramValue));
        });
    }


    @Test
    public void canTransformAllTheFilesInTheFolderRecursively() throws Exception {
        // given
        // create TextUtil
        Properties conf = new Properties();
        String paramName = "TEST_PARAM", paramValue = "VALUE";
        conf.setProperty(DefaultXMLParameterFileTransformer.PARAM_VAL_PROP_NAME_PREFIX + paramName, paramValue);
        Transformer transformer = new DefaultXMLParameterFileTransformer();
        transformer.set(conf);
        // create the ProcessPipe need to be tested
        Properties pipeConf = new Properties();
        TransformPipe pipToBeTested = new DefaultTransformPipe();
        pipToBeTested.set(conf);
        // method parameter
        String sourceDir = getTestDataRootPath(), targetDir = TEMPORARY_FOLDER.getRoot().getPath();

        // when
        pipToBeTested.transform(sourceDir, targetDir, transformer);

        // then
        File sourceDirectory = new File(sourceDir);
        Collection<File> sourceFiles = FileUtils.listFiles(sourceDirectory, null, true);
        Collection<String> sourceFileRelativePaths = sourceFiles.stream()
                .map(file -> file.getAbsolutePath().replace(sourceDirectory.getAbsolutePath(), ""))
                .collect(toList());
        File targetDirectory = new File(targetDir);
        Collection<String> targetFileRelativePaths = FileUtils.listFiles(targetDirectory, null, true).stream()
                .map(file -> file.getAbsolutePath().replace(targetDirectory.getAbsolutePath(), ""))
                .collect(toList());
        assertThat(targetDirectory).isDirectory();
        assertThat(targetFileRelativePaths).containsAll(sourceFileRelativePaths);

        sourceFileRelativePaths.stream().forEach(sourFileName -> {
            XmlAssert.assertThat(new File(targetDirectory, sourFileName))
                    .nodesByXPath("/root/project/workflow/parameter[@name='" + paramName + "']")
                    .are(getTextContentMatcher(paramValue));
        });
    }

    @Test
    public void canOnlyTransformTheFilesWithRightExtensions() throws Exception {
        // given
        // create TextUtil
        Properties conf = new Properties();
        String paramName = "TEST_PARAM", paramValue = "VALUE";
        conf.setProperty(DefaultXMLParameterFileTransformer.PARAM_VAL_PROP_NAME_PREFIX + paramName, paramValue);
        Transformer transformer = new DefaultXMLParameterFileTransformer();
        transformer.set(conf);
        // create the ProcessPipe need to be tested
        Properties pipeConf = new Properties();
        pipeConf.setProperty(
                DefaultTransformPipe.EXTENSIONS_CONF_NAME
                , String.join(DefaultTransformPipe.EXTENSIONS_SEPARATOR, Arrays.asList("xml", "param"))
        );
        TransformPipe pipToBeTested = new DefaultTransformPipe();
        pipToBeTested.set(pipeConf);
        // method parameter
        String sourceDir = getTestDataRootPath(), targetDir = TEMPORARY_FOLDER.getRoot().getPath();

        // when
        pipToBeTested.transform(sourceDir, targetDir, transformer);

        // then
        File sourceDirectory = new File(sourceDir);
        Collection<File> sourceFilesIncluded = FileUtils.listFiles(sourceDirectory, new String[]{"xml", "param"}, true);
        Collection<String> sourceFileRelativePathsIncluded = sourceFilesIncluded.stream()
                .map(file -> file.getAbsolutePath().replace(sourceDirectory.getAbsolutePath(), ""))
                .collect(toList());
        Collection<File> sourceFilesExcluded = FileUtils.listFiles(sourceDirectory, new String[]{"txt"}, true);
        Collection<String> sourceFileRelativePathsExcluded = sourceFilesExcluded.stream()
                .map(file -> file.getAbsolutePath().replace(sourceDirectory.getAbsolutePath(), ""))
                .collect(toList());
        File targetDirectory = new File(targetDir);
        Collection<String> targetFileRelativePaths = FileUtils.listFiles(targetDirectory, null, true).stream()
                .map(file -> file.getAbsolutePath().replace(targetDirectory.getAbsolutePath(), ""))
                .collect(toList());
        assertThat(targetDirectory).isDirectory();
        assertThat(targetFileRelativePaths).containsAll(sourceFileRelativePathsIncluded);
        assertThat(targetFileRelativePaths).doesNotContain(sourceFileRelativePathsExcluded.toArray(new String[0]));

        sourceFileRelativePathsIncluded.stream().forEach(sourFileName -> {
            XmlAssert.assertThat(new File(targetDirectory, sourFileName))
                    .nodesByXPath("/root/project/workflow/parameter[@name='" + paramName + "']")
                    .are(getTextContentMatcher(paramValue));
        });
    }
}
