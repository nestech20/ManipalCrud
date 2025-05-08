package com.example.crudapi.demo.dto;

import java.util.List;

public class AdminDto {

	private String fullName;
	private String email;
	
	private List<AddressDto> addresses;

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<AddressDto> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<AddressDto> addresses) {
		this.addresses = addresses;
	}
	

}
