package cc.xiaonuo.common.sqlhandler.tag;

import cc.xiaonuo.common.sqlhandler.node.MixedSqlNode;
import cc.xiaonuo.common.sqlhandler.node.SqlNode;
import cc.xiaonuo.common.sqlhandler.node.WhereSqlNode;
import org.dom4j.Element;

import java.util.List;


public class WhereHandler implements TagHandler{

    @Override
    public void handle(Element element, List<SqlNode> targetContents) {
        List<SqlNode> contents = XmlParser.parseElement(element);

        WhereSqlNode node = new WhereSqlNode(new MixedSqlNode(contents));
        targetContents.add(node);
    }
}
