package com.hms.service.request;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateJobRequest {

   @Valid
   private CreateJobDetailsRequest createJobDetailsRequest;
   
   @Valid
   private SourcingChannelRequest sourcingChannelRequest;
   
   @Valid
   private RecuriterAssignmentRequest recuriterAssignmentRequest;
   
   @Valid
   private JobDescriptionRequest jobDescriptionRequest;
   
   @Valid
   private JobCreationReviewRequest jobCreationReviewRequest;
   
   
}
