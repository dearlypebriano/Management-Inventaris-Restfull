import os
from minio import Minio
from minio.error import S3Error
import zipfile
import datetime

# Ambil nilai dari environment variable MINIO_URL, jika tidak ada, gunakan default
minio_url = os.getenv("MINIO_URL", "http://localhost:9001")
access_key = os.getenv("MINIO_ACCESS_KEY", "w53sVDQLEi8J8gJW5xYZ")
secret_key = os.getenv("MINIO_SECRET_KEY", "rJz0Ck9BKKRJplk4o923RILI1we2iyr4Ibosdqhy")
bucket_name = os.getenv("MINIO_BUCKET_NAME", "inventaris")
backup_dir = os.getenv("BACKUP_DIR", "./minio-backup")

# Inisialisasi client Minio
minio_client = Minio(
    minio_url.replace("http://", "").replace("https://", ""),
    access_key=access_key,
    secret_key=secret_key,
    secure=False
)


def zip_directory():
    # Dapatkan tanggal hari ini dalam format yang diinginkan (hari dalam bahasa Indonesia)
    today = datetime.datetime.now()
    bulan_indonesia = {
        1: "Januari", 2: "Februari", 3: "Maret", 4: "April",
        5: "Mei", 6: "Juni", 7: "Juli", 8: "Agustus",
        9: "September", 10: "Oktober", 11: "November", 12: "Desember"
    }
    tanggal_hari_ini = today.strftime(f"backup_minio-%d-{bulan_indonesia[today.month]}-%Y")
    zip_filename = os.path.join(backup_dir, f"{tanggal_hari_ini}.zip")
    
    with zipfile.ZipFile(zip_filename, 'w') as backup_zip:
        for folder_name, subfolders, filenames in os.walk(backup_dir):
            for filename in filenames:
                if filename == f"{tanggal_hari_ini}.zip":
                    continue
                file_path = os.path.join(folder_name, filename)
                backup_zip.write(file_path, os.path.relpath(file_path, backup_dir))
    
    print(f"Created zip file {zip_filename}")

try:
    zip_directory()
    print("Backup successful")
except S3Error as e:
    print(f"Error occurred: {e}")
