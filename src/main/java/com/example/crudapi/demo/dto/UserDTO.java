package com.example.crudapi.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.crudapi.demo.enums.Gender;
import com.example.crudapi.demo.enums.Title;

/**
 * Data Transfer Object for User entity.
 * Used for transferring user-related data between layers (controller, service).
 */
public class UserDTO {

    // --- User Details ---

    /** Full name of the user */
    private String fullName;

    /** Date of birth */
    private LocalDate dob;

    /** Gender of the user */
    private Gender gender;

    /** Title (e.g., Mr, Ms, Dr) */
    private Title title;

    /** PAN number (Permanent Account Number) */
    private String panNo;

    /** Annual income */
    private Long annualIncome;

    // --- Contact Information ---

    /** Mobile number */
    private String mobileNo;

    /** Email address */
    private String email;

    /** Alternate contact number */
    private String alternateNo;

    // --- Address Details ---

    /** Residential address */
    private String address;

    /** Postal code */
    private Long pincode;

    /** City name */
    private String city;

    /** State name */
    private String state;

    // --- Status and Audit ---

    /** Active/inactive status of the user */
    private Character status;

    /** Record creation timestamp */
    private LocalDateTime createdDate;

    /** Last update timestamp */
    private LocalDateTime updatedDate;

    // --- Associated Nominees ---

    /** List of nominee DTOs linked to this user */
    private List<NomineeDTO> nominees;

    /** Flag indicating if nominee is being updated (Y/N) */
    private Character isUpdatingNominee;

    // --- Getters and Setters ---

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
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

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
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

	public List<NomineeDTO> getNominees() {
		return nominees;
	}

	public void setNominees(List<NomineeDTO> nominees) {
		this.nominees = nominees;
	}

	public Character getIsUpdatingNominee() {
		return isUpdatingNominee;
	}

	public void setIsUpdatingNominee(Character isUpdatingNominee) {
		this.isUpdatingNominee = isUpdatingNominee;
	}
}
