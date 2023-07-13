package com.gtbackend.gtbackend.dao;

import com.gtbackend.gtbackend.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, String> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO feedback (email, timestamp, message, rating, status) VALUES (:email, :timestamp, :message, :rating, :status)", nativeQuery = true)
    void addFeedback(@Param("email") String email, @Param("timestamp") String timestamp, @Param("message") String message, @Param("rating") Float rating, @Param("status") String status);

    @Query(value = "SELECT * FROM feedback", nativeQuery = true)
    List<String> getFeedback();

    @Modifying
    @Transactional
    @Query(value = "UPDATE Feedback f SET f.status = :status where f.id = :id")
    void updateFeedbackStatus(@Param("id") Long id, @Param("status") String status);
}
