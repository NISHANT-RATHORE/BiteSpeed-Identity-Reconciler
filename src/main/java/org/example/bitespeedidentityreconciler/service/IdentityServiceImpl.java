package org.example.bitespeedidentityreconciler.service;


import org.example.bitespeedidentityreconciler.dto.IdentityRequest;
import org.example.bitespeedidentityreconciler.dto.IdentityResponse;
import org.example.bitespeedidentityreconciler.enums.LinkPrecedence;
import org.example.bitespeedidentityreconciler.model.Contact;
import org.example.bitespeedidentityreconciler.repository.ContactRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IdentityServiceImpl implements IdentityService {

    private final ContactRepository contactRepository;

    public IdentityServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    @Transactional
    public IdentityResponse identityContact(IdentityRequest request) {
        List<Contact> existingContacts = contactRepository.findByEmailOrPhoneNumber(
                request.getEmail(), request.getPhoneNumber());

        if (existingContacts.isEmpty()) {
            Contact newContact = createNewPrimaryContact(request);
            return buildResponse(newContact, Collections.emptyList());
        }

        // Determine primary and secondary contacts from the search
        List<Contact> primaryContacts = new ArrayList<>();
        List<Contact> secondaryContacts = new ArrayList<>();

        for (Contact contact : existingContacts) {
            if (contact.getLinkPrecedence() == LinkPrecedence.PRIMARY) {
                primaryContacts.add(contact);
            } else {
                secondaryContacts.add(contact);
            }
        }

        // Find the ultimate primary contact
        Contact primaryParent = findOldestPrimaryContact(primaryContacts, secondaryContacts);

        // Merge primary contacts if necessary
        mergePrimaryContacts(primaryContacts, primaryParent);

        // Check if a new secondary contact needs to be created
        createNewSecondaryIfNeeded(request, primaryParent, existingContacts);

        // Fetch all related contacts for the response
        List<Contact> allRelatedContacts = new ArrayList<>();
        allRelatedContacts.add(primaryParent);
        allRelatedContacts.addAll(contactRepository.findByLinkedContact(primaryParent));


        return buildResponse(primaryParent, allRelatedContacts);
    }

    private Contact createNewPrimaryContact(IdentityRequest request) {
        Contact contact = new Contact();
        contact.setEmail(request.getEmail());
        contact.setPhoneNumber(request.getPhoneNumber());
        contact.setLinkPrecedence(LinkPrecedence.PRIMARY);
        return contactRepository.save(contact);
    }

    private Contact findOldestPrimaryContact(List<Contact> primaryContacts, List<Contact> secondaryContacts) {
        // Start with the oldest primary from the direct matches
        Contact oldest = primaryContacts.stream()
                .min(Comparator.comparing(Contact::getCreatedAt))
                .orElse(null);

        // If no direct primary match, find the primary of the oldest secondary contact
        if (oldest == null) {
            return secondaryContacts.stream()
                    .min(Comparator.comparing(Contact::getCreatedAt))
                    .map(Contact::getLinkedContact)
                    .orElseThrow(() -> new IllegalStateException("No primary contact found"));
        }
        return oldest;
    }

    private void mergePrimaryContacts(List<Contact> primaryContacts, Contact mainPrimary) {
        for (Contact contact : primaryContacts) {
            if (!contact.getId().equals(mainPrimary.getId())) {
                contact.setLinkPrecedence(LinkPrecedence.SECONDARY);
                contact.setLinkedContact(mainPrimary);
                contactRepository.save(contact);
            }
        }
    }

    private void createNewSecondaryIfNeeded(IdentityRequest request, Contact primaryContact, List<Contact> existingContacts) {
        boolean emailExists = existingContacts.stream().anyMatch(c -> request.getEmail() != null && request.getEmail().equals(c.getEmail()));
        boolean phoneExists = existingContacts.stream().anyMatch(c -> request.getPhoneNumber() != null && request.getPhoneNumber().equals(c.getPhoneNumber()));

        if (!emailExists || !phoneExists) {
            Contact newSecondary = new Contact();
            newSecondary.setEmail(request.getEmail());
            newSecondary.setPhoneNumber(request.getPhoneNumber());
            newSecondary.setLinkPrecedence(LinkPrecedence.SECONDARY);
            newSecondary.setLinkedContact(primaryContact);
            contactRepository.save(newSecondary);
        }
    }

    private IdentityResponse buildResponse(Contact primary, List<Contact> allContacts) {
        Set<String> emails = new LinkedHashSet<>();
        Set<String> phoneNumbers = new LinkedHashSet<>();
        List<Integer> secondaryIds = new ArrayList<>();

        // Add primary first to ensure order
        emails.add(primary.getEmail());
        phoneNumbers.add(primary.getPhoneNumber());

        for (Contact c : allContacts) {
            emails.add(c.getEmail());
            phoneNumbers.add(c.getPhoneNumber());
            if (c.getLinkPrecedence() == LinkPrecedence.SECONDARY) {
                secondaryIds.add(c.getId());
            }
        }

        List<String> finalEmails = emails.stream().filter(Objects::nonNull).collect(Collectors.toList());
        List<String> finalPhoneNumbers = phoneNumbers.stream().filter(Objects::nonNull).collect(Collectors.toList());

        IdentityResponse.ContactPayload payload = new IdentityResponse.ContactPayload(
                primary.getId(),
                finalEmails,
                finalPhoneNumbers,
                secondaryIds
        );
        return new IdentityResponse(payload);
    }
}
