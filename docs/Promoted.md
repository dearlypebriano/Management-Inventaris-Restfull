# API Promoted Product Specification v7.0.0

## Introduction

- API Promoted Product Specification
- version: `v7.0.0`

## Created New Promoted Product

- Endpoint : `http://localhost/api/v1/promoted/create`
- Method : `POST`
- Request Body : `Multipart Form Data`
- Response Body : `JSON`
- Headers : { Authorization: `Bearer Token: {Token Authorization}`, `X-API-TOKEN: {Token}` }

### Request Body : 
#### Form Data
- `name` (optional): The name of the promotion.
- `description` (optional): The description of the promotion.
- `discountType` (optional): The type of discount (e.g., `PERCENTAGE`, `FIXED_AMOUNT`, `BUY_ONE_GET_ONE`).
- `discountValue` (optional): The value of the discount.
- `numberOfDays` (optional): The number of days the promotion should be valid.

```bash
curl -X POST "http://localhost/api/v1/promoted/create" \
     -H "Content-Type: multipart/form-data" \
     -F "name=Summer Sale" \
     -F "description=Discounts on summer items" \
     -F "discountType=PERCENTAGE" \
     -F "discountValue=20" \
     -F "numberOfDays=30" \
     -F "files=@/path/to/your/image1.jpg" \
     -F "files=@/path/to/your/image2.jpg"
```

### Response Body :
Jika proses request sudah berhasil, maka, system akan otomatis menghasilkan response seperti ini

```json
{
  "id": "67055c83-85df-4b54-8187-4832944e78e5",
  "name": "Test",
  "description": "Test",
  "discountType": "PERCENTAGE",
  "discountValue": 48,
  "formatDiscountValue": "48.00%",
  "imageUrls": [
    "http://localhost/api/minio/download/uploaded/promotion/{nameFile}.{extensionFile(png,jpg,jpeg,JPG,PNG,JPEG)}"
  ],
  "startDate": "2024-08-25T17:24:00.575773",
  "endDate": "2024-09-01T17:24:00.575776"
}
```