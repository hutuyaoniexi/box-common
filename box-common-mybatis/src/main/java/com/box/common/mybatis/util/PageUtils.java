package com.box.common.mybatis.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.box.common.core.constant.CommonConstants;
import com.box.common.core.response.PageResult;

/**
 * 分页工具。
 */
public final class PageUtils {

    private PageUtils() {
    }

    public static <T> Page<T> buildPage(Integer pageNum, Integer pageSize) {
        int actualPageNum = pageNum == null || pageNum < 1 ? CommonConstants.DEFAULT_PAGE_NUM : pageNum;
        int actualPageSize = pageSize == null || pageSize < 1 ? CommonConstants.DEFAULT_PAGE_SIZE : pageSize;
        return new Page<>(actualPageNum, actualPageSize);
    }

    public static <T> PageResult<T> toPageResult(IPage<T> page) {
        if (page == null) {
            return PageResult.empty();
        }
        return PageResult.of(page.getRecords(), page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    public static long offset(Integer pageNum, Integer pageSize) {
        int actualPageNum = pageNum == null || pageNum < 1 ? CommonConstants.DEFAULT_PAGE_NUM : pageNum;
        int actualPageSize = pageSize == null || pageSize < 1 ? CommonConstants.DEFAULT_PAGE_SIZE : pageSize;
        return (long) (actualPageNum - 1) * actualPageSize;
    }
}
