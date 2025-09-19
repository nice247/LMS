// MemberRepository.java
package com.baha.oop.repository;

import com.baha.oop.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByNameContainingIgnoreCase(String name);
    Optional<Member> findByEmail(String email);
    List<Member> findByPhone(String phone);
}