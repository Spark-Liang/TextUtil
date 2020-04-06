package org.sparkliang.textutil.impl;

import org.sparkliang.textutil.api.Transformer;
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
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultXMLParameterFileTransformer implements Transformer {

    public final static String PARAMETER_TAG_NAME = "parameter";
    public final static String PARAM_NAME_ATTR_NAME = "name";

    public final static String PARAM_VAL_PROP_NAME_PREFIX = "transformer.parameter.";

    private Map<String, String> parameterNameValueMap = new ConcurrentHashMap<>();
    private DocumentBuilderFactory bdf = DocumentBuilderFactory.newInstance();
    private TransformerFactory tf = TransformerFactory.newInstance();

    @Override
    public String[] apply(String[] input) {

        try (InputStream in = new ByteArrayInputStream(
                String.join(System.lineSeparator(), Arrays.asList(input)).getBytes(StandardCharsets.UTF_8))
        ) {
            try (OutputStream out = new ByteArrayOutputStream()) {
                this.apply(in, out);
                return out.toString().split(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new TextTransformUtilException(e);
        }
    }

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
                    if (null != nameAttributeNode && paramName.equals(nameAttributeNode.getNodeValue()))
                        node.setTextContent(paramValue);
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
                .forEach(name -> parameterNameValueMap.put(name.replace(PARAM_VAL_PROP_NAME_PREFIX, ""), conf.getProperty(name)));
    }


}
