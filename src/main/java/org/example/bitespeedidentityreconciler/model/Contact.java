package org.example.bitespeedidentityreconciler.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.bitespeedidentityreconciler.enums.LinkPrecedence;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact")
@Getter
@Setter
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String phoneNumber;

    private String email;

    @ManyToOne
    @JoinColumn(name = "linkedId")
    private Contact linkedContact;

    @Enumerated(EnumType.STRING)
    private LinkPrecedence linkPrecedence;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}