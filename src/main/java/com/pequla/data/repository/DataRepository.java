package com.pequla.data.repository;

import com.pequla.data.entity.CachedData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DataRepository extends JpaRepository<CachedData, Integer> {

    Optional<CachedData> findByDiscordId(String discordId);

    Page<CachedData> findAllByGuildId(String guildId, Pageable pageable);

    Optional<CachedData> findByUuid(String uuid);

    Optional<CachedData> findByNameIgnoreCase(String name);

    List<CachedData> findByNameIgnoreCaseContainingOrTagIgnoreCaseContaining(String name, String tag);

    @Query("SELECT d.id FROM data d")
    List<Integer> findAllIds();

    List<CachedData> findByCachedAtBefore(LocalDateTime threshold);
}
