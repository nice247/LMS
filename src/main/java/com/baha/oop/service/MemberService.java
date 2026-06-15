package com.baha.oop.service;

import com.baha.oop.exception.BusinessRuleException;
import com.baha.oop.exception.DuplicateResourceException;
import com.baha.oop.exception.ResourceNotFoundException;
import com.baha.oop.model.Member;
import com.baha.oop.repository.BorrowingRepository;
import com.baha.oop.repository.MemberRepository;
import com.baha.oop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BorrowingRepository borrowingRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
    }

    @Transactional
    public Member saveMember(Member member) {
        if (member.getEmail() != null && !member.getEmail().isEmpty()) {
            Optional<Member> existingMember = memberRepository.findByEmail(member.getEmail());
            if (existingMember.isPresent() && !existingMember.get().getId().equals(member.getId())) {
                throw new DuplicateResourceException("A member with email " + member.getEmail() + " already exists");
            }
        }

        if (member.getPhone() != null && !member.getPhone().isEmpty()) {
            List<Member> existingMembers = memberRepository.findByPhone(member.getPhone());
            if (!existingMembers.isEmpty() && !existingMembers.get(0).getId().equals(member.getId())) {
                throw new DuplicateResourceException("A member with phone " + member.getPhone() + " already exists");
            }
        }

        boolean isNew = (member.getId() == null);
        Member savedMember = memberRepository.save(member);

        if (isNew) {
            String username = savedMember.getEmail();
            if (username == null || username.trim().isEmpty()) {
                username = "member_" + savedMember.getId();
            }

            if (!userRepository.existsByUsername(username)) {
                com.baha.oop.model.User user = com.baha.oop.model.User.builder()
                        .username(username)
                        .password(passwordEncoder.encode("123456"))
                        .email(savedMember.getEmail())
                        .role(com.baha.oop.model.Role.MEMBER)
                        .memberId(savedMember.getId())
                        .build();
                userRepository.save(user);
            }
        }

        return savedMember;
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
        Long activeBorrowings = borrowingRepository.countActiveBorrowingsByMember(id);
        if (activeBorrowings > 0) {
            throw new BusinessRuleException("Cannot delete a member with " + activeBorrowings + " active borrowing(s)");
        }
        memberRepository.delete(member);
    }

    public List<Member> searchMembersByName(String name) {
        return memberRepository.findByNameContainingIgnoreCase(name);
    }
}