package cc.xiaonuo.common.sqlhandler.tag;


import cc.xiaonuo.common.sqlhandler.node.MixedSqlNode;
import cc.xiaonuo.common.sqlhandler.node.SetSqlNode;
import cc.xiaonuo.common.sqlhandler.node.SqlNode;
import org.dom4j.Element;

import java.util.List;


public class SetHandler implements TagHandler{

    @Override
    public void handle(Element element, List<SqlNode> targetContents) {
        List<SqlNode> contents = XmlParser.parseElement(element);

        SetSqlNode node = new SetSqlNode(new MixedSqlNode(contents));
        targetContents.add(node);
    }
}
