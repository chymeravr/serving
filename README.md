
# Serving
## Product Description
Serving module serves ad requests. It consists of following modules:-
* **Cache** - In memory cache to serve requests faster
* **Server** - Jetty server, dependency injections, configurations and API endpoints
* **Ad Response Generators (Worker module)** - Module to filter ads, do validity checks, budget checks, targeting checks and bidding
* **Logging** - Put the details of the ads served/failed into a Kafka queue, which will then be passed through Hadoop system for analytics.
  
## Usage

### Request

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

*  `mvn clean install`

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

## Understanding the modules
### Cache
Instead of serving ads by querying PostgreSQL for Advertiser and Publisher data realtime, Cache module query that periodically and stores it in memory
1. *dbconnector*: For establishing connection with the database server.
2. *generic/RefreshableDbCache*:  Implements the periodic DB querying and in-memory collection logic. Google's CQ Engine is used for Cache implementation.
3. *RefreshableDbCache implementations*: For each required entity, RefreshableDbCache is extended and logic for in-memory indexing and DB querying is written.

### Logging
This module implements a Kakfa Producer which put the required logs into the Kafka queue.

### Workers
Once an ad request is received, it passes through multiple processing steps before a response is created. Example steps are validating if ad request is valid or not, filtering ads that can be served to this ad request, implementing targeting logic, ranking the filtered ads, writing ad response etc.
This module contains logic for each such processing step

### Server
#### Servlet creation
* Jetty server is used
* API endpoint is exposed
#### Wiring of the whole application
* Guice is used for dependency injection
* *server/guice* contains configuration of other modules
* *server/dag* contains the logic for using different processing steps of *workers* module.

## License
This project is licensed under the MIT License

## Authors
* Rubbal Sidhu
* Sushil Kumar - [Github](https://github.com/sushilmiitb)
