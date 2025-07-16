package org.example.bitespeedidentityreconciler.controller;

import org.example.bitespeedidentityreconciler.dto.IdentityRequest;
import org.example.bitespeedidentityreconciler.dto.IdentityResponse;
import org.example.bitespeedidentityreconciler.service.IdentityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/identity")
public class IdentityController {

    private final IdentityService identityService;

    public IdentityController(IdentityService identityService) {
        this.identityService = identityService;
    }

    @PostMapping
    public ResponseEntity<IdentityResponse> identify(@RequestBody IdentityRequest request) {
        IdentityResponse response = identityService.identityContact(request);
        return ResponseEntity.ok(response);
    }
}