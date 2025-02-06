package cc.xiaonuo.flow.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 流程组件注解
 * 标注此注解的类将作为流程组件被容器管理
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface FlowComponent {
    
    /**
     * 组件名称
     * 等效于 name 属性
     */
    String value() default "";
    
    /**
     * 组件名称
     * 等效于 value 属性
     */
    String name() default "";
}
