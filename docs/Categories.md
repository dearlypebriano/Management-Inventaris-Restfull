# API Categories Specification v6.4.5

## Introduction

- API Categories Specification
- v6.4.5

## Create New Categories

- Endpoint : `http://localhost:5050/api/v1/categories/create`
- Method : `POST`
- Token : `Berarer : {Token} Token Mandatory In Header!`
- Request Body : `MULTIPART / FORM-DATA`
- Response Body : `JSON`

### Request Body:

```json
{
  "categoryName": "Fashion",
  "description": "Fashion description...",
  "file": "Multipart / File from uploaded"
}
```

### Response Body (Success):
`201 Created`

```json
{
  "id": "0f53c901-d4a8-43a5-aba6-6418ac384a52", 
  "categoryName": "Fashion",
  "description": "Fashion Description...",
  "imageUrl": "http://127.0.0.1:5050/api/minio/download/categories/83ff9015.png",
  "isConstant": true
}
```

### Response Body (Error):
`400 Bad Request`

```json
{
  "status": false,
  "error": {
    "code": 400,
    "message": "Bad Request!"
  }
}
```

## Update Data Categories

- Endpoint : `http://localhost:5050/api/v1/categories/update/{categoryId}`
- Method : `PATCH`
- Token : `Berarer : {Token} Token Mandatory In Header!`
- Request Body : `MULTIPART / FORM-DATA`
- Response Body : `JSON`

### Request Body:

```json
{
  "description": "Kategori fashion adalah segala sesuatu yang dikenakan pada tubuh, baik dengan maksud melindungi tubuh maupun memperindah penampilan tubuh"
}
```

### Response Body (Success):
`200 OK`

```json
{
  "id": "0f53c901-d4a8-43a5-aba6-6418ac384a52", 
  "categoryName": "Fashion",
  "description": "Kategori fashion adalah segala sesuatu yang dikenakan pada tubuh, baik dengan maksud melindungi tubuh maupun memperindah penampilan tubuh",
  "imageUrl": "http://127.0.0.1:5050/api/minio/download/categories/83ff9015.png",
  "isConstant": true
}
```

### Response Body (Error):
`400 Bad Request`

```json
{
  "status": false,
  "error": {
    "code": 400,
    "message": "Bad Request!"
  }
}
```

### Response Body (Error):
`404 Not Found`

```json
{
  "status": false,
  "error": {
    "code": 404,
    "message": "Category Not Found!"
  }
}
```

## Delete Data Categories

- Endpoint : `http://localhost:5050/api/v1/categories/delete/{categoryId}`
- Method : `DELETE`
- Token : `Berarer : {Token} Token Mandatory In Header!`
- Response Body : `JSON`

### Response Body (Success):
`200 OK`
`Categories deleted successfully`

### Response Body (Error):
`400 Bad Request`

```json
{
  "status": false,
  "error": {
    "code": 400,
    "message": "Bad Request!"
  }
}
```

### Response Body (Error):
`403 Forbidden`

```json
{
  "status": false,
  "error": {
    "code": 403,
    "message": "Forbidden!"
  }
}
```

### Response Body (Error):
`404 Not Found`

```json
{
  "status": false,
  "error": {
    "code": 404,
    "message": "Category Not Found!"
  }
}
```

## Find Data Categories By ID

- Endpoint : `http://localhost:5050/api/v1/categories/findById/{categoryId}`
- Example Endpoint : `http://localhost:5050/api/v1/categories/findById/0f53c901-d4a8-43a5-aba6-6418ac384a52`
- Method : `GET`
- Response Body : `JSON`

### Response Body (Success):
`200 OK`
```json
{
  "id": "0f53c901-d4a8-43a5-aba6-6418ac384a52",
  "categoryName": "Fashion",
  "description": "Kategori fashion adalah segala sesuatu yang dikenakan pada tubuh, baik dengan maksud melindungi tubuh maupun memperindah penampilan tubuh",
  "imageUrl": "http://127.0.0.1:5050/api/minio/download/categories/83ff9015.png",
  "isConstant": true
}
```

### Response Body (Error):
`400 Bad Request`

```json
{
  "status": false,
  "error": {
    "code": 400,
    "message": "Bad Request!"
  }
}
```

### Response Body (Error):
`404 Not Found`

```json
{
  "status": false,
  "error": {
    "code": 404,
    "message": "Category Not Found!"
  }
}
```

## Find Data Category By keyword

- Endpoint : `http://localhost:5050/api/v1/categories/findByKeyword/{keyword}`
- Example Endpoint : `http://localhost:5050/api/v1/categories/categoryName/fashion`
- Method : `GET`
- Response Body : `JSON`

### Response Body (Success):
`200 OK`
```json
[
  {
    "id": "0f53c901-d4a8-43a5-aba6-6418ac384a52",
    "categoryName": "Fashion",
    "description": "Kategori fashion adalah segala sesuatu yang dikenakan pada tubuh, baik dengan maksud melindungi tubuh maupun memperindah penampilan tubuh",
    "imageUrl": "http://127.0.0.1:5050/api/minio/download/categories/83ff9015.png",
    "isConstant": true
  },
  {"..."}
]
```

### Response Body (Error):
`400 Bad Request`

```json
{
  "status": false,
  "error": {
    "code": 400,
    "message": "Bad Request!"
  }
}
```

### Response Body (Error):
`404 Not Found`

```json
{
  "status": false,
  "error": {
    "code": 404,
    "message": "Category Not Found!"
  }
}
```

## Find Data Category By name

- Endpoint : `http://localhost:5050/api/v1/categories/findByName/{categoryName}`
- Example Endpoint : `http://localhost:5050/api/v1/categories/categoryName/Fashion`
- Method : `GET`
- Response Body : `JSON`

### Response Body (Success):
`200 OK`
```json
{
  "id": "0f53c901-d4a8-43a5-aba6-6418ac384a52",
  "categoryName": "Fashion",
  "description": "Kategori fashion adalah segala sesuatu yang dikenakan pada tubuh, baik dengan maksud melindungi tubuh maupun memperindah penampilan tubuh",
  "imageUrl": "http://127.0.0.1:5050/api/minio/download/categories/83ff9015.png",
  "isConstant": true
}
```

### Response Body (Error):
`400 Bad Request`

```json
{
  "status": false,
  "error": {
    "code": 400,
    "message": "Bad Request!"
  }
}
```

### Response Body (Error):
`404 Not Found`

```json
{
  "status": false,
  "error": {
    "code": 404,
    "message": "Category Not Found!"
  }
}
```

## List Data Category

- Endpoint : `http://localhost:5050/api/v1/categories/list`
- Method : `GET`
- Response Body : `JSON`

### Response Body (Success):
`200 OK`
`Categories deleted successfully`

### Response Body (Error):
`400 Bad Request`

```json
{
  "status": false,
  "error": {
    "code": 400,
    "message": "Bad Request!"
  }
}
```

### Response Body (Error):
`404 Not Found`

```json
{
  "status": false,
  "error": {
    "code": 404,
    "message": "Category Not Found!"
  }
}
```