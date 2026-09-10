package com.aimock.interview.repository;

import com.aimock.interview.entity.Interview;
import com.aimock.interview.entity.InterviewStatus;
import com.aimock.interview.entity.JobRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, UUID> {

    Page<Interview> findByUserId(UUID userId, Pageable pageable);

    Page<Interview> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    List<Interview> findTop5ByUserIdOrderByCreatedAtDesc(UUID userId);

    long countByUserId(UUID userId);

    long countByUserIdAndStatus(UUID userId, InterviewStatus status);

    @Query("SELECT AVG(i.overallScore) FROM Interview i WHERE i.user.id = :userId AND i.status = com.aimock.interview.entity.InterviewStatus.COMPLETED AND i.overallScore IS NOT NULL")
    Double calculateAverageScoreByUserId(@Param("userId") UUID userId);

    @Query("SELECT MAX(i.overallScore) FROM Interview i WHERE i.user.id = :userId AND i.status = com.aimock.interview.entity.InterviewStatus.COMPLETED AND i.overallScore IS NOT NULL")
    BigDecimal findBestScoreByUserId(@Param("userId") UUID userId);

    @Query("SELECT i.jobRole, AVG(i.overallScore), COUNT(i) FROM Interview i WHERE i.user.id = :userId AND i.status = com.aimock.interview.entity.InterviewStatus.COMPLETED AND i.overallScore IS NOT NULL GROUP BY i.jobRole")
    List<Object[]> findPerformanceByJobRole(@Param("userId") UUID userId);

    @Query("SELECT i FROM Interview i WHERE i.id = :id AND i.user.id = :userId")
    Optional<Interview> findByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    long countByStatus(InterviewStatus status);
}
