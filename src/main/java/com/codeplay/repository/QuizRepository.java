package com.codeplay.repository;

import com.codeplay.model.Quiz;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.time.LocalDateTime;

public interface QuizRepository extends MongoRepository<Quiz, String> {
    List<Quiz> findByCategoryAndActive(String category, boolean active);
    List<Quiz> findByDifficultyAndActive(String difficulty, boolean active);
    List<Quiz> findByCategoryAndDifficultyAndActive(String category, String difficulty, boolean active);
    List<Quiz> findByIsDailyChallengeAndActive(boolean isDailyChallenge, boolean active);
    List<Quiz> findByActive(boolean active);
    long countByCategory(String category);
}
