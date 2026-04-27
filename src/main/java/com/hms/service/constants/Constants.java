package com.hms.service.constants;


public class Constants {

	
	public static final String BUCKET = "srdemandsupportingdocumentsbucket";
	public static final String UNDER_SCORE = "_";
    public static final String USER_TYPE_REQUIRED = "User Type is required";
    public static final String FIRST_NAME_REQUIRED = "First Name is required";
    public static final String FIRST_NAME_SIZE = "First Name must be between 2 and 50 characters";
    public static final String FIRST_NAME_INVALID = "First Name should contain only alphabets";
    public static final String LAST_NAME_REQUIRED = "Last Name is required";
    public static final String LAST_NAME_SIZE = "Last Name must be between 1 and 50 characters";
    public static final String LAST_NAME_INVALID = "Last Name should contain only alphabets";
    public static final String EMPLOYEE_ID_REQUIRED = "Employee ID is required";
    public static final String EMAIL_REQUIRED = "Email is required";
    public static final String EMAIL_INVALID = "Invalid email format";
    public static final String MOBILE_REQUIRED = "Mobile number is required";
    public static final String MOBILE_INVALID = "Mobile number must contain only digits and optional +";
    public static final String ALT_MOBILE_INVALID = "Alternate number must contain only digits";
    public static final String DOB_REQUIRED = "Date of Birth is required";
    public static final String EMPLOYMENT_TYPE_REQUIRED = "Employment Type is required";
    public static final String BUSINESS_UNIT_REQUIRED = "Business Unit is required";
    public static final String DEPARTMENT_REQUIRED = "Department is required";
    public static final String ROLE_REQUIRED = "Role is required";
    public static final Integer MIN_AGE = 18;
    public static final int PASSWORD_LENGTH = 10;
    public static final int PIN_LENGTH = 6;
    public static final String NUMERIC_CODE = "0123456789";
    public static final String SPECIAL_CHARS_CODE = "@#$%";
    public static final String ALPHABETS_CAPS_CODE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String ALPHABETS_SMALL_CODE = "abcdefghijklmnopqrstuvwxyz";
	public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
	public static final String EMPLOYEE_ID_ALREADY_EXISTS = "Employee ID already exists";
	public static final String INVALID_DOB_FORMAT =  "Invalid Date of Birth format. Expected format: yyyy-MM-dd";
	public static final String USER_AGE_MUST_BE_ABOVE_18 = "User must be at least 18 years old";
	public static final String ALTERNATIVE_NUMBER_MUST_BE_DIFFERENT = "Alternate number cannot be same as mobile number";
	public static final String USER_CREATED_SUCCESSFULLY = "User created successfully";
	public static final String USER_FETCHED = "Users fetched";
	public static final String USER_NOT_FOUND = "User not found";
	public static final String STATUS_UPDATED_SUCCESSFULLY = "Status updated successfully";
	public static final String BUSINESS_UNITS_FETCHED_SUCCESSFULLY = "Business Units fetched successfully";
	public static final String DEPARTMENTS_FETCHED_SUCCESSFULLY = "Departments fetched successfully";
	public static final String EMPLOYMENT_TYPE_FETCHED_SUCCESSFULLY = "Employment types fetched successfully";
	public static final String ROLES_FETCHED_SUCCESSFULLY = "Roles fetched successfully";
	public static final String DESCRIPTION_IS_REQUIRED = "Description is required";
	public static final String NO_MODULES_FOUND = "Modules not found";
	public static final String MODULE_FETCH_SUCCESS = "Module Details Fetched sucessfully";
	public static final String MODULE_NAME_ALREADY_EXISTS = "Module already exists";
	public static final String MODULE_ADDED_SUCCESSFULLY = "Module Added sucessfully";
	public static final String ROLE_NAME_ALREADY_EXISTS = "Role name already exits";
	public static final String ROLE_ADDED_SUCCESSFULLY = "Role added successfully";
	public static final String MODULE_REQUIRED = "Module name required";

	public static final String MODULE_ID_REQUIRED = "Module id required";
	public static final String ROLES_PERMISSION_FETCHED_SUCCESSFULLY = "Roles and permission are fetched successfully";
	public static final String ROLE_PERMISSION_DETAILS_FETCHED_SUCCESSFULLY = "Role permission details fetched successfully";
	public static final String ROLE_PERMISSION_UPDATED_SUCCESSFULLY = "Role Permission updated sucessfully";


	public static final String SENIORITY_FETCHED_SUCCESSFULLY = "Seniority level fetched successfully";
	public static final String TRAVEL_REQUIREMENTS_FETCHED_SUCCESSFULLY = "Travel Requirements Fteched Successfully";
	public static final String USER_TYPES_FETCHED_SUCCESSFULLY = "user types fetched successfully";
	public static final String ROLE_NAME_IS_REQUIRED = "Role name is required";
	public static final String USER_UPDATED_SUCCESSFULLY = "User updated successfully";
	public static final String INVALID_CREDENTIALS = "Invalid credentials";
	public static final String PIN_IS_REQUIRED = "Pin is required";
	public static final String PASSWORD_IS_REQUIRED = "Password is required";
	public static final String CHANNEL_IS_REQUIRED = "Channel is required";
	public static final String SUCCESS =  "success";
	public static final String SR_ID_IS_REQUIRED = "SR ID is required";
	public static final String SR_ID_CANNOT_BE_NULL_OR_EMPTY = "SR Id cannot be null or empty";
	public static final String NO_DATA_FOUND = "No data found";
	public static final String INVALID_SR_ID_IS = "Invalid SR ID: ";
	public static final String SR_DATA_FETCHED_SUCCESSFULLY = "SR data fetched successfully";
	public static final String FAILED_TO_FETCH_SR_DATA = "Failed to fetch SR data";
	public static final String PAGE = "page";
	public static final String SIZE = "size";
	public static final String CREATED_ON = "createdOn";
	public static final String NO_RECORDS_FOUND_IN_THE_DATABASE = "No records found in the database";
	public static final String SR_ID = "srId";
	public static final String JOB_TITLE = "jobTitle";
	public static final String CREATED_DATE = "createdDate";
	public static final String APPROVED = "APPROVED";
	public static final String SUBMITTED = "SUBMITTED";
	public static final String DRAFT = "DRAFT";
	public static final String STATUS = "status";
	public static final String CONTENT = "content";
	public static final String CURRENT_PAGE = "currentPage";
	public static final String TOTAL_PAGES = "totalPages";
	public static final Object TOTAL_ELEMENTS = "totalElements";
	public static final String INVALID_BUSINESS_UNIT_ID = "Invalid business unit id";
	public static final String INVALID_DEPARTMENT_ID = "Invalid deparment id";
	public static final String WITHIN_RANGE = "Within Range";
	public static final String COULD_NOT_ATTACH_FILE = "We couldn’t attach the file. Please try again.";
	public static final String MAIL_FAILURE = "Failed to send the email";
	public static final String FORGOT_PASSWORD_SUBJECT = "Your New Login Credentials";

    public static final String FORGOT_PASSWORD_BODY =
            "<html><body>"
            + "<p>Dear %s,</p>"
            + "<p>Your password has been reset successfully.</p>"
            + "<p><b>Username:</b> %s</p>"
            + "<p><b>Password:</b> %s</p>"
            + "<p><b>PIN:</b> %s</p>"
            + "<br/>"
            + "<p>Please login and change your credentials immediately.</p>"
            + "<br/>"
            + "<p>Regards,<br/>Infospoke</p>"
            + "</body></html>";

	
}


