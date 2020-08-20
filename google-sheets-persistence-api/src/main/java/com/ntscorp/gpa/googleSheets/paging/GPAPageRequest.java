package com.ntscorp.gpa.googleSheets.paging;

public class GPAPageRequest {
	private int pageNum;
	private int size;

	public GPAPageRequest() {
	}

	public GPAPageRequest(int pageNum, int size) {
		this.pageNum = pageNum;
		this.size = size;
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
