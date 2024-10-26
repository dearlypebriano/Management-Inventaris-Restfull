package com.management.ManagementInventaris.provider;

/**
 * Sealed class representing a PostgreSQL backup service.
 * This class can only be extended by the specified permitted subclass (PostgreBackupServiceImpl).
 * Provides an abstract method to perform a backup, which should be implemented by subclasses.
 *
 * @version 6.4.5
 * @since 2024-07-05
 */
public sealed interface PostgresBackupService permits PostgresBackupServiceImpl {

    /**
     * Abstract method to perform a backup. This method should be implemented by subclasses.
     *
     * @throws Exception if an error occurs during the backup process.
     */
    void performBackup();

    /**
     * Abstract method to perform a restore. This method should be implemented by subclasses.
     *
     * @param targetDatabase the target database to restore the backup.
     * @throws Exception if an error occurs during the restore process.
     */
    void performRestore(String targetDatabase);
}