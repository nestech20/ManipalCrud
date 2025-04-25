package com.example.crudapi.demo.entity;

/**
 * Encapsulates pagination, sorting, and filtering parameters
 * for listing User entities.
 */
public class UserListing {

    /**
     * The page number to fetch (0-based index).
     */
	private int pageNo;

    /**
     * The number of records per page.
     */
	private int pageSize;

    /**
     * Field name to sort by (e.g., "fullName", "email").
     */
	private String sortBy;

    /**
     * Sort direction: "ASC" for ascending or "DESC" for descending.
     */
	private String sortOrder;

    /**
     * Filtering criteria for user listing.
     */
	private UserFilter userFilter;

    // --- Getters and Setters ---

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

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public UserFilter getUserFilter() {
		return userFilter;
	}

	public void setUserFilter(UserFilter userFilter) {
		this.userFilter = userFilter;
	}
}
