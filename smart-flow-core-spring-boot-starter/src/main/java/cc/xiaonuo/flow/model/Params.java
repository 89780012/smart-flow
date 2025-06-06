package cc.xiaonuo.flow.model;

import lombok.Data;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;
import java.util.List;

@Data
public class Params implements Serializable {
    // 新增查询参数和请求体参数分组
    private QueryParams queryParams;
    private BodyParams bodyParams;
    private JsonParams jsonParams;
}




