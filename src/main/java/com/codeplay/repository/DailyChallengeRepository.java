package com.codeplay.repository;

import com.codeplay.model.DailyChallenge;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface DailyChallengeRepository extends MongoRepository<DailyChallenge, String> {
    Optional<DailyChallenge> findByChallengeDateAndActive(LocalDate date, boolean active);
    Optional<DailyChallenge> findByChallengeDate(LocalDate date);
}
