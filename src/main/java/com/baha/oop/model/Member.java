package com.baha.oop.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq")
    @SequenceGenerator(name = "member_seq", sequenceName = "MEMBER_SEQ", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String email;

    private String phone;
    // إضافة هذه الدوال إلى كيان Member
    @Transient
    public boolean hasActiveBorrowings(List<Borrowing> borrowings) {
        return borrowings.stream()
                .anyMatch(b -> b.getMember().getId().equals(this.id) && b.isActive());
    }

    @Transient
    public long getActiveBorrowingsCount(List<Borrowing> borrowings) {
        return borrowings.stream()
                .filter(b -> b.getMember().getId().equals(this.id) && b.isActive())
                .count();
    }
}