package org.example.bitespeedidentityreconciler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class IdentityResponse {
    private ContactPayload contact;
    @Getter
    @Setter
    @AllArgsConstructor
    public static class ContactPayload {
        private Integer primaryContactId;
        private List<String> emails;
        private List<String> phoneNumbers;
        private List<Integer> secondaryContactIds;
    }
}
