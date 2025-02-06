package cc.xiaonuo.common.config;

import cc.xiaonuo.common.bean.SmartFlowConfig;
import cc.xiaonuo.common.cache.PluginCache;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class SmartFlowXmlParser {

    public static SmartFlowConfig parse(InputStream inputStream) {
        SmartFlowConfig config = new SmartFlowConfig();
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            Element root = document.getDocumentElement();

            // 解析数据源配置
            NodeList dataSources = root.getElementsByTagName("dataSource");
            for (int i = 0; i < dataSources.getLength(); i++) {
                Element ds = (Element) dataSources.item(i);
                String id = ds.getAttribute("id");
                String type = ds.getAttribute("type");
                Properties props = new Properties();
                log.info("读取文件中数据源配置,id:{},type:{}",id,type);
                PluginCache.dataSourceTypeMap.put(id, type);
                NodeList properties = ds.getElementsByTagName("property");
                for (int j = 0; j < properties.getLength(); j++) {
                    Element prop = (Element) properties.item(j);
                    String name = prop.getAttribute("name");
                    String value = prop.getTextContent();
                    props.setProperty(name, value);
                }
                
                config.getDataSources().put(id, props);
            }

            // 解析全局设置
            Element settings = (Element) root.getElementsByTagName("settings").item(0);
            if (settings != null) {
                NodeList settingProps = settings.getElementsByTagName("property");
                for (int i = 0; i < settingProps.getLength(); i++) {
                    Element prop = (Element) settingProps.item(i);
                    String name = prop.getAttribute("name");
                    String value = prop.getTextContent();
                    config.getSettings().setProperty(name, value);
                }
            }

            // 解析SFTP配置
            NodeList sftpServers = root.getElementsByTagName("server");
            for (int i = 0; i < sftpServers.getLength(); i++) {
                Element server = (Element) sftpServers.item(i);
                if (server.getParentNode().getNodeName().equals("sftp")) {
                    String id = server.getAttribute("id");
                    Properties props = new Properties();
                    
                    NodeList properties = server.getElementsByTagName("property");
                    for (int j = 0; j < properties.getLength(); j++) {
                        Element prop = (Element) properties.item(j);
                        String name = prop.getAttribute("name");
                        String value = prop.getTextContent();
                        props.setProperty(name, value);
                    }
                    
                    config.getSftp().put(id, props);
                }
            }

            // 解析Redis配置
            NodeList redisServers = root.getElementsByTagName("server");
            for (int i = 0; i < redisServers.getLength(); i++) {
                Element server = (Element) redisServers.item(i);
                if (server.getParentNode().getNodeName().equals("redis")) {
                    String id = server.getAttribute("id");
                    Properties props = new Properties();
                    
                    NodeList properties = server.getElementsByTagName("property");
                    for (int j = 0; j < properties.getLength(); j++) {
                        Element prop = (Element) properties.item(j);
                        String name = prop.getAttribute("name");
                        String value = prop.getTextContent();
                        props.setProperty(name, value);
                    }
                    
                    config.getRedis().put(id, props);
                }
            }

            // 解析邮件配置
            Element mail = (Element) root.getElementsByTagName("mail").item(0);
            if (mail != null) {
                NodeList mailProps = mail.getElementsByTagName("property");
                for (int i = 0; i < mailProps.getLength(); i++) {
                    Element prop = (Element) mailProps.item(i);
                    String name = prop.getAttribute("name");
                    String value = prop.getTextContent();
                    config.getMail().setProperty(name, value);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("解析配置文件失败: " + e.getMessage());
        }
        
        return config;
    }
}