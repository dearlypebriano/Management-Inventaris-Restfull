# E-Commerce Backend System

This project is a backend system for E-Commerce, developed using Java, with several integrated technologies for optimal performance and scalability.

## Technologies Used

- **Java**: The system is built using Java. Ensure you have at least **Java 17** installed.
- **Spring Boot**: This is the primary framework used for building the application.
- **PostgreSQL**: The system uses PostgreSQL as its primary database.
- **FlywayDB**: Integrated for database migrations and version control.
- **Redis**: Used for caching to improve performance and scalability.
- **MinIO**: Serves as the S3-compatible Cloud Object Storage.
- **Maven**: Dependency management and project build are handled by Maven.

## Prerequisites

Before running the system, ensure you have the following installed and properly configured:

1. **Java 17 or above**:
    - [Download Java From Oracle](https://www.oracle.com/id/java/technologies/downloads/)

   Verify installation:
    ```bash
    java -version
    ```
   Ensure the output shows Java 17 or above.

2. **PostgreSQL**:
    - [Download PostgreSQL](https://www.postgresql.org/download/)

   After installation, create a database for the system:
    ```sql
    CREATE DATABASE inventaris;
    ```

3. **Redis**:
    - [Download Redis](https://redis.io/download)

   Start the Redis server with command:
    ```bash
    redis-server
    ```

4. **MinIO**:
    - [Download MinIO](https://min.io/download)

   Run the MinIO server:
    ```bash
    minio server start --address=:9001 # 9001 is the MinIO Server Port that has been set
    ```

5. **Maven**:
    - [Download Maven](https://maven.apache.org/download.cgi)

   Verify installation:
    ```bash
    mvn -version
    ```

6. **FlywayDB**:
    - Flyway is integrated with the Spring Boot project, so no separate installation is needed. Flyway migrations will run automatically when you start the application.

## Setup and Run the Application

1. **Clone the repository**:
    ```bash
    git clone https://github.com/dearlypebriano/Management-Inventaris-Restfull
    cd Management-Inventaris-Restfull
    ```

2. **Configure the application**:

   Update the `application.properties` or `application.yml` with your database, Redis, and MinIO configurations:
    ```properties
   # Database Configuration
   spring.application.name=ManagementInventaris
    spring.datasource.driver-class-name=org.postgresql.Driver
    spring.datasource.username=postgres
    spring.datasource.password=090208
    spring.datasource.url=jdbc:postgresql://localhost:5432/inventaris
    spring.datasource.type=com.zaxxer.hikari.HikariDataSource
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=false
    spring.jpa.properties.hibernate.format_sql=true
    spring.jpa.database=postgresql
    spring.datasource.hikari.minimum-idle=10
    spring.datasource.hikari.maximum-pool-size=100
    spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
    spring.jpa.properties.hibernate.show_sql=true

    # Redis Server Configuration (With Configuration Code Java in Management-Inventaris-Restfull/src/main/java/com/management/ManagementInventaris/config/RedisConfig.java)
   redis.host=localhost
   redis.port=6379
   
    # MinIO Server Configuration
   minio.url=http://localhost:9001
    #minio.url=http://play.min.io:9443 # Enable this when using the Public Version of MinIO URL
    minio.username=minioadmin
    minio.password=minioadmin
   # The Access Key and Secret Key is obtained from the automatic generation of Minio access keys
    minio.accessKey=FrMHV8PConStRV2eNnLZ  
    minio.secretKey=2kFrXZBtRpKvP8tmAI2Bc3pKX5Q2fQ7Gk2MJiIyK
    minio.bucketName=inventaris
    minio.defaultProfilePath=profile/default/default_profile.jpg
    put-object-part-size=5242880
    spring.main.allow-bean-definition-overriding=true
    ```
   
3. **Run The Application:**
```bash
mvn spring-boot:run 
```
The application should now start, running on the default port 5050.

## Usage
After the application is up and running, you can start interacting with the API using tools like Postman or cURL.

## Example API Request
```bash
curl -X GET http://localhost:5050/api/v1/products/list \
-H "X-API-KEY: your_api_key"
```
Example Response:

```json
{
  "data": [],
  "paging": {
    "currentPage": 0, // Default page this 0
    "totalPage": 0, // Default total page this 0
    "size": 0 // The default size is 0 because there is no content in the data array
  }
}
```

## Database Migrations
Flyway automatically handles database migrations on startup. To manually trigger migrations, use:
```bash 
mvn flyway:migrate
```

## Caching With Redis
The system uses Redis to cache frequently accessed data. This improves response times and reduces the load on the database.

## Object Storage with MinIO
MinIO is used as the cloud object storage solution, providing an S3-compatible API for storing and retrieving files.

## Uploading Files
example of uploading a file using the MinIO API:
```bash
curl -X PUT -T "file.txt" http://localhost:9001/bucket-name/file.txt -H "Authorization: Bearer your_token"
```

## License
This Project is licensed under the  [MIT License](LICENSE)

----------------------------------------------------------------
**Author**: Dearly Febriano Irwansyah
```markdown
### Penjelasan README.md

- **Introduction**: Bagian ini menjelaskan secara singkat tentang sistem backend yang telah Anda bangun dan teknologi yang digunakan.
- **Technologies Used**: Menyebutkan teknologi utama yang digunakan dalam proyek ini, termasuk versi yang disarankan.
- **Prerequisites**: Bagian ini memberikan panduan untuk mengatur lingkungan pengembangan dengan teknologi yang diperlukan sebelum menjalankan aplikasi.
- **Setup and Run the Application**: Menjelaskan langkah-langkah untuk mengkloning repository, mengkonfigurasi aplikasi, dan menjalankannya.
- **Usage**: Memberikan contoh cara menggunakan API setelah sistem berjalan.
- **Database Migrations**: Informasi tentang bagaimana Flyway mengelola migrasi database.
- **Caching with Redis**: Menjelaskan penggunaan Redis untuk caching dalam sistem.
- **Object Storage with MinIO**: Menyediakan contoh cara mengunggah file menggunakan MinIO.
- **License**: Merujuk pada file `LICENSE` yang menjelaskan lisensi proyek ini.
```