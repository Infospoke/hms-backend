package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.request.ClientRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.service.IClientService;
import com.hms.service.wrappers.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/hms/client")

public class ClientManagementController {

	@Autowired
	private IClientService clientService;

	@PostMapping("/add-client")
	public ResponseEntity<ApiResponse<?>> createClient(@Valid @RequestBody ClientRequest request) {

		ApiResponse<?> response = clientService.createClient(request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/list")
	public ResponseEntity<ApiResponse<?>> getClientList(@RequestBody SpecificationFilterRequest request) {
		ApiResponse<?> response = clientService.getClientList(request);
		return new ResponseEntity<>(response, HttpStatus.OK);

	}
	
	@GetMapping("/client-details/{id}")
	public ResponseEntity<ApiResponse<?>> getClientById(
	        @PathVariable("id") Integer id) {

	    ApiResponse<?> response = clientService.getClientById(id);

	    return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
