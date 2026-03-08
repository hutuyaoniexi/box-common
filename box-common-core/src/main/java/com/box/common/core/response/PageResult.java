package com.box.common.core.response;

import com.box.common.core.constant.CommonConstants;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 通用分页结果。
 */
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<T> records;
    private long total;
    private int pageNum;
    private int pageSize;
    private long totalPages;

    public PageResult() {
        this(Collections.emptyList(), 0L, CommonConstants.DEFAULT_PAGE_NUM, CommonConstants.DEFAULT_PAGE_SIZE);
    }

    public PageResult(List<T> records, long total, int pageNum, int pageSize) {
        this.records = records == null ? Collections.emptyList() : records;
        this.total = total;
        this.pageNum = Math.max(pageNum, 1);
        this.pageSize = Math.max(pageSize, 1);
        recalculate();
    }

    public static <T> PageResult<T> of(List<T> records, long total, int pageNum, int pageSize) {
        return new PageResult<>(records, total, pageNum, pageSize);
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>();
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records == null ? Collections.emptyList() : records;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
        recalculate();
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = Math.max(pageNum, 1);
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = Math.max(pageSize, 1);
        recalculate();
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    private void recalculate() {
        this.totalPages = (total + pageSize - 1) / pageSize;
    }
}
