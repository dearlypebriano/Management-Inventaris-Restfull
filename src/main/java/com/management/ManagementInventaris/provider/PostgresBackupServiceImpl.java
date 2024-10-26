package com.management.ManagementInventaris.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Service implementation for performing PostgreSQL database backups and restores.
 * This service manages PostgreSQL backups using Python scripts and provides methods
 * for performing both backup and restore operations. The configuration values are read
 * from application properties.
 *
 * @version 6.5.4
 * @since 2024-07-05
 */
@Service
public final class PostgresBackupServiceImpl implements PostgresBackupService {

    @Value("${backup.directory}")
    private String backupDirectory;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    /**
     * Executes the PostgreSQL database backup process by invoking a Python script.
     * This method sets up the environment variables required by the backup script
     * and handles the process execution. The script `backup_postgree.py` is used for
     * performing the backup and is configured with the following environment variables:
     *
     * <ul>
     *   <li>DB_URL: The URL of the PostgreSQL database.</li>
     *   <li>DB_USER: The username for accessing the PostgreSQL database.</li>
     *   <li>DB_PASSWORD: The password for accessing the PostgreSQL database.</li>
     *   <li>BACKUP_DIR: The directory where the encrypted backup files will be stored.</li>
     * </ul>
     *
     * The method reads the output of the script and throws a {@link RuntimeException}
     * if the process fails.
     *
     * @throws RuntimeException if an error occurs during the backup process, including
     *                           script execution failures or non-zero exit codes.
     */
    @Override
    public void performBackup() {
        try {
            String scriptPath = Paths.get("./backup_postgree.py").toString();
            ProcessBuilder pb = new ProcessBuilder("python3", scriptPath);
            pb.environment().put("DB_URL", dbUrl);
            pb.environment().put("DB_USER", dbUser);
            pb.environment().put("DB_PASSWORD", dbPassword);
            pb.environment().put("BACKUP_DIR", backupDirectory + "/encrypt");
            Process process = pb.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Backup process failed with exit code: " + exitCode);
            }
            System.out.println("Backup process exited with code " + exitCode);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during backup process", e);
        }
    }

    /**
     * Decrypts the most recent backup file from the encrypted backups directory.
     * This method identifies the latest encrypted backup file, executes the decryption
     * Python script `decrypt_backup.py`, and places the decrypted file in the specified
     * directory. The environment variables used for the decryption script include:
     *
     * <ul>
     *   <li>ENCRYPTED_FILE: The path to the latest encrypted backup file.</li>
     *   <li>DECRYPTED_DIR: The directory where the decrypted file will be stored.</li>
     *   <li>DB_PASSWORD: The password for decrypting the file (if required by the script).</li>
     * </ul>
     *
     * If no encrypted files are found or if the decryption process fails, a
     * {@link RuntimeException} is thrown.
     *
     * @throws RuntimeException if no encrypted files are found or if an error occurs
     *                           during the decryption process.
     */
    private void decryptLatestBackup() {
        try {
            File encryptDirectory = new File(backupDirectory + "/encrypt/");
            File[] encryptedFiles = encryptDirectory.listFiles((dir, name) -> name.endsWith(".gpg"));
            if (encryptedFiles == null || encryptedFiles.length == 0) {
                throw new RuntimeException("No encrypted files found in " + encryptDirectory.getAbsolutePath());
            }
            Arrays.sort(encryptedFiles, Comparator.comparing(File::lastModified).reversed());
            File latestEncryptedFile = encryptedFiles[0];
            String latestEncryptedFilePath = latestEncryptedFile.getPath();

            String decryptScriptPath = Paths.get("./decrypt_backup.py").toString();
            System.out.println("Running decryption script: " + decryptScriptPath);
            ProcessBuilder decryptPb = new ProcessBuilder("python3", decryptScriptPath);
            decryptPb.environment().put("ENCRYPTED_FILE", latestEncryptedFilePath);
            decryptPb.environment().put("DECRYPTED_DIR", backupDirectory + "/decrypt/");
            decryptPb.environment().put("DB_PASSWORD", dbPassword);
            Process decryptProcess = decryptPb.start();

            BufferedReader decryptIn = new BufferedReader(new InputStreamReader(decryptProcess.getInputStream()));
            String line;
            while ((line = decryptIn.readLine()) != null) {
                System.out.println("Decryption Output: " + line);
            }

            int decryptExitCode = decryptProcess.waitFor();
            System.out.println("Decryption process exited with code " + decryptExitCode);
            if (decryptExitCode != 0) {
                throw new RuntimeException("Decryption process failed with exit code: " + decryptExitCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during decryption process", e);
        }
    }

    /**
     * Restores the PostgreSQL database using the most recent decrypted backup file.
     * This method identifies the latest decrypted file and executes the restore
     * Python script `restore_postgre.py` with the following environment variables:
     *
     * <ul>
     *   <li>DB_URL: The URL of the PostgreSQL database.</li>
     *   <li>DB_USER: The username for accessing the PostgreSQL database.</li>
     *   <li>DB_PASSWORD: The password for accessing the PostgreSQL database.</li>
     *   <li>BACKUP_FILE: The path to the latest decrypted backup file.</li>
     *   <li>TARGET_DATABASE: The name of the database to restore.</li>
     * </ul>
     *
     * If no decrypted files are found or if the restore process fails, a
     * {@link RuntimeException} is thrown.
     *
     * @param targetDatabase The name of the target database to restore. Must not be null or empty.
     * @throws RuntimeException if no decrypted files are found or if an error occurs
     *                           during the restore process.
     */
    private void restoreDatabase(String targetDatabase) {
        try {
            File decryptDirectory = new File(backupDirectory + "/decrypt/");
            File[] decryptedFiles = decryptDirectory.listFiles((dir, name) -> name.endsWith(".dec"));
            if (decryptedFiles == null || decryptedFiles.length == 0) {
                throw new RuntimeException("No decrypted files found in " + decryptDirectory.getAbsolutePath());
            }
            Arrays.sort(decryptedFiles, Comparator.comparing(File::lastModified).reversed());
            File latestDecryptedFile = decryptedFiles[0];
            String latestDecryptedFilePath = latestDecryptedFile.getPath();

            String restoreScriptPath = Paths.get("./restore_postgre.py").toString();
            System.out.println("Running restore script: " + restoreScriptPath);
            ProcessBuilder restorePb = new ProcessBuilder("python3", restoreScriptPath);
            restorePb.environment().put("DB_URL", dbUrl);
            restorePb.environment().put("DB_USER", dbUser);
            restorePb.environment().put("DB_PASSWORD", dbPassword);
            restorePb.environment().put("BACKUP_FILE", latestDecryptedFilePath);
            restorePb.environment().put("TARGET_DATABASE", targetDatabase);
            Process restoreProcess = restorePb.start();

            BufferedReader restoreIn = new BufferedReader(new InputStreamReader(restoreProcess.getInputStream()));
            String line;
            while ((line = restoreIn.readLine()) != null) {
                System.out.println("Restore Output: " + line);
            }

            int restoreExitCode = restoreProcess.waitFor();
            System.out.println("Restore process exited with code " + restoreExitCode);
            if (restoreExitCode != 0) {
                throw new RuntimeException("Restore process failed with exit code: " + restoreExitCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during restore process", e);
        }
    }

    /**
     * Performs a complete restore operation of the PostgreSQL database.
     * This method first decrypts the most recent backup file and then restores
     * the database using the decrypted file. It sequentially calls the methods
     * {@link #decryptLatestBackup()} and {@link #restoreDatabase(String)}.
     *
     * @param targetDatabase The name of the target database to restore. This parameter
     *                       should specify the database to which the decrypted backup
     *                       will be restored. It must be a valid database name.
     * @throws RuntimeException if an error occurs during either the decryption or restore
     *                           process.
     */
    @Override
    public void performRestore(String targetDatabase) {
        decryptLatestBackup();
        restoreDatabase(targetDatabase);
    }
}