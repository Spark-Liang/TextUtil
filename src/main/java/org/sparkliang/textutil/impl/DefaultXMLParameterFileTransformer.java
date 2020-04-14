package org.sparkliang.textutil.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparkliang.textutil.api.AbstractStreamBasedTransformer;
import org.sparkliang.textutil.exception.TextTransformUtilException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The default implementation on processing the informatica BDM parameter files.<br>
 * Configurations:
 * <p>
 *     <ul>
 *         <li>transformer.parameter.*: All the property that start with "transformer.parameter." means this property is to config the value that will apply on the parameter with the given name.
 *         For example, "transformer.parameter.PARAM_1=VALUE_2" means the value "VALUE_2" will be set to parameter named "PARAM_1".
 *         </li>
 *     </ul>
 * </p>
 *
 * @author spark
 * @date 2020-04-07
 * @since 1.0
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class DefaultXMLParameterFileTransformer extends AbstractStreamBasedTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultXMLParameterFileTransformer.class);


    public final static String PARAMETER_TAG_NAME = "parameter";
    public final static String PARAM_NAME_ATTR_NAME = "name";

    public final static String PARAM_VAL_PROP_NAME_PREFIX = "transformer.parameter.";

    private Map<String, String> parameterNameValueMap = new ConcurrentHashMap<>();
    private DocumentBuilderFactory bdf = DocumentBuilderFactory.newInstance();
    private TransformerFactory tf = TransformerFactory.newInstance();


    @Override
    public void apply(InputStream input, OutputStream output) {
        try {
            DocumentBuilder docBuilder = bdf.newDocumentBuilder();
            Document doc = docBuilder.parse(input);

            for (String paramName : parameterNameValueMap.keySet()) {
                String paramValue = parameterNameValueMap.get(paramName);
                NodeList nodeList = doc.getElementsByTagName(PARAMETER_TAG_NAME);
                for (int i = 0, maxI = nodeList.getLength(); i < maxI; i++) {
                    Node node = nodeList.item(i);
                    Node nameAttributeNode = node.getAttributes().getNamedItem(PARAM_NAME_ATTR_NAME);
                    if (null != nameAttributeNode && paramName.equals(nameAttributeNode.getNodeValue())) {
                        node.setTextContent(paramValue);
                    }
                }
            }
            DOMSource domSource = new DOMSource(doc);
            StreamResult result = new StreamResult(output);
            javax.xml.transform.Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(domSource, result);


        } catch (Exception e) {
            throw new TextTransformUtilException(e);
        }
    }

    @Override
    public void set(Properties conf) {
        conf.stringPropertyNames().stream()
                .filter(name -> name.startsWith(PARAM_VAL_PROP_NAME_PREFIX))
                .forEach(name -> {
                            String parameterName = name.replace(PARAM_VAL_PROP_NAME_PREFIX, ""), paramemterValue = conf.getProperty(name);
                            LOGGER.debug("will apply value \"{}\" on parameter \"{}\".", paramemterValue, parameterName);
                            parameterNameValueMap.put(parameterName, paramemterValue);
                        }
                );
    }


}
