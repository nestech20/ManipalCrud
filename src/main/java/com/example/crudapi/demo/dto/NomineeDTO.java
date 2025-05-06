package com.example.crudapi.demo.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object for Nominee entity. Used for transferring nominee data
 * between layers.
 */
public class NomineeDTO {

	/** Unique identifier for the nominee (used for update operations) */
	private Long id;

	/** First name of the nominee */
	private String firstName;

	/** Last name of the nominee */
	private String lastName;

	/** Date of birth of the nominee */
	private LocalDate dob;

	/** Mobile number of the nominee */
	private String mobileNo;

	/** Relationship of the nominee to the user (e.g., Spouse, Child, Parent) */
	private String relationship;

	/** Status of the nominee (active/inactive) */
	private Character status;

	// --- Getters and Setters ---

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}
}
