package com.box.common.mybatis.interceptor;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

/**
 * 分页拦截器工厂。
 */
public final class MybatisPaginationInterceptor {

    private MybatisPaginationInterceptor() {
    }

    public static MybatisPlusInterceptor create() {
        return create(DbType.MYSQL, 500L, false);
    }

    public static MybatisPlusInterceptor create(DbType dbType, long maxLimit, boolean overflow) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(dbType);
        paginationInnerInterceptor.setMaxLimit(maxLimit);
        paginationInnerInterceptor.setOverflow(overflow);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }
}
