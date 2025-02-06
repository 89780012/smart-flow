package cc.xiaonuo.common.sqlhandler.tag;

import cc.xiaonuo.common.sqlhandler.node.SqlNode;
import org.dom4j.Element;

import java.util.List;

public interface TagHandler {

    void handle(Element element, List<SqlNode> contents);
}
