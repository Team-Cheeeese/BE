package com.cheeeese.user.infrastructure.persistence;

import com.cheeeese.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
