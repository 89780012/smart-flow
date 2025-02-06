package cc.xiaonuo.flow.parser;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class BizFileParser {

    public List<ApiInfo> parseBizFile(File file) {
        List<ApiInfo> apiInfoList = new ArrayList<>();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList bizNodes = doc.getElementsByTagName("biz");

            for (int i = 0; i < bizNodes.getLength(); i++) {
                Element interfaceElement = (Element) bizNodes.item(i);
                String name = interfaceElement.getAttribute("name");
                String method = interfaceElement.getAttribute("method");
                String protocol = interfaceElement.getAttribute("protocol");
                String url = getElementContent(interfaceElement, "url");

                ApiInfo apiInfo = new ApiInfo(name, method, protocol, url);
                apiInfoList.add(apiInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return apiInfoList;
    }

    private String getElementContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }
}
