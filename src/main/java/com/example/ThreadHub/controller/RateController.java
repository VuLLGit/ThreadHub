package com.example.ThreadHub.controller;

import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.exception.UnauthorizedException;
import com.example.ThreadHub.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}")
public class RateController {
    private static final String MSG_UNAUTHORIZED ="Unauthorized";
    private static final String MSG_DELETE_SUCCESS ="delete success";

    private final RateService rateService;

    @Autowired
    public RateController(RateService rateService) {
        this.rateService = rateService;
    }

    @PostMapping("/positive-ratings/create")
    public ResponseEntity<?> createPositivePostRate(Authentication authentication,
                                                @PathVariable Long postId) {
        if(authentication == null){
            throw new UnauthorizedException(MSG_UNAUTHORIZED);
        }
        Account account = (Account) authentication.getPrincipal();
        return ResponseEntity.ok(rateService.CreatePositivePostRate(postId, account));
    }

    @PostMapping("/negative-ratings/create")
    public ResponseEntity<?> createNegativePostRate(Authentication authentication,
                                                @PathVariable Long postId) {
        if(authentication == null){
            throw new UnauthorizedException(MSG_UNAUTHORIZED);
        }
        Account account = (Account) authentication.getPrincipal();
        return ResponseEntity.ok(rateService.CreateNegativePostRate(postId, account));
    }

    @DeleteMapping("/ratings/{rateId}/delete")
    public ResponseEntity<?> deletePostRate(Authentication authentication,
                                        @PathVariable Long postId,
                                        @PathVariable Long rateId) {
        if(authentication == null){
            throw new UnauthorizedException(MSG_UNAUTHORIZED);
        }
        Account account = (Account) authentication.getPrincipal();
        rateService.deletePostRate(postId, rateId, account);
        return ResponseEntity.ok(MSG_DELETE_SUCCESS);
    }

    @PostMapping("/comments/{commentId}/positive-ratings/create")
    public ResponseEntity<?> createPositiveCommentRate(Authentication authentication,
                                                       @PathVariable Long postId,
                                                       @PathVariable Long commentId) {
        if(authentication == null){
            throw new UnauthorizedException(MSG_UNAUTHORIZED);
        }
        Account account = (Account) authentication.getPrincipal();
        return ResponseEntity.ok(rateService.CreatePositiveCommentRate(postId, commentId, account));
    }

    @PostMapping("/comments/{commentId}/negative-ratings/create")
    public ResponseEntity<?> createNegativeCommentRate(Authentication authentication,
                                                       @PathVariable Long postId,
                                                       @PathVariable Long commentId) {
        if(authentication == null){
            throw new UnauthorizedException(MSG_UNAUTHORIZED);
        }
        Account account = (Account) authentication.getPrincipal();
        return ResponseEntity.ok(rateService.CreateNegativeCommentRate(postId, commentId, account));
    }

    @DeleteMapping("/comments/{commentId}/ratings/{rateId}/delete")
    public ResponseEntity<?> deleteCommentRate(Authentication authentication,
                                               @PathVariable Long postId,
                                               @PathVariable Long commentId,
                                               @PathVariable Long rateId) {
        if(authentication == null){
            throw new UnauthorizedException(MSG_UNAUTHORIZED);
        }
        Account account = (Account) authentication.getPrincipal();
        rateService.deleteCommentRate(postId, commentId, rateId, account);
        return ResponseEntity.ok(MSG_DELETE_SUCCESS);
    }
}
