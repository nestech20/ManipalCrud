package com.example.crudapi.demo.entity;

import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.crudapi.demo.enums.Gender;
import com.example.crudapi.demo.enums.Title;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity class representing a User. Maps to the "users" table in the database.
 */
@Entity
@Table(name = "users")
public class User {

	/**
	 * Primary key - Unique identifier for each user.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	/**
	 * Timestamp of when the user was created. Automatically populated by Hibernate.
	 */
	@CreationTimestamp
	@Column(name = "created_date")
	private LocalDateTime createdDate;

	/**
	 * Timestamp of the last update to the user. Automatically populated by
	 * Hibernate.
	 */
	@UpdateTimestamp
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	// --- User Personal Details ---

	/**
	 * Full name of the user.
	 */
	@Column(name = "full_name")
	private String fullName;

	/**
	 * Date of birth of the user.
	 */
	@Column(name = "DateOfBirth")
	private Date dob;

	/**
	 * Gender of the user (Enum: MALE, FEMALE, OTHER).
	 */
	@Enumerated(EnumType.STRING)
	private Gender gender;

	/**
	 * Title of the user (Enum: MR, MS, MRS, etc.).
	 */
	@Enumerated(EnumType.STRING)
	private Title title;

	/**
	 * Permanent Account Number (PAN) of the user.
	 */
	@Column(name = "PanNo")
	private String panNo;

	/**
	 * Annual income of the user.
	 */
	@Column(name = "AnnualIncome")
	private Long annualIncome;

	// --- Contact Information ---

	/**
	 * Primary mobile number of the user.
	 */
	@Column(name = "MobileNo")
	private String mobileNo;

	/**
	 * Email address of the user.
	 */
	@Column(name = "email")
	private String email;

	/**
	 * Alternate mobile number.
	 */
	@Column(name = "AlternateNo")
	private String alternateNo;

	// --- Address Information ---

	/**
	 * Full address of the user.
	 */
	@Column(name = "Address")
	private String address;

	/**
	 * Area postal code (PIN code).
	 */
	@Column(name = "Pincode")
	private Long pincode;

	/**
	 * City where the user resides.
	 */
	@Column(name = "City")
	private String city;

	/**
	 * State where the user resides.
	 */
	@Column(name = "State")
	private String state;

	// --- User Status ---

	/**
	 * Status of the user (e.g., 'A' for active, 'I' for inactive).
	 */
	@Column(name = "Status")
	private Character status;

	// --- Getters and Setters ---

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAlternateNo() {
		return alternateNo;
	}

	public void setAlternateNo(String alternateNo) {
		this.alternateNo = alternateNo;
	}

	public String getPanNo() {
		return panNo;
	}

	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}

	public Long getAnnualIncome() {
		return annualIncome;
	}

	public void setAnnualIncome(Long annualIncome) {
		this.annualIncome = annualIncome;
	}

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Title getTitle() {
		return title;
	}

	public void setTitle(Title title) {
		this.title = title;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Long getPincode() {
		return pincode;
	}

	public void setPincode(Long pincode) {
		this.pincode = pincode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}
}
