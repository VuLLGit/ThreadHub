package com.example.ThreadHub.controller;

import com.example.ThreadHub.dto.request.CreateCommunityRequest;
import com.example.ThreadHub.dto.request.UpdateCommunityRequest;
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
    public ResponseEntity<?> createCommunity(@Valid @RequestBody CreateCommunityRequest createCommunityRequest,
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

        if (!comunityService.isNameAvailable(createCommunityRequest.getName())) {
            return ResponseEntity.badRequest().body("Community name already exists");
        }
        comunityService.createCommunity(createCommunityRequest, account);
        return ResponseEntity.ok().body("Community created successfully");
    }

    @GetMapping("/{communityId}/members")
    public ResponseEntity<?> getCommunityMembers(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "7") int size,
                                                 @PathVariable Long communityId,
                                                 Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();

        if (!communityMemberService.IsModerator(communityId, account.getId())) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        return ResponseEntity.ok().body(communityMemberService.getCommunityMembers(page, size, "createdAt", communityId));
    }

    @PatchMapping("/{communityId}/members/{communityMemberId}/assign-moderator")
    public ResponseEntity<?> assignModerator(@PathVariable Long communityMemberId,
                                            @PathVariable Long communityId,
                                            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();

        if (!communityMemberService.IsModerator(communityId, account.getId())) {
            return ResponseEntity.badRequest().body("unauthorized");
        }

        communityMemberService.assignModerator(communityMemberId);
        return ResponseEntity.ok().body("assign moderator successfully");
    }

    @PatchMapping("/{communityId}/members/{communityMemberId}/remove-moderator")
    public ResponseEntity<?> removeModerator(@PathVariable Long communityMemberId,
                                            @PathVariable Long communityId,
                                            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();

        if (!communityMemberService.IsModerator(communityId, account.getId())) {
            return ResponseEntity.badRequest().body("unauthorized");
        }

        communityMemberService.removeModerator(communityMemberId);
        return ResponseEntity.ok().body("remove moderator successfully");
    }

    @PatchMapping("/{communityId}/edit")
    public ResponseEntity<?> editCommunity(@PathVariable Long communityId,
                                           Authentication authentication,
                                           @RequestBody UpdateCommunityRequest updateCommunityRequest) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();

        if (!communityMemberService.IsModerator(communityId, account.getId())) {
            return ResponseEntity.badRequest().body("unauthorized");
        }

        comunityService.EditCommunity(updateCommunityRequest, communityId);

        return ResponseEntity.ok().body("Community edited successfully");
    }

    @PatchMapping("/{communityId}/inactive")
    public ResponseEntity<?> inactiveCommunity(@PathVariable Long communityId,
                                             Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();

        if (!communityMemberService.IsModerator(communityId, account.getId())) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        comunityService.inactivateCommunity(communityId);
        return ResponseEntity.ok().body("Community inactivated successfully");
    }

    @PatchMapping("/{communityId}/active")
    public ResponseEntity<?> activeCommunity(@PathVariable Long communityId,
                                             Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();

        if (!communityMemberService.IsModerator(communityId, account.getId())) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        comunityService.activateCommunity(communityId);
        return ResponseEntity.ok().body("Community activated successfully");
    }

    @PostMapping("/{communityId}/join")
    public ResponseEntity<?> joinCommunity(@PathVariable Long communityId,
                                           Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();
        communityMemberService.joinCommunity(communityId, account.getId());
        return ResponseEntity.ok().body("Joined community successfully");
    }
}
