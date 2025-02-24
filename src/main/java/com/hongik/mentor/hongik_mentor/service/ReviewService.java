package com.hongik.mentor.hongik_mentor.service;

import com.hongik.mentor.hongik_mentor.controller.dto.ReviewResponseDto;
import com.hongik.mentor.hongik_mentor.controller.dto.ReviewSaveDto;
import com.hongik.mentor.hongik_mentor.domain.Member;
import com.hongik.mentor.hongik_mentor.domain.Review;
import com.hongik.mentor.hongik_mentor.repository.MemberRepository;
import com.hongik.mentor.hongik_mentor.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;

    //저장
    @Transactional
    public ReviewResponseDto save(ReviewSaveDto reviewSaveDto) {

        Member writer = memberRepository.findById(reviewSaveDto.getWriterId()).orElseThrow();
        Member target = memberRepository.findById(reviewSaveDto.getTargetId()).orElseThrow();

        Review review = reviewSaveDto.toEntity(
                reviewSaveDto.getContent(),
                writer,
                target,
                reviewSaveDto.getRating()
        );

        Review savedReview = reviewRepository.save(review);

        return ReviewResponseDto.fromEntity(savedReview);
    }

    //단일 조회
    public ReviewResponseDto findById(Long id) {
        Review review = reviewRepository.findById(id);
        return review != null ? ReviewResponseDto.fromEntity(review) : null;
    }

    //전체 조회
    public List<ReviewResponseDto> findAll() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream()
                .map(ReviewResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    //리뷰 수정
    @Transactional
    public ReviewResponseDto update(Long id, String content, int rating) {
        Review review = reviewRepository.findById(id);
        if (review != null) {
            review.update(content, rating);
            return ReviewResponseDto.fromEntity(review);
        }
        return null;
    }

    //단일 삭제
    @Transactional
    public void delete(Long id) {
        reviewRepository.delete(id);
    }

    //전체 삭제
    @Transactional
    public void deleteAll() {
        reviewRepository.deleteAll();
    }
}
