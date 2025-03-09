package com.hongik.mentor.hongik_mentor.repository;

import com.hongik.mentor.hongik_mentor.domain.Category;
import com.hongik.mentor.hongik_mentor.domain.post.Post;
import com.hongik.mentor.hongik_mentor.domain.post.QPost;
import com.hongik.mentor.hongik_mentor.domain.post.QPostTag;
import com.hongik.mentor.hongik_mentor.domain.post.QTag;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static com.hongik.mentor.hongik_mentor.domain.post.QPost.post;
import static com.hongik.mentor.hongik_mentor.domain.post.QPostTag.*;

public class PostSearchRepositoryImpl implements PostSearchRepository{

    private final JPAQueryFactory queryFactory;

    public PostSearchRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Optional<Post> getPostById(Long id) {
        QPost p = post;

        Post post = queryFactory
                .select(p)
                .from(p)
                .where(p.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(post);
    }
/* 성능 개선 요소: 아래 SearchByTag / Category / TagsAndCategory 함수를 동적쿼리를 사용해서 하나로 변경가능*/
    @Override
    public List<Post> searchByTags(List<Long> tagIds) {
        QPost p = post;
        QTag t = QTag.tag;
        QPostTag pt = postTag;

        List<Post> posts = queryFactory
                .selectFrom(p)
                .where(tagsIn(tagIds))
                .fetch();

        return posts;
    }

    @Override
    public List<Post> searchByTagsAndCategory(List<Long> tagIds, Category category) {
        List<Post> posts = queryFactory
                .selectFrom(post)
                .where(categoryEq(category), tagsIn(tagIds))
                .fetch();

        return posts;
    }

    private BooleanExpression categoryEq(Category category) {
        return category == null ? null : post.category.eq(category);
    }

    private BooleanExpression tagsIn(List<Long> tagIds) {
        if(tagIds == null || tagIds.isEmpty()) return null;
        return post.id.in(
                JPAExpressions
                        .select(postTag.post.id)
                        .from(postTag)
                        .where(postTag.tag.id.in(tagIds))
                        .groupBy(postTag.post.id)
                        .having(postTag.tag.id.count().eq((long) tagIds.size()))
        );
    }

    @Override
    public List<Post> searchByCategory(Category category) {
        QPost p = post;

        return queryFactory
                .selectFrom(p)
                .where(p.category.eq(category))
                .fetch();
    }

}
