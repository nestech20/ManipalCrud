package com.example.crudapi.demo.serviceimp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.crudapi.demo.dto.AddressDto;
import com.example.crudapi.demo.dto.AdminDto;
import com.example.crudapi.demo.entity.Address;
import com.example.crudapi.demo.entity.Admin;
import com.example.crudapi.demo.repository.AdminRepository;
import com.example.crudapi.demo.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService{
	
	@Autowired
	private AdminRepository adminRepository;

	@Override
	public String saveAdmin(AdminDto adminDto) {
		Admin admin = new Admin();
		admin.setFullName(adminDto.getFullName());
		admin.setEmail(adminDto.getEmail());

		List<Address> addList = new ArrayList<>();
		if (adminDto.getAddresses() != null) {
			for (AddressDto dto : adminDto.getAddresses()) {
				Address address = new Address();
				address.setAddress(dto.getAddress());
				address.setCity(dto.getCity());
				address.setState(dto.getState());
				address.setPincode(dto.getPincode());
				address.setAdmin(admin);
				addList.add(address);
			}
			
			admin.setAddresses(addList);
			adminRepository.save(admin);
			
		}
		return "save";
		
	}


	@Override
	public List<AdminDto> getAllAdmins() {
		List<Admin> adminList = adminRepository.findAll();
		List<AdminDto> adminDtos = new ArrayList<>();
		
		for (Admin admin : adminList) {
			AdminDto adminDto = new AdminDto();
			
			adminDto.setFullName(admin.getFullName());
			adminDto.setEmail(admin.getEmail());
			
			List<AddressDto> addressDtos = new ArrayList<>();
			if (admin.getAddresses() != null) {
			for(Address addr : admin.getAddresses()) {
				AddressDto addrDto = new AddressDto();
				addrDto.setAddress(addr.getAddress());
				addrDto.setCity(addr.getCity());
				addrDto.setState(addr.getState());
				addrDto.setPincode(addr.getPincode());
				addressDtos.add(addrDto);
			
			}
			
			adminDto.setAddresses(addressDtos);
			adminDtos.add(adminDto);
				
			}
		}
		return adminDtos;
		
	}

	@Override
	public AdminDto getAdminById(Long id) {
	    Admin admin = adminRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + id));

	    AdminDto adminDto = new AdminDto();
	    adminDto.setFullName(admin.getFullName());
	    adminDto.setEmail(admin.getEmail());

	    List<AddressDto> addressDtos = new ArrayList<>();
	    if (admin.getAddresses() != null) {
	        for (Address addr : admin.getAddresses()) {
	            AddressDto addrDto = new AddressDto();
	            addrDto.setAddress(addr.getAddress());
	            addrDto.setCity(addr.getCity());
	            addrDto.setState(addr.getState());
	            addrDto.setPincode(addr.getPincode());
	            addressDtos.add(addrDto);
	        }
	    }
	    adminDto.setAddresses(addressDtos);

	    return adminDto;
	}

	
//	@Override
//	public AdminDto getAdminById(Long id) {
//		Admin admin = adminRepository.findById(id)
//			.orElseThrow(() -> new RuntimeException("Admin not found with id: " + id)); // Or custom exception
////		AdminDto adminDto = new AdminDto();
////		// Set basic fields
//		adminDto.setFullName(admin.getFullName());
//		adminDto.setEmail(admin.getEmail());
//
//		// Map list of addresses
//		List<AddressDto> addressDtos = new ArrayList<>();
//		if (admin.getAddresses() != null) {
//			for (Address addr : admin.getAddresses()) {
//				AddressDto addrDto = new AddressDto();
//				addrDto.setAddress(addr.getAddress());
//				addrDto.setCity(addr.getCity());
//				addrDto.setState(addr.getState());
//				addrDto.setPincode(addr.getPincode());
//				addressDtos.add(addrDto);
//			}
//		}
//
//		adminDto.setAddresses(addressDtos);
//
//		return adminDto;
//	}

//
//	@Override
//	public void deleteAdmin(Long id) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public String updateAdmin(Long id, AdminDto adminDto) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	

}
