package com.example.crudapi.demo.service;

import java.util.List;

import com.example.crudapi.demo.dto.AdminDto;
import com.example.crudapi.demo.entity.Admin;

public interface AdminService {

	    String saveAdmin(AdminDto adminDto);
	    List<AdminDto> getAllAdmins();
	    AdminDto getAdminById(Long id);
//	    void deleteAdmin(Long id);
//	    String updateAdmin(Long id, AdminDto adminDto);
}



