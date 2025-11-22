package com.example.ThreadHub.service;

import com.example.ThreadHub.dto.request.PostRequest;
import com.example.ThreadHub.dto.response.PostResponse;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Community;
import com.example.ThreadHub.entity.Media;
import com.example.ThreadHub.entity.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    Post getPostById(Long id);

    PostResponse createPost(PostRequest postRequest, Account account, Community community);

    PostResponse uploadFilesToPost(Post post, List<MultipartFile> files);

    PostResponse editPost(Account account ,Post post, PostRequest postRequest);

    PostResponse editFilesFromPost(Account account, Post post, List<MultipartFile> files);

    PostResponse inactivePost(Account account,Post post);

    PostResponse activePost(Account account, Post post);

    void deletePost(Account account, Post post);
}
