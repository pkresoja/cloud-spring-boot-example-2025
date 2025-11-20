package com.pequla.data.service;

import com.pequla.data.entity.CachedData;
import com.pequla.data.model.AccountModel;
import com.pequla.data.model.DataModel;
import com.pequla.data.model.UserModel;
import com.pequla.data.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncService {

    private final BackendService backendService;
    private final DataRepository dataRepository;

    @Scheduled(fixedRate = 5 * 60000, initialDelay = 5000)
    public void doSync() {
        try {
            List<Integer> localIds = dataRepository.findAllIds();
            List<Integer> globalList = backendService.getDataIds();

            // Delete records
            localIds.stream()
                    .filter(id -> !globalList.contains(id))
                    .forEach(dataRepository::deleteById);

            // Create new
            globalList.stream()
                    .filter(id -> !localIds.contains(id))
                    .forEach(this::saveCachedDataById);

            log.info("Sync finished");
        } catch (Exception e) {
            log.error("Failed to sync data", e);
        }
    }

    @Scheduled(cron = "0 0 5 * * *")
    public void doSyncStale() {
        try {
            // Update existing
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            dataRepository.findByCachedAtBefore(oneHourAgo)
                    .forEach(data -> saveCachedDataById(data.getId()));

            log.info("Stale sync finished");
        } catch (Exception e) {
            log.error("Failed to sync stale data", e);
        }
    }

    private void saveCachedDataById(Integer id) {
        try {
            DataModel model = backendService.getData(id);
            UserModel user = backendService.getUser(model.getUuid());
            AccountModel account = backendService.getAccount(model.getUuid());
            CachedData data = dataRepository.save(CachedData.builder()
                    .id(model.getId())
                    .name(account.getName())
                    .uuid(account.getId())
                    .discordId(user.getId())
                    .tag(user.getName())
                    .avatar(user.getAvatar())
                    .guildId(model.getGuild().getDiscordId())
                    .createdAt(model.getCreatedAt())
                    .cachedAt(LocalDateTime.now())
                    .build());
            dataRepository.save(data);
            log.info("Successfully saved data {}", id);
        } catch (Exception e) {
            log.warn("Failed to save data {}", id);
        }
    }
}
