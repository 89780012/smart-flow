package cc.xiaonuo.common.sqlhandler.tag;

import cc.xiaonuo.common.sqlhandler.node.IfSqlNode;
import cc.xiaonuo.common.sqlhandler.node.MixedSqlNode;
import cc.xiaonuo.common.sqlhandler.node.SqlNode;
import org.dom4j.Element;

import java.util.List;


public class IfHandler implements TagHandler {

    @Override
    public void handle(Element element, List<SqlNode> targetContents) {
        String test = element.attributeValue("test");
        if (test == null) {
            throw new RuntimeException("<if> tag missing test attribute");
        }

        List<SqlNode> contents = XmlParser.parseElement(element);

        IfSqlNode ifSqlNode = new IfSqlNode(test, new MixedSqlNode(contents));
        targetContents.add(ifSqlNode);

    }
}
