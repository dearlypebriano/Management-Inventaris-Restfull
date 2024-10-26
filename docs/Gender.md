# API Gender Specification v6.4.5

## Introduction

- API Gender Specification
- v6.4.5

## Create New Gender:
- Endpoint : `http://localhost:5050/api/v1/genders`
- Method : `POST`
- Authentication : `JWT Auth Bearer`
- Enumeration : `Gender Type [Laki-laki | Perempuan]`
- Request Body : `JSON`
- Response Body : `JSON`

### Request Body :

```json
{
    "name": "Laki-laki"
}
```

### Response Body (Success):
`201 Created`

```json
{
  "id": 1,
  "name": "Laki-laki"
}
```

### Response Body (Error):
`400 Bad Request`

```json
{
  "message": "Bad Request"
}
```

## Get All Gender

- Endpoint : `http://localhost:5050/api/v1/genders/list`
- Method : `GET`
- Response Body : `JSON`

### Response Body (Success):
`200 OK`

```json
{
  "content": [
    {
      "id": 1,
      "name": "Laki-laki"
    },
    {
      "id": 2,
      "name": "Perempuan"
    }
  ]
}
```

### Response Body (Error):
`400 Bad Request`

```json
{
  "message": "Bad Request"
}
```