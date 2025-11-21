package com.example.ThreadHub.service;

import com.example.ThreadHub.dto.request.CreatePostRequest;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Community;
import com.example.ThreadHub.entity.Media;
import com.example.ThreadHub.entity.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    Post getPostById(Long id);

    Post createPost(CreatePostRequest createPostRequest, Account account, Community community);

    List<Media> uploadFilesToPost(Post post, List<MultipartFile> files);
}
