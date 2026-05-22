package com.hms.service.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateJobRequest {

  
   private CreateJobDetailsRequest createJobDetailsRequest;
   
   private SourcingChannelRequest sourcingChannelRequest;
   
   private RecuriterAssignmentRequest recuriterAssignmentRequest;
   
   private JobDescriptionRequest jobDescriptionRequest;
  
   private String srId;
   
   private Boolean submit;
   
}
