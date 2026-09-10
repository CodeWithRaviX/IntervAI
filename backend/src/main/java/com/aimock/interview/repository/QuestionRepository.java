package com.aimock.interview.repository;

import com.aimock.interview.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    List<Question> findByInterviewIdOrderBySequenceNumberAsc(UUID interviewId);

    Optional<Question> findByInterviewIdAndSequenceNumber(UUID interviewId, Integer sequenceNumber);

    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.answer WHERE q.interview.id = :interviewId ORDER BY q.sequenceNumber ASC")
    List<Question> findQuestionsWithAnswersByInterviewId(@Param("interviewId") UUID interviewId);

    long countByInterviewId(UUID interviewId);
}
