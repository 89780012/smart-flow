package cc.xiaonuo.common.sqlhandler.node;


import cc.xiaonuo.common.sqlhandler.context.Context;

import java.util.Set;


public interface SqlNode {

    void apply(Context context);

    void applyParameter(Set<String> set);

}
