# API Employee Specification v7.0.0

## Introduction

- API Employee Specification
- v7.0.0

## Create New Employee

- Endpoint : `http://localhost:5050/api/v1/employees/create`
- Method : `POST`
- Request Body : `MULTIPART`
- Response Body : `JSON`

### Request Body:

```json
{
  "name": "Joko Riyanto",
  "birthDate": "2008-02-09",
  "gender": {
    "Laki-laki",
    "Perempuan",
  },
  "salary": "Rp. 8.000.000",
  "job": [
    "Software Developer",
    "Software Engineer",
    "Network Engineer",
    "UI / UX Designer"
  ],
  "provinceName": [
    "Jawa Barat",
    "Jawa Tengah",
    "Jawa Timur",
    "****"
  ],
  "regencyName": [
    "KABUPATEN BANDUNG",
    "KABUPATEN CIREBON",
    "KOTA SURABAYA"
  ],
  "districtName": [
    "WEDI",
    "MUYLOREJO",
    "SUKOHARJO"
  ],
  "villageName": [
    "WEDI",
    "MUYLOREJO",
    "SUKOHARJO"
  ],
  "postalCode": 13818
}
```

### Response Body (Success):
`201 Created`

```json
{
  "id": "P98hqwe-U82hisa", // Generated from Secure Random
  "name": "Joko Riyanto",
  "age": 20,
  "nip": 12345,
  "job": "Software Developer",
  "gender": "Laki-laki",
  "salary": "Rp. 8.000.000",
  "province": "JAWA TIMUR",
  "regency": "KOTA SURABAYA",
  "district": "MULYOREJO",
  "village": "MULYOREJO",
  "postalCode": 12345
}
```

### Response Body (Error):
`500 Internal Server Error || 403 Bad Request`

```json
{
  "error": true,
  "message": "Internal Server Error || 403 Bad Request"
}
```

## Update Data Employee

- Endpoint : `http://localhost:5050/api/v1/employees/update/{employeeId}`
- Method : `PATCH`
- Request Body : `MULTIPART`
- Response Body : `JSON`

### Request Body
```
name        |   Joko Lirboyo
birthDate   |   2008-02-09
gender      |   Perempuan
salary      |   10000000
job         |   UI / UX Designer
provinceName|   JAWA TIMUR
regencyName |   KOTA SURBAYA
districtName|   MULYOREJO
villageName |   MULYOREJO
postalCode  |   61732
```

### Response Body (Success):
`200 OK`

```json
{
  "id": "P98hqwe-U82hisa", // Generated from Secure Random
  "name": "Joko Lirboyo",
  "age": 20,
  "nip": 12345,
  "job": "UI / UX Designer",
  "gender": "Perempuan",
  "salary": "Rp. 10.000.000",
  "province": "JAWA TIMUR",
  "regency": "KOTA SURABAYA",
  "district": "MULYOREJO",
  "village": "MULYOREJO",
  "postalCode": 61732
}
```

### Response Body (Error):
`500 Internal Server Error || 403 Bad Request`

```json
{
  "error": true,
  "message": "Internal Server Error || 403 Bad Request"
}
```

## Delete Data Employee

- Endpoint : `http://localhost:5050/api/v1/employees/delete/{employeeId}`
- Method : `DELETE`
- Response Body : `JSON`

### Response Body:
`204 No Content`

```json
{
  "message": "OK!"
}
```

### Response Body (Error):
`403 Bad Request`
```json
{
  "message": "Bad Request"
}
```

## Get a list of employees (Private)

- Endpoint: `http://localhost:5050/api/v1/employees/list?view=private`
- Method: `GET`
- Response Body: `JSON`

### Response Body (Success):
`200 OK`

```json
{
  "content": [
    {
      "id": "P98hqwe-U82hisa",
      "name": "Joko Lirboyo",
      "age": 20,
      "nip": 12345,
      "job": "UI / UX Designer",
      "gender": "Laki-laki",
      "salary": "Rp. 10.000.000",
      "province": "JAWA TIMUR",
      "regency": "KOTA SURABAYA",
      "district": "MULYOREJO",
      "village": "MULYOREJO",
      "postalCode": 61732
    },
    {
      "id": "8gwuUsyd2kP",
      "name": "Dearly Febriano",
      "age": 20,
      "nip": 1532,
      "job": "Software Developer",
      "gender": "Laki-laki",
      "salary": "Rp. 14.000.000",
      "province": "JAWA TIMUR",
      "regency": "KOTA SURABAYA",
      "district": "MULYOREJO",
      "village": "MULYOREJO",
      "postalCode": 60115
    }
  ]
}
```

### Get a list of employees (Public)

- Endpoint: `http://localhost:5050/api/v1/employees/list?view=public`
- Method: `GET`
- Response Body: `JSON`

### Response Body (Success):
`200 OK`
```json
{
  "id": "P98hqwe-U82hisa",
  "name": "Joko Lirboyo",
  "age": 20,
  "job": "UI / UX Designer",
  "gender": "Laki-laki"
}
```

### Response Body (Error):
`403 Bad Request`
```json
{
  "message": "Bad Request"
}
```

## Get a name and nip of employees

- Endpoint: `http://localhost:5050/api/v1/employees/findByNameAndNip/{name}/{nip}`
- Method: `GET`
- Response Body: `JSON`

### Response Body (Success):
`200 OK`
```json
{
  "id": "8gwuUsyd2kP",
  "name": "Dearly Febriano",
  "age": 20,
  "nip": 1532,
  "job": "Software Developer",
  "gender": "Laki-laki",
  "salary": "Rp. 14.000.000",
  "province": "JAWA TIMUR",
  "regency": "KOTA SURABAYA",
  "district": "MULYOREJO",
  "village": "MULYOREJO",
  "postalCode": 60115
}
```

### Response Body (Error):
`404 Not Found`
```json
{
  "message": "Data employee not found"
}
```

## Get a list job from the database

- Endpoint: `http://localhost:5050/api/v1/employees/findAllByJob/Software?view=private`
- Method: `GET`
- Response Body: `JSON`

### Response Body (Success):
`200 OK`

```json
{
  "content": [
    {
      "id": "P98hqwe-U82hisa",
      "name": "Joko Lirboyo",
      "age": 20,
      "nip": 12345,
      "job": "UI / UX Designer",
      "gender": "Laki-laki",
      "salary": "Rp. 10.000.000",
      "province": "JAWA TIMUR",
      "regency": "KOTA SURABAYA",
      "district": "MULYOREJO",
      "village": "MULYOREJO",
      "postalCode": 61732
    },
    {
      "id": "8gwuUsyd2kP",
      "name": "Dearly Febriano",
      "age": 20,
      "nip": 1532,
      "job": "**Software Developer**",
      "gender": "Laki-laki",
      "salary": "Rp. 14.000.000",
      "province": "JAWA TIMUR",
      "regency": "KOTA SURABAYA",
      "district": "MULYOREJO",
      "village": "MULYOREJO",
      "postalCode": 60115
    }
  ]
}
```

### Get a list of employees (Public)

- Endpoint: `http://localhost:5050/api/v1/employees/list?view=public`
- Method: `GET`
- Response Body: `JSON`

### Response Body (Success):
`200 OK`
```json
{
  "id": "P98hqwe-U82hisa",
  "name": "Joko Lirboyo",
  "age": 20,
  "job": "**UI / UX Designer**",
  "gender": "Laki-laki"
}
```