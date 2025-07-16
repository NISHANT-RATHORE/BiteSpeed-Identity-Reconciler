package org.example.bitespeedidentityreconciler.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdentityRequest {
    private String email;
    private String phoneNumber;
}
