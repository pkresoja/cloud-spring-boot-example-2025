package com.pequla.data.repository;

import com.pequla.data.entity.Access;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccessRepository extends JpaRepository<Access, Integer> {
    Optional<Access> findByAddress(String address);
}
