package org.sparkliang.textutil.test.util;

import org.junit.Rule;
import org.junit.rules.TestName;

import java.io.File;

public class TestCasesWithExternalData {

    public static final String TEST_DATA_FOLDER_NAME = "testdata";

    @Rule
    public TestName testName = new TestName();

    protected String getTestDataRootPath() {
        Class thisClass = this.getClass();
        String className = thisClass.getSimpleName(), packageName = thisClass.getPackage().getName();
        String relativePath = String.format(
                "%s.%s.%s.%s"
                , packageName, TEST_DATA_FOLDER_NAME, className, testName.getMethodName()
        ).replace(".", File.separator);


        return thisClass.getClassLoader().getResource("").getPath() + relativePath;

    }
}
