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

        if (!comunityService.isNameAvailable(communityRequest.getName())) {
            return ResponseEntity.badRequest().body("Community name already exists");
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
        CommunityMember actorCommunityMember = communityMemberService.findByCommunityIdAndAccountId(communityId, account.getId());
        Community community = comunityService.getCommunityById(communityId);

        if (community.getCommunityStatus() == CommunityStatus.INACTIVE) {
            return ResponseEntity.badRequest().body("Community is inactive");
        }

        if (actorCommunityMember != null) {
            return ResponseEntity.badRequest().body("You are already a member of this community");
        }

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

        if (actorCommunityMember == null) {
            return ResponseEntity.badRequest().body("You are not a member of this community");
        }

        if (communityMemberService.isOwner(actorCommunityMember.getId())){
            return ResponseEntity.badRequest().body("You need to transfer Owner before leaving community");
        }

        communityMemberService.leaveCommunity(actorCommunityMember);
        return ResponseEntity.ok().body("Left community successfully");
    }

    @GetMapping("/my-communities")
    public ResponseEntity<?> getMyCommunities(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();

        return ResponseEntity.ok().body(comunityService.getAllCommunitiesByAccountId(account.getId()));
    }

    @PostMapping("/{communityId}/post/create")
    public ResponseEntity<?> createPost(@Valid @RequestBody PostRequest postRequest,
                                        @PathVariable Long communityId,
                                        Authentication authentication,
                                        BindingResult bindingResult) {

        if (authentication == null) {
            return ResponseEntity.status(401).body("unauthorized");
        }

        Account account = (Account) authentication.getPrincipal();
        Community community = comunityService.getCommunityById(communityId);

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errorMessage);
        }

        PostResponse postResponse = postService.createPost(postRequest, account, community);
        
        return ResponseEntity.ok(postResponse);
    }

    @PostMapping(value ="/post/{postId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPostFiles(@PathVariable Long postId,
                                             @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        // Kiểm tra nếu không có file
        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body("Không có file nào được upload");
        }

        // Kiểm tra file rỗng
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File rỗng: " + file.getOriginalFilename());
            }
        }

        Post post = postService.getPostById(postId);
        PostResponse postResponse = postService.uploadFilesToPost(post, files);

        return ResponseEntity.ok(postResponse);
    }
}
