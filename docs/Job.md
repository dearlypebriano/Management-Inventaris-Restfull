# API Job Specificaation v6.4.5

## Introduction

- API Job Specificaation
- v6.4.5

## Create New Job

- Endpoint: `http://localhost:5050/api/v1/jobs/create`
- Method: `POST`
- Request Body: `JSON`
- Response Body: `JSON`

### Request Body

```json
{
  "name": "Software Developer",
  "description": "Bertanggung jawab terhadap keseluruhan proses pengembangan sebuah software atau aplikasi"
}
```

### Response Body (Success):
`201 Created`

```json
{
  "id": 10,
  "name": "Software Developer",
  "description": "Bertanggung jawab terhadap keseluruhan proses pengembangan sebuah software atau aplikasi"
}
```

### Response Body (Failure):
`403 Bad Request`

```json
{
  "message": "Invalid request!"
}
```

## Update Data Job

- Endpoint: `http://localhost:5050/api/v1/jobs/update/{jobId}`
- Method: `PATCH`
- Request Body: `JSON`
- Response Body: `JSON`

### Request Body

```json
{
  "name": "Software Engineer",
  "description": "Menganalisis kebutuhan pengguna dan kemudian merancang perangkat lunak yang sesuai untuk memenuhi kebutuhan tersebut"
}
```

### Response Body (Success)
`200 OK`
```json
{
  "id": 10,
  "name": "Software Developer",
  "description": "Bertanggung jawab terhadap keseluruhan proses pengembangan sebuah software atau aplikasi"
}
```

### Response Body (Failure):
`403 Bad Request`

```json
{
  "message": "Invalid request!"
}
```

## Delete Data Job

- Endpoint: `http://localhost:5050/api/v1/jobs/delete/{jobId}`
- Method: `DELETE`
- Response Body: `JSON`

### Response Body (Success):
`204 No Content`
```json
{
  "message": "Job deleted"
}
```

### Response Body (Failure)
`403 Bad Request`

```json
{
  "message": "Invalid request!"
}
```

## Find By Id Data Job

- Endpoint: `http://localhost:5050/api/v1/jobs/fidById/{jobId}`
- Method: `GET`
- Response Body: `JSON`

### Response Body (Success):
`200 OK`

```json
{
  "id": 10,
  "name": "Software Engineer",
  "description": "Menganalisis kebutuhan pengguna dan kemudian merancang perangkat lunak yang sesuai untuk memenuhi kebutuhan tersebut"
}
```

### Response Body (Failure)
`403 Bad Request`

```json
{
  "message": "Invalid request!"
}
```

## Get All Data Job

- Endpoint: `http://localhost:5050/api/v1/jobs/list`
- Method: `GET`
- Response Body: `JSON`

### Response Body (Success):
`200 OK`

```json
{
  "content": [
    {
      "id": 10,
      "name": "Software Engineer",
      "description": "Menganalisis kebutuhan pengguna dan kemudian merancang perangkat lunak yang sesuai untuk memenuhi kebutuhan tersebut"
    },
    {
      "id": 11,
      "name": "UI / UX Designer",
      "description": "Merancang sebuah tampilan pada aplikasi ataupun website di mana produk atau layanannya dapat berfungsi dengan baik dan memiliki kemudahan akses oleh para penggunanya"
    }
  ]
}
```

### Response Body (Failure)
`403 Bad Request`

```json
{
  "message": "Invalid request!"
}
```

## Find By Name Data Job

- Endpoint: `http://localhost:5050/api/v1/jobs/findByName/{jobName}`
- Method: `GET`
- Response Body: `JSON`

### Response Body (Success):
`200 OK`

```json
{
  "id": 10,
  "name": "**Software Engineer**",
  "description": "Menganalisis kebutuhan pengguna dan kemudian merancang perangkat lunak yang sesuai untuk memenuhi kebutuhan tersebut"
}
```

### Response Body (Failure)
`403 Bad Request`

```json
{
  "message": "Invalid request!"
}
```