package com.example.crudapi.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.crudapi.demo.dto.AdminDto;
import com.example.crudapi.demo.response.ResponseHandler;
import com.example.crudapi.demo.serviceimp.AdminServiceImpl;


@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private AdminServiceImpl adminServiceImpl;
	
	@PostMapping("/add")
	public ResponseHandler addAdmin(@RequestBody AdminDto adminDto) {

		ResponseHandler handler = new ResponseHandler();

		try {

			String data = adminServiceImpl.saveAdmin(adminDto);
			handler.setMessage("Sucess");
			handler.setStatus(true);
			handler.setData(data);

		} catch (Exception e) {
			e.printStackTrace();
			handler.setData(new ArrayList<>());
			handler.setMessage(e.getMessage());
			handler.setStatus(false);
		}

		return handler;

	}
	
	@GetMapping
	public ResponseHandler getAllUsers() {
		ResponseHandler handler2 = new ResponseHandler();

		try {

			List<AdminDto> data = adminServiceImpl.getAllAdmins();

			handler2.setMessage("Sucess");
			handler2.setStatus(true);
			handler2.setData(data);

		} catch (Exception e) {

			handler2.setData(new ArrayList<>());
			handler2.setMessage("Fail");
			handler2.setStatus(false);

		}

		return handler2;

	}
	
	@GetMapping("/list_by_id/{id}")
	public ResponseHandler getUserById(@PathVariable Long id) {
		ResponseHandler handler3 = new ResponseHandler();
		try {
			AdminDto dto = adminServiceImpl.getAdminById(id);
			handler3.setMessage("Sucess");
			handler3.setStatus(true);
			handler3.setData(dto);
		} catch (Exception e) {
			handler3.setData( new ArrayList<>());
			handler3.setMessage("Fail");
			handler3.setStatus(false);
		}
		return handler3;
		
	}

}
