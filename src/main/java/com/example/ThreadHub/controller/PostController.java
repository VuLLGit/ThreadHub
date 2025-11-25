package com.example.ThreadHub.controller;

import com.example.ThreadHub.dto.request.PostRequest;
import com.example.ThreadHub.dto.response.PostResponse;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Community;
import com.example.ThreadHub.entity.Post;
import com.example.ThreadHub.service.CommunityService;
import com.example.ThreadHub.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    CommunityService communityService;
    PostService postService;

    public PostController(CommunityService comunityService, PostService postService) {
        this.communityService = comunityService;
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<?> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(defaultValue = "createdAt") String sortBy,
                                         @RequestParam(defaultValue = "") String search) {
        Page<PostResponse> posts = postService.getAllPosts(page, size, sortBy, search);

        return ResponseEntity.ok().body(posts);
    }

    @GetMapping("/community/{communityId}")
    public ResponseEntity<?> getAllPostsByCommunity(@PathVariable Long communityId,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "createdAt") String sortBy,
                                                    @RequestParam(defaultValue = "") String search) {
        Page<PostResponse> posts = postService.getAllPostsByCommunity(communityId, page, size, sortBy, search);

        return ResponseEntity.ok().body(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable Long postId) {
        PostResponse postResponse = postService.getPostById(postId);
        return ResponseEntity.ok().body(postResponse);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@Valid @RequestBody PostRequest postRequest,
                                        Authentication authentication,
                                        BindingResult bindingResult) {

        if (authentication == null) {
            return ResponseEntity.status(401).body("unauthorized");
        }

        Account account = (Account) authentication.getPrincipal();

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errorMessage);
        }

        PostResponse postResponse = postService.createPost(postRequest, account, postRequest.getCommunityId());

        return ResponseEntity.ok(postResponse);
    }

    @PostMapping(value ="/{postId}/add-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPostFiles(@PathVariable Long postId,
                                             @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        PostResponse postResponse = postService.uploadFilesToPost(postId, files);

        return ResponseEntity.ok(postResponse);
    }

    @PatchMapping("/{postId}/edit")
    public ResponseEntity<?> editPost(@PathVariable Long postId,
                                      @Valid @RequestBody PostRequest postRequest,
                                      BindingResult bindingResult,
                                      Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("unauthorized");
        }

        Account account = (Account) authentication.getPrincipal();

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errorMessage);
        }

        PostResponse postResponse = postService.editPost(account, postId, postRequest);

        return ResponseEntity.ok(postResponse);
    }

    @PatchMapping("/{postId}/edit-files")
    public ResponseEntity<?> editPostFiles(@PathVariable Long postId,
                                           @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                           Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("unauthorized");
        }

        Account account = (Account) authentication.getPrincipal();
        PostResponse postResponse = postService.editFilesFromPost(account, postId, files);
        return ResponseEntity.ok(postResponse);
    }

    @PatchMapping("/{postId}/inactive")
    public ResponseEntity<?> inactivePost(@PathVariable Long postId,
                                          Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("unauthorized");
        }

        Account account = (Account) authentication.getPrincipal();
        PostResponse postResponse = postService.inactivePost(account, postId);
        return ResponseEntity.ok(postResponse);
    }

    @PatchMapping("/{postId}/active")
    public ResponseEntity<?> activePost(@PathVariable Long postId,
                                        Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("unauthorized");
        }

        Account account = (Account) authentication.getPrincipal();
        PostResponse postResponse = postService.activePost(account, postId);
        return ResponseEntity.ok(postResponse);
    }

    @DeleteMapping("/{postId}/delete")
    public ResponseEntity<?> deletePost(@PathVariable Long postId,
                                        Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("unauthorized");
        }

        Account account = (Account) authentication.getPrincipal();

        postService.deletePost(account, postId);
        return ResponseEntity.ok().body("post deleted");
    }
}
