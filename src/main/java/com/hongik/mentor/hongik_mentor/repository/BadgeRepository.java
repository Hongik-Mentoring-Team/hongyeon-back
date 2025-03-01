package com.hongik.mentor.hongik_mentor.repository;

import com.hongik.mentor.hongik_mentor.domain.Badge;
import com.hongik.mentor.hongik_mentor.domain.member.MemberBadge;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BadgeRepository {
    @PersistenceContext
    private EntityManager em;

    //create
    public Long save(Badge badge) {
        em.persist(badge);
        return badge.getId();
    }

    public void saveMemberBadge(MemberBadge memberBadge) {
        em.persist(memberBadge);
    }

    //read
    public Badge findById(Long id) {
        Badge findBadge = em.find(Badge.class, id);
        return findBadge;
    }

    public List<Badge> findAll() {
        List badges = em.createQuery("select b from Badge b")
                .getResultList();
        return badges;
    }
    //update

    //delete
    public void delete(Long id) {
        Badge findBadge = em.find(Badge.class, id);
        em.remove(findBadge);
    }

    public Long count() {
        Long num = (Long) em.createQuery("select count(b) from Badge b")
                .getSingleResult();
        return num;
    }
}
