package com.example.ThreadHub.controller;

import com.example.ThreadHub.dto.request.CommunityRequest;
import com.example.ThreadHub.dto.response.CommunityMemberResponse;
import com.example.ThreadHub.dto.response.CommunityResponse;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.CommunityMember;
import com.example.ThreadHub.entity.enums.MemberRole;
import com.example.ThreadHub.exception.ForbiddenException;
import com.example.ThreadHub.exception.UnauthorizedException;
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

    /* ===== MESSAGE CONSTANTS ===== */
    private static final String MSG_AUTH_NULL = "Authentication is null";
    private static final String MSG_JOIN_COMMUNITY_SUCCESS = "Joined community successfully";
    private static final String MSG_LEAVE_COMMUNITY_SUCCESS = "Left community successfully";
    private static final String MSG_FORBIDDEN_ACTION = "You are not allowed to perform this action";

    private final CommunityService communityService;
    private final CommunityMemberService communityMemberService;

    @Autowired
    public CommunityController(CommunityService communityService,
                               CommunityMemberService communityMemberService) {
        this.communityService = communityService;
        this.communityMemberService = communityMemberService;
    }

    @GetMapping()
    public ResponseEntity<?> getAllCommunities(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "createdAt") String sortBy,
                                               @RequestParam(defaultValue = "") String search) {
        Page<CommunityResponse> communities =
                communityService.getAllCommunities(page, size, sortBy, search);

        return ResponseEntity.ok(communities);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyCommunities(Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException(MSG_AUTH_NULL);
        }
        Account account = (Account) authentication.getPrincipal();

        return ResponseEntity.ok(
                communityService.getAllCommunitiesByAccountId(account.getId())
        );
    }

    @GetMapping("/{communityId}")
    public ResponseEntity<?> getCommunityById(@PathVariable Long communityId) {
        CommunityResponse communityResponse =
                communityService.getCommunityById(communityId);
        return ResponseEntity.ok(communityResponse);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCommunity(@Valid @RequestBody CommunityRequest communityRequest,
                                             Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException(MSG_AUTH_NULL);
        }
        Account account = (Account) authentication.getPrincipal();

        CommunityResponse communityResponse =
                communityService.createCommunity(communityRequest, account);
        return ResponseEntity.ok(communityResponse);
    }

    @PostMapping("/{communityId}/join")
    public ResponseEntity<?> joinCommunity(@PathVariable Long communityId,
                                           Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException(MSG_AUTH_NULL);
        }
        Account account = (Account) authentication.getPrincipal();

        communityMemberService.joinCommunity(communityId, account.getId());
        return ResponseEntity.ok(MSG_JOIN_COMMUNITY_SUCCESS);
    }

    @DeleteMapping("/{communityId}/leave")
    public ResponseEntity<?> leaveCommunity(@PathVariable Long communityId,
                                            Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException(MSG_AUTH_NULL);
        }
        Account account = (Account) authentication.getPrincipal();

        communityMemberService.leaveCommunity(communityId, account.getId());
        return ResponseEntity.ok(MSG_LEAVE_COMMUNITY_SUCCESS);
    }

    // Moderator actions
    @GetMapping("/{communityId}/members")
    public ResponseEntity<?> getCommunityMembers(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "7") int size,
                                                 @PathVariable Long communityId,
                                                 Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException(MSG_AUTH_NULL);
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember =
                communityMemberService.findByCommunityIdAndAccountId(
                        communityId, account.getId()
                );

        if (!actorCommunityMember.getMemberRole().equals(MemberRole.MODERATOR)
                && !actorCommunityMember.getMemberRole().equals(MemberRole.OWNER)) {
            throw new ForbiddenException(MSG_FORBIDDEN_ACTION);
        }

        Page<CommunityMemberResponse> communityMembers =
                communityMemberService.getCommunityMembers(
                        page, size, "createdAt", communityId
                );

        return ResponseEntity.ok(communityMembers);
    }

    @PatchMapping("/{communityId}/members/{communityMemberId}/assign-moderator")
    public ResponseEntity<?> assignModerator(@PathVariable Long communityMemberId,
                                             @PathVariable Long communityId,
                                             Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException(MSG_AUTH_NULL);
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember =
                communityMemberService.findByCommunityIdAndAccountId(
                        communityId, account.getId()
                );

        if (!actorCommunityMember.getMemberRole().equals(MemberRole.MODERATOR)
                && !actorCommunityMember.getMemberRole().equals(MemberRole.OWNER)) {
            throw new ForbiddenException(MSG_FORBIDDEN_ACTION);
        }

        CommunityMemberResponse communityMemberResponse =
                communityMemberService.assignModerator(communityMemberId);

        return ResponseEntity.ok(communityMemberResponse);
    }

    @PatchMapping("/{communityId}/members/{communityMemberId}/remove-moderator")
    public ResponseEntity<?> removeModerator(@PathVariable Long communityMemberId,
                                             @PathVariable Long communityId,
                                             Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException(MSG_AUTH_NULL);
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember =
                communityMemberService.findByCommunityIdAndAccountId(
                        communityId, account.getId()
                );

        if (!actorCommunityMember.getMemberRole().equals(MemberRole.OWNER)) {
            throw new ForbiddenException(MSG_FORBIDDEN_ACTION);
        }

        CommunityMemberResponse communityMemberResponse =
                communityMemberService.removeModerator(communityMemberId);

        return ResponseEntity.ok(communityMemberResponse);
    }

    @PatchMapping("/{communityId}/members/{communityMemberId}/transfer-owner")
    public ResponseEntity<?> transferOwner(@PathVariable Long communityMemberId,
                                           @PathVariable Long communityId,
                                           Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException(MSG_AUTH_NULL);
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember =
                communityMemberService.findByCommunityIdAndAccountId(
                        communityId, account.getId()
                );

        if (!actorCommunityMember.getMemberRole().equals(MemberRole.OWNER)) {
            throw new ForbiddenException(MSG_FORBIDDEN_ACTION);
        }

        CommunityMemberResponse communityMemberResponse =
                communityMemberService.transferOwner(
                        actorCommunityMember.getId(), communityMemberId
                );

        return ResponseEntity.ok(communityMemberResponse);
    }

    @PatchMapping("/{communityId}/edit")
    public ResponseEntity<?> editCommunity(@PathVariable Long communityId,
                                           Authentication authentication,
                                           @Valid @RequestBody CommunityRequest communityRequest) {
        if (authentication == null) {
            throw new UnauthorizedException(MSG_AUTH_NULL);
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember =
                communityMemberService.findByCommunityIdAndAccountId(
                        communityId, account.getId()
                );

        if (!actorCommunityMember.getMemberRole().equals(MemberRole.MODERATOR)
                && !actorCommunityMember.getMemberRole().equals(MemberRole.OWNER)) {
            throw new ForbiddenException(MSG_FORBIDDEN_ACTION);
        }

        CommunityResponse communityResponse =
                communityService.editCommunity(communityRequest, communityId);

        return ResponseEntity.ok(communityResponse);
    }

    @PatchMapping("/{communityId}/inactive")
    public ResponseEntity<?> inactiveCommunity(@PathVariable Long communityId,
                                               Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException(MSG_AUTH_NULL);
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember =
                communityMemberService.findByCommunityIdAndAccountId(
                        communityId, account.getId()
                );

        if (!actorCommunityMember.getMemberRole().equals(MemberRole.MODERATOR)
                && !actorCommunityMember.getMemberRole().equals(MemberRole.OWNER)) {
            throw new ForbiddenException(MSG_FORBIDDEN_ACTION);
        }

        CommunityResponse communityResponse =
                communityService.inactivateCommunity(communityId);

        return ResponseEntity.ok(communityResponse);
    }

    @PatchMapping("/{communityId}/active")
    public ResponseEntity<?> activeCommunity(@PathVariable Long communityId,
                                             Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException(MSG_AUTH_NULL);
        }
        Account account = (Account) authentication.getPrincipal();
        CommunityMember actorCommunityMember =
                communityMemberService.findByCommunityIdAndAccountId(
                        communityId, account.getId()
                );

        if (!actorCommunityMember.getMemberRole().equals(MemberRole.MODERATOR)
                && !actorCommunityMember.getMemberRole().equals(MemberRole.OWNER)) {
            throw new ForbiddenException(MSG_FORBIDDEN_ACTION);
        }

        CommunityResponse communityResponse =
                communityService.activateCommunity(communityId);

        return ResponseEntity.ok(communityResponse);
    }
}
