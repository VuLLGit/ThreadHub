package com.example.ThreadHub.controller;

import com.example.ThreadHub.dto.request.CommunityRequest;
import com.example.ThreadHub.dto.request.PostRequest;
import com.example.ThreadHub.dto.response.CommunityResponse;
import com.example.ThreadHub.dto.response.PostResponse;
import com.example.ThreadHub.entity.*;
import com.example.ThreadHub.entity.enums.CommunityStatus;
import com.example.ThreadHub.service.CommunityMemberService;
import com.example.ThreadHub.service.CommunityService;
import com.example.ThreadHub.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/community")
public class CommunityPublicController {

    private CommunityService comunityService;
    private CommunityMemberService communityMemberService;
    private PostService postService;

    @Autowired
    public CommunityPublicController(CommunityService comunityService, CommunityMemberService communityMemberService, PostService postService) {
        this.comunityService = comunityService;
        this.communityMemberService = communityMemberService;
        this.postService = postService;
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
}
