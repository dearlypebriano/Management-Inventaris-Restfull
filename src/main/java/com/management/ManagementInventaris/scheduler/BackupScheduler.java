package com.management.ManagementInventaris.scheduler;

import com.management.ManagementInventaris.file.MinioBackupService;
import com.management.ManagementInventaris.provider.PostgresBackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * This class is responsible for scheduling and managing backup operations.
 * It uses a {@link PostgresBackupService} to perform the actual backup and restore operations.
 * It uses a {@link MinioBackupService} to perform the actual backup and restore operations.
 */
@Component
public class BackupScheduler {

    /**
     * The {@link PostgresBackupService} used to perform backup and restore operations.
     */
    @Autowired
    private PostgresBackupService postgreBackupService;

    /**
     * The {@link MinioBackupService} used to perform backup and restore operations.
     */
    @Autowired
    private MinioBackupService minioBackupService;

    /**
     * Schedules a backup operation to be executed every 5 hours.
     * The backup is performed by calling {@link PostgresBackupService#performBackup()}.
     * * The backup is performed by calling {@link MinioBackupService#performBackup()}.
     * If an {@link IOException} occurs during the backup process, it is caught and a message is printed to the console.
     */
    @Scheduled(cron = "0 0 */5 * * *")
    public void scheduleBackup() {
        try {
            postgreBackupService.performBackup();
            minioBackupService.performBackup();
        } catch (Exception e) {
            System.out.println("Gagal melakukan backup: " + e.getMessage());
        }
    }
}