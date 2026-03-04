package com.codeplay.repository;

import com.codeplay.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByClerkId(String clerkId);
    Optional<User> findByEmail(String email);
    boolean existsByClerkId(String clerkId);
    boolean existsByEmail(String email);
    List<User> findAllByOrderByTotalPointsDesc();
    Page<User> findAllByOrderByTotalPointsDesc(Pageable pageable);
}
