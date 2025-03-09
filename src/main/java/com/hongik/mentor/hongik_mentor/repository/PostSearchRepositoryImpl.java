package com.hongik.mentor.hongik_mentor.repository;

import com.hongik.mentor.hongik_mentor.domain.Category;
import com.hongik.mentor.hongik_mentor.domain.post.Post;
import com.hongik.mentor.hongik_mentor.domain.post.QPost;
import com.hongik.mentor.hongik_mentor.domain.post.QPostTag;
import com.hongik.mentor.hongik_mentor.domain.post.QTag;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public class PostSearchRepositoryImpl implements PostSearchRepository{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public PostSearchRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        this.em = em;
    }

    public Optional<Post> getPostById(Long id) {
        QPost p = QPost.post;

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
        QPost p = QPost.post;
        QTag t = QTag.tag;
        QPostTag pt = QPostTag.postTag;

        List<Post> posts = queryFactory
                .selectFrom(p)
                .where(p.id.in(
                        JPAExpressions
                                .select(pt.post.id)
                                .from(pt)
                                .where(pt.tag.id.in(tagIds))
                                .groupBy(pt.post.id)
                                .having(pt.tag.id.count().eq((long) tagIds.size()))
                ))
                .fetch();

        return posts;
    }

    @Override
    public List<Post> searchByTagsAndCategory(List<Long> tagIds, Category category) {
        QPost p = QPost.post;
        QPostTag pt = QPostTag.postTag;
        List<Post> posts = queryFactory
                .selectFrom(p)
                .where(p.category.eq(category))
                .where(p.id.in(
                        JPAExpressions
                                .select(pt.post.id)
                                .from(pt)
                                .where(pt.tag.id.in(tagIds))
                                .groupBy(pt.post.id)
                                .having(pt.tag.id.count().eq((long) tagIds.size()))
                ))
                .fetch();

        return posts;
    }

    @Override
    public List<Post> searchByCategory(Category category) {
        QPost p = QPost.post;

        return queryFactory
                .selectFrom(p)
                .where(p.category.eq(category))
                .fetch();
    }

    @Override
    public List<Post> searchByTitle(String keyword) {
        return em.createQuery("select p from Post p where p.title like concat('%', :keyword, '%')", Post.class)
                .setParameter("keyword", keyword)
                .getResultList();
    }

    @Override
    public List<Post> searchByContent(String keyword) {
        return em.createQuery("select p from Post p where p.content like concat('%', :keyword, '%')", Post.class)
                .setParameter("keyword", keyword)
                .getResultList();
    }

    @Override
    public List<Post> searchByMember(String keyword) {
        return em.createQuery("select p from Post p join p.member m where m.name like concat('%', :keyword, '%')", Post.class)
                .setParameter("keyword", keyword)
                .getResultList();
    }

}
