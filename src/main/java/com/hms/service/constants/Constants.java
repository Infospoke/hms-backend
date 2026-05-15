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
    public static final String MOBILE_INVALID = "Mobile number must contain only digits";
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
	public static final String APPROVED = "Approved";
	public static final String SUBMITTED = "Submitted";
	public static final String DRAFT = "Draft";
	public static final String STATUS = "status";
	public static final String CONTENT = "content";
	public static final String CURRENT_PAGE = "currentPage";
	public static final String TOTAL_PAGES = "totalPages";
	public static final Object TOTAL_ELEMENTS = "totalElements";
	public static final String INVALID_BUSINESS_UNIT_ID = "Invalid business unit id";
	public static final String INVALID_DEPARTMENT_ID = "Invalid deparment id";
	public static final String WITHIN_RANGE = "Within Range";
	public static final String CANDIDATE_ALREADY_EXISTS = "Candidate already exists";
	public static final String VALID_TOKEN = "Valid token";
	public static final String STATUS_CODE_SUCCESS = "1";
	public static final String OFFER_SENT = "Offer Sent";
	public static final String BUCKET_FOLDER = "upload-documents/";
	public static final String REGION = "Asia/Kolkata";
	public static final String CANDIDATE_JOINED_SUBJECT = "Kindly arrange IT assets for new employee";
	public static final String IT_MAIL_BODY = "<p>Dear IT Team</p>"
			+ "<p>This is to inform you that a new employee has joined our organization.</p>"
			+ "<p> Kindly arrange the required IT assets and system access for the employee at the earliest.</p>"
			+ "<p> <b>Employee Details</b></p>" + "<b><ul><li><p>First name : %s </p></li>"
			+ "<li><p>Job Role : %s </p></li></ul></b>" + "<br>" + "<br>" + "<br>" + "<br>"
			+ "<p>Please let us know if any additional information is required from our end to proceed with the setup.</p>"
			+ "<p>Best regards,<br/>" + "<b>HR Team</b><br/>" + "<b>Infospoke Integrated Solutions LLP</b></p>";

	public static final String CAREERS_INDIA = "careers@infospoke.in";
	public static final String CAREERS_USA = "careers@infospokellc.com";
	public static final String INDIA = "India";
	public static final String USA = "USA";
	public static final String NOREPLY_INDIA = "noreply@infospoke.in";
	public static final String NOREPLY_USA = "noreply@infospokellc.com";
	public static final String CANDIDATE_CREATION_SUBJECT = "Congratulations! You have moved ahead in our recruitment process";
	public static final String OFFER_LETTER = "offerLetter_";

	public static final String OFFER_LETTER_MAIL_BODY = "<p>Dear <b>%s</b>,</p>" + "<p>Congratulations!</p>"
			+ "<p>We are pleased to offer you the position of "
			+ "<b>%s</b> at <b>Infospoke Integrated Solutions LLP</b>.</p>"
			+ "<p>Your joining date will be communicated shortly by our HR team.</p>"
			+ "<p>Please find the offer details attached in this email.</p>" + "<br/>"
			+ "<p>We look forward to having you on our team.</p>" + "<p>Best regards,<br/>" + "<b>HR Team</b><br/>"
			+ "<b>Infospoke Integrated Solutions LLP</b></p>";

	public static final String CANDIDATE_NOT_FOUND = "Candidate not found";
	public static final String CANDIDATE_UPDATED_SUCCESSFULLY = "Candidate updated successfully";
	public static final String CANDIDATE_DELETED_SUCCESSFULLY = "Candidate deleted successfully";
	public static final String FAILED_TO_DELETE_PRE_ONBOARDING_DOCUMENTS = "Failed to delete Pre OnBoarding documents";
	public static final String CANDIDATE_PRE_ONBOARDED_SUCCESSFULLY = "Candidate pre-onboarded successfully";
	public static final String PRE_ONBOARDING_ALLOWED_ONLY_FOR_APPROVED_CANDIDATES = "Pre-onboarding allowed only for APPROVED candidates";
	public static final String CANDIDATE_ALREADY_PREONBOARDED = "The Candidate is already pre-onboarded..!";
	public static final String BGV_CLEARED = "Background Verification Cleared";
	public static final String BGV_REJECTED = "Background Verification Rejected";
	public static final String BGV_INITIATED = "Background Verification Initiated";
	public static final String REJECTED = "Rejected";
	public static final String JOINED = "Joined";
	public static final String OFFER_REJECTED = "Offer Rejected";
	public static final String IT_MAIL_ID = "satya.k@infospoke.in";
	public static final String STATUS_CODE_FAILURE = "0";
	public static final String CTC = "ctc";
	public static final String OFFERLETTER = "offerLetter";
	public static final String BGV_FILE = "bgvFile";
	public static final String ISSUED_DATE = "issuedDate";
	public static final String VIEW = "View";
	public static final String INVALID_FILE_TYPE = "Invalid file type";
	public static final String CONVERTED_FROM_APPLICANT_TO_CANDIDATE = " converted from applicant to candidate.";
	public static final String JOINED_IN_THE_ORGANIZATION = " joined in the Organization";
	public static final String FAILED_TO_DELETE_FILE_FROM_MINIO = "Failed to delete file from MinIO";
	public static final String FAILED_TO_DOWNLOAD_FILE_FROM_MINIO = "Failed to delete file from MinIO";
	public static final String CANDIDATE_CREATION = "Candidate created successfully. Offer letter sent.";
	public static final String ACCEPTED = "Accepted";
	public static final String EXCEPTION_OCCURED = "Exception occured";
	public static final String ONBOARDING = "Pre Onboarding";
	public static final String ORGANISATION_NAME = "Organization name";
	public static final String RELIEVING_LETTER = "relievingLetter_";
	public static final String EMPTY_STRING = " ";
	public static final String PAY_SLIP = "payslip";
	public static final String FAILED_TO_PROCESS_EXPERIENCED_DETAILS = "Failed to process experience details";
	public static final String AADHAR = "aadhar";
	public static final String PREONBOARDING_INITIATED_FOR = "Pre-Onboarding initiated for ";
	public static final String EDUCATION = "education";
	public static final String FAILED_TO_UPLOAD_PAYSLIPS = "Failed to upload payslips";
	public static final String FAILED_TO_UPLOAD_DOCUMENTS = "Failed to upload documents";
	public static final String FIRST_MONTH = "1";
	public static final String ZERO_FILTER = "0";
	public static final String SECOND_MONTH = "2";
	public static final String THIRD_MONTH = "3";
	public static final String CUSTOM_FILTERING = "5";
	public static final String WEEK_FILTERING = "4";
	public static final String PNG = "png";
	public static final String JPG = "jpg";
	public static final String JPEG = "jpeg";
	public static final String PDF = "pdf";
	public static final String JOB_CODE_ALREADY_EXISTS = "Job code already exists";
	public static final String JOB_ADDED_SUCCESSFULLY = "Job added sucessfully";
	public static final String NO_JOB_FOUND = "No job found";
	public static final String JOB_DELETED_SUCCESSFULLY = "Job deleted successfully";
	public static final String JOB_UPDATED_SUCCESSFULLY = "Job details updated successfully";
	public static final String JOB_ID = "jobId";
	public static final String EXPERIENCE = "Experience";
	public static final String NO_QUESTION_FOUND_FOR_SKILL_ID = "No question found for skillId=";
	public static final String EXPERIENCE_LEVEL = ", expLevel=";
	public static final String WEIGHTAGE = ", weightage=";
	public static final String SHORTLISTED = "SHORTLISTED";
	public static final String NOT_SHORTLISTED = "NOT SHORTLISTED";
	public static final String APPLICANTS_FETCHING_SUCCESS = "Applicant records loaded";
	public static final String APPLICANTS_FETCHING_FAILURE = "Something went wrong while loading";
	public static final String INTERVIEW = "INTERVIEW";
	public static final String SCREENED = "SCREENED";
	public static final String APPLIED = "APPLIED";
	public static final String OFFER = "OFFER";
	public static final String HIRED = "HIRED";
	public static final String UNABLE_TO_FETCH = "Unable to fetch applicant details for id : ";
	public static final String JOB_MAPPED_TO_APPLICANT_NOT_FOUND = "Job mapped to the applicant not found. jobId: ";
	public static final String NO_APPLICANTS_FOUND = "No applicants found";

	public static final String INVALID_JOB_TITLE = "Invalid job title";
	public static final String INVALID_JOB_CODE = "Invalid job code";
	public static final String JOB = "Job ";
	public static final String WAS_PUBLISHED = " was published";
	public static final String JOB_CODE = "jobCode";
	public static final String FROM_DATE = "fromDate";
	public static final String TO_DATE = "toDate";
	public static final String MONTH_CODE = "monthCode";
	public static final String COULD_NOT_ATTACH_FILE = "We couldn’t attach the file. Please try again.";
	public static final String MAIL_FAILURE = "Failed to send the email";
	public static final String FORGOT_PASSWORD_SUBJECT = "Your New Login Credentials";

	public static final String FORGOT_PASSWORD_BODY =
	        "<html><body>"
	        + "<p>Dear %s,</p>"

	        + "<p>This is to inform you that your password for the Nexus HMS Portal has been successfully reset.</p>"

	        + "<p>Please use the temporary password below to log in:</p>"

	        + "<p><b>Username:</b> %s<br/>"
	        + "<b>Temporary Password:</b> %s</p>"

	        + "<p>You are required to log in and change your password immediately. "
	        + "This temporary password is valid for a limited time and will expire after first use or as per system policy.</p>"

	        + "<p><b>Security Advisory:</b></p>"
	        + "<ul>"
	        + "<li>Do not share your password with anyone</li>"
	        + "<li>Ensure your new password complies with the organization’s security standards</li>"
	        + "<li>If you did not initiate this request, please report it immediately</li>"
	        + "</ul>"

	        + "<p>For any assistance, please contact the IT Support Team </p>"

	        + "<br/>"
	        + "<p>Regards,<br/>IT Support Team<br/>Nexus HMS</p>"

	        + "</body></html>";
    
	public static final String FORGOT_PIN_BODY =
	        "<html><body>"
	        + "<p>Dear %s,</p>"

	        + "<p>This is to inform you that your Mobile PIN for accessing the Nexus HMS application has been successfully reset.</p>"

	        + "<p>Please find your temporary Mobile PIN below:</p>"

	        + "<p><b>Username:</b> %s<br/>"
	        + "<b>Temporary Mobile PIN:</b> %s</p>"

	        + "<p>You are required to log in to the mobile application and update your PIN immediately. "
	        + "This temporary PIN is valid for a limited duration and will expire after first use or as per system policy.</p>"

	        + "<p><b>Security Advisory:</b></p>"
	        + "<ul>"
	        + "<li>Do not share your Mobile PIN with anyone</li>"
	        + "<li>Avoid using easily guessable PINs (e.g., 1234, birth dates)</li>"
	        + "<li>If you did not initiate this request, please report it immediately to the IT Support Team</li>"
	        + "</ul>"

	        + "<p>For any assistance, please contact IT Support </p>"

	        + "<br/>"
	        + "<p>Regards,<br/>IT Support Team<br/>Nexus HMS</p>"

	        + "</body></html>";
    		
    		
	public static final String USER_CREATED_MAIL_SUBJECT = "User Account Created - HMS";
	public static final String USER_CREATED_MAIL_BODY =
		    "<div style='font-family:Segoe UI,Arial,sans-serif; background-color:#f5f7fa; padding:20px;'>" +

		    "<div style='max-width:600px; margin:auto; background:#ffffff; border-radius:10px; " +
		    "box-shadow:0 2px 8px rgba(0,0,0,0.1); padding:30px;'>" +

		    "<p>Dear <b>%s</b>,</p>" +

		    "<p>Greetings from the HR Team at <b>Nexus HMS</b>.</p>" +

		    "<p>This is to inform you that your user account for the <b>Nexus HMS Portal</b> has been successfully created.</p>" +

		    "<p>Please find your system-generated login credentials below:</p>" +

		    "<div style='background:#f1f3f6; padding:20px; border-radius:8px; margin:20px 0;'>" +

		    "<p style='margin:8px 0;'><b>Username:</b> %s</p>" +
		    "<p style='margin:8px 0;'><b>Temporary Password:</b> %s</p>" +
		    "<p style='margin:8px 0;'><b>Temporary PIN:</b> %s</p>" +

		    "</div>" +

		    "<p style='color:#C0392B;'><b>Note:</b> These credentials are valid only for 24 hours from the time of issuance.</p>" +

		    "<p>You are required to log in using the above credentials and complete the initial setup process. " +
		    "For security compliance, you must change your password and PIN upon first login.</p>" +

		    "<p>Please ensure that your new credentials meet the organization’s security standards and are kept strictly confidential.</p>" +

		    "<p>If you encounter any issues while accessing the portal, please contact the HR/IT Support Team at " +
		    ".</p>" +

		    "<p>We wish you a successful and rewarding journey with Nexus HMS.</p>" +

		    "<br>" +
		    "<p>Warm regards,<br><b>HR Team</b><br>Nexus HMS</p>" +

		    "</div></div>";
	public static final String EMPLOYEE_ID_SIZE = "Employee id must be between 1 and 4 characters";
	public static final String INVALID_ROLE_ID = "Invalid role id";
	public static final String INVALID_DEPARTMENT_FOR_BUSINESS_UNIT = "Invalid departmet for bussiness id";
	public static final String ROLE_NOT_BELONG_TO_DEPARTMENT = "Role id doesnot belong to this department";
	public static final String PENDING = "Pending";
	
	public static final String SR_SUBMITTED_MAIL_BODY = "<html><body>"
			+ "<p>Greetings</p>"
			+ "<p>Your Staffing Requisition (SR) with ID <b>%s</b> has been successfully submitted.</p>"
			+ "<p>We will review your request and get back to you shortly.</p>"
			+ "<br/>"
			+ "<p>Best regards,<br/>HMS Team</p>"
			+ "</body></html>";
	
	public static final String SR_SUBMITTED_MAIL_SUBJECT = "SR Submitted Successfully";
	
	public static final String SR_TO_BE_APPROVED_MAIL_BODY = "<html><body>"
			+ "<p>Greetings</p>"
			+ "<p>The Staffing Requisition (SR) with ID <b>%s</b> is pending your approval.</p>"
			+ "<p>Please review the request and take the necessary action at your earliest convenience.</p>"
			+ "<br/>"
			+ "<p>Best regards,<br/>HMS Team</p>"
			+ "</body></html>";
	
	public static final String SR_TO_BE_APPROVED_NOTIFY = "%s is awaiting your approval.";
	public static final String SR_APPROVED_NOTIFY = "%s has been approved by %s.";
	public static final String SR_REJECTED_NOTIFY = "%s has been rejected by %s.";
	
	public static final String CHAIN_CREATED_SUCESSFULLY_MAIL_BODY = "<html><body>"
			+ "<p>Greetings</p>"
			+ "<p>Chain sucessfully created  with ID <b>%s</b>for the <b>%s</b></p>"
			+ "<p>We will review your request and get back to you shortly.</p>"
			+ "<br/>"
			+ "<p>Best regards,<br/>HMS Team</p>"
			+ "</body></html>";
	
	public static final String CHAIN_CREATED_MAIL_SUBJECT = "chain created  Successfully";
	
	public static final String CHAIN_TO_BE_APPROVED= "<html><body>"
			+ "<p>Greetings</p>"
			+ "<p>Chain sucessfully created  with ID <b>%s</b>for the <b>%s</b></p>"
			+ "<p>Waiting for your approval.</p>"
			+ "<br/>"
			+ "<p>Best regards,<br/>HMS Team</p>"
			+ "</body></html>";
	public static final String CHAIN_APPROVED_MAIL_SUBJECT = "chain created waiting for your approval";
	
	public static final String CHAIN_REJECTED_MAIL_SUBJECT = "Approval Chain Creation Rejected";

	public static final String CHAIN_APPROVER_CONFIRMATION_SUBJECT = "You have approved an Approval Chain";

	public static final String CHAIN_REJECTION_CONFIRMATION_SUBJECT = "You have rejected an Approval Chain";

	public static final String CHAIN_DEACTIVATED_MAIL_SUBJECT = "Approval Chain Deactivated Successfully";

	public static final String CHAIN_DEACTIVE_REJECTED_MAIL_SUBJECT = "Approval Chain Deactivation Rejected";

	public static final String CHAIN_DEACTIVE_APPROVER_SUBJECT = "You approved Deactivation Request";

	public static final String CHAIN_DEACTIVE_REJECTION_CONFIRMATION_SUBJECT = "You rejected Deactivation Request";

	public static final String CHAIN_DEACTIVE_REQUEST_MAIL_SUBJECT = "Approval Chain Deactivation Request";
	
	public static final String CHAIN_ACTIVATED_MAIL_SUBJECT = "Approval Chain Activated Successfully";

	public static final String CHAIN_ACTIVATE_APPROVER_SUBJECT = "You approved Activation Request";

	public static final String CHAIN_ACTIVATION_REJECTED_MAIL_SUBJECT = "Approval Chain Activation Rejected";

	public static final String CHAIN_ACTIVATE_REJECTION_CONFIRMATION_SUBJECT = "You rejected Activation Request";
	
	public static final String CHAIN_ACTIVATION_REQUEST_MAIL_SUBJECT = "Approval Chain Activation Request Raised";

	public static final String CHAIN_ACTIVATION_REQUEST_MAIL_BODY = "<html><body>" + "<p>Greetings,</p>"
			+ "<p>Activation request has been raised for Approval Chain ID <b>%s</b> "
			+ "under functionality <b>%s</b>.</p>"
			+ "<p>The request is currently pending for administrator approval.</p>" + "<br/>"
			+ "<p>Best Regards,<br/>HMS Team</p>" + "</body></html>";
	
	public static final String CHAIN_DEACTIVATION_REQUEST_APPROVER_SUBJECT = "Approval Chain Deactivation Request Received";
	public static final String CHAIN_DEACTIVATION_REQUEST_APPROVER_BODY = "<html><body>" + "<p>Greetings,</p>"
			+ "<p>A deactivation request has been raised for Approval Chain ID <b>%s</b> "
			+ "under functionality <b>%s</b>.</p>" + "<p>Please review and take appropriate action.</p>" + "<br/>"
			+ "<p>Best Regards,<br/>HMS Team</p>" + "</body></html>";
	
	public static final String CHAIN_ACTIVATION_REQUEST_APPROVER_SUBJECT = "Approval Chain Activation Request Received";
	
	public static final String CHAIN_ACTIVATION_REQUEST_APPROVER_BODY = "<html><body>" + "<p>Greetings,</p>"
			+ "<p>An activation request has been raised for Approval Chain ID <b>%s</b> "
			+ "under functionality <b>%s</b>.</p>" + "<p>Please review and take appropriate action.</p>" + "<br/>"
			+ "<p>Best Regards,<br/>HMS Team</p>" + "</body></html>";

	public static final String CHAIN_DEACTIVATION_REQUEST_MAIL_SUBJECT = "Approval Chain Deactivation Request Raised";

	public static final String CHAIN_DEACTIVATION_REQUEST_MAIL_BODY = "<html><body>" + "<p>Greetings,</p>"
			+ "<p>Deactivation request has been raised for Approval Chain ID <b>%s</b> "
			+ "under functionality <b>%s</b>.</p>"
			+ "<p>The request is currently pending for administrator approval.</p>" + "<br/>"
			+ "<p>Best Regards,<br/>HMS Team</p>" + "</body></html>";
	
	public static final String CHAIN_APPROVED_MAIL_BODY = "<html><body>" + "<p>Greetings</p>"
			+ "<p>Your Approval Chain with ID <b>%s</b> for <b>%s</b> has been approved successfully.</p>"
			+ "<br/><p>Best regards,<br/>HMS Team</p>" + "</body></html>";

	public static final String CHAIN_REJECTED_MAIL_BODY = "<html><body>" + "<p>Greetings</p>"
			+ "<p>Your Approval Chain with ID <b>%s</b> for <b>%s</b> has been rejected.</p>"
			+ "<br/><p>Best regards,<br/>HMS Team</p>" + "</body></html>";

	public static final String CHAIN_APPROVER_CONFIRMATION_BODY = "<html><body>" + "<p>Greetings</p>"
			+ "<p>You have successfully approved Approval Chain <b>%s</b> for <b>%s</b>.</p>"
			+ "<br/><p>Best regards,<br/>HMS Team</p>" + "</body></html>";

	public static final String CHAIN_REJECTION_CONFIRMATION_BODY = "<html><body>" + "<p>Greetings</p>"
			+ "<p>You have rejected Approval Chain <b>%s</b> for <b>%s</b>.</p>"
			+ "<br/><p>Best regards,<br/>HMS Team</p>" + "</body></html>";

	public static final String CHAIN_DEACTIVATED_MAIL_BODY = "<html><body>" + "<p>Greetings</p>"
			+ "<p>Approval Chain <b>%s</b> for <b>%s</b> has been deactivated successfully.</p>"
			+ "<br/><p>Best regards,<br/>HMS Team</p>" + "</body></html>";

	public static final String CHAIN_DEACTIVE_REJECTED_MAIL_BODY = "<html><body>" + "<p>Greetings</p>"
			+ "<p>Your deactivation request for Approval Chain <b>%s</b> for <b>%s</b> was rejected.</p>"
			+ "<br/><p>Best regards,<br/>HMS Team</p>" + "</body></html>";

	public static final String CHAIN_DEACTIVE_REQUEST_MAIL_BODY = "<html><body>" + "<p>Greetings</p>"
			+ "<p>Approval Chain <b>%s</b> for <b>%s</b> has requested deactivation.</p>"
			+ "<br/><p>Best regards,<br/>HMS Team</p>" + "</body></html>";
	public static final String CHAIN_DEACTIVE_APPROVER_BODY = "<html><body>" + "<p>Greetings</p>"
			+ "<p>You have successfully approved the deactivation request for Approval Chain "
			+ "<b>%s</b> for functionality <b>%s</b>.</p>" + "<br/><p>Best regards,<br/>HMS Team</p>"
			+ "</body></html>";

	public static final String CHAIN_DEACTIVE_REJECTION_CONFIRMATION_BODY = "<html><body>" + "<p>Greetings</p>"
			+ "<p>You have rejected the deactivation request for Approval Chain "
			+ "<b>%s</b> for functionality <b>%s</b>.</p>" + "<br/><p>Best regards,<br/>HMS Team</p>"
			+ "</body></html>";

	public static final String CHAIN_ACTIVATED_MAIL_BODY = "<html><body>" + "<p>Greetings</p>"
			+ "<p>Approval Chain <b>%s</b> for <b>%s</b> has been activated successfully.</p>"
			+ "<br/><p>Best regards,<br/>HMS Team</p>" + "</body></html>";

	public static final String CHAIN_ACTIVATION_REJECTED_MAIL_BODY = "<html><body>" + "<p>Greetings</p>"
			+ "<p>Your activation request for Approval Chain <b>%s</b> for <b>%s</b> was rejected.</p>"
			+ "<br/><p>Best regards,<br/>HMS Team</p>" + "</body></html>";

	public static final String CHAIN_ACTIVATE_APPROVER_BODY = "<html><body>" + "<p>Greetings</p>"
			+ "<p>You have approved activation of Approval Chain <b>%s</b> for <b>%s</b>.</p>"
			+ "<br/><p>Best regards,<br/>HMS Team</p>" + "</body></html>";

	public static final String CHAIN_ACTIVATE_REJECTION_CONFIRMATION_BODY = "<html><body>" + "<p>Greetings</p>"
			+ "<p>You have rejected activation of Approval Chain <b>%s</b> for <b>%s</b>.</p>"
			+ "<br/><p>Best regards,<br/>HMS Team</p>" + "</body></html>";
}


