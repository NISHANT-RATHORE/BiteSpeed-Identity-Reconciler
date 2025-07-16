# Identity Reconciliation Service for Bitespeed

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)

---
## 🌐 Hosted Endpoint

The service is live and can be accessed at the following endpoint.

**POST URL**: `https://bitespeed-identity-reconciler-1.onrender.com/identify`

---

## 📋 Assignment Overview

This project is a robust backend service designed to solve a common Customer Data Platform challenge: **identity reconciliation**. For a platform like **Bitespeed**, which aims to provide a personalized customer experience, it's crucial to know that multiple orders from the same person (even with different contact details) belong to a single, unified identity.

This service exposes a single API endpoint, `/identify`, that intelligently processes incoming customer contact information (email and/or phone number). It identifies existing customer profiles or creates new ones, and is capable of merging distinct customer profiles when a new request links them together. The core logic ensures that for any given customer, there is always a single "primary" contact that acts as the source of truth, with all other associated contacts being "secondary".

The primary goal is to maintain a consistent and consolidated view of each customer, which is fundamental for analytics, marketing, and providing a seamless user experience.

---

## ✨ Core Features

* **Contact Identification**: Processes incoming requests containing an email and/or a phone number to identify a customer.
* **New Contact Creation**: If no existing contact is found, it creates a new **primary** contact record.
* **Secondary Contact Linking**: If a request shares some information with an existing contact but also provides new details, it creates a new **secondary** contact linked to the original primary contact.
* **Identity Merging**: Intelligently merges two previously separate customer identities if a new request links them. The older contact record is preserved as the primary, ensuring consistency.
* **Consolidated Response**: Always returns a single, unified JSON response detailing the customer's entire contact profile, including one primary contact ID, all associated emails, all phone numbers, and all secondary contact IDs.
* **Transactional Integrity**: All database operations for a single request are performed within a single transaction to prevent data inconsistency.

---

## 🛠️ Tech Stack

* **Language**: Java 21
* **Framework**: Spring Boot 3
* **Data Persistence**: Spring Data JPA with Hibernate
* **Database**: PostgreSQL
* **Build Tool**: Maven

---

## 🚀 API Endpoint Details

The service exposes one primary endpoint for all identity operations.

### `POST /identify`

This endpoint is the core of the service. It receives contact information and returns the consolidated contact profile.

#### Request Body

The request body must be a JSON object with at least one of the two keys.

```json
{
  "email": "mcfly@hillvalley.edu",
  "phoneNumber": "123456"
}
```

#### Response Body

The service will return a consolidated contact view. The first email and phone number in the arrays always belong to the primary contact.

```json
{
    "contact": {
        "primaryContactId": 1,
        "emails": [
            "lorraine@hillvalley.edu",
            "mcfly@hillvalley.edu"
        ],
        "phoneNumbers": [
            "123456"
        ],
        "secondaryContactIds": [
            23
        ]
    }
}
```
