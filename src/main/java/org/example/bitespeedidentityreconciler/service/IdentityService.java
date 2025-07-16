package org.example.bitespeedidentityreconciler.service;


import org.example.bitespeedidentityreconciler.dto.IdentityRequest;
import org.example.bitespeedidentityreconciler.dto.IdentityResponse;
import org.springframework.stereotype.Service;

@Service
public interface IdentityService {
    IdentityResponse identityContact(IdentityRequest request);
}
