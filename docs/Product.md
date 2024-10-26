# API Product Specification v6.4.5

## Introduction

- API Product Specification
- version: `v6.4.5`

## Create New Product

- Endpoint : `http:/localhost/api/v1/products/create`
- Method : `POST`
- Header : `Authorization: Bearer <JWT token>`
- Request Body : `MULTIPART / FORM-DATA`
- Response Body : `JSON`

### Request Body:

```json
{
  "units": [
    "Set",
    "Paket",
    "..."
  ],
  "title": "Sepatu Pelajar",
  "description": "Panel admin adalah alat internal, jadi kemungkinan besar Anda memiliki sistem untuk login karyawan Anda",
  "price": 500000,
  "quantity": 55,
  "variants": [
    {
      "name": "Ukuran 41",
      "price": 550000
    },
    {
      "name": "Ukuran 42",
      "price": 625999
    },
    {
      "name": "...",
      "price": 0
    }
  ],
  "categories": [
    "Fashion",
    "..."
  ]
}
```

### Response Body (Success):
`201 Created`

```json
{
  "id": "cc17bf2abf04081239cd8a0ee022ce03",
  "units": [
    "Set",
    "Pcs",
    "Paket"
  ],
  "title": "Sepatu Pelajar Hommyped",
  "description": "Panel admin adalah alat internal, jadi kemungkinan besar Anda memiliki sistem untuk login karyawan Anda",
  "price": 500000,
  "quantity": 55,
  "rating": "0.0",
  "variants": [
    {
      "name": "Ukuran 41",
      "price": 550000
    },
    {
      "name": "Ukuran 42",
      "price": 625999
    },
    {
      "name": "Ukuran 44",
      "price": 650000
    }
  ],
  "categories": [
    "Fashion"
  ],
  "imageUrls": [
    "http://localhost/api/minio/download/product/d9f90a0d.jpeg"
  ],
  "barcodeProduct": "http://localhost/api/minio/download/barcodes/file**.png",
  "shareLink": "http://localhost/api/v1/products/findById/cc17bf2abf04081239cd8a0ee022ce03",
  "createdAt": "2024-06-09 12:18:47 WIB",
  "updatedAt": "2024-06-10 12:19:17 WIB",
  "uploadedBy": {
    "id": 1,
    "email": "dearlyfebrianoi@gmail.com",
    "firstname": "Febriano",
    "lastname": "Irwansyah",
    "phone": 6283854436555,
    "whatsappUrl": "https://wa.me/6283854436555",
    "userUrl": "http://localhost/api/v1/auth/findUserById/1",
    "role": "ADMIN",
    "location": "Jawa timur, Kota surabaya, Mulyorejo, Mulyorejo",
    "imageUrl": "http://localhost:5050/api/minio/download/user/default/default_profile.jpeg"
  }
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

## Update Data Product

- Endpoint : `http://localhost:5050/api/v1/products/update/{productId}`
- Method : `PATCH`
- Header : `Authorization: Bearer <JWT token>`
- Request Body : `MULTIPART / FORM-DATA`
- Response Body : `JSON`

### Request Body:

```json
{
  "units": [
    "Set",
    "Paket",
    "..."
  ],
  "title": "Sepatu Pelajar",
  "description": "Panel admin adalah alat internal, jadi kemungkinan besar Anda memiliki sistem untuk login karyawan Anda",
  "price": 500000,
  "quantity": 55,
  "variants": [
    {
      "name": "Ukuran 41",
      "price": 550000
    },
    {
      "name": "Ukuran 42",
      "price": 625999
    },
    {
      "name": "...",
      "price": 0
    }
  ],
  "categories": [
    "Fashion",
    "..."
  ]
}
```

### Response Body (Success):
`200 OK`
```json
{
  "id": "cc17bf2abf04081239cd8a0ee022ce03",
  "units": [
    "Set",
    "Pcs",
    "Paket"
  ],
  "title": "Sepatu Pelajar Hommyped",
  "description": "Panel admin adalah alat internal, jadi kemungkinan besar Anda memiliki sistem untuk login karyawan Anda",
  "price": 500000,
  "quantity": 55,
  "rating": "0.0",
  "variants": [
    {
      "name": "Ukuran 41",
      "price": 550000
    },
    {
      "name": "Ukuran 42",
      "price": 625999
    },
    {
      "name": "Ukuran 44",
      "price": 650000
    }
  ],
  "categories": [
    "Fashion"
  ],
  "imageUrls": [
    "http://localhost/api/minio/download/product/d9f90a0d.jpeg"
  ],
  "barcodeProduct": "http://localhost/api/minio/download/barcodes/file**.png",
  "shareLink": "http://localhost/api/v1/products/findById/cc17bf2abf04081239cd8a0ee022ce03",
  "createdAt": "2024-06-09 12:18:47 WIB",
  "updatedAt": "2024-06-10 19:12:17 WIB",
  "uploadedBy": {
    "id": 1,
    "email": "dearlyfebrianoi@gmail.com",
    "firstname": "Febriano",
    "lastname": "Irwansyah",
    "phone": 6283854436555,
    "whatsappUrl": "https://wa.me/6283854436555",
    "userUrl": "http://localhost/api/v1/auth/findUserById/1",
    "role": "ADMIN",
    "location": "Jawa timur, Kota surabaya, Mulyorejo, Mulyorejo",
    "imageUrl": "http://localhost:5050/api/minio/download/user/default/default_profile.jpeg"
  }
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

## Delete Data Product

- Endpoint : `http://localhost:5050/api/v1/products/delete/{productId}`
- Method : `DELETE`
- Header : `Authorization: Bearer <JWT token>`
- Response Body : `JSON`

### Response Body (Success):
`204 No Content`

### Response Body (Error):
`400 Bad Request || 404 Not Found`

```json
{
  "status": false,
  "error": {
    "code": 400 || 404,
    "message": "Bad Request! || Not Found"
  }
}
```

## Find All Data Product

- Endpoint : `http://localhost:5050/api/v1/products/list`
- Method : `GET`
- Response Body : `JSON`

### Response Body (Success):
`200 OK`
```json
{
  "totalPages": 1,
  "totalElements": 2,
  "first": true,
  "last": true,
  "size": 10,
  "content": [
    {
      "id": "6654c45b47382ef35225cb6c4d76ca9d",
      "units": [
        "Set",
        "Paket"
      ],
      "title": "Website Admin Panel",
      "description": "Panel admin adalah alat internal, jadi kemungkinan besar Anda memiliki sistem untuk login karyawan Anda",
      "price": 4000000,
      "quantity": 100,
      "rating": "0.0",
      "variants": [
        {
          "name": "Green",
          "price": 4400000
        },
        {
          "name": "White",
          "price": 4200000
        }
      ],
      "categories": [
        "Website"
      ],
      "imageUrls": [
        "http://localhost/api/minio/download/product/ffb6284c.webp"
      ],
      "barcodeProduct": "http://localhost/api/minio/download/barcodes/file**.png",
      "shareLink": "http://localhost/api/v1/products/findById/6654c45b47382ef35225cb6c4d76ca9d",
      "createdAt": "2024-06-09 08:35:35 WIB",
      "updatedAt": "2024-06-09 11:35:55 WIB",
      "uploadedBy": {
        "id": 1,
        "email": "dearlyfebrianoi@gmail.com",
        "firstname": "Febriano",
        "lastname": "Irwansyah",
        "phone": 6283854436555,
        "whatsappUrl": "https://wa.me/6283854436555",
        "userUrl": "http://localhost/api/v1/auth/findUserById/1",
        "role": "ADMIN",
        "location": "Jawa timur, Kota surabaya, Mulyorejo, Mulyorejo",
        "imageUrl": "http://localhost:5050/api/minio/download/user/default/default_profile.jpeg"
      }
    },
    {
      "id": "cc17bf2abf04081239cd8a0ee022ce03",
      "units": [
        "Set",
        "Pcs",
        "Paket"
      ],
      "title": "Sepatu Pelajar Hommyped",
      "description": "Panel admin adalah alat internal, jadi kemungkinan besar Anda memiliki sistem untuk login karyawan Anda",
      "price": 500000,
      "quantity": 55,
      "rating": "0.0",
      "variants": [
        {
          "name": "Ukuran 41",
          "price": 550000
        },
        {
          "name": "Ukuran 42",
          "price": 625999
        },
        {
          "name": "Ukuran 44",
          "price": 650000
        }
      ],
      "categories": [
        "Fashion"
      ],
      "imageUrls": [
        "http://localhost/api/minio/download/product/d9f90a0d.jpeg"
      ],
      "barcodeProduct": "http://localhost/api/minio/download/barcodes/file**.png",
      "shareLink": "http://localhost/api/v1/products/findById/cc17bf2abf04081239cd8a0ee022ce03",
      "createdAt": "2024-06-09 12:18:47 WIB",
      "updatedAt": "2024-06-10 12:19:17 WIB",
      "uploadedBy": {
        "id": 1,
        "email": "dearlyfebrianoi@gmail.com",
        "firstname": "Febriano",
        "lastname": "Irwansyah",
        "phone": 6283854436555,
        "whatsappUrl": "https://wa.me/6283854436555",
        "userUrl": "http://localhost/api/v1/auth/findUserById/1",
        "role": "ADMIN",
        "location": "Jawa timur, Kota surabaya, Mulyorejo, Mulyorejo",
        "imageUrl": "http://localhost:5050/api/minio/download/user/default/default_profile.jpeg"
      }
    }
  ],
  "number": 0,
  "sort": {
    "empty": true,
    "sorted": false,
    "unsorted": true
  },
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "numberOfElements": 2,
  "empty": false
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

## Find Data Product By ID

- Endpoint : `http://localhost:5050/api/v1/products/findById/{productId}`
- Method : `GET`
- Response Body : `JSON`

### Response Body (Success):
`200 OK`
```json
{
  "id": "cc17bf2abf04081239cd8a0ee022ce03",
  "units": ["Set", "Pcs", "Paket"],
  "title": "Sepatu Pelajar Hommyped",
  "description": "Panel admin adalah alat internal, jadi kemungkinan besar Anda memiliki sistem untuk login karyawan Anda",
  "price": 500000,
  "quantity": 55,
  "rating": "0.0",
  "variants": [
    {"name": "Ukuran 41", "price": 550000},
    {"name": "Ukuran 42", "price": 625999},
    {"name": "Ukuran 44", "price": 650000}
  ],
  "categories": ["Fashion"],
  "imageUrls": [
    "http://localhost/api/minio/download/product/d9f90a0d.jpeg"
  ],
  "barcodeProduct": "http://localhost/api/minio/download/barcodes/file**.png",
  "shareLink": "http://localhost/api/v1/products/findById/cc17bf2abf04081239cd8a0ee022ce03",
  "createdAt": "2024-06-09 12:18:47 WIB",
  "updatedAt": "2024-06-10 12:19:17 WIB",
  "uploadedBy": {
    "id": 1,
    "email": "dearlyfebrianoi@gmail.com",
    "firstname": "Febriano",
    "lastname": "Irwansyah",
    "phone": 6283854436555,
    "whatsappUrl": "https://wa.me/6283854436555",
    "userUrl": "http://localhost/api/v1/auth/findUserById/1",
    "role": "ADMIN",
    "location": "Jawa timur, Kota surabaya, Mulyorejo, Mulyorejo",
    "imageUrl": "http://localhost:5050/api/minio/download/user/default/default_profile.jpeg"
  }
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

## Find Data Product By Keyword

- Endpoint : `http://localhost:5050/api/v1/products/search/products/{keyword}`
- Method : `GET`
- Response Body : `JSON`

### Response Body (Success):
`200 OK`
```json
{
  "id": "cc17bf2abf04081239cd8a0ee022ce03",
  "units": ["Set", "Pcs", "Paket"],
  "title": "Sepatu Pelajar Hommyped",
  "description": "Panel admin adalah alat internal, jadi kemungkinan besar Anda memiliki sistem untuk login karyawan Anda",
  "price": 500000,
  "quantity": 55,
  "rating": "0.0",
  "variants": [
    {"name": "Ukuran 41", "price": 550000},
    {"name": "Ukuran 42", "price": 625999},
    {"name": "Ukuran 44", "price": 650000}
  ],
  "categories": ["Fashion"],
  "imageUrls": [
    "http://localhost/api/minio/download/product/d9f90a0d.jpeg"
  ],
  "barcodeProduct": "http://localhost/api/minio/download/barcodes/file**.png",
  "shareLink": "http://localhost/api/v1/products/findById/cc17bf2abf04081239cd8a0ee022ce03",
  "createdAt": "2024-06-09 12:18:47 WIB",
  "updatedAt": "2024-06-10 12:19:17 WIB",
  "uploadedBy": {
    "id": 1,
    "email": "dearlyfebrianoi@gmail.com",
    "firstname": "Febriano",
    "lastname": "Irwansyah",
    "phone": 6283854436555,
    "whatsappUrl": "https://wa.me/6283854436555",
    "userUrl": "http://localhost/api/v1/auth/findUserById/1",
    "role": "ADMIN",
    "location": "Jawa timur, Kota surabaya, Mulyorejo, Mulyorejo",
    "imageUrl": "http://localhost:5050/api/minio/download/user/default/default_profile.jpeg"
  }
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

## Find Data Product By Kategori

- Endpoint : `http://localhost:5050/api/v1/products/search/productsByCategory/{categoryName}`
- Method : `GET`
- Response Body : `JSON`

### Response Body (Success):
`200 OK`
```json
[
  {
    "id": "cc17bf2abf04081239cd8a0ee022ce03",
    "units": [
      "Set",
      "Pcs",
      "Paket"
    ],
    "title": "Sepatu Pelajar Hommyped",
    "description": "Panel admin adalah alat internal, jadi kemungkinan besar Anda memiliki sistem untuk login karyawan Anda",
    "price": 500000,
    "quantity": 55,
    "rating": "0.0",
    "variants": [
      {
        "name": "Ukuran 41",
        "price": 550000
      },
      {
        "name": "Ukuran 42",
        "price": 625999
      },
      {
        "name": "Ukuran 44",
        "price": 650000
      }
    ],
    "categories": [
      "Fashion"
    ],
    "imageUrls": [
      "http://localhost/api/minio/download/product/d9f90a0d.jpeg"
    ],
    "barcodeProduct": "http://localhost/api/minio/download/barcodes/file**.png",
    "shareLink": "http://localhost/api/v1/products/findById/cc17bf2abf04081239cd8a0ee022ce03",
    "createdAt": "2024-06-09 12:18:47 WIB",
    "updatedAt": "2024-06-10 12:19:17 WIB",
    "uploadedBy": {
      "id": 1,
      "email": "dearlyfebrianoi@gmail.com",
      "firstname": "Febriano",
      "lastname": "Irwansyah",
      "phone": 6283854436555,
      "whatsappUrl": "https://wa.me/6283854436555",
      "userUrl": "http://localhost/api/v1/auth/findUserById/1",
      "role": "ADMIN",
      "location": "Jawa timur, Kota surabaya, Mulyorejo, Mulyorejo",
      "imageUrl": "http://localhost:5050/api/minio/download/user/default/default_profile.jpeg"
    }
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

## Check Data Quantity Product

- Endpoint : `http://localhost:5050/api/v1/products/checkStock`
- Method : `GET`
- Response Body : `JSON`

### Response Body (Success):
`200 OK`
```json
"Owner mengirim notifikasi kepada device yang digunakan Owner"
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

## Check Data Product From User Uploaded

- Endpoint : `http://localhost:5050/api/v1/products/getProductsWithUser`
- Authorization Mandatory : `Bearer: {token}`
- Response Body : `JSON`

### Response Body ()