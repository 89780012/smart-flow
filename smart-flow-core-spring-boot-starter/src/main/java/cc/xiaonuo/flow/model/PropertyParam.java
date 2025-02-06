package cc.xiaonuo.flow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyParam implements Serializable {
    private String seq;
    private String paramType;
    private String keyName;
    private String dataType;
    private String val;
    private String val2;
    private String valueCategory;
    private String valDesc;

}