package com.pequla.data.service;

import com.pequla.data.AppUtils;
import com.pequla.data.entity.CachedData;
import com.pequla.data.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DataService {

    private final DataRepository repository;

    public Page<CachedData> getData(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<CachedData> getById(Integer id) {
        return repository.findById(id);
    }

    public Optional<CachedData> getByDiscordId(String discordId) {
        return repository.findByDiscordId(discordId);
    }

    public Page<CachedData> getAllByGuildId(String guildId, Pageable pageable) {
        return repository.findAllByGuildId(guildId, pageable);
    }

    public Optional<CachedData> getByUuid(String uuid) {
        return repository.findByUuid(AppUtils.cleanUUID(uuid));
    }

    public Optional<CachedData> getByName(String name) {
        return repository.findByNameIgnoreCase(name);
    }

    public List<CachedData> searchByName(String query) {
        return repository.findByNameIgnoreCaseContainingOrTagIgnoreCaseContaining(query, query);
    }

}
