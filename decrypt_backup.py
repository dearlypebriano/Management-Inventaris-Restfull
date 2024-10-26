import os
import subprocess

def decrypt_latest_file():
    db_password = os.getenv('DB_PASSWORD', '090208')  # Password yang sama untuk enkripsi dan dekripsi
    encrypt_dir = os.getenv('BACKUP_DIR', './backup/encrypt')
    decrypt_dir = os.path.join(os.getenv('BACKUP_DIR', './backup'), 'decrypt')

    # Validasi folder input
    if not os.path.isdir(encrypt_dir):
        print(f"Error: Folder {encrypt_dir} tidak ditemukan.")
        return

    if not os.path.isdir(decrypt_dir):
        os.makedirs(decrypt_dir)  # Buat folder decrypt_dir jika belum ada

    # Temukan file terenkripsi terbaru di folder encrypt
    encrypted_files = [f for f in os.listdir(encrypt_dir) if f.endswith('.gpg')]

    if not encrypted_files:
        print("Tidak ada file terenkripsi ditemukan di folder.")
        return

    latest_file = max(encrypted_files, key=lambda f: os.path.getmtime(os.path.join(encrypt_dir, f)))
    encrypted_file_path = os.path.join(encrypt_dir, latest_file)
    decrypted_file_path = os.path.join(decrypt_dir, latest_file.replace('.gpg', '.dec'))

    print(f"Decrypting the latest file: {encrypted_file_path}")

    try:
        # Perintah untuk mendekripsi file
        decrypt_command = [
            'gpg', '--batch', '--yes', '--decrypt', '--passphrase', db_password,
            '--output', decrypted_file_path, encrypted_file_path
        ]
        subprocess.run(decrypt_command, check=True)

        print(f"File berhasil didekripsi dan disimpan di: {decrypted_file_path}")

        # Menggunakan pg_restore untuk mengubah file hasil decrypt menjadi SQL yang dapat dibaca
        sql_output_path = decrypted_file_path.replace('.dec', '')
        pg_restore_command = [
            'pg_restore', '-f', sql_output_path, '-F', 'c', decrypted_file_path
        ]
        subprocess.run(pg_restore_command, check=True)

        print(f"File SQL berhasil dihasilkan di: {sql_output_path}")

    except subprocess.CalledProcessError as e:
        print(f"Error saat mendekripsi atau memproses file {encrypted_file_path}: {e}")

if __name__ == "__main__":
    decrypt_latest_file()