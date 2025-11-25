package com.example.ThreadHub.controller;

import com.example.ThreadHub.dto.request.CommunityRequest;
import com.example.ThreadHub.dto.response.CommunityMemberResponse;
import com.example.ThreadHub.dto.response.CommunityResponse;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.CommunityMember;
import com.example.ThreadHub.entity.enums.MemberRole;
import com.example.ThreadHub.service.CommunityMemberService;
import com.example.ThreadHub.service.CommunityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/communities")
public class CommunityController {

    private CommunityService comunityService;
    private CommunityMemberService communityMemberService;

    @Autowired
    public CommunityController(CommunityService comunityService, CommunityMemberService communityMemberService) {
        this.comunityService = comunityService;
        this.communityMemberService = communityMemberService;
    }

    // user action
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

        CommunityResponse communityResponse = comunityService.createCommunity(communityRequest, account);
        return ResponseEntity.ok(communityResponse);
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

    @DeleteMapping("/{communityId}/leave")
    public ResponseEntity<?> leaveCommunity(@PathVariable Long communityId,
                                            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();

        communityMemberService.leaveCommunity(communityId, account.getId());
        return ResponseEntity.ok().body("Left community successfully");
    }

    // Moderator actions
    @GetMapping("/{communityId}/members")
    public ResponseEntity<?> getCommunityMembers(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "7") int size,
                                                 @PathVariable Long communityId,
                                                 Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember = communityMemberService.findByCommunityIdAndAccountId(communityId, account.getId());

        if (!actorCommunityMember.getMemberRole().equals(MemberRole.MODERATOR) && (!actorCommunityMember.getMemberRole().equals(MemberRole.OWNER))) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        Page<CommunityMemberResponse> communityMembers = communityMemberService.getCommunityMembers(page, size, "createdAt", communityId);

        return ResponseEntity.ok(communityMembers);
    }

    @PatchMapping("/{communityId}/members/{communityMemberId}/assign-moderator")
    public ResponseEntity<?> assignModerator(@PathVariable Long communityMemberId,
                                             @PathVariable Long communityId,
                                             Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember = communityMemberService.findByCommunityIdAndAccountId(communityId, account.getId());

        if (!actorCommunityMember.getMemberRole().equals(MemberRole.MODERATOR) && !actorCommunityMember.getMemberRole().equals(MemberRole.OWNER)){
            return ResponseEntity.status(403).body("Forbidden");
        }

        CommunityMemberResponse communityMemberResponse = communityMemberService.assignModerator(communityMemberId);

        return ResponseEntity.ok(communityMemberResponse);
    }

    @PatchMapping("/{communityId}/members/{communityMemberId}/remove-moderator")
    public ResponseEntity<?> removeModerator(@PathVariable Long communityMemberId,
                                             @PathVariable Long communityId,
                                             Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember = communityMemberService.findByCommunityIdAndAccountId(communityId, account.getId());

        if (!actorCommunityMember.getMemberRole().equals(MemberRole.OWNER)){
            return ResponseEntity.status(403).body("Forbidden");
        }

        CommunityMemberResponse communityMemberResponse = communityMemberService.removeModerator(communityMemberId);

        return ResponseEntity.ok(communityMemberResponse);
    }

    @PatchMapping("/{communityId}/members/{communityMemberId}/transfer-owner")
    public ResponseEntity<?> transferOwner(@PathVariable Long communityMemberId,
                                           @PathVariable Long communityId,
                                           Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember = communityMemberService.findByCommunityIdAndAccountId(communityId, account.getId());

        if (!actorCommunityMember.getMemberRole().equals(MemberRole.OWNER)){
            return ResponseEntity.status(403).body("Forbidden");
        }

        CommunityMemberResponse communityMemberResponse = communityMemberService.transferOwner(actorCommunityMember.getId(), communityMemberId);

        return ResponseEntity.ok(communityMemberResponse);
    }

    @PatchMapping("/{communityId}/edit")
    public ResponseEntity<?> editCommunity(@PathVariable Long communityId,
                                           Authentication authentication,
                                           @Valid @RequestBody CommunityRequest communityRequest,
                                           BindingResult bindingResult) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember = communityMemberService.findByCommunityIdAndAccountId(communityId, account.getId());

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errorMessage);
        }

        if (!actorCommunityMember.getMemberRole().equals(MemberRole.MODERATOR) && !actorCommunityMember.getMemberRole().equals(MemberRole.OWNER)){
            return ResponseEntity.status(403).body("Forbidden");
        }

        CommunityResponse communityResponse = comunityService.editCommunity(communityRequest, communityId);

        return ResponseEntity.ok(communityResponse);
    }

    @PatchMapping("/{communityId}/inactive")
    public ResponseEntity<?> inactiveCommunity(@PathVariable Long communityId,
                                               Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember = communityMemberService.findByCommunityIdAndAccountId(communityId, account.getId());

        if (!actorCommunityMember.getMemberRole().equals(MemberRole.MODERATOR) && !actorCommunityMember.getMemberRole().equals(MemberRole.OWNER)){
            return ResponseEntity.status(403).body("Forbidden");
        }

        CommunityResponse communityResponse = comunityService.inactivateCommunity(communityId);

        return ResponseEntity.ok(communityResponse);
    }

    @PatchMapping("/{communityId}/active")
    public ResponseEntity<?> activeCommunity(@PathVariable Long communityId,
                                             Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember = communityMemberService.findByCommunityIdAndAccountId(communityId, account.getId());

        if (!actorCommunityMember.getMemberRole().equals(MemberRole.MODERATOR) && !actorCommunityMember.getMemberRole().equals(MemberRole.OWNER)){
            return ResponseEntity.status(403).body("Forbidden");
        }

        CommunityResponse communityResponse = comunityService.activateCommunity(communityId);

        return ResponseEntity.ok(communityResponse);
    }
}
