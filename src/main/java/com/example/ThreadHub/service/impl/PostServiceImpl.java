package com.example.ThreadHub.service.impl;

import com.example.ThreadHub.dto.request.CreatePostRequest;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Community;
import com.example.ThreadHub.entity.Media;
import com.example.ThreadHub.entity.Post;
import com.example.ThreadHub.entity.enums.MediaType;
import com.example.ThreadHub.entity.enums.PostStatus;
import com.example.ThreadHub.repository.CommunityRepository;
import com.example.ThreadHub.repository.PostRepository;
import com.example.ThreadHub.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Override
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Post createPost(CreatePostRequest createPostRequest, Account account, Community community) {
        Post post = new Post();
        post.setTitle(createPostRequest.getTitle());
        post.setContent(createPostRequest.getContent());
        post.setPostStatus(PostStatus.ACTIVE);
        post.setCommunity(community);
        post.setAccount(account);

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public List<Media> uploadFilesToPost(Post post, List<MultipartFile> files) {
        List<Media> mediaList = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
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

        post.getMedias().addAll(mediaList);
        postRepository.save(post);

        return mediaList;
    }

}
