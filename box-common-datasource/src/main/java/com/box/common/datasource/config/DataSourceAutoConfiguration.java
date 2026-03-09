package com.box.common.datasource.config;

import com.box.common.datasource.aspect.DynamicDatasourceAspect;
import com.box.common.datasource.creator.DynamicDataSourceCreator;
import com.box.common.datasource.properties.DataSourceProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

import javax.sql.DataSource;

/**
 * 动态数据源自动配置。
 */
@AutoConfiguration
@ConditionalOnClass(DataSource.class)
@Conditional(DynamicDataSourceCondition.class)
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DynamicDataSourceCreator dynamicDataSourceCreator() {
        return new DynamicDataSourceCreator();
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicDatasourceAspect dynamicDatasourceAspect() {
        return new DynamicDatasourceAspect();
    }

    @Bean(name = "dataSource")
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dataSource(DataSourceProperties properties, DynamicDataSourceCreator creator) {
        return creator.createRoutingDataSource(properties);
    }
}
