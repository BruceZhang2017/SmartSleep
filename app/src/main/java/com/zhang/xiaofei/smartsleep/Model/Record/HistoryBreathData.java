package com.zhang.xiaofei.smartsleep.Model.Record;

import java.io.Serializable;

public class HistoryBreathData implements Serializable {
    int total;
    int totalPages;
    int first;
    int offset;
    int limit;
    int pageNo;
    int pageSize;
    HistoryBreath[] rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public HistoryBreath[] getRows() {
        return rows;
    }

    public void setRows(HistoryBreath[] rows) {
        this.rows = rows;
    }
}


