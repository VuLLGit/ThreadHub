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

    @PostMapping("/ratings/positive")
    public ResponseEntity<?> createPositiveRate(Authentication authentication,
                                                @PathVariable Long postId) {
        if(authentication == null){
            throw new UnauthorizedException(MSG_UNAUTHORIZED);
        }
        Account account = (Account) authentication.getPrincipal();
        return ResponseEntity.ok(rateService.CreatePositiveRate(postId, account));
    }

    @PostMapping("/ratings/negative")
    public ResponseEntity<?> createNegativeRate(Authentication authentication,
                                                @PathVariable Long postId) {
        if(authentication == null){
            throw new UnauthorizedException(MSG_UNAUTHORIZED);
        }
        Account account = (Account) authentication.getPrincipal();
        return ResponseEntity.ok(rateService.CreateNegativeRate(postId, account));
    }

    @DeleteMapping("/ratings/delete")
    public ResponseEntity<?> deleteRate(Authentication authentication,
                                        @PathVariable Long postId) {
        if(authentication == null){
            throw new UnauthorizedException(MSG_UNAUTHORIZED);
        }
        Account account = (Account) authentication.getPrincipal();
        rateService.deleteRate(postId, account);
        return ResponseEntity.ok(MSG_DELETE_SUCCESS);
    }
}
