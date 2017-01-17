# Serving

## Request
1. Path: `/ads/`
2. Request
```json
{
  "timestamp": 123123,
  "appId": "myapp",
  "placements": [
    {
      "id": 1,
      "format": "IMG_360"
    }, 
    {
      "id": 2,
      "format": "IMG_360"
    }
  ],
  "osId": "Android",
  "osVersion": "x.y.z",
  "userId": "asdf98asdf9asd9f",
  "hmdId": 2,
  "location": {
    "lat": 34.0,
    "lon": 31.1,
    "accuracy": 84.33
  },
  "demographics": {
    "dob": "yyyy/mm/dd",
    "gender": "string",
    "email": "abc@def.ghi"
  },
  "deviceInfo": {
    "manufacturer": "Samsung",
    "model": "Note 7 Boom Boom",
    "ram": "4gb"
  },
  "connectivity": "Wifi",
  "wifi-name": "MarioNGV"
}
```
3. Response
```json
{
    "statusCode": 200,
    "status": "OK",
    "experimentId": 91,
    "ads" : {
      "placementId1": {
        "servingId": "2345872089ggggg",
        "mediaUrl": "http://chymeravr.com"
      },
      "placementId2": {
        "servingId": "2345872089ggggg",
        "mediaUrl": "http://chymeravr.com"
      }
    }
}
```
