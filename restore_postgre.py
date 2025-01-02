import os
import subprocess

def perform_restore():
    db_url = os.getenv('DB_URL', "jdbc:postgresql://localhost:5432/inventaris")
    db_user = os.getenv('DB_USER', 'postgres')
    db_password = os.getenv('DB_PASSWORD', '090208')
    decrypt_dir = os.path.join(os.getenv('BACKUP_DIR', './backup'), 'decrypt')

    if not os.path.isdir(decrypt_dir):
        print(f"Error: Folder {decrypt_dir} tidak ditemukan.")
        return

    # Temukan file didekripsi terbaru di folder decrypt
    decrypted_files = [f for f in os.listdir(decrypt_dir) if f.endswith('.dec')]

    if not decrypted_files:
        print("Tidak ada file didekripsi ditemukan di folder.")
        return

    latest_file = max(decrypted_files, key=lambda f: os.path.getmtime(os.path.join(decrypt_dir, f)))
    backup_file = os.path.join(decrypt_dir, latest_file)
    target_db = os.getenv('DB_TARGET', 'inventaris')

    try:
        # Set the PGPASSWORD environment variable for pg_restore
        env = os.environ.copy()
        env['PGPASSWORD'] = db_password

        # Path to pg_restore from PostgreSQL installation
        pg_restore_path = "/Applications/Postgres.app/Contents/Versions/latest/bin/pg_restore"

        # Perform the restore using pg_restore
        command = [
            pg_restore_path,
            '-h', 'localhost',
            '-p', '5432',
            '-U', db_user,
            '-d', target_db,
            '-v',  # verbose
            '-c',  # clean
            backup_file
        ]

        print(f"Restoring file: {backup_file}")
        print("Running command:", ' '.join(command))
        subprocess.run(command, env=env, check=True)

        print(f"Restore berhasil ke database: {target_db}")

    except Exception as e:
        print(f"Error saat melakukan restore: {e}")

if __name__ == "__main__":
    perform_restore()
