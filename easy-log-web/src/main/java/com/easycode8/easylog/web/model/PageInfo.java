package com.easycode8.easylog.web.model;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class PageInfo<T> {
    /** 页码*/
    private int pageIndex;
    /** 分页大小*/
    private int pageSize;
    /** 总数*/
    private long total;
    /** 分页结果数据*/
    private List<T> rows;

    public PageInfo() {
        if(null == rows ){
            this.rows = new ArrayList<>();
        }
    }


    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }


    /**
     * 执行计算内存分页及结果设值
     */
    public  <T> PageInfo doPage(List<T> allowedList, Callback callback) {
        if (CollectionUtils.isEmpty(allowedList)) {
            return new PageInfo<>();
        }
        this.setTotal(allowedList.size());
        int fromIndex = this.getPageIndex() < 1 ? 0 : (this.getPageIndex() - 1) * this.getPageSize();
        int toIndex = fromIndex + this.getPageSize();
        toIndex = toIndex > this.getTotal() ? (int) this.getTotal() : toIndex;
        List<T> pageList = allowedList.subList(fromIndex, toIndex);
        this.setRows(callback.buildRowsData(pageList));
        if (null == this.getRows()) {
            this.setRows(new ArrayList<>());
        }
        return this;
    }
      public interface Callback<T,R> {
        /**
         * 回调设值选择内存分页的具体业务数据
         * @param chooseList
         * @return
         */
        List<R> buildRowsData(List<T> chooseList);
    }
}
