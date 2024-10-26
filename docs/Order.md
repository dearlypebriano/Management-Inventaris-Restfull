# API Order Product Specification v6.4.5

## Introduction

- API Order Product Specification
- version: `v6.4.5`

## Create Order Product With User and Add To Cart

- Endpoint : `http://localhost/api/v1/orders/request-order`
- method : `POST`
- Header : `Authorization: Bearer <JWT TOKEN>` 
- Request Body : `JSON`
- Response Body : `JSON`

### Request Body :

```json
{
  "productId": "{uuid product}",
  "quantity": 3
}
```

### Response Body (Success) :
`200 OK!`

```json
{
    "id": "{random uuid Order String}",
    "user": {
        "id": 123,
        "firstname": "test",
        "lastname": "test",
        "whatsappUrl": "https://wa.me/628xxx",
        "userUrl": "http://127.0.0.1:5050/api/v1/auth/findUserById/123",
        "location": "Jawa timur, Kota surabaya, Mulyorejo, Mulyorejo",
        "imageUrl": "http://localhost/api/minio/download/profile/default/default_profile.jpg"
    },
    "product": {
        "id": "{random uuid Product String}",
        "units": [
            "Lembaran",
            "Box",
            "Pack"
        ],
        "title": "STYROFOAM BOX JAYA ICE",
        "description": "Bahan berkualitas, Harga kompetitif, Garansi Perjalanan, Pelayanan Maksimal, Cepat dan tepat",
        "priceRange": "Rp. 9.500,00 - Rp. 105.000,00",
        "price": 9500.00,
        "formattedPrice": "Rp. 9.500,00",
        "quantity": 78,
        "rating": "0.0",
        "variants": [
            {
                "name": "40 x 26 x 17cm",
                "price": 9500.00,
                "formattedPrice": "Rp. 9.500,00"
            },
            {
                "name": "34 x 25 x 30cm",
                "price": 105000.00,
                "formattedPrice": "Rp. 105.000,00"
            },
            {
                "name": "47 x 31 x 29cm",
                "price": 18000.00,
                "formattedPrice": "Rp. 18.000,00"
            },
            {
                "name": "53 x 38 x 34cm",
                "price": 19000.00,
                "formattedPrice": "Rp. 19.000,00"
            },
            {
                "name": "75 x 42 x 32cm",
                "price": 28000.00,
                "formattedPrice": "Rp. 28.000,00"
            },
            {
                "name": "70 x 49 x 40cm",
                "price": 35000.00,
                "formattedPrice": "Rp. 35.000,00"
            }
        ],
        "categories": [
            "Peralatan Rumah"
        ],
        "imageUrls": [
            "http://localhost/api/minio/download/uploaded/product/190f68f53ab_267ed22f119f.png"
        ],
        "barcodeProduct": "http://localhost/api/minio/download/barcodes/{id product}.png",
        "shareLink": "http://localhost/api/v1/products/findById/{id product}",
        "createdAt": "2024-07-28 06:38:26 WIB",
        "updatedAt": "2024-07-28 06:41:08 WIB",
        "uploadedBy": {
            "id": 1,
            "firstname": "Febriano",
            "lastname": "Irwansyah",
            "whatsappUrl": "https://wa.me/6283854436555",
            "userUrl": "http://127.0.0.1:5050/api/v1/auth/findUserById/1",
            "location": "Jawa timur, Kota surabaya, Mulyorejo, Mulyorejo",
            "imageUrl": "http://localhost/api/minio/download/profile/default/default_profile.jpg"
        }
    },
    "quantity": 3,
    "orderDate": "2024-08-01 17:55:15 WIB",
    "deletedByOwner": false
}
```

### Response Body (Error):

```json
{
  "error": true,
  "message": "Error Message",
  "status": 500
}
```

## Get Data Product In Cart User

- Endpoint: `http://localhost/api/v1/carts/my-cart`
- Method: `GET`
- Header: `Authorization: Bearer <JWT TOKEN>`
- Response Body: `JSON`

### Response Body (Success (Don't Have Data Order))
`200 OK!`
```json
[
  {
    "user": {
      "id": 402,
      "firstname": "test",
      "lastname": "test",
      "whatsappUrl": "https://wa.me/6283854436555",
      "userUrl": "http://127.0.0.1:5050/api/v1/auth/findUserById/402",
      "location": "Jawa timur, Kota surabaya, Mulyorejo, Mulyorejo",
      "imageUrl": "http://localhost/api/minio/download/profile/default/default_profile.jpg"
    }
  }
]
```

### Response Body (Success (With Data Order))
`200 OK!`
```json
[
  {
    "id": "25d9ec89-0f04-4a92-b799-5977bd0c854c",
    "user": {
      "id": 402,
      "firstname": "test",
      "lastname": "test",
      "whatsappUrl": "https://wa.me/6283854436555",
      "userUrl": "http://127.0.0.1:5050/api/v1/auth/findUserById/402",
      "location": "Jawa timur, Kota surabaya, Mulyorejo, Mulyorejo",
      "imageUrl": "http://localhost/api/minio/download/profile/default/default_profile.jpg"
    },
    "totalPrice": 28500.00,
    "formattedTotalPrice": "Rp. 28.500,00",
    "deliveryDate": "2024-08-01 18:00:27 WIB",
    "deliveryAddress": "Jawa timur, Kota surabaya, Mulyorejo, Mulyorejo",
    "deliveryNote": [],
    "orders": [
      {
        "id": "ed4251b9-d43f-43a0-b353-c426f4c3a871",
        "user": {
          "id": 402,
          "firstname": "test",
          "lastname": "test",
          "whatsappUrl": "https://wa.me/6283854436555",
          "userUrl": "http://127.0.0.1:5050/api/v1/auth/findUserById/402",
          "location": "Jawa timur, Kota surabaya, Mulyorejo, Mulyorejo",
          "imageUrl": "http://localhost/api/minio/download/profile/default/default_profile.jpg"
        },
        "product": {
          "id": "526390fe-b234-46ef-9a8e-1b9486ec4fee",
          "units": [
            "Lembaran",
            "Box",
            "Pack"
          ],
          "title": "STYROFOAM BOX JAYA ICE",
          "description": "Bahan berkualitas, Harga kompetitif, Garansi Perjalanan, Pelayanan Maksimal, Cepat dan tepat",
          "priceRange": "Rp. 9.500,00 - Rp. 105.000,00",
          "price": 9500.00,
          "formattedPrice": "Rp. 9.500,00",
          "quantity": 78,
          "rating": "0.0",
          "variants": [
            {
              "name": "40 x 26 x 17cm",
              "price": 9500.00,
              "formattedPrice": "Rp. 9.500,00"
            },
            {
              "name": "34 x 25 x 30cm",
              "price": 105000.00,
              "formattedPrice": "Rp. 105.000,00"
            },
            {
              "name": "47 x 31 x 29cm",
              "price": 18000.00,
              "formattedPrice": "Rp. 18.000,00"
            },
            {
              "name": "53 x 38 x 34cm",
              "price": 19000.00,
              "formattedPrice": "Rp. 19.000,00"
            },
            {
              "name": "75 x 42 x 32cm",
              "price": 28000.00,
              "formattedPrice": "Rp. 28.000,00"
            },
            {
              "name": "70 x 49 x 40cm",
              "price": 35000.00,
              "formattedPrice": "Rp. 35.000,00"
            }
          ],
          "categories": [
            "Peralatan Rumah"
          ],
          "imageUrls": [
            "http://localhost/api/minio/download/uploaded/product/190f68f53ab_267ed22f119f.png"
          ],
          "barcodeProduct": "http://localhost/api/minio/download/barcodes/526390fe-b234-46ef-9a8e-1b9486ec4fee.png",
          "shareLink": "http://localhost/api/v1/products/findById/526390fe-b234-46ef-9a8e-1b9486ec4fee",
          "createdAt": "2024-07-28 06:38:26 WIB",
          "updatedAt": "2024-07-28 06:41:08 WIB",
          "uploadedBy": {
            "id": 1,
            "firstname": "Febriano",
            "lastname": "Irwansyah",
            "whatsappUrl": "https://wa.me/6283854436555",
            "userUrl": "http://127.0.0.1:5050/api/v1/auth/findUserById/1",
            "location": "Jawa timur, Kota surabaya, Mulyorejo, Mulyorejo",
            "imageUrl": "http://localhost/api/minio/download/profile/default/default_profile.jpg"
          }
        },
        "quantity": 3,
        "orderDate": "2024-08-01 17:55:15 WIB",
        "deletedByOwner": false
      }
    ]
  }
]
```

### Response Body (Error):

```json
{
  "error": true,
  "message": "Error Message",
  "status": 403
}
```

## Delete Data Order Product From Cart User

- Endpoint : `http://localhost/api/v1/orders/remove-product/{Order UUID By User}`
- Method : `DELETE`
- Header : `Authorization: Bearer <JWT TOKEN>`
- Parameters : `Order ID User`
- Response Body : `No Content`

### Response Body (Success):
`204 No Content`

### Response Body (Error):

```json
{
  "error": true,
  "message": "Error Message",
  "status": 500
}
```

## Get Data Order History Product With User

- Endpoint : `http://localhost/api/v1/order-history/history-data`
- Method : `GET`
- Header : `Authorization : Bearer <JWT TOKEN>`
- Response Body : `JSON`

### Response Body (Success (Don't have Order History Response)):

```json
{
    "id": "b9920117-df93-42e2-af23-90dd636b1cf7",
    "user": {
        "id": 402,
        "firstname": "test",
        "lastname": "test",
        "whatsappUrl": "https://wa.me/6283854436555",
        "userUrl": "http://127.0.0.1:5050/api/v1/auth/findUserById/402",
        "location": "Jawa timur, Kota surabaya, Mulyorejo, Mulyorejo",
        "imageUrl": "http://localhost/api/minio/download/profile/default/default_profile.jpg"
    },
    "orderResponses": []
}
```

### Response Body (Success (With Order History Response)):

```json
{
  "id": "b9920117-df93-42e2-af23-90dd636b1cf7",
  "user": {
    "id": 402,
    "firstname": "test",
    "lastname": "test",
    "whatsappUrl": "https://wa.me/6283854436555",
    "userUrl": "http://127.0.0.1:5050/api/v1/auth/findUserById/402",
    "location": "Jawa timur, Kota surabaya, Mulyorejo, Mulyorejo",
    "imageUrl": "http://localhost/api/minio/download/profile/default/default_profile.jpg"
  },
  "orderResponses": [
    {
      "totalPrice": 28500.00,
      "formattedTotalPrice": "Rp. 28.500,00",
      "deliveryDate": "2024-08-01 19:18:54 WIB",
      "deliveryAddress": "Jawa timur, Kota surabaya, Mulyorejo, Mulyorejo",
      "deliveryNote": [],
      "orders": [
        {
          "id": "a5dc563c-a607-4aa7-afcd-da27f829ac9c",
          "user": {
            "id": 402,
            "firstname": "test",
            "lastname": "test",
            "whatsappUrl": "https://wa.me/6283854436555",
            "userUrl": "http://127.0.0.1:5050/api/v1/auth/findUserById/402",
            "location": "Jawa timur, Kota surabaya, Mulyorejo, Mulyorejo",
            "imageUrl": "http://localhost/api/minio/download/profile/default/default_profile.jpg"
          },
          "product": {
            "id": "526390fe-b234-46ef-9a8e-1b9486ec4fee",
            "units": [
              "Lembaran",
              "Box",
              "Pack"
            ],
            "title": "STYROFOAM BOX JAYA ICE",
            "description": "Bahan berkualitas, Harga kompetitif, Garansi Perjalanan, Pelayanan Maksimal, Cepat dan tepat",
            "priceRange": "Rp. 9.500,00 - Rp. 105.000,00",
            "price": 9500.00,
            "formattedPrice": "Rp. 9.500,00",
            "quantity": 78,
            "rating": "0.0",
            "variants": [
              {
                "name": "40 x 26 x 17cm",
                "price": 9500.00,
                "formattedPrice": "Rp. 9.500,00"
              },
              {
                "name": "34 x 25 x 30cm",
                "price": 105000.00,
                "formattedPrice": "Rp. 105.000,00"
              },
              {
                "name": "47 x 31 x 29cm",
                "price": 18000.00,
                "formattedPrice": "Rp. 18.000,00"
              },
              {
                "name": "53 x 38 x 34cm",
                "price": 19000.00,
                "formattedPrice": "Rp. 19.000,00"
              },
              {
                "name": "75 x 42 x 32cm",
                "price": 28000.00,
                "formattedPrice": "Rp. 28.000,00"
              },
              {
                "name": "70 x 49 x 40cm",
                "price": 35000.00,
                "formattedPrice": "Rp. 35.000,00"
              }
            ],
            "categories": [
              "Peralatan Rumah"
            ],
            "imageUrls": [
              "http://localhost/api/minio/download/uploaded/product/190f68f53ab_267ed22f119f.png"
            ],
            "barcodeProduct": "http://localhost/api/minio/download/barcodes/526390fe-b234-46ef-9a8e-1b9486ec4fee.png",
            "shareLink": "http://localhost/api/v1/products/findById/526390fe-b234-46ef-9a8e-1b9486ec4fee",
            "createdAt": "2024-07-28 06:38:26 WIB",
            "updatedAt": "2024-07-28 06:41:08 WIB",
            "uploadedBy": {
              "id": 1,
              "firstname": "Febriano",
              "lastname": "Irwansyah",
              "whatsappUrl": "https://wa.me/6283854436555",
              "userUrl": "http://127.0.0.1:5050/api/v1/auth/findUserById/1",
              "location": "Jawa timur, Kota surabaya, Mulyorejo, Mulyorejo",
              "imageUrl": "http://localhost/api/minio/download/profile/default/default_profile.jpg"
            }
          },
          "quantity": 3,
          "orderDate": "2024-08-01 19:18:47 WIB",
          "deletedByOwner": false
        }
      ]
    }
  }
}
```

### Response Body (Error):

```json
{
  "error": true,
  "message": "Error Message",
  "status": 500
}
```