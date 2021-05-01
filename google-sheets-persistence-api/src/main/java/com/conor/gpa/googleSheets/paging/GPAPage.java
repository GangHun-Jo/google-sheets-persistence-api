package com.conor.gpa.googleSheets.paging;

import java.util.List;

public class GPAPage<T> {
	private int pageNum;
	private int size;
	private int totalSize;
	private int totalPage;
	private boolean hasNextPage;
	private List<T> content;

	public GPAPage(int pageNum, int size, int totalSize, int totalPage, boolean hasNextPage, List<T> content) {
		this.pageNum = pageNum;
		this.size = size;
		this.totalSize = totalSize;
		this.totalPage = totalPage;
		this.hasNextPage = hasNextPage;
		this.content = content;
	}

	public int getPageNum() {
		return pageNum;
	}

	public int getSize() {
		return size;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public boolean isHasNextPage() {
		return hasNextPage;
	}

	public List<T> getContent() {
		return content;
	}

	@Override
	public String toString() {
		return "GPAPage{" +
			"pageNum=" + pageNum +
			", size=" + size +
			", totalSize=" + totalSize +
			", totalPage=" + totalPage +
			", hasNextPage=" + hasNextPage +
			", content=" + content +
			'}';
	}
}
