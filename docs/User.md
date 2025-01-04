# API USER AUTH / REGISTER SPECIFICATION v7.0.0

## Register User :

- Endpoint : `http://localhost:5050/api/v1/auth/register`
- Method : `POST`
- Request Body : `JSON`
- Response Body : `JSON`

### Request Body :
```js
if (repository.existingEmail(request.getEmail()) != null){
    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
}
```

```json
{
  "firstname": "Dearly",
  "lastname": "Febriano",
  "email": "example01@gmail.com",
  "password": "examplepass",
  "phone": "083xxx",
  "role": [
    "ADMIN",
    "USER",
    "MANAGER"
    ],
  "gender": [
    "Laki-laki",
    "Perempuan"
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
  ]
}
```

### Response Body (Success) :
``200 OK``

```json
{
  "access_token": "access_token_string",
  "refresh_token": "refresh_token_string"
}
```

### Response Body (Error) :
``400 Bad Request``
```json
{
  "error": "Bad Request"
}
```

## Login User :

- Endpoint : `http://localhost:5050/api/v1/auth/authenticate`
- Method : `POST`
- Request Body : `JSON`
- Response Body : `JSON`

### Request Body :

```json
{
  "email": "example01@gmail.com",
  "password": "examplepass"
}
```

### Response Body (Success) :
``200 OK``

```json
{
  "access_token": "access_token_string",
  "refresh_token": "refresh_token_string"
}
```

### Response Body (Error) :
``400 Bad Request``
```json
{
  "error": "Bad Request"
}
```

## Logout User :

- Method : `POST`
- Request Body : `JSON`
- Response Body : `JSON`

### Request Header :
```json
{
  "Authorization": "access_token_string"
}
```

### Response Body (Success) :
``200 OK``

```json
{
  "message": "Logout Success"
}
```

### Response Body (Error) :
``400 Bad Request``
```json
{
  "error": "Bad Request"
}
```

## Find User By Email (ADMIN ONLY)

- Endpoint : `http://localhost:5050/api/v1/auth/findUserByEmail/details?email={email}`
- Method: `GET`
- Response Body : `JSON`

### Response Body (Success):

```json
{
  "firstname": "Dearly",
  "lastname": "Febriano",
  "email": "example01@gmail.com",
  "password": "examplepass",
  "phone": "083xxx",
  "role": [   
    "ADMIN",
    "USER",
    "MANAGER"
    ],
  "gender": [
    "Laki-laki",
    "Perempuan"
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
  ]
}
```

### Response Body (Error):
`404 Not Found | 403 Forbidden`

```json
{
  "error": "Email with name: example@gmail.com Not Found | Access Was Denied"
}
```

## Save User Profile

- Endpoint : `http://localhost:5050/api/v1/auth/saveUserProfile`
- Method: `POST`
- Request Body : `MULTIPART FORM DATA`
- Response Body : `JSON`

### Request Body :
```json
{
  "upload_profile": "upload with file users"
}
```

### Response Body (Success) :
``200 OK``

```json
{
  "message": "Upload Success"
}
```

### Response Body (Error) :
``400 Bad Request``
```json
{
  "error": "Bad Request"
}
```