// MemberService.java
package com.baha.oop.service;

import com.baha.oop.exception.ResourceNotFoundException;
import com.baha.oop.model.Member;
import com.baha.oop.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
    }

    public Member saveMember(Member member) {
        // التحقق من البريد الإلكتروني إذا كان موجوداً
        if (member.getEmail() != null && !member.getEmail().isEmpty()) {
            Optional<Member> existingMember = memberRepository.findByEmail(member.getEmail());
            if (existingMember.isPresent() && !existingMember.get().getId().equals(member.getId())) {
                throw new RuntimeException("البريد الإلكتروني يجب أن يكون فريداً");
            }
        }

        // التحقق من الهاتف إذا كان موجوداً
        if (member.getPhone() != null && !member.getPhone().isEmpty()) {
            List<Member> existingMembers = memberRepository.findByPhone(member.getPhone());
            if (!existingMembers.isEmpty() && !existingMembers.get(0).getId().equals(member.getId())) {
                throw new RuntimeException("رقم الهاتف يجب أن يكون فريداً");
            }
        }

        return memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
        memberRepository.delete(member);
    }

    public List<Member> searchMembersByName(String name) {
        return memberRepository.findByNameContainingIgnoreCase(name);
    }
}