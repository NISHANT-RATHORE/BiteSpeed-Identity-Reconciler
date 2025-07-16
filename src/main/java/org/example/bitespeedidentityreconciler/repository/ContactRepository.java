package org.example.bitespeedidentityreconciler.repository;

import org.example.bitespeedidentityreconciler.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

    List<Contact> findByEmailOrPhoneNumber(String email, String phoneNumber);

    Collection<? extends Contact> findByLinkedContact(Contact primaryParent);
}
