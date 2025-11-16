package com.example.ThreadHub.controller;

import com.example.ThreadHub.dto.request.CommunityRequest;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.service.CommunityMemberService;
import com.example.ThreadHub.service.CommunityService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private CommunityService comunityService;
    private CommunityMemberService communityMemberService;

    @Autowired
    private CommunityController(CommunityService comunityService, CommunityMemberService communityMemberService) {
        this.comunityService = comunityService;
        this.communityMemberService = communityMemberService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCommunity(@Valid @RequestBody CommunityRequest communityRequest,
                                             BindingResult bindingResult,
                                             Authentication authentication) {
        // validate authentication
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errorMessage);
        }

        if (!comunityService.isNameAvailable(communityRequest.getName())) {
            return ResponseEntity.badRequest().body("Community name already exists");
        }
        comunityService.createCommunity(communityRequest, account);
        return ResponseEntity.ok().body("Community created successfully");
    }

    @GetMapping("/{communityId}/members")
    public ResponseEntity<?> getCommunityMembers(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "7") int size,
                                                 @PathVariable Long communityId) {
        return ResponseEntity.ok().body(communityMemberService.getCommunityMembers(page, size, "createdAt", communityId));
    }
}
