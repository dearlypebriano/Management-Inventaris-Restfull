package com.management.ManagementInventaris.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;

@Service
public class MinioBackupService {

    @Value("${backup.minio.directory}")
    private String backupDirectory;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.bucketName}")
    private String bucketName;

    public void performBackup() {
        try {
            String scriptPath = Paths.get("./backup_minio.py").toString();
            ProcessBuilder pb = new ProcessBuilder("python3", scriptPath);
            pb.environment().put("MINIO_URL", minioUrl);
            pb.environment().put("MINIO_ACCESS_KEY", accessKey);
            pb.environment().put("MINIO_SECRET_KEY", secretKey);
            pb.environment().put("MINIO_BUCKET_NAME", bucketName);
            pb.environment().put("BACKUP_DIR", backupDirectory);
            Process process = pb.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}