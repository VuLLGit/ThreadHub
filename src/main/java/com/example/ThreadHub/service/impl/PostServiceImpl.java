package com.example.ThreadHub.service.impl;

import com.example.ThreadHub.dto.request.PostRequest;
import com.example.ThreadHub.dto.response.PostResponse;
import com.example.ThreadHub.entity.*;
import com.example.ThreadHub.entity.enums.MediaType;
import com.example.ThreadHub.entity.enums.PostStatus;
import com.example.ThreadHub.exception.NotFoundException;
import com.example.ThreadHub.repository.CommunityRepository;
import com.example.ThreadHub.repository.PostRepository;
import com.example.ThreadHub.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    private final S3Client s3Client;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, CommunityRepository communityRepository, S3Client s3Client) {
        this.postRepository = postRepository;
        this.communityRepository = communityRepository;
        this.s3Client = s3Client;
    }

    //lưu file lên S3
    private String saveFileToS3(MultipartFile file, String folder) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String key = folder + fileName;

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket("threadhub")
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload to S3", e);
        }

        return "https://threadhub.s3.ap-southeast-2.amazonaws.com/" + key;
    }

    private void deleteFileFromS3(String url) {
        String prefix = "https://threadhub.s3.ap-southeast-2.amazonaws.com/Posts/";
        String key = url.replace(prefix, "");

        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket("threadhub")
                        .key(key)
                        .build()
        );
    }

    private PostResponse mapToDto(Post post) {
        PostResponse postResponse = new PostResponse();
        postResponse.setId(post.getId());
        postResponse.setTitle(post.getTitle());
        postResponse.setContent(post.getContent());
        postResponse.setPostStatus(post.getPostStatus().getValue());
        postResponse.setCommunityName(post.getCommunity().getName());
        postResponse.setCommunityImageUrl(post.getCommunity().getImageUrl());
        postResponse.setAccountUsername(post.getAccount().getUsername());
        postResponse.setAccountAvatarUrl(post.getAccount().getAvatarUrl());

        if(post.getMedias() != null) {
            List<String> mediaUrls = new ArrayList<>();
            for (Media media : post.getMedias()) {
                mediaUrls.add(media.getUrl());
            }
            postResponse.setMediaUrls(mediaUrls);
        }
        return postResponse;
    }

    private void saveMedias(Post post, List<MultipartFile> files, List<Media> mediaList) {
        for (MultipartFile file : files) {
            String url = saveFileToS3(file, "posts/");

            Media media = new Media();
            media.setUrl(url);
            media.setPost(post);

            if (file.getContentType() != null && file.getContentType().startsWith("image")) {
                media.setMediaType(MediaType.IMAGE);
            } else {
                media.setMediaType(MediaType.VIDEO);
            }

            mediaList.add(media);
        }
    }

    @Override
    public Page<PostResponse> getAllPosts(int page, int size, String sortBy, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

        Page<Post> posts;
        if (search == null || search.isBlank()) {
            posts = postRepository.findAll(pageable);
        } else {
            posts = postRepository.findAllSearchedPosts(search.trim(), pageable);
        }

        return posts.map(this::mapToDto);
    }

    @Override
    public Page<PostResponse> getAllPostsByCommunity(Long communityId, int page, int size, String sortBy, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

        Page<Post> posts;
        if (search == null || search.isBlank()) {
            posts = postRepository.findAllByCommunityId(communityId, pageable);
        } else {
            posts = postRepository.findAllSearchedPostsInCommunity(communityId, search.trim(), pageable);
        }

        return posts.map(this::mapToDto);
    }

    @Override
    public PostResponse getPostById(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if(post == null) {
            throw new NotFoundException("Post not found");
        }
        return mapToDto(post);
    }

    @Override
    public PostResponse createPost(PostRequest postRequest, Account account, Long communityId) {
        Community community = communityRepository.findById(communityId).orElse(null);
        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setPostStatus(PostStatus.ACTIVE);
        post.setCommunity(community);
        post.setAccount(account);

        return mapToDto(postRepository.save(post));
    }

    @Override
    public PostResponse uploadFilesToPost(Long postId, List<MultipartFile> files) {
        Post post = postRepository.findById(postId).orElse(null);
        if(post == null) {
            throw new NotFoundException("Post not found");
        }

        List<Media> mediaList = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            saveMedias(post, files, mediaList);
        }

        post.getMedias().addAll(mediaList);
        postRepository.save(post);

        return mapToDto(post);
    }

    @Override
    public PostResponse editPost(Account account, Long postId, PostRequest postRequest) {
        Post post = postRepository.findById(postId).orElse(null);
        if(post == null) {
            throw new NotFoundException("Post not found");
        }

        if (!post.getAccount().getId().equals(account.getId())) {
            throw new RuntimeException("You don't have permission to edit this post");
        }

        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        return mapToDto(postRepository.save(post));
    }

    @Override
    public PostResponse editFilesFromPost(Account account, Long postId, List<MultipartFile> files) {
        Post post = postRepository.findById(postId).orElse(null);
        if(post == null) {
            throw new NotFoundException("Post not found");
        }

        if (!post.getAccount().getId().equals(account.getId())) {
            throw new RuntimeException("You don't have permission to edit this post");
        }
        List<Media> mediaList = new ArrayList<>();

        if (files != null && !files.isEmpty()) {

            for (Media media : post.getMedias()) {
                deleteFileFromS3(media.getUrl());
            }
            post.getMedias().clear();

            saveMedias(post, files, mediaList);
        }

        post.getMedias().addAll(mediaList);
        postRepository.save(post);

        return mapToDto(post);
    }

    @Override
    public PostResponse inactivePost(Account account, Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if(post == null) {
            throw new NotFoundException("Post not found");
        }

        if (!post.getAccount().getId().equals(account.getId())) {
            throw new RuntimeException("You don't have permission to inactive this post");
        }
        post.setPostStatus(PostStatus.INACTIVE);
        return mapToDto(postRepository.save(post));
    }

    @Override
    public PostResponse activePost(Account account, Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if(post == null) {
            throw new NotFoundException("Post not found");
        }

        if (!post.getAccount().getId().equals(account.getId())) {
            throw new RuntimeException("You don't have permission to active this post");
        }
        post.setPostStatus(PostStatus.ACTIVE);
        return mapToDto(postRepository.save(post));
    }
    
    @Override
    public void deletePost(Account account,Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if(post == null) {
            throw new NotFoundException("Post not found");
        }

        if (!post.getAccount().getId().equals(account.getId())) {
            throw new RuntimeException("You don't have permission to delete this post");
        }
        postRepository.delete(post);
        postRepository.flush();
    }
}
