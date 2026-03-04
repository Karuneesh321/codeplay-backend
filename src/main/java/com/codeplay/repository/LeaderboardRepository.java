package com.codeplay.repository;

import com.codeplay.model.LeaderboardEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaderboardRepository extends MongoRepository<LeaderboardEntry, String> {

    Page<LeaderboardEntry> findAllByOrderByTotalPointsDesc(Pageable pageable);

    List<LeaderboardEntry> findTop10ByOrderByTotalPointsDesc();

    Optional<LeaderboardEntry> findByUserId(String userId);

    Optional<LeaderboardEntry> findByClerkId(String clerkId);
}
