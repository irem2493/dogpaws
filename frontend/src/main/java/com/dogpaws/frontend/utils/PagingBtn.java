package com.dogpaws.frontend.utils;

import lombok.Data;

@Data
public class PagingBtn {
    private int totalPage;
    private int currentPage;
    private int startPage;
    private int endPage;
    private boolean prevBtn;
    private boolean nextBtn;
    private boolean firstPageBtn;
    private boolean lastPageBtn;

    public PagingBtn(int totalCount, int currentPage, int pageSize, int pageBtnCount) {
        if (totalCount == 0) {
            this.totalPage = 1;
            this.currentPage = 1;
            this.startPage = 1;
            this.endPage = 1;
            this.prevBtn = false;
            this.nextBtn = false;
            this.firstPageBtn = false;
            this.lastPageBtn = false;
        } else {
            this.totalPage = (int) Math.ceil((double) totalCount / pageSize);
            this.currentPage = Math.max(1, Math.min(currentPage, totalPage));
            this.startPage = ((currentPage - 1) / pageBtnCount) * pageBtnCount + 1;
            this.endPage = Math.min(startPage + pageBtnCount - 1, totalPage);

            this.prevBtn = startPage > 1;
            this.nextBtn = endPage < totalPage;
            this.firstPageBtn = currentPage > 1;
            this.lastPageBtn = currentPage < totalPage;
        }
    }
}