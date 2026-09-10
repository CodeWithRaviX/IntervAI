package com.aimock.interview.repository;

import com.aimock.interview.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {

    Optional<Answer> findByQuestionId(UUID questionId);

    @Query("SELECT a FROM Answer a JOIN a.question q WHERE q.interview.id = :interviewId ORDER BY q.sequenceNumber ASC")
    List<Answer> findAnswersByInterviewId(@Param("interviewId") UUID interviewId);
}
