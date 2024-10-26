# API Mail Sender Specivication v6.4.5

## Configuration Your Email Admin In File Management-Inventaris-Restfull/src/main/resources/application.properties

## Send Mail No Attachment To Admin:
- Endpoint : `http://localhost:5050/sendMail`
- Method : `POST`
- Request Body : `JSON`
- Response Body : `JSON`

### Request Body :

```json
{
  "recipient": "recipient",
  "msgBody": "message",
  "subject": "subject"
}
```

### Response Body (Success):
``Mail sent successfully!!``

### Response Body (Error):
``Error while sending mail``

## Send Mail With Attachment To Admin:
- Endpoint : `http://localhost:5050/sendMailWithAttachment`
- Method : `POST`
- Request Body : `JSON`
- Response Body : `JSON`

### Request Body :

```json
{
  "recipient": "recipient",
  "msgBody": "message",
  "subject": "subject",
  "attachment": "attachment"
}
```

### Response Body (Success):
``Mail sent successfully!!``

### Response Body (Error):
``Error while sending mail``