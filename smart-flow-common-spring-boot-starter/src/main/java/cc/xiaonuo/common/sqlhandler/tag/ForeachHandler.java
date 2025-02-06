package cc.xiaonuo.common.sqlhandler.tag;

import cn.hutool.core.util.StrUtil;
import cc.xiaonuo.common.sqlhandler.node.ForeachSqlNode;
import cc.xiaonuo.common.sqlhandler.node.MixedSqlNode;
import cc.xiaonuo.common.sqlhandler.node.SqlNode;
import org.dom4j.Element;

import java.util.List;


public class ForeachHandler implements TagHandler {
    @Override
    public void handle(Element element, List<SqlNode> targetContents) {
        List<SqlNode> contents = XmlParser.parseElement(element);

        String open = element.attributeValue("open");
        String close = element.attributeValue("close");
        String collection = element.attributeValue("collection");
        String separator = element.attributeValue("separator");
        String item = element.attributeValue("item");
        String index = element.attributeValue("index");

        if (StrUtil.isBlank(collection)) {
            throw new RuntimeException("<foreach> attribute missing : collection");
        }
        if (StrUtil.isBlank(item)) {
            item = "item";
        }
        if (StrUtil.isBlank(index)) {
            index = "index";
        }

        ForeachSqlNode foreachSqlNode = new ForeachSqlNode(collection, open, close, separator, item, index, new MixedSqlNode(contents));
        targetContents.add(foreachSqlNode);

    }
}
