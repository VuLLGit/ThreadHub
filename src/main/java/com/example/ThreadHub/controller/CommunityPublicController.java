package com.example.ThreadHub.controller;

import com.example.ThreadHub.dto.request.CreateCommunityRequest;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.CommunityMember;
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
public class CommunityPublicController {

    private CommunityService comunityService;
    private CommunityMemberService communityMemberService;

    @Autowired
    private CommunityPublicController(CommunityService comunityService, CommunityMemberService communityMemberService) {
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
        CommunityMember actorCommunityMember = communityMemberService.findByCommunityIdAndAccountId(communityId, account.getId());

        if (communityMemberService.isOwner(actorCommunityMember.getId())){
            return ResponseEntity.badRequest().body("You need to transfer Owner before leaving community");
        }

        communityMemberService.leaveCommunity(communityId, account.getId());
        return ResponseEntity.ok().body("Left community successfully");
    }
}
