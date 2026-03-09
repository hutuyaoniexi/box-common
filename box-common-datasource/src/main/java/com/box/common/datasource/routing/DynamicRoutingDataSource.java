package com.box.common.datasource.routing;

import com.box.common.datasource.context.DynamicDataSourceContextHolder;
import com.box.common.datasource.exception.DataSourceException;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 根据上下文路由到目标数据源。
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    private final boolean strict;
    private final String primary;

    public DynamicRoutingDataSource(boolean strict, String primary) {
        this.strict = strict;
        this.primary = primary;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceContextHolder.peek();
    }

    @Override
    protected DataSource determineTargetDataSource() {
        Object lookupKey = determineCurrentLookupKey();
        if (lookupKey == null) {
            return (DataSource) getResolvedDefaultDataSource();
        }
        Map<Object, DataSource> resolvedDataSources = getResolvedDataSources();
        DataSource dataSource = resolvedDataSources.get(lookupKey);
        if (dataSource != null) {
            return dataSource;
        }
        if (strict) {
            throw new DataSourceException("未找到数据源: " + lookupKey);
        }
        return (DataSource) getResolvedDefaultDataSource();
    }

    public String getPrimary() {
        return primary;
    }
}
