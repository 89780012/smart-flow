package cc.xiaonuo.flow.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResultItem implements Serializable {
    private String name;
    private String type;
    private String stepType;
    private String required;
    private String description;
    private String example;
} 