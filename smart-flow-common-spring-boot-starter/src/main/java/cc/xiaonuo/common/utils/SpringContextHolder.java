package cc.xiaonuo.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Spring Context 工具类
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的回调方法，设置上下文环境
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    /**
     * 获取ApplicationContext
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取Bean
     * @param name Bean名称
     * @return Bean
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 从 Spring 应用上下文中获取指定类型的 Bean。
     *
     * @param clazz 要获取的 Bean 的类型
     * @param <T>   要获取的 Bean 的类型
     * @return 返回指定类型的 Bean 实例，如果没有找到则返回 null
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据 Bean 的名称和类型从 Spring 应用上下文中获取指定的 Bean。
     *
     * @param name  Bean 的名称
     * @param clazz 要获取的 Bean 的类型
     * @param <T>   要获取的 Bean 的类型
     * @return 返回指定名称和类型的 Bean 实例，如果没有找到则返回 null
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 从 Spring 应用上下文中获取指定类型的 Bean。
     *
     * @param clazz 要获取的 Bean 的类型
     * @param <T>   要获取的 Bean 的类型
     * @return 返回指定类型的 Bean 实例，如果没有找到则返回 null
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }

    /**
     * 获取配置文件配置项的值
     * @param key 配置项key
     * @return 配置项值
     */
    public static String getProperty(String key) {
        return applicationContext.getEnvironment().getProperty(key);
    }

    public static String getActiveProfile() {
        return applicationContext.getEnvironment().getActiveProfiles()[0];
    }

    public static void clearHolder() {
        applicationContext = null;
    }
}