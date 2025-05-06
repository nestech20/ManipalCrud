package com.example.crudapi.demo.entity;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entity representing a Nominee associated with a User.
 */
@Entity
public class Nominee {

	/**
	 * Primary key - Unique identifier for the nominee.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nominee_id")
	private Long id;

	/**
	 * Foreign key - References the associated User entity.
	 */
	@Column(name = "user_id")
	private Long userId;

	/**
	 * Nominee's first name.
	 */
	@Column(name = "firstName")
	private String firstName;

	/**
	 * Nominee's last name.
	 */
	@Column(name = "lastName")
	private String lastName;

	/**
	 * Nominee's date of birth.
	 */
	@Column(name = "dateOfBirth")
	private LocalDate dob;

	/**
	 * Nominee's mobile contact number.
	 */
	@Column(name = "mobileNo")
	private String mobileNo;

	/**
	 * Relationship of the nominee to the user (e.g., spouse, child).
	 */
	@Column(name = "relationship")
	private String relationship;

	/**
	 * Status of the nominee record (e.g., 'A' for active, 'I' for inactive).
	 */
	@Column(name = "status")
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}

	/**
	 * Overloaded method placeholder (possibly unused). Consider removing if not
	 * required.
	 */
	public void setStatus(Object status2) {
		// TODO Auto-generated method stub
	}
}
