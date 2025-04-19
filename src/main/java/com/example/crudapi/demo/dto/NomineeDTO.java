package com.example.crudapi.demo.dto;

import java.time.LocalDate;

import jakarta.persistence.Column;

public class NomineeDTO {
	
	private Long id;
	
	private String firstName;

     private String lastName;

     private LocalDate dob;

     private String mobileNo;                                                                                                                                                                                                                  
   
     private String relationship;
     
 	
    private Character status;
     
     public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
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
   
	  public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}


}
