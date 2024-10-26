import os
from datetime import datetime
import subprocess
from urllib.parse import urlparse

def perform_backup():
    db_url = os.getenv('DB_URL', "jdbc:postgresql://localhost:5432/inventaris")
    db_user = os.getenv('DB_USER', 'postgres')
    db_password = os.getenv('DB_PASSWORD', '090208')  # Password enkripsi yang sama
    backup_dir = os.getenv('BACKUP_DIR', './backup/encrypt')

    try:
        # Remove the 'jdbc:' prefix if present
        if db_url.startswith("jdbc:"):
            db_url = db_url[5:]

        # Parse the db_url
        result = urlparse(db_url)

        dbname = result.path.lstrip('/')
        host = result.hostname
        port = result.port

        if not all([dbname, host, port]):
            raise ValueError("Invalid DB_URL format")

        # Debugging output
        print(f"Parsed dbname: {dbname}")
        print(f"Parsed host: {host}")
        print(f"Parsed port: {port}")

        # Create the backup directory if it doesn't exist
        if not os.path.exists(backup_dir):
            os.makedirs(backup_dir)

        # Create a backup file with a timestamp
        backup_file = os.path.join(backup_dir, f'inventaris-{datetime.now().strftime("%Y-%m-%d-%H-%M-%S")}.sql')

        # Set the PGPASSWORD environment variable for pg_dump
        env = os.environ.copy()
        env['PGPASSWORD'] = db_password

        # Path to pg_dump from Postgres.app
        pg_dump_path = "/Applications/Postgres.app/Contents/Versions/latest/bin/pg_dump"

        # Perform the backup using pg_dump
        command = [
            pg_dump_path,
            '-h', host,
            '-p', str(port),
            '-U', db_user,
            '-F', 'c',
            '-b',
            '-v',
            '-f', backup_file,
            dbname
        ]

        print("Running command:", ' '.join(command))
        subprocess.run(command, env=env, check=True)

        # Encrypt the backup file with the same password as DB_PASSWORD
        encrypted_backup_file = backup_file + '.gpg'
        encrypt_command = [
            'gpg', '--symmetric', '--cipher-algo', 'AES256', '--batch', '--yes',
            '--passphrase', db_password, '--output', encrypted_backup_file, backup_file
        ]
        subprocess.run(encrypt_command, check=True)
        os.remove(backup_file)  # Remove the unencrypted file if encryption was successful
        print(f"Backup berhasil disimpan dan dienkripsi di: {encrypted_backup_file}")

    except Exception as e:
        print(f"Error saat melakukan backup: {e}")

if __name__ == "__main__":
    perform_backup()