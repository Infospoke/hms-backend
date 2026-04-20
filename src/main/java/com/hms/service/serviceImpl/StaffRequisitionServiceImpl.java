package com.hms.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hms.service.constants.Constants;
import com.hms.service.entity.StaffingRequisitionEntity;
import com.hms.service.entity.StaffingRequisitionEntitys;
import com.hms.service.repository.Staffing;
import com.hms.service.repository.StaffingRequisitionRepository;
import com.hms.service.request.StaffingRequisitionRequest;
import com.hms.service.service.IStaffingRequisitionService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class StaffRequisitionServiceImpl implements IStaffingRequisitionService{
	
	@Autowired
	private StaffingRequisitionRepository staffingRequisitionRepository;
	
	@Autowired
	private Staffing staffing;
	
	@Autowired
	private MinioClient minioClient;
	@Override
//	public ApiResponse<?>newStaffingRequisition(StaffingRequisitionRequest request) {
////		StaffingRequisitionEntity staffingRequisitionEntity=new StaffingRequisitionEntity();
//		StaffingRequisitionEntitys u = new StaffingRequisitionEntitys();
//		u.setJobTitle(request.getJobTitle());
//		u.setBusinessUnit(request.getBusinessUnit());
//		u.setDepartment(request.getDepartment());
//		u.setReportingManagerInfo(request.getReportingManagerInfo());
//		u.setLocation(request.getLocation());
//		u.setSeniorityLevel(request.getSeniorityLevel());
//		u.setOpenings(request.getOpenings());
//		u.setTargetStartDate(request.getTargetStartDate());
//		u.setWorkMode(request.getWorkMode());
//		u.setEmploymentType(request.getEmploymentType());
//		u.setPriority(request.getPriority());
////		staffingRequisitionRepository.save(staffingRequisitionEntity);
//		staffing.save(u);
//		return new ApiResponse<>(ResponseCode.SUCCESS,"created successfully");
//		
//	}
	
	public ApiResponse<?> newStaffingRequisition(StaffingRequisitionRequest request,MultipartFile file) {

	    StaffingRequisitionEntitys staffingRequisitionEntitys = new StaffingRequisitionEntitys();
//       int id= staffing.findTopByOrderByIdDesc();
//       System.out.println("The id from the repo is : "+id);
	    staffingRequisitionEntitys.setJobTitle(request.getJobTitle());
	    staffingRequisitionEntitys.setBusinessUnitId(request.getBusinessUnitId());
	    staffingRequisitionEntitys.setDepartmentId(request.getDepartmentId());
	    staffingRequisitionEntitys.setReportingManagerInfo(request.getReportingManagerInfo());
	    staffingRequisitionEntitys.setLocation(request.getLocation());
	    staffingRequisitionEntitys.setSeniorityLevel(request.getSeniorityLevel());
	    staffingRequisitionEntitys.setOpenings(request.getOpenings());
	    staffingRequisitionEntitys.setTargetStartDate(request.getTargetStartDate());
	    staffingRequisitionEntitys.setWorkMode(request.getWorkMode());
	    staffingRequisitionEntitys.setEmploymentType(request.getEmploymentType());
	    staffingRequisitionEntitys.setPriority(request.getPriority());
	    staffingRequisitionEntitys.setRequisitionType(request.getRequisitionType());
	    staffingRequisitionEntitys.setBusinessCase(request.getBusinessCase());
	    staffingRequisitionEntitys.setImpactIfNotFilled(request.getImpactIfNotFilled());
	    staffingRequisitionEntitys.setReplacesEmployee(request.getReplacesEmployee());
	    try {
			String fileKey = Constants.BUCKET  + Constants.UNDER_SCORE
					 + Constants.UNDER_SCORE +
					Constants.UNDER_SCORE + file.getOriginalFilename();
			uploadToMinio(file, fileKey);

			staffingRequisitionEntitys.setDocument(fileKey);
		} catch (Exception e) {
			//log.info("::Error Ocurred in Uploading to Minio Method" + e.getMessage());
		}
	    
	    staffingRequisitionEntitys.setProposedTotalCompensation(request.getProposedTotalCompensation());
	   
	    if (Boolean.TRUE.equals(request.getSigningBonus())) {
	        staffingRequisitionEntitys.setSigningBonusAmount(
	            request.getSigningBonusAmount()
	        );
	    }

	    if (Boolean.TRUE.equals(request.getEquity())) {
	        staffingRequisitionEntitys.setEquityAmount(
	            request.getEquityAmount()
	        );
	    }

	    if (Boolean.TRUE.equals(request.getRelocationBudget())) {
	        staffingRequisitionEntitys.setRelocationBudgetAmount(
	            request.getRelocationBudgetAmount()
	        );
	    }
	    staffingRequisitionEntitys.setAnnualHiringCost(request.getAnnualHiringCost());
	   
	    staffing.save(staffingRequisitionEntitys);
	    
	    return new ApiResponse<>(ResponseCode.SUCCESS, "created successfully");
	}
	private void uploadToMinio(MultipartFile offerLetter, String fileKey) throws Exception {

		log.info("staffingRequisitonServiceImpl::Inside uploadToMinio method");

		minioClient.putObject(PutObjectArgs.builder().bucket(Constants.BUCKET).object(fileKey)
				.stream(offerLetter.getInputStream(), offerLetter.getSize(), -1)
				.contentType(offerLetter.getContentType()).build());

		log.info("staffingRequisitionServiceImpl::Exit from uploadToMinio method");
	}
	    // 🔥 Generate SR ID
//	    String srId = generateSrId(
//	            request.getJobTitle(),
//	            request.getBusinessUnit()   // assuming this is int
//	    );
//
//	    u.setSrId(srId);  // ✅ IMPORTANT

	  
//	}
//	
//	private String generateSrId(String jobTitle, int businessUnitId) {
//
//        String prefix = "SR";
//
//        int year = java.time.Year.now().getValue();
//
//        String jobCode = getJobCode(jobTitle);
//
//        String formattedId = formatBusinessId(businessUnitId);
//
//        return prefix + "-" + year + "-" + jobCode + "-" + formattedId;
//    }
//
//    // 👉 HELPER METHODS ALSO BELOW
//    private String getJobCode(String jobTitle) {
//        String[] words = jobTitle.split(" ");
//        StringBuilder code = new StringBuilder();
//
//        for (String word : words) {
//            code.append(word.charAt(0));
//        }
//
//        return code.toString().toUpperCase();
//    }
//
//    private String formatBusinessId(int businessUnitId) {
//        return String.format("%04d", businessUnitId);
//    }


	public void test() {
	    StaffingRequisitionEntitys u = new StaffingRequisitionEntitys();
	    u.setJobTitle("Venkat");
	    
	    staffing.save(u);
	}
	
}
