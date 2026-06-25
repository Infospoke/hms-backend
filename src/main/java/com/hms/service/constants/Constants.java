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
	public static final String INVALID_DOB_FORMAT = "Invalid Date of Birth format. Expected format: yyyy-MM-dd";
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
	public static final String SUCCESS = "success";
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

	public static final String FORGOT_PASSWORD_BODY = "<html><body>" + "<p>Dear %s,</p>"

			+ "<p>This is to inform you that your password for the Nexus HMS Portal has been successfully reset.</p>"

			+ "<p>Please use the temporary password below to log in:</p>"

			+ "<p><b>Username:</b> %s<br/>" + "<b>Temporary Password:</b> %s</p>"

			+ "<p>You are required to log in and change your password immediately. "
			+ "This temporary password is valid for a limited time and will expire after first use or as per system policy.</p>"

			+ "<p><b>Security Advisory:</b></p>" + "<ul>" + "<li>Do not share your password with anyone</li>"
			+ "<li>Ensure your new password complies with the organization’s security standards</li>"
			+ "<li>If you did not initiate this request, please report it immediately</li>" + "</ul>"

			+ "<p>For any assistance, please contact the IT Support Team </p>"

			+ "<br/>" + "<p>Regards,<br/>IT Support Team<br/>Nexus HMS</p>"

			+ "</body></html>";

	public static final String FORGOT_PIN_BODY = "<html><body>" + "<p>Dear %s,</p>"

			+ "<p>This is to inform you that your Mobile PIN for accessing the Nexus HMS application has been successfully reset.</p>"

			+ "<p>Please find your temporary Mobile PIN below:</p>"

			+ "<p><b>Username:</b> %s<br/>" + "<b>Temporary Mobile PIN:</b> %s</p>"

			+ "<p>You are required to log in to the mobile application and update your PIN immediately. "
			+ "This temporary PIN is valid for a limited duration and will expire after first use or as per system policy.</p>"

			+ "<p><b>Security Advisory:</b></p>" + "<ul>" + "<li>Do not share your Mobile PIN with anyone</li>"
			+ "<li>Avoid using easily guessable PINs (e.g., 1234, birth dates)</li>"
			+ "<li>If you did not initiate this request, please report it immediately to the IT Support Team</li>"
			+ "</ul>"

			+ "<p>For any assistance, please contact IT Support </p>"

			+ "<br/>" + "<p>Regards,<br/>IT Support Team<br/>Nexus HMS</p>"

			+ "</body></html>";

	public static final String USER_CREATED_MAIL_SUBJECT = "User Account Created - HMS";
	public static final String USER_CREATED_MAIL_BODY = "<div style='font-family:Segoe UI,Arial,sans-serif; background-color:#f5f7fa; padding:20px;'>"
			+

			"<div style='max-width:600px; margin:auto; background:#ffffff; border-radius:10px; "
			+ "box-shadow:0 2px 8px rgba(0,0,0,0.1); padding:30px;'>" +

			"<p>Dear <b>%s</b>,</p>" +

			"<p>Greetings from the HR Team at <b>Nexus HMS</b>.</p>" +

			"<p>This is to inform you that your user account for the <b>Nexus HMS Portal</b> has been successfully created.</p>"
			+

			"<p>Please find your system-generated login credentials below:</p>" +

			"<div style='background:#f1f3f6; padding:20px; border-radius:8px; margin:20px 0;'>" +

			"<p style='margin:8px 0;'><b>Username:</b> %s</p>"
			+ "<p style='margin:8px 0;'><b>Temporary Password:</b> %s</p>"
			+ "<p style='margin:8px 0;'><b>Temporary PIN:</b> %s</p>" +

			"</div>" +

			"<p style='color:#C0392B;'><b>Note:</b> These credentials are valid only for 24 hours from the time of issuance.</p>"
			+

			"<p>You are required to log in using the above credentials and complete the initial setup process. "
			+ "For security compliance, you must change your password and PIN upon first login.</p>" +

			"<p>Please ensure that your new credentials meet the organization’s security standards and are kept strictly confidential.</p>"
			+

			"<p>If you encounter any issues while accessing the portal, please contact the HR/IT Support Team at "
			+ ".</p>" +

			"<p>We wish you a successful and rewarding journey with Nexus HMS.</p>" +

			"<br>" + "<p>Warm regards,<br><b>HR Team</b><br>Nexus HMS</p>" +

			"</div></div>";
	public static final String EMPLOYEE_ID_SIZE = "Employee id must be between 1 and 4 characters";
	public static final String INVALID_ROLE_ID = "Invalid role id";
	public static final String INVALID_DEPARTMENT_FOR_BUSINESS_UNIT = "Invalid departmet for bussiness id";
	public static final String ROLE_NOT_BELONG_TO_DEPARTMENT = "Role id doesnot belong to this department";
	public static final String PENDING = "Pending";


	public static final String SR_SUBMITTED_MAIL_BODY = "<html><body>" + "<p>Dear %s,</p>"
			+ "<p>Your Staffing Requisition (SR) has been successfully created in the "
			+ "Hiring Management System (HMS).</p>" + "<p><b>Requisition Summary:</b></p>" + "<ul>"
			+ "<li><b>SR ID:</b> %s</li>" + "<li><b>Job Title:</b> %s</li>" + "<li><b>Department:</b> %s</li>"
			+ "<li><b>Number of Positions:</b> %s</li>" + "<li><b>Job Location:</b> %s</li>"
			+ "<li><b>Employment Type:</b> %s</li>" + "<li><b>Priority:</b> %s</li>"
			+ "<li><b>Created Date:</b> %s</li>" + "</ul>"
			+ "<p>The requisition has been successfully submitted and forwarded for the "
			+ "approval process as per the configured workflow.</p>"
			+ "<p>You will receive further notifications on the approval status and " + "subsequent actions.</p>"
			+ "<br/>" + "<p>Regards,<br/>" + "Hiring Management System (HMS)</p>"
			+ "<p><i>This is a system-generated email. Please do not reply to this email.</i></p>" + "</body></html>";

	

	public static final String SR_TO_BE_APPROVED_BY_FIRST_APPROVER_MAIL_BODY = "<html><body>" + "<p>Dear %s,</p>"

			+ "<p>A new Staffing Requisition (SR) has been successfully submitted by the Hiring Manager "
			+ "and is currently pending your review and approval in the Nexus Hiring Management System (HMS).</p>"

			+ "<p>Please find the requisition summary below:</p>"

			+ "<ul>" + "<li><b>SR ID:</b> %s</li>" + "<li><b>Job Title:</b> %s</li>" + "<li><b>Department:</b> %s</li>"
			+ "<li><b>Hiring Manager:</b> %s</li>" + "<li><b>Number of Positions:</b> %s</li>"
			+ "<li><b>Job Location:</b> %s</li>" + "<li><b>Employment Type:</b> %s</li>"
			+ "<li><b>Priority:</b> %s</li>" + "<li><b>Submission Date:</b> %s</li>" + "</ul>"

			+ "<p>The request has been submitted as part of the ongoing hiring requirements "
			+ "and is awaiting your approval to proceed further in the recruitment workflow.</p>"

			+ "<p>Kindly review the requisition and take the necessary action at your earliest convenience.</p>"

			+ "<p>If additional clarification is required, please coordinate with the respective Hiring Manager.</p>"

			+ "<br/>" + "<p>Regards,<br/>" + "Nexus Hiring Management System (HMS)</p>"

			+ "<p><i>This is a system-generated email. Please do not reply to this email.</i></p>"

			+ "</body></html>";

	public static final String SR_TO_BE_APPROVED_MAIL_BODY = "<html><body>" + "<p>Dear %s,</p>"
			+ "<p>The below Staffing Requisition (SR) has been reviewed and approved by "
			+ "the previous approver and is now pending your review and approval in the "
			+ "Nexus Hiring Management System (HMS).</p>" + "<p><b>Requisition Summary:</b></p>" + "<ul>"
			+ "<li><b>SR ID:</b> %s</li>" + "<li><b>Job Title:</b> %s</li>" + "<li><b>Department:</b> %s</li>"
			+ "<li><b>Hiring Manager:</b> %s</li>" + "<li><b>Number of Positions:</b> %s</li>"
			+ "<li><b>Job Location:</b> %s</li>" + "<li><b>Employment Type:</b> %s</li>"
			+ "<li><b>Priority:</b> %s</li>" + "</ul>"
			+ "<p>The approval workflow is currently with you for further action.</p>"
			+ "<p>Kindly review the requisition and take the necessary action at your " + "earliest convenience.</p>"
			+ "<p>For any additional clarification, please contact the respective " + "Hiring Manager.</p>" + "<br/>"
			+ "<p>Regards,<br/>" + "Nexus Hiring Management System (HMS)</p>"
			+ "<p><i>This is a system-generated email. Please do not reply to this email.</i></p>" + "</body></html>";

	public static final String SR_FULLY_APPROVED_NOTIFY = "<html><body>" + "<p>Dear %s,</p>"
			+ "<p>We are pleased to inform you that the below Staffing Requisition (SR) "
			+ "has been successfully approved by all required approvers in the " + "Nexus Hiring Management System (HMS).</p>"
			+ "<p><b>Requisition Summary:</b></p>" + "<ul>" + "<li><b>SR ID:</b> %s</li>"
			+ "<li><b>Job Title:</b> %s</li>" + "<li><b>Department:</b> %s</li>"
			+ "<li><b>Number of Positions:</b> %s</li>" + "<li><b>Job Location:</b> %s</li>"
			+ "<li><b>Employment Type:</b> %s</li>" + "<li><b>Priority:</b> %s</li>" + "</ul>"
			+ "<p><b>Approval Status:</b></p>" + "<ul>" + "<li><b>Final Approval Status:</b> Approved</li>"
			+ "<li><b>Approved On:</b> %s</li>" + "<li><b>Recruitment Status:</b> Open for Hiring</li>" + "</ul>"
			+ "<p>The requisition approval workflow has been completed successfully, "
			+ "and the SR is now available for the recruitment and recruiter assignment process.</p>" + "<br/>"
			+ "<p>Regards,<br/>" + "Nexus Hiring Management System (HMS)</p>"
			+ "<p><i>This is a system-generated email. Please do not reply to this email.</i></p>" + "</body></html>";

	public static final String SR_REJECTED_NOTIFY = "<html><body>" + "<p>Dear %s,</p>"
			+ "<p>The below Staffing Requisition (SR) has been reviewed and rejected "
			+ "during the approval process in the Nexus Hiring Management System (HMS).</p>"
			+ "<p><b>Requisition Summary:</b></p>" + "<ul>" + "<li><b>SR ID:</b> %s</li>"
			+ "<li><b>Job Title:</b> %s</li>" + "<li><b>Department:</b> %s</li>"
			+ "<li><b>Number of Positions:</b> %s</li>" + "<li><b>Job Location:</b> %s</li>"
			+ "<li><b>Employment Type:</b> %s</li>" + "<li><b>Priority:</b> %s</li>" + "</ul>"
			+ "<p><b>Approval Workflow Details:</b></p>" + "<ul>" + "<li><b>Current Workflow Stage:</b> %s</li>"
			+ "<li><b>Reviewed By:</b> %s</li>" + "<li><b>Approval Status:</b> Rejected</li>"
			+ "<li><b>Reviewed On:</b> %s</li>" + "</ul>" + "<p><b>Reviewer Comments / Remarks:</b><br/>%s</p>"
			+ "<p>The approval workflow has been stopped due to the rejection at the " + "above approval stage.</p>"
			+ "<p>Kindly review the comments provided and make the necessary corrections "
			+ "before resubmitting the requisition, if required.</p>"
			+ "Nexus Hiring Management System (HMS)</p>"
			+ "<p><i>This is a system-generated email. Please do not reply to this email.</i></p>" + "</body></html>";

	public static final String SR_APPROVED_NOTIFY = "<html><body>"

			+ "<p>Dear %s,</p>"

			+ "<p>The below Staffing Requisition (SR) has been reviewed and approved by %s in the Nexus Hiring Management System (HMS).</p>"

			+ "<p><b>Requisition Summary:</b></p>"

			+ "<ul>" + "<li><b>SR ID:</b> %s</li>" + "<li><b>Job Title:</b> %s</li>" + "<li><b>Department:</b> %s</li>"
			+ "<li><b>Number of Positions:</b> %s</li>" + "<li><b>Job Location:</b> %s</li>"
			+ "<li><b>Employment Type:</b> %s</li>" + "<li><b>Priority:</b> %s</li>" + "</ul>"

			+ "<p><b>Approval Details:</b></p>"

			+ "<ul>" + "<li><b>Approved By:</b> %s</li>" + "<li><b>Approval Status:</b> Approved</li>"
			+ "<li><b>Approved On:</b> %s</li>"
			+ "<li><b>Current Workflow Status:</b> Moved to Next Approval Stage</li>" + "</ul>"

			+ "<p>The requisition has been successfully moved to the next step in the "
			+ "approval workflow for further review and processing.</p>"

			+ "<br/>"

			+ "<p>Regards,<br/>" + "Nexus Hiring Management System (HMS)</p>"

			+ "<p><i>This is a system-generated email. Please do not reply to this email.</i></p>"
			+ "</body></html>";

	
	public static final String SR_SUBMITTED_MAIL_SUBJECT = "SR Submitted Successfully";

	

	public static final String SR_TO_BE_APPROVED_NOTIFY = "%s is awaiting your approval.";
	

	public static final String CHAIN_CREATED_SUCESSFULLY_MAIL_BODY = "<html><body>"

			+ "<p>Dear %s,</p>"

			+ "<p>" + "Your approval chain configuration has been created successfully "
			+ "in the Nexus Hiring Management System(HMS) and submitted for approval." + "</p>"

			+ "<p><b>Chain Configuration Details:</b></p>"

			+ "<p><b>Chain ID:</b> %s</p>" + "<p><b>Chain Name:</b> %s</p>" + "<p><b>Description:</b> %s</p>"
			+ "<p><b>Status:</b> Pending Approval</p>" + "<p><b>Submitted By:</b> %s</p>"
			+ "<p><b>Submitted On:</b> %s</p>"

			+ "<br/>"

			+ "<p>" + "The approval chain is currently under review and awaiting approval " + "from the Administrator."
			+ "</p>"

			+ "<br/>"

			+ "<p>" + "Regards,<br/>" + "Nexus Hiring management System(HMS)" + "</p>"

			+ "<br/>"

			+ "<p>" + "This is a system-generated email. Please do not reply to this email." + "</p>"


			+ "</body></html>";

	public static final String CHAIN_CREATED_MAIL_SUBJECT = "Chain created  Successfully";



	public static final String CHAIN_TO_BE_APPROVED = "<html><body>"

			+ "<p>Dear Administrator,</p>"

			+ "<p>" + "A new approval chain configuration has been created and submitted "
			+ "for approval in the Nexus Hiring Management System(HMS)." + "</p>"

			+ "<p><b>Chain Configuration Details:</b></p>"

			+ "<p><b>Chain ID:</b> %s</p>" + "<p><b>Chain Name:</b> %s</p>" + "<p><b>Description:</b> %s</p>"
			+ "<p><b>Status:</b> Pending Approval</p>" + "<p><b>Submitted By:</b> %s</p>"
			+ "<p><b>Submitted On:</b> %s</p>"

			+ "<br/>"

			+ "<p>" + "The approval workflow is currently awaiting your review and approval "
			+ "for further activation." + "</p>"

			+ "<p>" + "Kindly review the configuration and take the necessary action " + "at your earliest convenience."
			+ "</p>"

			+ "<br/>"

			+ "<p>" + "Regards,<br/>" + "Nexus Hiring management System(HMS)" + "</p>"

			+ "<br/>"

			+ "<p>" + "This is a system-generated email. Please do not reply to this email." + "</p>"

			+ "</body></html>";
	public static final String CHAIN_TO_BE_APPROVED_MAIL_SUBJECT = "Chain created and waiting for your approval";
	
	public static final String CHAIN_APPROVED_MAIL_SUBJECT = "Your chain is approved successfully";

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

	public static final String CHAIN_ACTIVATION_REQUEST_MAIL_BODY = "<html><body>"

			+ "<p>Dear %s,</p>"

			+ "<p>" + "Your request for activation of the below approval chain "
			+ "has been successfully submitted in Nexus HMS." + "</p>"

			+ "<p><b>Chain Configuration Details:</b></p>"

			+ "<p><b>Chain ID:</b> %s</p>" + "<p><b>Chain Name:</b> %s</p>" + "<p><b>Description:</b> %s</p>"
			+ "<p><b>Request Type:</b> Activation Request</p>" + "<p><b>Status:</b> Submitted for Approval</p>"
			+ "<p><b>Requested On:</b> %s</p>"

			+ "<br/>"

			+ "<p>" + "The request is currently under the approval process " + "and awaiting reviewer action." + "</p>"

			+ "<p>" + "You will receive further notifications based on " + "the approval workflow status." + "</p>"

			+ "<p>" + "Please log in to the Nexus HMS portal for additional details." + "</p>"

			+ "<br/>"

			+ "<p>" + "Regards,<br/>" + "Nexus Hiring Management System (HMS)" + "</p>"

			+ "<br/>"

			+ "<p>" + "This is a system-generated email. Please do not reply to this email." + "</p>"

			+ "</body></html>";
	public static final String CHAIN_DEACTIVATION_REQUEST_APPROVER_SUBJECT = "Approval Chain Deactivation Request Received";
	public static final String CHAIN_DEACTIVATION_REQUEST_APPROVER_BODY = "<html><body>"

			+ "<p>Dear Administrator,</p>"

			+ "<p>" + "A request for the deactivation of the below approval chain "
			+ "has been submitted in the Nexus Hiring Management System and is awaiting your review." + "</p>"

			+ "<p><b>Chain Configuration Details:</b></p>"

			+ "<p><b>Chain ID:</b> %s</p>" + "<p><b>Chain Name:</b> %s</p>" + "<p><b>Description:</b> %s</p>"
			+ "<p><b>Functionality:</b> %s</p>" + "<p><b>Request Type:</b> Deactivation Request</p>"
			+ "<p><b>Request Status:</b> Pending Approval</p>" + "<p><b>Requested By:</b> %s</p>"
			+ "<p><b>Requested On:</b> %s</p>"

			+ "<br/>"

			+ "<p>" + "The approval workflow is currently awaiting your review "
			+ "and approval for further processing." + "</p>"

			+ "<p>" + "Kindly review the configuration and take the necessary action " + "at your earliest convenience."
			+ "</p>"

			+ "<br/>"

			+ "<p>" + "Regards,<br/>" + "Nexus Hiring Management System(HMS)" + "</p>"

			+ "<br/>"

			+ "<p>" + "This is a system-generated email. Please do not reply to this email." + "</p>"

			+ "</body></html>";

	public static final String CHAIN_ACTIVATION_REQUEST_APPROVER_SUBJECT = "Approval Chain Activation Request Received";

	public static final String CHAIN_ACTIVATION_REQUEST_APPROVER_BODY = "<html><body>"

			+ "<p>Dear Administrator,</p>"

			+ "<p>" + "A request for activation of the below approval chain "
			+ "has been submitted and is awaiting your review in Nexus Hiring Management System(HMS)." + "</p>"

			+ "<p><b>Chain Configuration Details:</b></p>"

			+ "<p><b>Chain ID:</b> %s</p>" + "<p><b>Chain Name:</b> %s</p>" + "<p><b>Description:</b> %s</p>"
			+ "<p><b>Request Type:</b> Activation Request</p>" + "<p><b>Status:</b> Pending Approval</p>"
			+ "<p><b>Submitted By:</b> %s</p>" + "<p><b>Submitted On:</b> %s</p>"

			+ "<br/>"

			+ "<p>" + "Kindly review the request and take the necessary action." + "</p>"

			+ "<p>" + "Please log in to the Nexus HMS portal for additional details." + "</p>"

			+ "<br/>"

			+ "<p>" + "Regards,<br/>" + "Nexus Hiring Management System (HMS)" + "</p>"

			+ "<br/>"

			+ "<p>" + "This is a system-generated email. Please do not reply to this email." + "</p>"

			+ "</body></html>";
	public static final String CHAIN_DEACTIVATION_REQUEST_MAIL_SUBJECT = "Approval Chain Deactivation Request Raised";

	public static final String CHAIN_DEACTIVATION_REQUEST_MAIL_BODY = "<html><body>"

			+ "<p>Dear %s,</p>"

			+ "<p>" + "Your request for the deactivation of the below approval chain "
			+ "has been successfully submitted in the Nexus Hiring Management System(HMS)." + "</p>"

			+ "<p><b>Chain Configuration Details:</b></p>"

			+ "<p><b>Chain ID:</b> %s</p>" + "<p><b>Chain Name:</b> %s</p>" + "<p><b>Description:</b> %s</p>"
			+ "<p><b>Functionality:</b> %s</p>" + "<p><b>Request Type:</b> Deactivation Request</p>"
			+ "<p><b>Request Status:</b> Submitted for Approval</p>" + "<p><b>Requested On:</b> %s</p>"

			+ "<br/>"

			+ "<p>" + "The request is currently under the approval process " + "and awaiting reviewer action." + "</p>"

			+ "<p>" + "You will receive further notifications based on " + "the approval workflow status." + "</p>"

			+ "<p>" + "You can track the request status from the Nexus Portal." + "</p>"

			+ "<br/>"

			+ "<p>" + "Regards,<br/>" + "Nexus Hiring Management System(HMS)" + "</p>"

			+ "<br/>"

			+ "<p>" + "This is a system-generated email. Please do not reply to this email." + "</p>"

			+ "</body></html>";

	public static final String CHAIN_REJECTED_MAIL_BODY = "<html><body>"

			+ "<p>Dear %s,</p>"

			+ "<p>" + "This is to inform you that the below approval chain " + "has been rejected in Nexus Hiring Management System(HMS)."
			+ "</p>"

			+ "<p><b>Chain Configuration Details:</b></p>"

			+ "<p><b>Chain ID:</b> %s</p>" + "<p><b>Chain Name:</b> %s</p>" + "<p><b>Description:</b> %s</p>"
			+ "<p><b>Status:</b> Rejected</p>" + "<p><b>Reviewed On:</b> %s</p>"

			+ "<p><b>Reviewer Comments / Remarks:</b></p>" + "<p>%s</p>"

			+ "<br/>"

			+ "<p>" + "The approval chain has not been activated in the system." + "</p>"

			+ "<p>" + "Please log in to the Nexus HMS portal for additional details." + "</p>"

			+ "<br/>"

			+ "<p>" + "Regards,<br/>" + "Nexus Hiring Management System (HMS)" + "</p>"

			+ "<br/>"

			+ "<p>" + "This is a system-generated email. Please do not reply to this email." + "</p>"

			+ "</body></html>";

	public static final String CHAIN_APPROVED_MAIL_BODY = "<html><body>"

			+ "<p>Dear %s,</p>"

			+ "<p>" + "This is to inform you that the below approval chain "
			+ "has been successfully approved in Nexus HMS." + "</p>"

			+ "<p><b>Chain Configuration Details:</b></p>"

			+ "<p><b>Chain ID:</b> %s</p>" + "<p><b>Chain Name:</b> %s</p>" + "<p><b>Description:</b> %s</p>"
			+ "<p><b>Status:</b> Approved</p>" + "<p><b>Approved On:</b> %s</p>"

			+ "<br/>"

			+ "<p>" + "The approval chain is now active and available " + "for workflow processing in the system."
			+ "</p>"

			+ "<p>" + "Please log in to the Nexus HMS portal for additional details." + "</p>"

			+ "<br/>"

			+ "<p>" + "Regards,<br/>" + "Nexus Hiring Management System (HMS)" + "</p>"

			+ "<br/>"

			+ "<p>" + "This is a system-generated email. Please do not reply to this email." + "</p>"

			+ "</body></html>";
	
	public static final String CHAIN_APPROVER_CONFIRMATION_BODY = "<html><body>"

			+ "<p>Dear %s,</p>"

			+ "<p>" + "This is to inform you that you have successfully approved "
			+ "the below approval chain in Nexus Hiring Management System(HMS)." + "</p>"

			+ "<p><b>Chain Configuration Details:</b></p>"

			+ "<p><b>Chain ID:</b> %s</p>" + "<p><b>Chain Name:</b> %s</p>" + "<p><b>Description:</b> %s</p>"
			+ "<p><b>Status:</b> Approved</p>" + "<p><b>Approved On:</b> %s</p>"

			+ "<br/>"

			+ "<p>" + "The approval chain is now active and available " + "for workflow processing in the system."
			+ "</p>"

			+ "<p>" + "Please log in to the Nexus HMS portal for additional details." + "</p>"

			+ "<br/>"

			+ "<p>" + "Regards,<br/>" + "Nexus Hiring Management System (HMS)" + "</p>"

			+ "<br/>"

			+ "<p>" + "This is a system-generated email. Please do not reply to this email." + "</p>"

			+ "</body></html>";
	

	public static final String CHAIN_REJECTION_CONFIRMATION_BODY = "<html><body>"

			+ "<p>Dear %s,</p>"

			+ "<p>" + "This is to inform you that you have successfully rejected "
			+ "the below approval chain in Nexus Hiring Management System(HMS)." + "</p>"

			+ "<p><b>Chain Configuration Details:</b></p>"

			+ "<p><b>Chain ID:</b> %s</p>" + "<p><b>Chain Name:</b> %s</p>" + "<p><b>Description:</b> %s</p>"
			+ "<p><b>Status:</b> Rejected</p>" + "<p><b>Rejected On:</b> %s</p>"

			+ "<p><b>Reviewer Comments / Remarks:</b></p>" + "<p>%s</p>"

			+ "<br/>"

			+ "<p>" + "The approval chain has not been activated in the system." + "</p>"

			+ "<p>" + "Please log in to the Nexus HMS portal for additional details." + "</p>"

			+ "<br/>"

			+ "<p>" + "Regards,<br/>" + "Nexus Hiring Management System (HMS)" + "</p>"

			+ "<br/>"

			+ "<p>" + "This is a system-generated email. Please do not reply to this email." + "</p>"

			+ "</body></html>";

	public static final String CHAIN_DEACTIVE_REJECTED_MAIL_BODY = "<html><body>"

			+ "<p>Dear %s,</p>"

			+ "<p>" + "This is to inform you that the deactivation request raised "
			+ "for the below approval chain has been reviewed and rejected " + "in Nexus Hiring Management System(HMS)." + "</p>"

			+ "<p><b>Chain Configuration Details:</b></p>"

			+ "<p><b>Chain ID:</b> %s</p>" + "<p><b>Chain Name:</b> %s</p>" + "<p><b>Description:</b> %s</p>"
			+ "<p><b>Request Type:</b> Deactivation Request</p>" + "<p><b>Status:</b> Rejected</p>"
			+ "<p><b>Reviewed On:</b> %s</p>"

			+ "<p><b>Reviewer Comments / Remarks:</b></p>" + "<p>%s</p>"

			+ "<br/>"

			+ "<p>" + "The approval chain will remain active as the deactivation " + "request was not approved."
			+ "</p>"

			+ "<p>" + "Please log in to the Nexus HMS portal for additional details." + "</p>"

			+ "<br/>"

			+ "<p>" + "Regards,<br/>" + "Nexus Hiring Management System (HMS)" + "</p>"

			+ "<br/>"

			+ "<p>" + "This is a system-generated email. Please do not reply to this email." + "</p>"

			+ "</body></html>";

	public static final String CHAIN_DEACTIVE_MAIL_BODY = "<html><body>"

			+ "<p>Dear %s,</p>"

			+ "<p>" + "You have successfully approved the below approval chain deactivation "
			+ "request in the Nexus Hiring Management System(HMS)" + "</p>"

			+ "<p><b>Chain Configuration Details:</b></p>"

			+ "<p><b>Chain ID:</b> %s</p>" + "<p><b>Chain Name:</b> %s</p>" + "<p><b>Description:</b> %s</p>"
			+ "<p><b>Functionality:</b> %s</p>" + "<p><b>Request Type:</b> Deactivation Request</p>"
			+ "<p><b>Approval Status:</b> Approved</p>" + "<p><b>Approved On:</b> %s</p>"

			+ "<br/>"

			+ "<p>" + "The approval chain has now been successfully deactivated "
			+ "and is no longer active for workflow processing." + "</p>"

			+ "<p>" + "You can review the request details from the Nexus Portal." + "</p>"

			+ "<br/>"

			+ "<p>" + "Regards,<br/>" + "Nexus Hiring Management System(HMS)" + "</p>"

			+ "<br/>"

			+ "<p>" + "This is a system-generated email. Please do not reply to this email." + "</p>"

			+ "</body></html>";

	public static final String CHAIN_DEACTIVATED_MAIL_BODY = "<html><body>"

			+ "<p>Dear %s,</p>"

			+ "<p>" + "This is to inform you that the below approval chain "
			+ "has been successfully deactivated by the Administrator " + "in Nexus Hiring Management System(HMS)." + "</p>"

			+ "<p><b>Chain Configuration Details:</b></p>"

			+ "<p><b>Chain ID:</b> %s</p>" + "<p><b>Chain Name:</b> %s</p>" + "<p><b>Description:</b> %s</p>"
			+ "<p><b>Status:</b> Deactivated</p>" + "<p><b>Deactivated On:</b> %s</p>"

			+ "<br/>"

			+ "<p>" + "The approval chain is now inactive and will no longer " + "be available for workflow processing."
			+ "</p>"

			+ "<p>" + "Please log in to the Nexus HMS portal for additional details." + "</p>"

			+ "<br/>"

			+ "<p>" + "Regards,<br/>" + "Nexus Hiring Management System (HMS)" + "</p>"

			+ "<br/>"

			+ "<p>" + "This is a system-generated email. Please do not reply to this email." + "</p>"

			+ "</body></html>";
	public static final String CHAIN_DEACTIVE_REJECTION_CONFIRMATION_BODY = "<html><body>"

			+ "<p>Dear Administrator,</p>"

			+ "<p>" + "You have successfully rejected the deactivation request "
			+ "for the below approval chain in Nexus Hiring Management System(HMS)" + "</p>"

			+ "<p><b>Chain Configuration Details:</b></p>"

			+ "<p><b>Chain ID:</b> %s</p>" + "<p><b>Chain Name:</b> %s</p>" + "<p><b>Description:</b> %s</p>"
			+ "<p><b>Request Type:</b> Deactivation Request</p>" + "<p><b>Status:</b> Rejected</p>"
			+ "<p><b>Rejected On:</b> %s</p>"

			+ "<p><b>Reviewer Comments / Remarks:</b></p>" + "<p>%s</p>"

			+ "<br/>"

			+ "<p>" + "The approval chain will remain active in the system." + "</p>"

			+ "<p>" + "Please log in to the Nexus HMS portal for additional details." + "</p>"

			+ "<br/>"

			+ "<p>" + "Regards,<br/>" + "Nexus Hiring Management System (HMS)" + "</p>"

			+ "<br/>"

			+ "<p>" + "This is a system-generated email. Please do not reply to this email." + "</p>"

			+ "</body></html>";

	public static final String CHAIN_ACTIVATED_MAIL_BODY = "<html><body>"

			+ "<p>Dear %s,</p>"

			+ "<p>" + "This is to inform you that the activation request for the below "
			+ "approval chain has been successfully approved in Nexus Hiring Management System(HMS)." + "</p>"

			+ "<p><b>Chain Configuration Details:</b></p>"

			+ "<p><b>Chain ID:</b> %s</p>" + "<p><b>Chain Name:</b> %s</p>" + "<p><b>Description:</b> %s</p>"
			+ "<p><b>Request Type:</b> Activation Request</p>" + "<p><b>Status:</b> Approved</p>"
			+ "<p><b>Approved On:</b> %s</p>"

			+ "<br/>"

			+ "<p>" + "The approval chain is now active and available " + "for workflow processing in the system."
			+ "</p>"

			+ "<p>" + "Please log in to the Nexus HMS portal for additional details." + "</p>"

			+ "<br/>"

			+ "<p>" + "Regards,<br/>" + "Nexus Hiring Management System (HMS)" + "</p>"

			+ "<br/>"

			+ "<p>" + "This is a system-generated email. Please do not reply to this email." + "</p>"

			+ "</body></html>";
	public static final String CHAIN_ACTIVATE_APPROVER_BODY = "<html><body>"

			+ "<p>Dear Administrator,</p>"

			+ "<p>" + "This is to inform you that you have successfully approved "
			+ "the activation request for the below approval chain in Nexus Hiring Management System(HMS)" + "</p>"

			+ "<p><b>Chain Configuration Details:</b></p>"

			+ "<p><b>Chain ID:</b> %s</p>" + "<p><b>Chain Name:</b> %s</p>" + "<p><b>Description:</b> %s</p>"
			+ "<p><b>Request Type:</b> Activation Request</p>" + "<p><b>Status:</b> Approved</p>"
			+ "<p><b>Approved On:</b> %s</p>"

			+ "<br/>"

			+ "<p>" + "The approval chain is now active and available " + "for workflow processing in the system."
			+ "</p>"

			+ "<p>" + "Please log in to the Nexus HMS portal for additional details." + "</p>"

			+ "<br/>"

			+ "<p>" + "Regards,<br/>" + "Nexus Hiring Management System (HMS)" + "</p>"

			+ "<br/>"

			+ "<p>" + "This is a system-generated email. Please do not reply to this email." + "</p>"

			+ "</body></html>";

	public static final String CHAIN_ACTIVATION_REJECTED_MAIL_BODY = "<html><body>"

			+ "<p>Dear %s,</p>"

			+ "<p>" + "This is to inform you that the activation request for the below "
			+ "approval chain has been rejected inNexus Hiring Management System(HMS)" + "</p>"

			+ "<p><b>Chain Configuration Details:</b></p>"

			+ "<p><b>Chain ID:</b> %s</p>" + "<p><b>Chain Name:</b> %s</p>" + "<p><b>Description:</b> %s</p>"
			+ "<p><b>Request Type:</b> Activation Request</p>" + "<p><b>Status:</b> Rejected</p>"
			+ "<p><b>Reviewed On:</b> %s</p>"

			+ "<p><b>Reviewer Comments / Remarks:</b></p>" + "<p>%s</p>"

			+ "<br/>"

			+ "<p>" + "The approval chain will remain inactive in the system." + "</p>"

			+ "<p>" + "Please log in to the Nexus HMS portal for additional details." + "</p>"

			+ "<br/>"

			+ "<p>" + "Regards,<br/>" + "Nexus Hiring Management System (HMS)" + "</p>"

			+ "<br/>"

			+ "<p>" + "This is a system-generated email. Please do not reply to this email." + "</p>"

			+ "</body></html>";
	
	public static final String CHAIN_ACTIVATE_REJECTION_CONFIRMATION_BODY = "<html><body>"

			+ "<p>Dear %s,</p>"

			+ "<p>" + "This is to inform you that you have successfully rejected "
			+ "the activation request for the below approval chain in Nexus Hiring Management System(HMS)" + "</p>"

			+ "<p><b>Chain Configuration Details:</b></p>"

			+ "<p><b>Chain ID:</b> %s</p>" + "<p><b>Chain Name:</b> %s</p>" + "<p><b>Description:</b> %s</p>"
			+ "<p><b>Request Type:</b> Activation Request</p>" + "<p><b>Status:</b> Rejected</p>"
			+ "<p><b>Rejected On:</b> %s</p>"

			+ "<p><b>Reviewer Comments / Remarks:</b></p>" + "<p>%s</p>"

			+ "<br/>"

			+ "<p>" + "The approval chain will remain inactive in the system." + "</p>"

			+ "<p>" + "Please log in to the Nexus HMS portal for additional details." + "</p>"

			+ "<br/>"

			+ "<p>" + "Regards,<br/>" + "Nexus Hiring Management System (HMS)" + "</p>"

			+ "<br/>"

			+ "<p>" + "This is a system-generated email. Please do not reply to this email." + "</p>"

			+ "</body></html>";

	public static final String FUNCTIONALITY_FETCHED_SUCCESSFULLY = "Functionality fetched successfully";
	public static final String CREATED_AT = "createdAt";
	
	//Approval
	public static final String INTERVIEW_PLAN_APPROVED_MAIL_SUBJECT =
	        "Interview Plan Approved";

	public static final String INTERVIEW_PLAN_APPROVED_MAKER_BODY =
			"Your Interview Plan has been approved.\n\n"
			+ "Plan Id : %s\n"
			+ "Plan Name : %s\n"
			+ "Approved By : %s\n"
			+ "Approved On : %s";
	
	//Approval Confirmation (Checker)
	public static final String INTERVIEW_PLAN_APPROVER_CONFIRMATION_SUBJECT =
	        "Interview Plan Approval Confirmation";

	public static final String INTERVIEW_PLAN_APPROVED_CHECKER_BODY =
			"You have approved the Interview Plan.\n\n"
			+ "Plan Id : %s\n"
			+ "Plan Name : %s\n"
			+ "Approved By : %s\n"
			+ "Approved On : %s";
	
	//Rejected
	public static final String INTERVIEW_PLAN_REJECTED_MAIL_SUBJECT =
	        "Interview Plan Rejected";

	public static final String INTERVIEW_PLAN_REJECTED_MAKER_BODY =
			"Your Interview Plan has been rejected.\n\n"
			+ "Plan Id : %s\n"
			+ "Plan Name : %s\n"
			+ "Comments : %s\n"
			+ "Rejected On : %s";
	
	//Rejected Confirmation
	public static final String INTERVIEW_PLAN_REJECTION_CONFIRMATION_SUBJECT =
	        "Interview Plan Rejection Confirmation";

	public static final String INTERVIEW_PLAN_REJECTED_CHECKER_BODY =
			"You have rejected the Interview Plan.\n\n"
			+ "Plan Id : %s\n"
			+ "Plan Name : %s\n"
			+ "Comments : %s\n"
			+ "Rejected On : %s";
	
	//Activation Request
	public static final String INTERVIEW_PLAN_ACTIVATION_REQUEST_MAIL_SUBJECT =
	        "Interview Plan Activation Request";

	public static final String INTERVIEW_PLAN_ACTIVATION_REQUEST_MAKER_BODY =
			"Your activation request has been submitted successfully.\n\n"
			+ "Plan Id : %s\n"
			+ "Plan Name : %s\n"
			+ "Reason : %s\n"
			+ "Raised By : %s\n"
			+ "Raised On : %s";
	
	public static final String INTERVIEW_PLAN_ACTIVATION_REQUEST_CHECKER_BODY =
			"An activation request is pending for your approval.\n\n"
			+ "Plan Id : %s\n"
			+ "Plan Name : %s\n"
			+ "Reason : %s\n"
			+ "Requested By : %s\n"
			+ "Requested On : %s";
	
	//Activation Approved
	public static final String INTERVIEW_PLAN_ACTIVATED_MAIL_SUBJECT =
	        "Interview Plan Activated";

	public static final String INTERVIEW_PLAN_ACTIVATED_MAKER_BODY =
			"Your activation request has been approved.\n\n"
			+ "Plan Id : %s\n"
			+ "Plan Name : %s\n"
			+ "Approved By : %s\n"
			+ "Approved On : %s";
	
	public static final String INTERVIEW_PLAN_ACTIVATED_CHECKER_BODY =
			"You have successfully approved the activation request.\n\n"
			+ "Plan Id : %s\n"
			+ "Plan Name : %s\n"
			+ "Approved By : %s\n"
			+ "Approved On : %s";
	
	//Activation Rejected
	public static final String INTERVIEW_PLAN_ACTIVATION_REJECTED_MAIL_SUBJECT =
	        "Interview Plan Activation Rejected";

	public static final String INTERVIEW_PLAN_ACTIVATION_REJECTED_MAKER_BODY =
			"Your activation request has been rejected.\n\n"
			+ "Plan Id : %s\n"
			+ "Plan Name : %s\n"
			+ "Comments : %s\n"
			+ "Rejected On : %s";
	
	public static final String INTERVIEW_PLAN_ACTIVATION_REJECTED_CHECKER_BODY =
			"You have rejected the activation request.\n\n"
			+ "Plan Id : %s\n"
			+ "Plan Name : %s\n"
			+ "Comments : %s\n"
			+ "Rejected On : %s";
	
	//Deactivation Request
	public static final String INTERVIEW_PLAN_DEACTIVATION_REQUEST_MAIL_SUBJECT =
	        "Interview Plan Deactivation Request";

	public static final String INTERVIEW_PLAN_DEACTIVATION_REQUEST_MAKER_BODY =
			"Your deactivation request has been submitted successfully.\n\n"
			+ "Plan Id : %s\n"
			+ "Plan Name : %s\n"
			+ "Reason : %s\n"
			+ "Raised By : %s\n"
			+ "Raised On : %s";
	
	public static final String INTERVIEW_PLAN_DEACTIVATION_REQUEST_CHECKER_BODY =
			"A deactivation request is pending for your approval.\n\n"
			+ "Plan Id : %s\n"
			+ "Plan Name : %s\n"
			+ "Reason : %s\n"
			+ "Requested By : %s\n"
			+ "Requested On : %s";
	
	//Deactivated
	public static final String INTERVIEW_PLAN_DEACTIVATED_MAIL_SUBJECT =
	        "Interview Plan Deactivated";

	public static final String INTERVIEW_PLAN_DEACTIVATED_MAKER_BODY =
			"Your deactivation request has been approved.\n\n"
			+ "Plan Id : %s\n"
			+ "Plan Name : %s\n"
			+ "Approved By : %s\n"
			+ "Approved On : %s";
	
	public static final String INTERVIEW_PLAN_DEACTIVATED_CHECKER_BODY =
			"You have successfully approved the deactivation request.\n\n"
			+ "Plan Id : %s\n"
			+ "Plan Name : %s\n"
			+ "Approved By : %s\n"
			+ "Approved On : %s";
	
	//Deactivation Rejected
	

	public static final String INTERVIEW_PLAN_DEACTIVATION_REJECTED_MAIL_SUBJECT =
        "Interview Plan Deactivation Request Rejected";
	
	public static final String INTERVIEW_PLAN_DEACTIVATION_REJECTED_MAKER_BODY =
			"Your deactivation request has been rejected.\n\n"
			+ "Plan Id : %s\n"
			+ "Plan Name : %s\n"
			+ "Comments : %s\n"
			+ "Rejected On : %s";
	
	public static final String INTERVIEW_PLAN_DEACTIVATION_REJECTED_CHECKER_BODY =
			"You have rejected the deactivation request.\n\n"
			+ "Plan Id : %s\n"
			+ "Plan Name : %s\n"
			+ "Comments : %s\n"
			+ "Rejected On : %s";
	public static final String APPLICATION_NOT_FOUND = "Application not found";
	public static final String FILE_NOT_UPLOADED = "File not uploaded";
	public static final String RESUME = "Resume";
	public static final String ADDITIONAL = "Additional";
	public static final String BUCKETNAME = "infospokejobapplicationsbucket";
public static final String MOVE_TO_INTERVIEW ="Move to Interview";
}
