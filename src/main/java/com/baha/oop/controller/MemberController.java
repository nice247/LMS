// MemberController.java
package com.baha.oop.controller;

import com.baha.oop.exception.ResourceNotFoundException;
import com.baha.oop.model.Member;
import com.baha.oop.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping
    public String listMembers(@RequestParam(required = false) String name, Model model) {
        List<Member> members;

        if (name != null && !name.isEmpty()) {
            members = memberService.searchMembersByName(name);
        } else {
            members = memberService.getAllMembers();
        }

        model.addAttribute("members", members);
        model.addAttribute("name", name);

        return "members";
    }

    @GetMapping("/new")
    public String showMemberForm(Model model) {
        model.addAttribute("member", new Member());
        return "member-form";
    }

    @PostMapping("/save")
    public String saveMember(@ModelAttribute Member member, RedirectAttributes redirectAttributes) {
        try {
            memberService.saveMember(member);
            redirectAttributes.addFlashAttribute("message", "تم حفظ العضو بنجاح");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "خطأ في حفظ العضو: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/members";
    }

    @GetMapping("/{id}/edit")
    public String editMember(@PathVariable Long id, Model model) {
        try {
            Member member = memberService.getMemberById(id);
            model.addAttribute("member", member);
            return "member-form";
        } catch (ResourceNotFoundException e) {
            return "redirect:/members";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteMember(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            memberService.deleteMember(id);
            redirectAttributes.addFlashAttribute("message", "تم حذف العضو بنجاح");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "خطأ في حذف العضو: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/members";
    }

    @GetMapping("/{id}")
    public String viewMember(@PathVariable Long id, Model model) {
        try {
            Member member = memberService.getMemberById(id);
            model.addAttribute("member", member);
            return "member-details";
        } catch (ResourceNotFoundException e) {
            return "redirect:/members";
        }
    }
}