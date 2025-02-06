package cc.xiaonuo.flow.parser;

import lombok.Data;

@Data
public class ApiInfo {
    private String name;
    private String method;
    private String protocol;
    private String url;

    public ApiInfo(String name, String method, String protocol, String url) {
        this.name = name;
        this.method = method;
        this.protocol = protocol;
        this.url = url;
    }

}
