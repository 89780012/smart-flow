package cc.xiaonuo.flow.parser.orange.engine;

import cc.xiaonuo.common.cache.PluginCache;
import cc.xiaonuo.common.sqlhandler.SqlMeta;
import cc.xiaonuo.common.sqlhandler.context.Context;
import cc.xiaonuo.common.sqlhandler.node.SqlNode;
import cc.xiaonuo.common.sqlhandler.tag.XmlParser;
import cc.xiaonuo.common.sqlhandler.token.TokenHandler;
import cc.xiaonuo.common.sqlhandler.token.TokenParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DynamicSqlEngine {

    public SqlMeta parse(String text, Map<String, Object> params) {
        text = String.format("<root>%s</root>", text);
        SqlNode sqlNode = parseXml2SqlNode(text);
        Context context = new Context(params);
        parseSqlText(sqlNode, context);
        parseParameter(context);
        SqlMeta sqlMeta = new SqlMeta(context.getSql(), context.getJdbcParameters());
        return sqlMeta;
    }

    public Set<String> parseParameter(String text) {
        text = String.format("<root>%s</root>", text);
        SqlNode sqlNode = parseXml2SqlNode(text);
        HashSet<String> set = new HashSet<>();
        sqlNode.applyParameter(set);
        return set;
    }

    private SqlNode parseXml2SqlNode(String text) {
        SqlNode node = PluginCache.getNodeCache().get(text);
        if (node == null) {
            node = XmlParser.parseXml2SqlNode(text);
            PluginCache.getNodeCache().put(text, node);
        }
        return node;
    }

    /**
     * 解析标签，去除标签，替换 ${}为常量值, #{}保留不变
     *
     * @param sqlNode
     * @param context
     */
    private void parseSqlText(SqlNode sqlNode, Context context) {
        sqlNode.apply(context);
    }

    /**
     * #{}替换成?，并且将?对应的参数值按顺序保存起来
     *
     * @param context
     */
    private void parseParameter(Context context) {
        TokenParser tokenParser = new TokenParser("#{", "}", new TokenHandler() {
            @Override
            public String handleToken(String content) {
                Object value = context.getOgnlValue(content);
                if (value == null) {
                    throw new RuntimeException("could not found value : " + content);
                }
                context.addParameter(value);
                return "?";
            }
        });
        String sql = tokenParser.parse(context.getSql());
        context.setSql(sql);
    }

    public static void main(String[] args) {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("<root>select <if test='minId != null'>id > ${minId} #{minId} <if test='maxId != null'> and id &lt; ${maxId} #{maxId}</if> </if></root>");
        Map<String, Object> map = new HashMap<>();
        map.put("minId", 100);
        map.put("maxId", 500);
        engine.parse(sql, map);
    }
}
