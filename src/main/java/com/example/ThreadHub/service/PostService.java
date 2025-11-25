package com.example.ThreadHub.service;

import com.example.ThreadHub.dto.request.PostRequest;
import com.example.ThreadHub.dto.response.PostResponse;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Community;
import com.example.ThreadHub.entity.Media;
import com.example.ThreadHub.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    Page<PostResponse> getAllPosts(int page, int size, String sortBy, String search);

    Page<PostResponse> getAllPostsByCommunity(Long communityId, int page, int size, String sortBy, String search);

    PostResponse getPostById(Long postId);

    PostResponse createPost(PostRequest postRequest, Account account, Long communityId);

    PostResponse uploadFilesToPost(Long postId, List<MultipartFile> files);

    PostResponse editPost(Account account, Long postId, PostRequest postRequest);

    PostResponse editFilesFromPost(Account account, Long postId, List<MultipartFile> files);

    PostResponse inactivePost(Account account,Long postId);

    PostResponse activePost(Account account, Long postId);

    void deletePost(Account account, Long postId);
}
