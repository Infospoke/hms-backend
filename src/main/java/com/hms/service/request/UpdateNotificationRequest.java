package com.hms.service.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNotificationRequest {
	private List<Integer> ids;
    private Boolean isRead;


}
