package com.example.crudapi.demo.serviceimp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.crudapi.demo.dto.NomineeDTO;
import com.example.crudapi.demo.dto.UserDTO;
import com.example.crudapi.demo.entity.Nominee;
import com.example.crudapi.demo.entity.User;
import com.example.crudapi.demo.enums.Gender;
import com.example.crudapi.demo.enums.Title;
import com.example.crudapi.demo.repository.NomineeRepository;
import com.example.crudapi.demo.repository.UserRepository;
import com.example.crudapi.demo.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NomineeRepository nomineeRepository;

	// ========================= Add User =========================
	@Override
	public String addUser(UserDTO userDTO) {

		User adddto = new User();

		// Validate and set full name
		String fullName = userDTO.getFullName();
		if (fullName == null || fullName.trim().isEmpty()) {
			throw new IllegalArgumentException("FullName is required");
		}
		adddto.setFullName(fullName);

		// Validate and set date of birth
		LocalDate dob = userDTO.getDob();
		if (dob == null) {
			throw new IllegalArgumentException("DateOfBirth is required");
		} else if (dob.isAfter(LocalDate.now())) {
			throw new IllegalArgumentException("DateOfBirth cannot be in the Future Date");
		}
		adddto.setDob(dob);

		// Validate and set city
		String city = userDTO.getCity();
		if (city == null || city.trim().isEmpty()) {
			throw new IllegalArgumentException("City is required.");
		}
		adddto.setCity(city);

		// Validate and set address
		String add = userDTO.getAddress();
		if (add == null || add.trim().isEmpty()) {
			throw new IllegalArgumentException("Address is required.");
		}
		adddto.setAddress(add);

		// Validate email format and uniqueness
		String email = userDTO.getEmail();
		if (email == null || !email.matches("^[A-Za-z0-9+.-]+@[A-Za-z0-9.-]+$")) {
			throw new IllegalArgumentException("Email must be Valid.");
		}
		User existEmailUser = userRepository.findByEmail(email);
		if (existEmailUser != null) {
			throw new IllegalArgumentException("Email Already use.");
		}
		adddto.setEmail(email);

		// Validate and set gender
		Gender gender = userDTO.getGender();
		if (gender == null) {
			throw new IllegalArgumentException("Gender is required");
		}
		adddto.setGender(gender);

		// Validate and set annual income
		Long annaualIncome = userDTO.getAnnualIncome();
		if (annaualIncome == null || annaualIncome <= 0) {
			throw new IllegalArgumentException("Annual Income must be greater than 0.");
		}
		adddto.setAnnualIncome(annaualIncome);

		// Validate PAN format and uniqueness
		String panNo = userDTO.getPanNo();
		if (panNo == null || !panNo.matches("[A-Z]{5}[0-9]{4}[A-Z]")) {
			throw new IllegalArgumentException("PAN number must be in valid format (ABCDE1234F).");
		}
		User existPan = userRepository.findByPanNo(panNo);
		if (existPan != null) {
			throw new IllegalArgumentException(panNo + " is Already in use.");
		}
		adddto.setPanNo(panNo);

		// Validate and set pincode
		Long pinCode = userDTO.getPincode();
		if (pinCode == null || !String.valueOf(pinCode).matches("^[1-9][0-9]{5}$")) {
			throw new IllegalArgumentException("Pincode must be a valid 6-digit number.");
		}
		adddto.setPincode(pinCode);

		// Validate and set title
		Title title = userDTO.getTitle();
		if (title == null) {
			throw new IllegalArgumentException("Title is required");
		}
		adddto.setTitle(title);

		// Validate mobile number format and uniqueness
		String mobNo = userDTO.getMobileNo();
		if (mobNo == null || !mobNo.matches("[6-9][0-9]{9}")) {
			throw new IllegalArgumentException("Mobile number must be a valid 10-digit Indian number.");
		}
		User existMobNO = UserRepository.findByMobileNo(mobNo);
		if (existMobNO != null) {
			throw new IllegalArgumentException(mobNo + " is Already in use.");
		}
		adddto.setMobileNo(mobNo);

		// Validate alternate number if provided
		String altNo = userDTO.getAlternateNo();
		if (altNo != null || !altNo.isEmpty()) {
			if (!altNo.matches("[6-9][0-9]{9}")) {
				throw new IllegalArgumentException("Alternate number must be a valid 10-digit Indian number.");
			}
		}
		adddto.setAlternateNo(altNo);

		// Validate and set state
		String state = userDTO.getState();
		if (state == null || state.trim().isEmpty()) {
			throw new IllegalArgumentException("State is required.");
		}
		adddto.setState(state);

		// Validate and set status
		Character status = userDTO.getStatus();
		if (status == null || !(status == 'Y' || status == 'N')) {
			throw new IllegalArgumentException("Status must be valid (Y/N).");
		}
		adddto.setStatus(status);

		// Set creation and update dates
		adddto.setCreatedDate(userDTO.getCreatedDate());
		adddto.setUpdatedDate(userDTO.getUpdatedDate());

		// Save user and get generated ID
		User details = userRepository.save(adddto);
		Long userId = details.getId();

		// Process and save nominee details
		List<NomineeDTO> nomineeDetails = userDTO.getNominees();
		List<Nominee> nomineeEnties = new ArrayList<>();

		for (NomineeDTO nomineeDTO : nomineeDetails) {
			Nominee nominee = new Nominee();
			nominee.setFirstName(nomineeDTO.getFirstName());
			nominee.setLastName(nomineeDTO.getLastName());
			nominee.setDob(nomineeDTO.getDob());
			nominee.setMobileNo(nomineeDTO.getMobileNo());
			nominee.setRelationship(nomineeDTO.getRelationship());
			nominee.setStatus(nomineeDTO.getStatus());
			nominee.setUserId(userId);

			nomineeEnties.add(nominee);
		}

		// Save all nominees
		nomineeRepository.saveAll(nomineeEnties);

		return "User and nominees added successfully.";
	}

	// ========================= Get All Active Users =========================
	@Override
	public List<UserDTO> getAllUser() {
		List<User> perlist = userRepository.findByStatus('Y');
		List<UserDTO> addUser = new ArrayList<>();

		for (User user : perlist) {
			UserDTO addDto = new UserDTO();

			// Map user entity to DTO
			addDto.setFullName(user.getFullName());
			addDto.setDob(user.getDob());
			addDto.setCity(user.getCity());
			addDto.setAddress(user.getAddress());
			addDto.setEmail(user.getEmail());
			addDto.setGender(user.getGender());
			addDto.setAnnualIncome(user.getAnnualIncome());
			addDto.setPanNo(user.getPanNo());
			addDto.setPincode(user.getPincode());
			addDto.setTitle(user.getTitle());
			addDto.setMobileNo(user.getMobileNo());
			addDto.setAlternateNo(user.getAlternateNo());
			addDto.setState(user.getState());
			addDto.setStatus(user.getStatus());
			addDto.setCreatedDate(user.getCreatedDate());
			addDto.setUpdatedDate(user.getUpdatedDate());

		
			addUser.add(addDto);
		}

		return addUser;
	}

	// ========================= Get User by ID =========================
	@Override
	public UserDTO getUserById(Long id) {
		Optional<User> userOptional = userRepository.findById(id);

		if (userOptional.isPresent()) {
			User user = userOptional.get();
			UserDTO setDetails = new UserDTO();

			// Copy user fields to DTO
			setDetails.setFullName(user.getFullName());
			setDetails.setDob(user.getDob());
			setDetails.setCity(user.getCity());
			setDetails.setAddress(user.getAddress());
			setDetails.setEmail(user.getEmail());
			setDetails.setGender(user.getGender());
			setDetails.setAnnualIncome(user.getAnnualIncome());
			setDetails.setPanNo(user.getPanNo());
			setDetails.setPincode(user.getPincode());
			setDetails.setTitle(user.getTitle());
			setDetails.setMobileNo(user.getMobileNo());
			setDetails.setAlternateNo(user.getAlternateNo());
			setDetails.setState(user.getState());
			setDetails.setStatus(user.getStatus());
			setDetails.setCreatedDate(user.getCreatedDate());
			setDetails.setUpdatedDate(user.getUpdatedDate());

			// Load and map nominee list
			List<Nominee> nominees = nomineeRepository.findByUserId(user.getId());
			List<NomineeDTO> nomineeDList = new ArrayList<>();

			for (Nominee nominee : nominees) {
				NomineeDTO nomineeDTO = new NomineeDTO();
				nomineeDTO.setFirstName(nominee.getFirstName());
				nomineeDTO.setLastName(nominee.getLastName());
				nomineeDTO.setDob(nominee.getDob());
				nomineeDTO.setMobileNo(nominee.getMobileNo());
				nomineeDTO.setRelationship(nominee.getRelationship());
				nomineeDTO.setStatus(nominee.getStatus());
				nomineeDList.add(nomineeDTO);
			}

			setDetails.setNominees(nomineeDList);
			return setDetails;

		} else {
			throw new RuntimeException("User not found with id: " + id);
		}
	}

	// ========================= Update User =========================
	@Override
	public String updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
	    Optional<User> abc = userRepository.findByIdAndStatus(id, 'Y');

	    if (abc.isPresent()) {
	        User existUser = abc.get();

	        // === Update USER fields ===
	        if (userDTO.getFullName() != null && !userDTO.getFullName().trim().isEmpty())
	            existUser.setFullName(userDTO.getFullName());

	        if (userDTO.getDob() != null && !userDTO.getDob().isAfter(LocalDate.now()))
	            existUser.setDob(userDTO.getDob());

	        if (userDTO.getCity() != null && !userDTO.getCity().trim().isEmpty())
	            existUser.setCity(userDTO.getCity());

	        if (userDTO.getAddress() != null && !userDTO.getAddress().trim().isEmpty())
	            existUser.setAddress(userDTO.getAddress());

	        if (userDTO.getEmail() != null && !userDTO.getEmail().trim().isEmpty()) {
	            if (!userDTO.getEmail().matches("^[A-Za-z0-9+.-]+@[A-Za-z0-9.-]+$"))
	                throw new IllegalArgumentException("Email must be valid.");

	            User existEmailUser = userRepository.findByEmail(userDTO.getEmail());
	            if (existEmailUser != null && !existEmailUser.getId().equals(id))
	                throw new IllegalArgumentException("Email already in use.");

	            existUser.setEmail(userDTO.getEmail());
	        }

	        if (userDTO.getGender() != null)
	            existUser.setGender(userDTO.getGender());

	        if (userDTO.getAnnualIncome() != null && userDTO.getAnnualIncome() > 0)
	            existUser.setAnnualIncome(userDTO.getAnnualIncome());

	        if (userDTO.getPanNo() != null && userDTO.getPanNo().matches("[A-Z]{5}[0-9]{4}[A-Z]")) {
	            User existPan = userRepository.findByPanNo(userDTO.getPanNo());
	            if (existPan != null && !existPan.getId().equals(id))
	                throw new IllegalArgumentException(userDTO.getPanNo() + " is already in use.");

	            existUser.setPanNo(userDTO.getPanNo());
	        }

	        if (userDTO.getPincode() != null && String.valueOf(userDTO.getPincode()).matches("^[1-9][0-9]{5}$"))
	            existUser.setPincode(userDTO.getPincode());

	        if (userDTO.getTitle() != null)
	            existUser.setTitle(userDTO.getTitle());

	        if (userDTO.getMobileNo() != null && userDTO.getMobileNo().matches("[6-9][0-9]{9}")) {
	            User existMobNO = UserRepository.findByMobileNo(userDTO.getMobileNo());
	            if (existMobNO != null && !existMobNO.getId().equals(id))
	                throw new IllegalArgumentException(userDTO.getMobileNo() + " is already in use.");

	            existUser.setMobileNo(userDTO.getMobileNo());
	        }

	        if (userDTO.getAlternateNo() != null && !userDTO.getAlternateNo().isEmpty()) {
	            if (!userDTO.getAlternateNo().matches("[6-9][0-9]{9}")) {
	                throw new IllegalArgumentException("Alternate number must be a valid 10-digit Indian number.");
	            }
	            existUser.setAlternateNo(userDTO.getAlternateNo());
	        }

	        if (userDTO.getState() != null && !userDTO.getState().trim().isEmpty())
	            existUser.setState(userDTO.getState());

	        // ===NOMINEES update or creation ===
	        List<NomineeDTO> nomineeDTOs = userDTO.getNominees();
	        if (nomineeDTOs != null) {
	            for (NomineeDTO dto : nomineeDTOs) {
	                Nominee nominee;

	                if (dto.getId() != null) {
	                    // Update existing nominee
	                    Optional<Nominee> optionalNominee = nomineeRepository.findById(dto.getId());
	                    if (optionalNominee.isPresent()) {
	                        nominee = optionalNominee.get();
	                    } else {
	                        //  create new
	                        nominee = new Nominee();
	                        nominee.setUserId(id);
	                    }
	                } else {
	                    // New nominee creation
	                    nominee = new Nominee();
	                    nominee.setUserId(id);

//	                    // check for duplicates
//	                    boolean isDuplicate = nomineeRepository.findByUserId(id).stream().anyMatch(n ->
//	                        n.getFirstName().equals(dto.getFirstName()) &&
//	                        n.getLastName().equals(dto.getLastName()) &&
//	                        n.getDob().equals(dto.getDob())
//	                    );
//	                    if (isDuplicate) continue; // skip duplicate
	                }

	                // Set nominee fields (skip nulls)
	                if (dto.getFirstName() != null) nominee.setFirstName(dto.getFirstName());
	                if (dto.getLastName() != null) nominee.setLastName(dto.getLastName());
	                if (dto.getDob() != null) nominee.setDob(dto.getDob());
	                if (dto.getMobileNo() != null) nominee.setMobileNo(dto.getMobileNo());
	                if (dto.getRelationship() != null) nominee.setRelationship(dto.getRelationship());

	                // Set or default status
	                nominee.setStatus(dto.getStatus() != null ? dto.getStatus() : "Y");

	                nomineeRepository.save(nominee); // Save updated or new nominee
	            }
	        }

	        userRepository.save(existUser); // Save final user object
	        return "Updated";
	    } else {
	        return "Not Updated";
	    }
	}

	// ========================= Delete User =========================
	@Override
	public void delete(Long id) {
		Optional<User> optionalUser = userRepository.findById(id);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			user.setStatus('N'); // Mark user as inactive
			userRepository.save(user);

			// Also deactivate all associated nominees
			List<Nominee> nomineeList = nomineeRepository.findByUserId(user.getId());
			for (Nominee nominee : nomineeList) {
				nominee.setStatus('N');
			}
			nomineeRepository.saveAll(nomineeList);
		}
	}
}
