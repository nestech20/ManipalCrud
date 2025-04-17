package com.example.crudapi.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Nominee {
	     @Id
	     @GeneratedValue(strategy = GenerationType.IDENTITY)
	     @Column(name = "nominee_id")
         private Long id;
	     
	     @Column(name = "firstName")
         private String firstName;
	     
	     @Column(name = "lastName")
         private String lastName;
	     
	     @Column(name = "dateOfBirth")
         private LocalDate dob;
	     
	     @Column(name = "mobileNo")
         private String mobileNo;
	     
	     @Column(name = "relationship")
         private String relationship;

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
	     
	     
}
