package com.pinyougou.vo;

import java.io.Serializable;
import java.util.List;

public class PageResult implements Serializable {
    //总记录数
    private long total;

    //列表 ?占位符，如果赋值以后则不可以修改
    private List<?> rows;

    public PageResult(long total, List<?> rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }
}
