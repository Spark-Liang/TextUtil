package org.sparkliang.textutil.test.util;

import org.assertj.core.api.Condition;
import org.w3c.dom.Node;

public class XMLTestUtil {


    public static Condition<Node> getTextContentMatcher(String textContent) {
        return new Condition<Node>() {
            @Override
            public boolean matches(Node value) {
                return value.getTextContent() == textContent;
            }
        };
    }
}
