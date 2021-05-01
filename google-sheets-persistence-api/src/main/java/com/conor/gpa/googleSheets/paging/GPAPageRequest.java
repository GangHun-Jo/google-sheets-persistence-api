package com.conor.gpa.googleSheets.paging;

public class GPAPageRequest {
	private int pageNum;
	private int size;
	private GPASort sort;

	public GPAPageRequest() {
	}

	public GPAPageRequest(int pageNum, int size) {
		this.pageNum = pageNum;
		this.size = size;
		if (pageNum < 0 || size <= 0) {
			throw new RuntimeException("잘못된 PageRequest 입니다{pageNum:" + pageNum + ", size:" + size + "}");
		}
	}

	public GPAPageRequest(int pageNum, int size, GPASort sort) {
		this.pageNum = pageNum;
		this.size = size;
		this.sort = sort;
	}

	public GPASort getSort() {
		return sort;
	}

	public void setSort(GPASort sort) {
		this.sort = sort;
	}

	public int getStartIndex() {
		return pageNum * size;
	}

	public int getEndIndex() {
		return pageNum * size + size;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
