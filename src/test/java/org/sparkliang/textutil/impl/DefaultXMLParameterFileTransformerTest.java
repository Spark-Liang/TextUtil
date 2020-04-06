package org.sparkliang.textutil.impl;

import org.junit.Test;
import org.sparkliang.textutil.api.Transformer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import java.util.Properties;

import static org.sparkliang.textutil.test.util.XMLTestUtil.getTextContentMatcher;
import static org.xmlunit.assertj.XmlAssert.assertThat;


public class DefaultXMLParameterFileTransformerTest {

    private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private XPathFactory xpf = XPathFactory.newInstance();


    /**
     * Source xml:
     * <pre>{@code
     *      <?xml version="1.0" encoding="UTF-8"?>
     *      <root version="2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema" xmlns="http://www.informatica.com/Parameterization/1.0">
     *         <project name="Orders">
     *            <workflow name="Customer_Workflow">
     *               <parameter name="TEST_PARAM">100</parameter>
     *              <parameter name="TEST_PARAM_OTHER">200</parameter>
     *            </workflow>
     *         </project>
     *      </root>
     * }</pre>
     * With this text util, the above xml will be changed to following xml.
     * <pre>{@code
     *       <?xml version="1.0" encoding="UTF-8"?>
     *       <root version="2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema" xmlns="http://www.informatica.com/Parameterization/1.0">
     *          <project name="Orders">
     *             <workflow name="Customer_Workflow">
     *                <parameter name="TEST_PARAM">VALUE</parameter>
     *               <parameter name="TEST_PARAM_OTHER">200</parameter>
     *             </workflow>
     *          </project>
     *       </root>
     *  }</pre>
     */
    @Test
    public void canApplyTheValueIntoCorrectParameter() throws Exception {
        //given
        String xmlBeforeApply = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<root version=\"2.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://www.informatica.com/Parameterization/1.0\">\n" +
                "   <project name=\"Orders\">\n" +
                "      <workflow name=\"Customer_Workflow\">\n" +
                "         <parameter name=\"TEST_PARAM\">100</parameter>\n" +
                "         <parameter name=\"TEST_PARAM_OTHER\">200</parameter>\n" +
                "      </workflow>\n" +
                "   </project>\n" +
                "</root>";

        String testParamName = "TEST_PARAM", testParamVale = "VALUE";
        Properties conf = new Properties() {{
            setProperty(DefaultXMLParameterFileTransformer.PARAM_VAL_PROP_NAME_PREFIX + testParamName, testParamVale);
        }};
        Transformer utilToBeTested = new DefaultXMLParameterFileTransformer();
        utilToBeTested.set(conf);

        //when
        String[] result = utilToBeTested.apply(xmlBeforeApply.split(System.lineSeparator()));

        //then
        String resultXMLStr = String.join(System.lineSeparator(), result);

        assertThat(resultXMLStr)
                .nodesByXPath("/root/project/workflow/parameter[@name='" + testParamName + "']")
                .are(getTextContentMatcher(testParamVale));
        assertThat(resultXMLStr)
                .nodesByXPath("/root/project/workflow/parameter[@name='TEST_PARAM_OTHER']")
                .are(getTextContentMatcher("200"));

    }


    /**
     * Source xml:
     * <pre>{@code
     *       <?xml version="1.0" encoding="UTF-8"?>
     *       <root version="2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema" xmlns="http://www.informatica.com/Parameterization/1.0">
     *          <project name="Orders">
     *             <workflow name="Customer_Workflow">
     *                <parameter name="TEST_PARAM">100</parameter>
     *               <parameter name="TEST_PARAM_OTHER">200</parameter>
     *             </workflow>
     *             <workflow name="Customer_Workflow">
     *                 <parameter name="TEST_PARAM">300</parameter>
     *                 <parameter name="TEST_PARAM_OTHER_2">400</parameter>
     *              </workflow>
     *          </project>
     *       </root>
     *  }</pre>
     * With this text util, the above xml will be changed to following xml.
     * <pre>{@code
     *       <?xml version="1.0" encoding="UTF-8"?>
     *       <root version="2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema" xmlns="http://www.informatica.com/Parameterization/1.0">
     *          <project name="Orders">
     *             <workflow name="Customer_Workflow">
     *                <parameter name="TEST_PARAM">VALUE</parameter>
     *               <parameter name="TEST_PARAM_OTHER">200</parameter>
     *             </workflow>
     *             <workflow name="Customer_Workflow">
     *                 <parameter name="TEST_PARAM">VALUE</parameter>
     *                 <parameter name="TEST_PARAM_OTHER_2">400</parameter>
     *              </workflow>
     *          </project>
     *       </root>
     *  }</pre>
     */
    @Test
    public void canApplyOnMultipleValues() throws Exception {
        //given
        String xmlBeforeApply = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<root version=\"2.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://www.informatica.com/Parameterization/1.0\">\n" +
                "   <project name=\"Orders\">\n" +
                "      <workflow name=\"Customer_Workflow\">\n" +
                "         <parameter name=\"TEST_PARAM\">100</parameter>\n" +
                "         <parameter name=\"TEST_PARAM_OTHER\">200</parameter>\n" +
                "      </workflow>\n" +
                "      <workflow name=\"Customer_Workflow_2\">\n" +
                "         <parameter name=\"TEST_PARAM\">100</parameter>\n" +
                "         <parameter name=\"TEST_PARAM_OTHER_2\">400</parameter>\n" +
                "      </workflow>\n" +
                "   </project>\n" +
                "</root>";

        String testParamName = "TEST_PARAM", testParamVale = "VALUE";
        Properties conf = new Properties() {{
            setProperty(DefaultXMLParameterFileTransformer.PARAM_VAL_PROP_NAME_PREFIX + testParamName, testParamVale);
        }};
        Transformer utilToBeTested = new DefaultXMLParameterFileTransformer();
        utilToBeTested.set(conf);

        //when
        String[] result = utilToBeTested.apply(xmlBeforeApply.split(System.lineSeparator()));

        //then
        String resultXMLStr = String.join(System.lineSeparator(), result);

        assertThat(resultXMLStr)
                .nodesByXPath("/root/project/workflow/parameter[@name='" + testParamName + "']")
                .are(getTextContentMatcher(testParamVale));
        assertThat(resultXMLStr)
                .nodesByXPath("/root/project/workflow/parameter[@name='TEST_PARAM_OTHER']")
                .are(getTextContentMatcher("200"));
        assertThat(resultXMLStr)
                .nodesByXPath("/root/project/workflow/parameter[@name='TEST_PARAM_OTHER_2']")
                .are(getTextContentMatcher("400"));

    }


}