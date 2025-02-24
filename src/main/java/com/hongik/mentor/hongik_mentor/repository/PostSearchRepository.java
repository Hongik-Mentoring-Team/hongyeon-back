package com.hongik.mentor.hongik_mentor.repository;

import com.hongik.mentor.hongik_mentor.domain.Category;
import com.hongik.mentor.hongik_mentor.domain.post.Post;

import java.util.List;
import java.util.Optional;

public interface PostSearchRepository {

    List<Post> searchByTags(List<Long> tagIds);
    List<Post> searchByTagsAndCategory(List<Long> tagIds, Category category);
    List<Post> searchByCategory(Category category);
    Optional<Post> getPostById(Long id);
}
