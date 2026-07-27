package com.hms.service.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResumeReuploadRequestedEvent {

	private final Integer applicationId;

}
