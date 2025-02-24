package com.hongik.mentor.hongik_mentor.repository;

import com.hongik.mentor.hongik_mentor.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    // TODO : QueryDSL로 코드 변경 예정
    @Query("select f from Follow f where f.follower.id =: followerId and f.followee.id =: followingId")
    Optional<Follow> findByFollowerIdWithFollowingId(@Param("followerId") Long followerId,
                                                     @Param("followingId") Long followingId);

    List<Follow> findByFollowerId(Long followerId);

    List<Follow> findByFolloweeId(Long followingId);

    int countByFollowerId(Long followerId);

    int countByFolloweeId(Long followingId);
}
