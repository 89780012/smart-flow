package cc.xiaonuo.database.config;

import cc.xiaonuo.database.handler.MySqlPaginationHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = "com.mysql.cj.jdbc.Driver")
public class MySqlPaginationAutoConfiguration {
    
    @Bean
    public MySqlPaginationHandler mySqlPaginationHandler() {
        return new MySqlPaginationHandler();
    }
}