# Serving

## Request
Path: `/api/v1/ads`
Domain name: `serving.chymeravr.com:8080`

#### Request
```json
{
  "sdkVersion": 1,
  "timestamp": 123123,
  "appId": "myapp",
  "placements": [
    {
      "id": "1uuid",
      "adFormat": "IMG_360"
    }, 
    {
      "id": "2uuid",
      "adFormat": "IMG_360"
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
  "wifiName": "MarioNGV"
}
```
#### Response
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
        "servingId": "2345872s089ggggg",
        "mediaUrl": "http://chymeravr.com"
      }
    }
}
```

#### Execution
* `mvn clean install`
* Build the `docker` image named `serving` using from the root folder containing the Dockerfile (The jar path should be in the same or child directories as docker does not allow resources from outside the current directory and its children)
```bash
sudo docker build --build-arg JAR=/path/to/assembly/jar -t serving .
```
* Run the image `serving` (Make sure already running containers for this image are stopped as it will throw an error while binding to the port)
```bash
sudo docker run -t -e PORT=8080 --net=host -v /var/log/serving:/var/log/serving serving

```

#### Logging
To enable debug logging, set the following header
```
chym_trace : true
```
The logs will be in the /var/log/serving directory. Note that in the Docker image, the log volume is mounted to be an external end point. So you do not need to log into the docker container to view the logs.
