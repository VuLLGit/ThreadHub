package com.example.ThreadHub.controller;

import com.example.ThreadHub.dto.request.UpdateCommunityRequest;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.CommunityMember;
import com.example.ThreadHub.service.CommunityMemberService;
import com.example.ThreadHub.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community/{communityId}/moderator")
public class CommunityModeratorController {

    private CommunityService comunityService;
    private CommunityMemberService communityMemberService;

    @Autowired
    private CommunityModeratorController(CommunityService comunityService, CommunityMemberService communityMemberService) {
        this.comunityService = comunityService;
        this.communityMemberService = communityMemberService;
    }

    @GetMapping("/members")
    public ResponseEntity<?> getCommunityMembers(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "7") int size,
                                                 @PathVariable Long communityId,
                                                 Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember = communityMemberService.findByCommunityIdAndAccountId(communityId, account.getId());

        if (!communityMemberService.isModerator(actorCommunityMember.getId()) && !communityMemberService.isOwner(actorCommunityMember.getId())){
            return ResponseEntity.status(403).body("Forbidden");
        }
        return ResponseEntity.ok().body(communityMemberService.getCommunityMembers(page, size, "createdAt", communityId));
    }

    @PatchMapping("/members/{communityMemberId}/assign-moderator")
    public ResponseEntity<?> assignModerator(@PathVariable Long communityMemberId,
                                             @PathVariable Long communityId,
                                             Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember = communityMemberService.findByCommunityIdAndAccountId(communityId, account.getId());

        if (!communityMemberService.isModerator(actorCommunityMember.getId()) && !communityMemberService.isOwner(actorCommunityMember.getId())){
            return ResponseEntity.status(403).body("Forbidden");
        }

        if(!communityMemberService.isMemberExist(communityMemberId)){
            return ResponseEntity.badRequest().body("cannot find member");
        }

        communityMemberService.assignModerator(communityMemberId);
        return ResponseEntity.ok().body("assign moderator successfully");
    }

    @PatchMapping("/members/{communityMemberId}/remove-moderator")
    public ResponseEntity<?> removeModerator(@PathVariable Long communityMemberId,
                                             @PathVariable Long communityId,
                                             Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember = communityMemberService.findByCommunityIdAndAccountId(communityId, account.getId());

        if (!communityMemberService.isOwner(actorCommunityMember.getId())){
            return ResponseEntity.status(403).body("Forbidden");
        }

        if(!communityMemberService.isMemberExist(communityMemberId)){
            return ResponseEntity.badRequest().body("cannot find member");
        }

        communityMemberService.removeModerator(communityMemberId);
        return ResponseEntity.ok().body("remove moderator successfully");
    }

    @PatchMapping("/members/{communityMemberId}/transfer-owner")
    public ResponseEntity<?> transferOwner(@PathVariable Long communityMemberId,
                                           @PathVariable Long communityId,
                                           Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember = communityMemberService.findByCommunityIdAndAccountId(communityId, account.getId());

        if (!communityMemberService.isOwner(actorCommunityMember.getId())){
            return ResponseEntity.status(403).body("Forbidden");
        }

        if(!communityMemberService.isMemberExist(communityMemberId)){
            return ResponseEntity.badRequest().body("cannot find member");
        }

        communityMemberService.transferOwner(actorCommunityMember.getId(), communityMemberId);
        return ResponseEntity.ok().body("transfer owner successfully");
    }
    @PatchMapping("/edit")
    public ResponseEntity<?> editCommunity(@PathVariable Long communityId,
                                           Authentication authentication,
                                           @RequestBody UpdateCommunityRequest updateCommunityRequest) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember = communityMemberService.findByCommunityIdAndAccountId(communityId, account.getId());

        if (!communityMemberService.isModerator(actorCommunityMember.getId()) && !communityMemberService.isOwner(actorCommunityMember.getId())){
            return ResponseEntity.status(403).body("Forbidden");
        }

        comunityService.EditCommunity(updateCommunityRequest, communityId);

        return ResponseEntity.ok().body("Community edited successfully");
    }

    @PatchMapping("/inactive")
    public ResponseEntity<?> inactiveCommunity(@PathVariable Long communityId,
                                               Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember = communityMemberService.findByCommunityIdAndAccountId(communityId, account.getId());

        if (!communityMemberService.isModerator(actorCommunityMember.getId()) && !communityMemberService.isOwner(actorCommunityMember.getId())){
            return ResponseEntity.status(403).body("Forbidden");
        }
        comunityService.inactivateCommunity(communityId);
        return ResponseEntity.ok().body("Community inactivated successfully");
    }

    @PatchMapping("/active")
    public ResponseEntity<?> activeCommunity(@PathVariable Long communityId,
                                             Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember = communityMemberService.findByCommunityIdAndAccountId(communityId, account.getId());

        if (!communityMemberService.isModerator(actorCommunityMember.getId()) && !communityMemberService.isOwner(actorCommunityMember.getId())){
            return ResponseEntity.status(403).body("Forbidden");
        }
        comunityService.activateCommunity(communityId);
        return ResponseEntity.ok().body("Community activated successfully");
    }
}
