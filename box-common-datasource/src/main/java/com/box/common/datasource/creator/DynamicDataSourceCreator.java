package com.box.common.datasource.creator;

import com.box.common.datasource.exception.DataSourceException;
import com.box.common.datasource.properties.DataSourceProperties;
import com.box.common.datasource.properties.DataSourceProperties.Item;
import com.box.common.datasource.routing.DynamicRoutingDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 根据配置构建动态数据源。
 */
public class DynamicDataSourceCreator {

    public DynamicRoutingDataSource createRoutingDataSource(DataSourceProperties properties) {
        if (CollectionUtils.isEmpty(properties.getDatasources())) {
            throw new DataSourceException("未配置任何数据源，请检查 box.datasource.datasources.*");
        }

        Map<Object, Object> targetDataSources = new LinkedHashMap<>();
        DataSource primaryDataSource = null;
        String primary = properties.getPrimary();

        for (Map.Entry<String, Item> entry : properties.getDatasources().entrySet()) {
            String key = entry.getKey();
            DataSource dataSource = createSingleDataSource(key, entry.getValue());
            targetDataSources.put(key, dataSource);
            if (key.equals(primary)) {
                primaryDataSource = dataSource;
            }
        }

        if (primaryDataSource == null) {
            throw new DataSourceException("未找到主数据源配置: " + primary);
        }

        DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource(properties.isStrict(), primary);
        routingDataSource.setDefaultTargetDataSource(primaryDataSource);
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }

    private DataSource createSingleDataSource(String name, Item item) {
        if (!StringUtils.hasText(item.getUrl())) {
            throw new DataSourceException("数据源 " + name + " 缺少 url 配置");
        }
        if (!StringUtils.hasText(item.getUsername())) {
            throw new DataSourceException("数据源 " + name + " 缺少 username 配置");
        }

        HikariConfig config = new HikariConfig();
        config.setPoolName("box-" + name);
        config.setJdbcUrl(item.getUrl());
        config.setUsername(item.getUsername());
        config.setPassword(item.getPassword());
        config.setDriverClassName(item.getDriverClassName());
        config.setMaximumPoolSize(item.getMaxPoolSize());
        config.setMinimumIdle(item.getMinIdle());
        config.setConnectionTimeout(item.getConnectionTimeout());
        config.setValidationTimeout(item.getValidationTimeout());
        config.setIdleTimeout(item.getIdleTimeout());
        config.setMaxLifetime(item.getMaxLifetime());
        if (StringUtils.hasText(item.getConnectionTestQuery())) {
            config.setConnectionTestQuery(item.getConnectionTestQuery());
        }
        return new HikariDataSource(config);
    }
}
