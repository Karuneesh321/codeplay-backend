package com.codeplay.repository;

import com.codeplay.model.Puzzle;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface PuzzleRepository extends MongoRepository<Puzzle, String> {
    List<Puzzle> findByActiveOrderByLevelNumberAsc(boolean active);
    Optional<Puzzle> findByLevelNumber(int levelNumber);
    List<Puzzle> findByUnlockRequirementLessThanEqual(int points);
}
