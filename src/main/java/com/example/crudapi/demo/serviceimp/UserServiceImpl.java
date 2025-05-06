package com.example.crudapi.demo.serviceimp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.crudapi.demo.dto.NomineeDTO;
import com.example.crudapi.demo.dto.UserDTO;
import com.example.crudapi.demo.entity.FileQueue;
import com.example.crudapi.demo.entity.Nominee;
import com.example.crudapi.demo.entity.User;
import com.example.crudapi.demo.entity.UserFilter;
import com.example.crudapi.demo.entity.UserImportLog;
import com.example.crudapi.demo.entity.UserListing;
import com.example.crudapi.demo.enums.Gender;
import com.example.crudapi.demo.enums.Title;
import com.example.crudapi.demo.repository.FileQueueRepository;
import com.example.crudapi.demo.repository.NomineeRepository;
import com.example.crudapi.demo.repository.UserImportLogRepository;
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

	@Autowired
	private UserImportLogRepository userImportLogRepository;
	
	@Autowired
	private FileQueueRepository queueRepository;

	// ========== Fetch Proposers by Filters and Sorting =============
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

			if (userDTO.getGender() != null)
				existUser.setGender(userDTO.getGender());

			if (userDTO.getTitle() != null)
				existUser.setTitle(userDTO.getTitle());

			if (userDTO.getPanNo() != null && !userDTO.getPanNo().trim().isEmpty()) {
				if (!userDTO.getPanNo().matches("[A-Z]{5}[0-9]{4}[A-Z]{1}"))
					throw new IllegalArgumentException("PAN format is invalid.");
				existUser.setPanNo(userDTO.getPanNo());
			}

			if (userDTO.getAnnualIncome() != null && userDTO.getAnnualIncome() > 0)
				existUser.setAnnualIncome(userDTO.getAnnualIncome());

			if (userDTO.getMobileNo() != null && userDTO.getMobileNo().matches("\\d{10}"))
				existUser.setMobileNo(userDTO.getMobileNo());

			if (userDTO.getAlternateNo() != null && userDTO.getAlternateNo().matches("\\d{10}"))
				existUser.setAlternateNo(userDTO.getAlternateNo());

			if (userDTO.getPincode() != null && userDTO.getPincode() > 100000 && userDTO.getPincode() < 999999)
				existUser.setPincode(userDTO.getPincode());

			if (userDTO.getState() != null && !userDTO.getState().trim().isEmpty())
				existUser.setState(userDTO.getState());

			if (userDTO.getStatus() != null && (userDTO.getStatus() == 'Y' || userDTO.getStatus() == 'N'))
				existUser.setStatus(userDTO.getStatus());

			existUser.setUpdatedDate(LocalDateTime.now()); // Always update modified timestamp

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
		String[] headers = { "User ID", "Title", "Full Name", "Gender", "Date of Birth", "PAN Number", "Annual Income",
				"Email", "Mobile No", "Alternate No", "Address", "Pincode", "City", "State", "Status" };

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

	// ================= Import USER Table in Excel =======

	@Override
	public String importExcelToUser(InputStream file) throws IOException {
		// List to collect all error messages during validation
		List<String> errorMessages = new ArrayList<>();
		List<UserImportLog> errorTab = new ArrayList<>();

		// Create workbook from the uploaded Excel file (XLSX/XLS supported)
		Workbook workbook = WorkbookFactory.create(file);
		Sheet sheet = workbook.getSheetAt(0); // Get first sheet from the file

		int totalRecords = sheet.getLastRowNum();
		Integer batchSize = 3;
		if (totalRecords > 5) {
			
			String filePath = "C:/batchuploads/";
			new File(filePath).mkdir();
			
			String fileName = "QueueFile_" + UUID.randomUUID().toString().substring(0, 4) + ".xlsx";
			String fullFilePath=filePath+fileName;
		
		   try(FileOutputStream out = new FileOutputStream(fullFilePath)){
			  workbook.write(out); 
		   }
		   FileQueue queue = new FileQueue();  
		   queue.setFilePath(fullFilePath);
		   queue.setIsProcessed("N");
		   queue.setRowCount(totalRecords);
		   queue.setRowRead(0);
		   queue.setStatus("N");
		   queue.setLastProcess(batchSize); // usually 0 if nothing has been processed yet

	        queueRepository.save(queue); // ✅ correct usage

	        workbook.close();
	        return "File queued for batch processing.";
	    }

		Row newRow = sheet.getRow(0);
		int lastColunm = newRow.getLastCellNum();
		newRow.createCell(lastColunm).setCellValue("Status");
		newRow.createCell(lastColunm + 1).setCellValue("ErrorMessage");

		// Loop over each row in the sheet
		for (Row row : sheet) {
			// Skip header and completely blank rows
			if (row.getRowNum() == 0 || isRowBlank(row))
				continue;

			User user = new User(); // Entity to store validated user data
			boolean hasError = false; // row-level validation failure
			int rowNum = row.getRowNum();

			try {
				// ----------- 1. Title -----------
				Cell cell = row.getCell(1);

				if (cell == null || cell.getCellType() != CellType.STRING) {
					errorMessages.add("row:" + rowNum + "|field:Title|msg:Missing or invalid title");
					hasError = true;
				} else {
					String val = cell.getStringCellValue().trim();
					if (val.isEmpty()) {
						errorMessages.add("row:" + rowNum + "|field:Title|msg:Empty title");
						hasError = true;
					} else {
						boolean matched = false;
						for (Title title : Title.values()) {
							if (title.name().equalsIgnoreCase(val)) {
								user.setTitle(title);
								matched = true;
								break;
							}
						}
						if (!matched) {
							errorMessages.add("row:" + rowNum + "|field:Title|msg:Invalid title value");
							hasError = true;
						}
					}
				}

				// ----------- 2. Full Name -----------
				Cell cell2 = row.getCell(2);
				if (cell2 == null || cell2.getCellType() != CellType.STRING) {
					errorMessages.add("row:" + rowNum + "|field:FullNAME|msg:Missing or invalid full name");
					hasError = true;
				} else {
					String val = cell2.getStringCellValue().trim();
					if (val.isEmpty() || !val.matches("^[A-Za-z ]+$")) {
						errorMessages.add("row:" + rowNum + "|field:FullNAME|msg:Invalid full name");
						hasError = true;
					} else {
						user.setFullName(val);
					}
				}

				// ----------- 3. Gender -----------
				Cell cell3 = row.getCell(3);
				if (cell3 == null || cell3.getCellType() != CellType.STRING) {
					errorMessages.add("row:" + rowNum + "|field:Gender|msg:Missing or invalid gender");
					hasError = true;
				} else {
					String val = cell3.getStringCellValue().trim();
					if (val.isEmpty()) {
						errorMessages.add("row:" + rowNum + "|field:Gender|msg:Empty gender");
						hasError = true;
					} else {
						boolean matched = false;
						for (Gender g : Gender.values()) {
							if (g.name().equalsIgnoreCase(val)) {
								user.setGender(g);
								matched = true;
								break;
							}
						}
						if (!matched) {
							errorMessages.add("row:" + rowNum + "|field:Gender|msg:Invalid gender value");
							hasError = true;
						}
					}
				}

				// ----------- 4. DOB -----------
				Cell dobCell = row.getCell(4);
				if (dobCell == null) {
					errorMessages.add("row:" + rowNum + "|field:DateOfBirth|msg:DOB is required");
					hasError = true;
				} else if (dobCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dobCell)) {
					user.setDob(dobCell.getLocalDateTimeCellValue().toLocalDate());
				} else {
					errorMessages.add("row:" + rowNum + "|field:DateOfBirth|msg:DOB must be a date-formatted cell");
					hasError = true;
				}

				// ----------- 5. PAN No -----------
				Cell cell5 = row.getCell(5);
				if (cell5 == null || cell5.getCellType() != CellType.STRING) {
					errorMessages.add("row:" + rowNum + "|field:PanNO|msg:Missing or invalid PAN");
					hasError = true;
				} else {
					String val = cell5.getStringCellValue().trim();
					if (!val.matches("^[A-Z]{5}[0-9]{4}[A-Z]$")) {
						errorMessages.add("row:" + rowNum + "|field:PanNO|msg:Invalid PAN format");
						hasError = true;
					} else if (userRepository.existsByPanNo(val)) {
						errorMessages.add("row:" + rowNum + "|field:PanNO|msg:Duplicate PAN number");
						hasError = true;
					} else {
						user.setPanNo(val);
					}
				}

				// ----------- 6. Annual Income -----------
				Cell cell6 = row.getCell(6);
				if (cell6 == null || cell6.getCellType() != CellType.NUMERIC) {
					errorMessages.add("row:" + rowNum + "|field:AnnualIncome|msg:Missing or invalid income");
					hasError = true;
				} else {
					double val = cell6.getNumericCellValue();
					if (val < 0) {
						errorMessages.add("row:" + rowNum + "|field:AnnualIncome|msg:Negative income not allowed");
						hasError = true;
					} else {
						user.setAnnualIncome((long) val);
					}
				}

				// ----------- 7. Email -----------
				Cell cell7 = row.getCell(7);
				if (cell7 == null || cell7.getCellType() != CellType.STRING) {
					errorMessages.add("row:" + rowNum + "|field:Email|msg:Missing or invalid email");
					hasError = true;
				} else {
					String val = cell7.getStringCellValue().trim();
					if (!val.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
						errorMessages.add("row:" + rowNum + "|field:Email|msg:Invalid email format");
						hasError = true;
					} else if (userRepository.existsByEmail(val)) {
						errorMessages.add("row:" + rowNum + "|field:Email|msg:Duplicate email");
						hasError = true;
					} else {
						user.setEmail(val);
					}
				}

				// ----------- 8. Mobile No -----------
				Cell cell8 = row.getCell(8);
				String val8 = null;
				if (cell8 == null) {
					errorMessages.add("row:" + rowNum + "|field:MobileNo|msg:Missing mobile number");
					hasError = true;
				} else if (cell8.getCellType() == CellType.STRING) {
					val8 = cell8.getStringCellValue().trim();
				} else if (cell8.getCellType() == CellType.NUMERIC) {
					val8 = BigDecimal.valueOf(cell8.getNumericCellValue()).toPlainString();
				} else {
					errorMessages.add("row:" + rowNum + "|field:MobileNo|msg:Invalid mobile cell type");
					hasError = true;
				}
				if (val8 != null && !val8.matches("^\\d{10}$")) {
					errorMessages.add("row:" + rowNum + "|field:MobileNo|msg:Invalid mobile format");
					hasError = true;
				} else if (userRepository.existsByMobileNo(val8)) {
					errorMessages.add("row:" + rowNum + "|field:MobileNo|msg:Duplicate mobile number");
					hasError = true;
				} else {
					user.setMobileNo(val8);
				}

				// ----------- 9. Alternate No (optional) -----------
				Cell cell9 = row.getCell(9);
				String val9 = null;
				if (cell9 != null) {
					if (cell9.getCellType() == CellType.STRING) {
						val9 = cell9.getStringCellValue().trim();
					} else if (cell9.getCellType() == CellType.NUMERIC) {
						val9 = BigDecimal.valueOf(cell9.getNumericCellValue()).toPlainString();
					} else {
						errorMessages
								.add("row:" + rowNum + "|field:AlternateNo|msg:Invalid alternate number cell type");
						hasError = true;
					}
					if (val9 != null && !val9.isEmpty() && !val9.matches("^\\d{10}$")) {
						errorMessages
								.add("row:" + rowNum + "|field:Al     ternateNo|msg:Invalid alternate number format");
						hasError = true;
					}
				}
				user.setAlternateNo(val9);

				// ----------- 10. Address -----------
				Cell cell10 = row.getCell(10);
				if (cell10 == null || cell10.getCellType() != CellType.STRING) {
					errorMessages.add("row:" + rowNum + "|field:Address|msg:Missing or invalid address");
					hasError = true;
				} else {
					String val = cell10.getStringCellValue().trim();
					if (val.isEmpty()) {
						errorMessages.add("row:" + rowNum + "|field:Address|msg:Empty address");
						hasError = true;
					} else {
						user.setAddress(val);
					}
				}

				// ----------- 11. Pincode -----------
				Cell cell11 = row.getCell(11);
				if (cell11 == null || cell11.getCellType() != CellType.NUMERIC) {
					errorMessages.add("row:" + rowNum + "|field:Pincode|msg:Missing or invalid pincode");
					hasError = true;
				} else {
					user.setPincode((long) cell11.getNumericCellValue());
				}

				// ----------- 12. City -----------
				Cell cell12 = row.getCell(12);
				if (cell12 == null || cell12.getCellType() != CellType.STRING) {
					errorMessages.add("row:" + rowNum + "|field:City|msg:Missing or invalid city");
					hasError = true;
				} else {
					String val = cell12.getStringCellValue().trim();
					if (val.isEmpty()) {
						errorMessages.add("row:" + rowNum + "|field:City|msg:Empty city");
						hasError = true;
					} else {
						user.setCity(val);
					}
				}

				// ----------- 13. State -----------
				Cell cell13 = row.getCell(13);
				if (cell13 == null || cell13.getCellType() != CellType.STRING) {
					errorMessages.add("row:" + rowNum + "|field:State|msg:Missing or invalid state");
					hasError = true;
				} else {
					String val = cell13.getStringCellValue().trim();
					if (val.isEmpty()) {
						errorMessages.add("row:" + rowNum + "|field:State|msg:Empty state");
						hasError = true;
					} else {
						user.setState(val);
					}
				}

				// ----------- 14. Status -----------
				Cell cell14 = row.getCell(14);
				if (cell14 == null || cell14.getCellType() != CellType.STRING) {
					errorMessages.add("row:" + rowNum + "|field:Status|msg:Missing or invalid status");
					hasError = true;
				} else {
					String val = cell14.getStringCellValue().trim();
					if (val.isEmpty()) {
						errorMessages.add("row:" + rowNum + "|field:Status|msg:Empty status");
						hasError = true;
					} else {
						user.setStatus(val.charAt(0));
					}
				}

				Cell status = row.createCell(lastColunm);
				Cell errorMessage = row.createCell(lastColunm + 1);

				// ----------- Logging Result -----------
				UserImportLog log = new UserImportLog();
				log.setTimestamp(LocalDateTime.now());

				if (!hasError) {
					userRepository.save(user); // Save valid user

					log.setErrorMessage("User added!");
					log.setErrorField("N/A");
					log.setRowNumber(rowNum);
					log.setStatus("SUCCESS");

					userImportLogRepository.save(log);

					status.setCellValue("Sucess");
					errorMessage.setCellValue("Saved SucessFull");
				} else {
					// Filter only matching row errors (starts with expected prefix)          
					List<String> rowErrors = errorMessages.stream().filter(msg -> msg.startsWith("row:" + rowNum))
							.collect(Collectors.toList());

					List<String> errorMessage1 = new ArrayList<>();

					for (String error : rowErrors) {
						String[] parts = error.split("\\|");

						if (parts.length < 3)
							continue; // Defensive check

						String field = parts[1].replace("field:", "").trim();
						String message = parts[2].replace("msg:", "").trim();

						errorMessage1.add(message);

						UserImportLog errorLog = new UserImportLog();
						errorLog.setTimestamp(LocalDateTime.now());
						errorLog.setErrorField(field);
						errorLog.setErrorMessage(message);
						errorLog.setRowNumber(rowNum);
						errorLog.setStatus("FAILED");

						errorTab.add(errorLog);
						userImportLogRepository.save(errorLog);

					}

					status.setCellValue("Failed");
					errorMessage.setCellValue(String.join(", ", errorMessage1));
				}

			} catch (Exception e) {
				// Catching any unexpected error
				errorMessages.add("Unexpected error at row " + rowNum + ": " + e.getMessage());
			}
		}
		// Ensure directory exists
		String outputDir = "C:\\ErrorFile\\";
		new File(outputDir).mkdirs();

		// Unique file name
		String fileName = "errorLog_" + UUID.randomUUID().toString().substring(0, 6) + ".xlsx";
		String filePath = outputDir + fileName;

		try (FileOutputStream uploadFile = new FileOutputStream(filePath)) {
			workbook.write(uploadFile); // Write BEFORE closing
		}

		workbook.close(); // Cleanup resource

		if (!errorTab.isEmpty()) {
			XSSFWorkbook workbookk = new XSSFWorkbook();
			try {
				XSSFSheet sheet1 = workbookk.createSheet("Error_Data");

				// Header row
				String[] headers = { "Row Number", "Field", "Error Message", "Timestamp" };
				XSSFRow headerRow = sheet1.createRow(0);
				for (int i = 0; i < headers.length; i++) {
					headerRow.createCell(i).setCellValue(headers[i]);
				}

				// Data rows
				int rowIndex = 1;
				for (UserImportLog errorLog : errorTab) {
					XSSFRow row = sheet1.createRow(rowIndex++);
					row.createCell(0).setCellValue(errorLog.getRowNumber());
					row.createCell(1).setCellValue(errorLog.getErrorField());
					row.createCell(2).setCellValue(errorLog.getErrorMessage());
					row.createCell(3).setCellValue(errorLog.getTimestamp().toString());
				}

				throw new RuntimeException("Check error on this : " + filePath + fileName);

			} finally {
				try {
					workbookk.close(); // ✅ Close only once, and only if not already closed
				} catch (IOException e) {
					System.err.println("Error closing workbook: " + e.getMessage());
				}
			}
		}
		
		return "All proposers saved successfully";

	}
	
	@Override
	@Scheduled(fixedRate = 3000)
	public void processExcelBatch() throws IOException {
	    List<FileQueue> data = queueRepository.findByIsProcessed("N");

	    for (FileQueue queueData : data) {
	        try (FileInputStream fis = new FileInputStream(queueData.getFilePath());
	             Workbook workbook = new XSSFWorkbook(fis)) {

	            Sheet sheet = workbook.getSheetAt(0);
	            int rowStart = queueData.getRowRead() + 1;
	            int totalRows = queueData.getRowCount();
	            int batchSize = 3;

	            for (int i = rowStart; i <= totalRows && i < rowStart + batchSize; i++) {
	                Row row = sheet.getRow(i);
	                if (row == null || isRowBlank(row)) continue;

	                List<String> errorMessages = new ArrayList<>();
	                List<UserImportLog> errorTab = new ArrayList<>();
	                User user = new User();
	                boolean hasError = false;

	                int rowNum = row.getRowNum();

	                try {
	                	// ----------- 1. Title -----------
	    				Cell cell = row.getCell(1);

	    				if (cell == null || cell.getCellType() != CellType.STRING) {
	    					errorMessages.add("row:" + rowNum + "|field:Title|msg:Missing or invalid title");
	    					hasError = true;
	    				} else {
	    					String val = cell.getStringCellValue().trim();
	    					if (val.isEmpty()) {
	    						errorMessages.add("row:" + rowNum + "|field:Title|msg:Empty title");
	    						hasError = true;
	    					} else {
	    						boolean matched = false;
	    						for (Title title : Title.values()) {
	    							if (title.name().equalsIgnoreCase(val)) {
	    								user.setTitle(title);
	    								matched = true;
	    								break;
	    							}
	    						}
	    						if (!matched) {
	    							errorMessages.add("row:" + rowNum + "|field:Title|msg:Invalid title value");
	    							hasError = true;
	    						}
	    					}
	    				}

	    				// ----------- 2. Full Name -----------
	    				Cell cell2 = row.getCell(2);
	    				if (cell2 == null || cell2.getCellType() != CellType.STRING) {
	    					errorMessages.add("row:" + rowNum + "|field:FullNAME|msg:Missing or invalid full name");
	    					hasError = true;
	    				} else {
	    					String val = cell2.getStringCellValue().trim();
	    					if (val.isEmpty() || !val.matches("^[A-Za-z ]+$")) {
	    						errorMessages.add("row:" + rowNum + "|field:FullNAME|msg:Invalid full name");
	    						hasError = true;
	    					} else {
	    						user.setFullName(val);
	    					}
	    				}

	    				// ----------- 3. Gender -----------
	    				Cell cell3 = row.getCell(3);
	    				if (cell3 == null || cell3.getCellType() != CellType.STRING) {
	    					errorMessages.add("row:" + rowNum + "|field:Gender|msg:Missing or invalid gender");
	    					hasError = true;
	    				} else {
	    					String val = cell3.getStringCellValue().trim();
	    					if (val.isEmpty()) {
	    						errorMessages.add("row:" + rowNum + "|field:Gender|msg:Empty gender");
	    						hasError = true;
	    					} else {
	    						boolean matched = false;
	    						for (Gender g : Gender.values()) {
	    							if (g.name().equalsIgnoreCase(val)) {
	    								user.setGender(g);
	    								matched = true;
	    								break;
	    							}
	    						}
	    						if (!matched) {
	    							errorMessages.add("row:" + rowNum + "|field:Gender|msg:Invalid gender value");
	    							hasError = true;
	    						}
	    					}
	    				}

	    				// ----------- 4. DOB -----------
	    				Cell dobCell = row.getCell(4);
	    				if (dobCell == null) {
	    					errorMessages.add("row:" + rowNum + "|field:DateOfBirth|msg:DOB is required");
	    					hasError = true;
	    				} else if (dobCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dobCell)) {
	    					user.setDob(dobCell.getLocalDateTimeCellValue().toLocalDate());
	    				} else {
	    					errorMessages.add("row:" + rowNum + "|field:DateOfBirth|msg:DOB must be a date-formatted cell");
	    					hasError = true;
	    				}

	    				// ----------- 5. PAN No -----------
	    				Cell cell5 = row.getCell(5);
	    				if (cell5 == null || cell5.getCellType() != CellType.STRING) {
	    					errorMessages.add("row:" + rowNum + "|field:PanNO|msg:Missing or invalid PAN");
	    					hasError = true;
	    				} else {
	    					String val = cell5.getStringCellValue().trim();
	    					if (!val.matches("^[A-Z]{5}[0-9]{4}[A-Z]$")) {
	    						errorMessages.add("row:" + rowNum + "|field:PanNO|msg:Invalid PAN format");
	    						hasError = true;
	    					} else if (userRepository.existsByPanNo(val)) {
	    						errorMessages.add("row:" + rowNum + "|field:PanNO|msg:Duplicate PAN number");
	    						hasError = true;
	    					} else {
	    						user.setPanNo(val);
	    					}
	    				}

	    				// ----------- 6. Annual Income -----------
	    				Cell cell6 = row.getCell(6);
	    				if (cell6 == null || cell6.getCellType() != CellType.NUMERIC) {
	    					errorMessages.add("row:" + rowNum + "|field:AnnualIncome|msg:Missing or invalid income");
	    					hasError = true;
	    				} else {
	    					double val = cell6.getNumericCellValue();
	    					if (val < 0) {
	    						errorMessages.add("row:" + rowNum + "|field:AnnualIncome|msg:Negative income not allowed");
	    						hasError = true;
	    					} else {
	    						user.setAnnualIncome((long) val);
	    					}
	    				}

	    				// ----------- 7. Email -----------
	    				Cell cell7 = row.getCell(7);
	    				if (cell7 == null || cell7.getCellType() != CellType.STRING) {
	    					errorMessages.add("row:" + rowNum + "|field:Email|msg:Missing or invalid email");
	    					hasError = true;
	    				} else {
	    					String val = cell7.getStringCellValue().trim();
	    					if (!val.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
	    						errorMessages.add("row:" + rowNum + "|field:Email|msg:Invalid email format");
	    						hasError = true;
	    					} else if (userRepository.existsByEmail(val)) {
	    						errorMessages.add("row:" + rowNum + "|field:Email|msg:Duplicate email");
	    						hasError = true;
	    					} else {
	    						user.setEmail(val);
	    					}
	    				}

	    				// ----------- 8. Mobile No -----------
	    				Cell cell8 = row.getCell(8);
	    				String val8 = null;
	    				if (cell8 == null) {
	    					errorMessages.add("row:" + rowNum + "|field:MobileNo|msg:Missing mobile number");
	    					hasError = true;
	    				} else if (cell8.getCellType() == CellType.STRING) {
	    					val8 = cell8.getStringCellValue().trim();
	    				} else if (cell8.getCellType() == CellType.NUMERIC) {
	    					val8 = BigDecimal.valueOf(cell8.getNumericCellValue()).toPlainString();
	    				} else {
	    					errorMessages.add("row:" + rowNum + "|field:MobileNo|msg:Invalid mobile cell type");
	    					hasError = true;
	    				}
	    				if (val8 != null && !val8.matches("^\\d{10}$")) {
	    					errorMessages.add("row:" + rowNum + "|field:MobileNo|msg:Invalid mobile format");
	    					hasError = true;
	    				} else if (userRepository.existsByMobileNo(val8)) {
	    					errorMessages.add("row:" + rowNum + "|field:MobileNo|msg:Duplicate mobile number");
	    					hasError = true;
	    				} else {
	    					user.setMobileNo(val8);
	    				}

	    				// ----------- 9. Alternate No (optional) -----------
	    				Cell cell9 = row.getCell(9);
	    				String val9 = null;
	    				if (cell9 != null) {
	    					if (cell9.getCellType() == CellType.STRING) {
	    						val9 = cell9.getStringCellValue().trim();
	    					} else if (cell9.getCellType() == CellType.NUMERIC) {
	    						val9 = BigDecimal.valueOf(cell9.getNumericCellValue()).toPlainString();
	    					} else {
	    						errorMessages
	    								.add("row:" + rowNum + "|field:AlternateNo|msg:Invalid alternate number cell type");
	    						hasError = true;
	    					}
	    					if (val9 != null && !val9.isEmpty() && !val9.matches("^\\d{10}$")) {
	    						errorMessages
	    								.add("row:" + rowNum + "|field:Al     ternateNo|msg:Invalid alternate number format");
	    						hasError = true;
	    					}
	    				}
	    				user.setAlternateNo(val9);

	    				// ----------- 10. Address -----------
	    				Cell cell10 = row.getCell(10);
	    				if (cell10 == null || cell10.getCellType() != CellType.STRING) {
	    					errorMessages.add("row:" + rowNum + "|field:Address|msg:Missing or invalid address");
	    					hasError = true;
	    				} else {
	    					String val = cell10.getStringCellValue().trim();
	    					if (val.isEmpty()) {
	    						errorMessages.add("row:" + rowNum + "|field:Address|msg:Empty address");
	    						hasError = true;
	    					} else {
	    						user.setAddress(val);
	    					}
	    				}

	    				// ----------- 11. Pincode -----------
	    				Cell cell11 = row.getCell(11);
	    				if (cell11 == null || cell11.getCellType() != CellType.NUMERIC) {
	    					errorMessages.add("row:" + rowNum + "|field:Pincode|msg:Missing or invalid pincode");
	    					hasError = true;
	    				} else {
	    					user.setPincode((long) cell11.getNumericCellValue());
	    				}

	    				// ----------- 12. City -----------
	    				Cell cell12 = row.getCell(12);
	    				if (cell12 == null || cell12.getCellType() != CellType.STRING) {
	    					errorMessages.add("row:" + rowNum + "|field:City|msg:Missing or invalid city");
	    					hasError = true;
	    				} else {
	    					String val = cell12.getStringCellValue().trim();
	    					if (val.isEmpty()) {
	    						errorMessages.add("row:" + rowNum + "|field:City|msg:Empty city");
	    						hasError = true;
	    					} else {
	    						user.setCity(val);
	    					}
	    				}

	    				// ----------- 13. State -----------
	    				Cell cell13 = row.getCell(13);
	    				if (cell13 == null || cell13.getCellType() != CellType.STRING) {
	    					errorMessages.add("row:" + rowNum + "|field:State|msg:Missing or invalid state");
	    					hasError = true;
	    				} else {
	    					String val = cell13.getStringCellValue().trim();
	    					if (val.isEmpty()) {
	    						errorMessages.add("row:" + rowNum + "|field:State|msg:Empty state");
	    						hasError = true;
	    					} else {
	    						user.setState(val);
	    					}
	    				}

	    				// ----------- 14. Status -----------
	    				Cell cell14 = row.getCell(14);
	    				if (cell14 == null || cell14.getCellType() != CellType.STRING) {
	    					errorMessages.add("row:" + rowNum + "|field:Status|msg:Missing or invalid status");
	    					hasError = true;
	    				} else {
	    					String val = cell14.getStringCellValue().trim();
	    					if (val.isEmpty()) {
	    						errorMessages.add("row:" + rowNum + "|field:Status|msg:Empty status");
	    						hasError = true;
	    					} else {
	    						user.setStatus(val.charAt(0));
	    					}
	    				}


	                    // ---- Finalizing per-row result ----
	                    UserImportLog log = new UserImportLog();
	                    log.setTimestamp(LocalDateTime.now());
	                    log.setRowNumber(rowNum);

	                    if (!hasError) {
	                        userRepository.save(user);
	                        log.setErrorMessage("User added!");
	                        log.setErrorField("N/A");
	                        log.setStatus("SUCCESS");
	                    } else {
	                        List<String> rowErrors = errorMessages.stream()
	                                .filter(msg -> msg.startsWith("row:" + rowNum))
	                                .collect(Collectors.toList());

	                        for (String error : rowErrors) {
	                            String[] parts = error.split("\\|");
	                            if (parts.length >= 3) {
	                                UserImportLog errLog = new UserImportLog();
	                                errLog.setTimestamp(LocalDateTime.now());
	                                errLog.setRowNumber(rowNum);
	                                errLog.setErrorField(parts[1].replace("field:", "").trim());
	                                errLog.setErrorMessage(parts[2].replace("msg:", "").trim());
	                                errLog.setStatus("FAILED");
	                                userImportLogRepository.save(errLog);
	                            }
	                        }
	                    }
	                } catch (Exception e) {
	                    errorMessages.add("Unexpected error at row " + rowNum + ": " + e.getMessage());
	                }

	                queueData.setRowRead(i);
	            }

	            if (queueData.getRowRead() >= totalRows) {
	                queueData.setIsProcessed("Y");
	                queueData.setStatus("Y");
	            }

	            queueRepository.save(queueData);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}


	// check if a row is completely empty (used to stop processing trailing empty
	// rows)
	private boolean isRowBlank(Row row) {
		if (row == null)
			return true;
		for (Cell cell : row) {
			if (cell != null && cell.getCellType() != CellType.BLANK) {
				if (cell.getCellType() == CellType.STRING && !cell.getStringCellValue().trim().isEmpty())
					return false;
				if (cell.getCellType() != CellType.STRING)
					return false;
			}
		}
		return true;
	}
}
