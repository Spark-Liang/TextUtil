package org.sparkliang.textutil.app;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sparkliang.textutil.test.util.TestCasesWithExternalData;
import org.xmlunit.assertj.XmlAssert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sparkliang.textutil.test.util.XMLTestUtil.getTextContentMatcher;

public class CLITest extends TestCasesWithExternalData {

    @Rule
    public TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        TEMP_FOLDER.create();
    }

    @Test
    public void canPrintHelpString() {
        // given
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = new String[]{"-h"};

        // when
        CLI.main(args);

        // then
        assertThat(outContent.toString()).contains(CLI.HELP_STRING);
        outContent.toString();
    }

    @Test
    public void canTransformTheFileInFolderWithConfigFile() {
        // given
        String configFileName = "transform.properties";
        String source = getTestDataRootPath(), target = TEMP_FOLDER.getRoot().getPath();
        String[] command = new String[]{"--default-conf-dir", source + File.separator + configFileName, source, target};

        // when
        CLI.main(command);

        // then
        String paramName = "TEST_PARAM", paramValue = "CLI_TEST_VALUE";

        List<String> sourFileNames = FileUtils
                .listFiles(new File(source), new String[]{"xml"}, true)
                .stream().map(file -> file.getName()).collect(Collectors.toList());

        File targetDirectory = new File(target);
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
    public void canReadConfigurationFromCommandLine() {
        // given
        String configFileName = "transform.properties";
        String source = getTestDataRootPath(), target = TEMP_FOLDER.getRoot().getPath();
        String paramName = "TEST_PARAM", paramValue = "CLI_TEST_VALUE";
        String commandLineConf = "transformer.parameter." + paramName + "=" + paramValue;
        String[] command = new String[]{"--default-conf-dir", source + File.separator + configFileName, source, target};

        // when
        CLI.main(command);

        // then
        List<String> sourFileNames = FileUtils
                .listFiles(new File(source), new String[]{"xml"}, true)
                .stream().map(file -> file.getName()).collect(Collectors.toList());

        File targetDirectory = new File(target);
        assertThat(targetDirectory).isDirectory();
        List<String> targetFileNames = Arrays.asList(targetDirectory.list());
        assertThat(targetFileNames).containsAll(sourFileNames);

        sourFileNames.stream().forEach(sourFileName -> {
            XmlAssert.assertThat(new File(targetDirectory, sourFileName))
                    .nodesByXPath("/root/project/workflow/parameter[@name='" + paramName + "']")
                    .are(getTextContentMatcher(paramValue));
        });

    }
}
