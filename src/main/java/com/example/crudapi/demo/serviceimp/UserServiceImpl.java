package com.example.crudapi.demo.serviceimp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.crudapi.demo.dto.NomineeDTO;
import com.example.crudapi.demo.dto.UserDTO;
import com.example.crudapi.demo.entity.Nominee;
import com.example.crudapi.demo.entity.User;
import com.example.crudapi.demo.entity.UserFilter;
import com.example.crudapi.demo.entity.UserListing;
import com.example.crudapi.demo.enums.Gender;
import com.example.crudapi.demo.enums.Title;
import com.example.crudapi.demo.repository.NomineeRepository;
import com.example.crudapi.demo.repository.UserRepository;
import com.example.crudapi.demo.service.UserService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NomineeRepository nomineeRepository;

	@Autowired
	private EntityManager entityManager;

	// ========================= Fetch Proposers by Filters and Sorting
	// =========================
	@Override
	public List<User> fetchAllProposerByStringBuilder(UserListing listing) {
		StringBuilder sb = new StringBuilder("SELECT u FROM User u WHERE u.status = 'Y'");
		Map<String, Object> params = new HashMap<>();

		UserFilter filter = listing.getUserFilter();

		// Add dynamic filters based on user-provided filter criteria
		if (filter != null) {
			// Filter by full name (case insensitive)
			if (filter.getFullName() != null && !filter.getFullName().isBlank()) {
				sb.append(" AND LOWER(u.fullName) LIKE :fullName");
				params.put("fullName", "%" + filter.getFullName().toLowerCase() + "%");
			}

			// Filter by email (case insensitive)
			if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
				sb.append(" AND LOWER(u.email) LIKE :email");
				params.put("email", "%" + filter.getEmail().toLowerCase() + "%");
			}

			// Filter by mobile number
			if (filter.getMobileNo() != null && !filter.getMobileNo().isBlank()) {
				sb.append(" AND u.mobileNo LIKE :mobileNo");
				params.put("mobileNo", "%" + filter.getMobileNo() + "%");
			}
		}

		// Handle sorting based on the provided fields and order
		String sortBy = listing.getSortBy();
		String sortOrder = listing.getSortOrder();

		if (sortBy == null || sortBy.isBlank())
			sortBy = "id"; // Default sorting by 'id'
		if (sortOrder == null || sortOrder.isBlank()
				|| !(sortOrder.equalsIgnoreCase("asc") || sortOrder.equalsIgnoreCase("desc"))) {
			sortOrder = "desc"; // Default to descending order
		}

		sb.append(" ORDER BY u.").append(sortBy).append(" ").append(sortOrder);

		// Create and execute the dynamic query using EntityManager
		TypedQuery<User> query = entityManager.createQuery(sb.toString(), User.class);
		params.forEach(query::setParameter);

		// Handle pagination: calculate offsets and limits
		int page = listing.getPageNo();
		int size = listing.getPageSize();

		if (size > 0 && page > 0) {
			query.setFirstResult((page - 1) * size);
			query.setMaxResults(size);
		} else if (page > 0 && size == 0 || page == 0 && size > 0) {
			throw new IllegalArgumentException("Page number must be greater than 0");
		}

		return query.getResultList();
	}

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

		// Validate email format and check uniqueness
		String email = userDTO.getEmail();
		if (email == null || !email.matches("^[A-Za-z0-9+.-]+@[A-Za-z0-9.-]+$")) {
			throw new IllegalArgumentException("Email must be Valid.");
		}
		User existEmailUser = userRepository.findByEmail(email);
		if (existEmailUser != null) {
			throw new IllegalArgumentException("Email Already in use.");
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

		// Validate PAN number format and check uniqueness
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

		// Validate mobile number format and check uniqueness
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
		if (altNo != null && !altNo.isEmpty()) {
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

		// Validate and set status (Y/N)
		Character status = userDTO.getStatus();
		if (status == null || !(status == 'Y' || status == 'N')) {
			throw new IllegalArgumentException("Status must be valid (Y/N).");
		}
		adddto.setStatus(status);

		// Set creation and update dates
		adddto.setCreatedDate(userDTO.getCreatedDate());
		adddto.setUpdatedDate(userDTO.getUpdatedDate());

		// Save the user and retrieve the generated ID
		User details = userRepository.save(adddto);
		Long userId = details.getId();

		// Process and save nominee details
		List<NomineeDTO> nomineeDetails = userDTO.getNominees();

		// Ensure that only one nominee is added
		if (nomineeDetails.size() > 1) {
			throw new IllegalArgumentException("Only one nominee is allowed per user.");
		}

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

		// Save nominee(s)
		nomineeRepository.saveAll(nomineeEnties);

		return "User and nominee added successfully.";
	}

	// ========================= Get All Active Users =========================
	@Override
	public List<UserDTO> getAllUser() {
		// Fetch all active users (status 'Y')
		List<User> perlist = userRepository.findByStatus('Y');
		List<UserDTO> addUser = new ArrayList<>();

		// Convert each User entity to UserDTO for response
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

			// Add UserDTO to the list
			addUser.add(addDto);
		}

		// Return the list of active users
		return addUser;
//		 return userRepository.getAllActiveUsersWithNominee();
	}

	// ========================= Get User by ID =========================
	@Override
	public UserDTO getUserById(Long id) {
		// Fetch user by ID
		Optional<User> userOptional = userRepository.findById(id);

		// If user exists, map the entity to DTO
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			UserDTO setDetails = new UserDTO();

			// Map user fields to DTO
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

			// Fetch and map nominee details
			List<Nominee> nominees = nomineeRepository.findByUserIdAndStatus(user.getId(), 'Y');
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

			// Set nominee list in the userDTO
			setDetails.setNominees(nomineeDList);
			return setDetails;

		} else {
			// If user not found, throw an exception
			throw new RuntimeException("User not found with id: " + id);
		}
	}

	// ========================= Update User =========================
	@Override
	public String updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
		// Fetch the user by ID and check if active (status 'Y')
		Optional<User> abc = userRepository.findByIdAndStatus(id, 'Y');

		if (abc.isPresent()) {
			User existUser = abc.get();

			// === 1. Handle nominee update if requested ===
			Character isUpdatingNominee = userDTO.getIsUpdatingNominee();

			if (isUpdatingNominee != null && isUpdatingNominee == 'Y') {

				// Validate nominee input
				List<NomineeDTO> nomineeDTOs = userDTO.getNominees();
				if (nomineeDTOs == null || nomineeDTOs.isEmpty()) {
					throw new IllegalArgumentException("Nominee details are mandatory when isUpdatingNominee is 'Y'.");
				}
				if (nomineeDTOs.size() > 1) {
					throw new IllegalArgumentException("Only one nominee is allowed per user.");
				}

				// Logically delete existing active nominee(s)
				List<Nominee> existingNominees = nomineeRepository.findByUserId(id);
				for (Nominee existingNominee : existingNominees) {
					if (existingNominee.getStatus() != null && existingNominee.getStatus() == 'Y') {
						existingNominee.setStatus('N'); // Logical deletion
						nomineeRepository.save(existingNominee);
					}
				}

				// Save new nominee
				NomineeDTO dto = nomineeDTOs.get(0);

				// Validate nominee fields
				if (dto.getFirstName() == null || dto.getLastName() == null || dto.getDob() == null
						|| dto.getMobileNo() == null || dto.getRelationship() == null) {
					throw new IllegalArgumentException(
							"All nominee fields are required when isUpdatingNominee is 'Y'.");
				}

				Nominee nominee = new Nominee();
				nominee.setUserId(id);
				nominee.setFirstName(dto.getFirstName());
				nominee.setLastName(dto.getLastName());
				nominee.setDob(dto.getDob());
				nominee.setMobileNo(dto.getMobileNo());
				nominee.setRelationship(dto.getRelationship());
				nominee.setStatus('Y'); // New nominee is active

				nomineeRepository.save(nominee);

			} else if (isUpdatingNominee != null && isUpdatingNominee == 'N') {
				// Skip nominee update if 'N'
			} else {
				// Throw an exception for invalid isUpdatingNominee value
				throw new IllegalArgumentException(
						"Invalid value for isUpdatingNominee. Allowed values are 'Y' or 'N'.");
			}

			// === 2. Update User fields with validations ===

			// Update the user's fields only if the new value is valid
			if (userDTO.getFullName() != null && !userDTO.getFullName().trim().isEmpty())
				existUser.setFullName(userDTO.getFullName());

			if (userDTO.getDob() != null && !userDTO.getDob().isAfter(LocalDate.now()))
				existUser.setDob(userDTO.getDob());

			if (userDTO.getCity() != null && !userDTO.getCity().trim().isEmpty())
				existUser.setCity(userDTO.getCity());

			if (userDTO.getAddress() != null && !userDTO.getAddress().trim().isEmpty())
				existUser.setAddress(userDTO.getAddress());

			// Validate and update email, ensuring it's unique
			if (userDTO.getEmail() != null && !userDTO.getEmail().trim().isEmpty()) {
				if (!userDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
					throw new IllegalArgumentException("Email must be valid.");

				User existEmailUser = userRepository.findByEmail(userDTO.getEmail());
				if (existEmailUser != null && !existEmailUser.getId().equals(id))
					throw new IllegalArgumentException("Email already in use.");

				existUser.setEmail(userDTO.getEmail());
			}

			// Similar validations for PAN, pincode, and other fields...
			// ... continue updating fields

			// Save the updated user entity
			userRepository.save(existUser);
			return "Updated";
		} else {
			// If user not found, return "Not Updated"
			return "Not Updated";
		}
	}

	// ========================= Delete User =========================
	@Override
	public void delete(Long id) {
		// Fetch the user by ID
		Optional<User> optionalUser = userRepository.findById(id);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			user.setStatus('N'); // Mark user as inactive
			userRepository.save(user);

			// Also deactivate all associated nominees
			List<Nominee> nomineeList = nomineeRepository.findByUserId(user.getId());
			for (Nominee nominee : nomineeList) {
				nominee.setStatus('N'); // Set nominee status to 'N' (inactive)
			}
			nomineeRepository.saveAll(nomineeList);
		}
	}

	// ================= Export USER Table in Excel =================
	@Override
	public String exportUsersToExcel() throws ServletException, IOException {

		List<User> allUser = userRepository.findByStatus('Y');

		XSSFWorkbook workbook = new XSSFWorkbook();

		XSSFSheet sheet = workbook.createSheet("User_Data");

//		XSSFRow headerRow = sheet.createRow(0);

//		headerRow.createCell(0).setCellValue("User ID");
//		headerRow.createCell(1).setCellValue("Title");
//		headerRow.createCell(2).setCellValue("FullName");
//		headerRow.createCell(3).setCellValue("Gender");
//		headerRow.createCell(4).setCellValue("Date of Birth");
//		headerRow.createCell(5).setCellValue("PAN Number");
//		headerRow.createCell(6).setCellValue("Status");
//		headerRow.createCell(7).setCellValue("Email");
//		headerRow.createCell(8).setCellValue("Mobile No");
//		headerRow.createCell(9).setCellValue("Alternate No");
//		headerRow.createCell(10).setCellValue("Address");
//		headerRow.createCell(11).setCellValue("Pincode");
//		headerRow.createCell(12).setCellValue("City");
//		headerRow.createCell(13).setCellValue("State");

		// Define headers in an array
		String[] headers = {
			    "User ID", "Title", "Full Name", "Gender", "Date of Birth", "PAN Number", "Annual Income",
			    "Email", "Mobile No", "Alternate No", "Address", "Pincode", "City", "State", "Status"
			};

		// Create header row
		XSSFRow headerRow = sheet.createRow(0);

		// Fill headers using loop
		for (int i = 0; i < headers.length; i++) {
			headerRow.createCell(i).setCellValue(headers[i]);
		}

		String SystemPath = "C:/ExcelFiles";
		new File(SystemPath).mkdirs();

//		String timestamp = LocalDateTime.now()
//	            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_hh-mm-ss"));
		String filename = "user_data_" + UUID.randomUUID().toString().substring(0, 6) + ".xlsx";
		String filepath = SystemPath + "/" + filename;

		try (FileOutputStream uploadFile = new FileOutputStream(filepath)) {
			workbook.write(uploadFile);
		}

		workbook.close();

		return filepath;
	}

	@Override
	public void importExcelToUser(InputStream file) throws IOException {
	    List<User> userList = new ArrayList<>();
	    Workbook workbook = WorkbookFactory.create(file);
	    Sheet sheet = workbook.getSheetAt(0);
	    DataFormatter formatter = new DataFormatter();

	    for (Row row : sheet) {
	        if (row.getRowNum() == 0) continue; // Skip header

	        User user = new User();

	        // Title (Enum)
	        String titleVal = formatter.formatCellValue(row.getCell(1));
	        if (!titleVal.isBlank()) {
	            user.setTitle(Title.valueOf(titleVal.trim().toUpperCase()));
	        }

	        user.setFullName(formatter.formatCellValue(row.getCell(2)));

	        // Gender (Enum)
	        String genderVal = formatter.formatCellValue(row.getCell(3));
	        if (!genderVal.isBlank()) {
	            user.setGender(Gender.valueOf(genderVal.trim().toUpperCase()));
	        }

	        // DOB (date)
	        Cell dobCell = row.getCell(4);
	        if (dobCell != null && DateUtil.isCellDateFormatted(dobCell)) {
	            user.setDob(dobCell.getLocalDateTimeCellValue().toLocalDate());
	        }

	        user.setPanNo(formatter.formatCellValue(row.getCell(5)));

	        String incomeVal = formatter.formatCellValue(row.getCell(6));
	        if (!incomeVal.isBlank()) {
	            user.setAnnualIncome(Long.parseLong(incomeVal));
	        }

	        user.setEmail(formatter.formatCellValue(row.getCell(7)));
	        user.setMobileNo(formatter.formatCellValue(row.getCell(8)));
	        user.setAlternateNo(formatter.formatCellValue(row.getCell(9)));
	        user.setAddress(formatter.formatCellValue(row.getCell(10)));

	        String pincodeVal = formatter.formatCellValue(row.getCell(11));
	        if (!pincodeVal.isBlank()) {
	            user.setPincode(Long.parseLong(pincodeVal));
	        }

	        user.setCity(formatter.formatCellValue(row.getCell(12)));
	        user.setState(formatter.formatCellValue(row.getCell(13)));

	        String statusVal = formatter.formatCellValue(row.getCell(14));
	        if (!statusVal.isBlank()) {
	            user.setStatus(statusVal.trim().charAt(0));
	        }

	        userList.add(user);
	    }

	    workbook.close();
	    userRepository.saveAll(userList);
	}


}
