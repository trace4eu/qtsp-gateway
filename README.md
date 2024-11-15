# TRACE4EU-TSA-CLIENT

Java application built with Spring Boot framework
It provides two methods:
- generate a timestamp token given a data
- verify a timestamp token from the original data

Swagger file can be accessed https://api-dev.trace4eu.eu/trace4eu/tsa-client/swagger-ui/index.html

### How to get an access token

The endpoint for generating a timestamp is protected by an access token.

The following library has been developed: https://www.npmjs.com/package/@trace4eu/authorisation-wrapper  
With that library you can get an access token:
```
const did = 'did:ebsi:zfEmvX5twhXjQJiCWsukvQA';
const entityKeys = [
  {
    alg: Algorithm.ES256,
    privateKeyHex:
      '<ecc private key>',
  },
];
const wallet = WalletFactory.createInstance(false, did, entityKeys);
const trace4euAuthorisationApi = new Trace4euAuthorisationApi(wallet);
const tokenResponse = await trace4euAuthorisationApi.getAccessToken('ES256', 'qtsp:timestamp');
```

The scopes related to the OCS are:
- `qtsp:timestamp`: It allows to create a timestamp

Before using the OCS, you need to request access to the T2.2 contributors. They will create the client in the Trace4eu authorisation server, following the script located [here](https://github.com/trace4eu/authorization-and-authentication/blob/main/examples/scenario1/admin.py)  
If you want to make the calls to the Authorization server by your own, you can take this [python script](https://github.com/trace4eu/authorization-and-authentication/blob/main/examples/scenario1/client.py) as an example.


## CONFIGURATION PARAMETERS
This an example (with dev values) of the `application.yml` file:

```yaml
tsa:
  config:
    endpoint: https://tsa.test4mind.com/tsa/user
    authentication: cGFibG8uY29zaW9AdmFsaWRhdGVkaWQuY29tOjZyZ2MzcHRjMThzNzNwbzczMWZzN241NzZu
server:
  port: 8080
  servlet:
    context-path: /trace4eu/tsa-client

logging:
  level:
    root: INFO
    com:
      trace4eu: INFO

trace4eu:
  security:
    introspect-endpoint: https://api-dev-admin-auth.trace4eu.eu/admin/oauth2/introspect
    required-scope: qtsp:timestamp
    admin-token: 
      
open-api:
  server-url: "http://localhost:8080/trace4eu/tsa-client"
```

## RUN APPLICATION
You need to compile the application using maven for generating the artifact (.jar file).  
The command `mvn clean package` will generate the artifact.  
It will include the version that is defined in the [pom.xml](pom.xml) file. The jar file will be place in `/target`.  
For example if version `0.4.9` is defined, you can run the application with this command:
```
java -jar trace4eu-tsa-client-0.4.9.jar --spring.config.location=classpath:/,file:<relative path to the properties file>
```
Example:
```
java -jar trace4eu-tsa-client-0.0.1.jar --spring.config.location=classpath:/,file:config/application.yml
```

## BUILD CONTAINER
Setup the version (x.y.z accordingly) to the one that is in the [pom.xml](./pom.xml) (`version` xml tag)
```sh
docker build --no-cache -t trace4eu-tsa-client:x.y.z .
```

## RUN CONTAINER
The application needs a `application.tml` file. It is located within the container (internal path: `/app/config/application.yml`) so at the time of running the container you can map a local properties file into the container.
```sh
docke run -d -v {absolute path to the config.properties file}:/app/config/application.yml -p {hostPort}:8080 --name trace4eu-tsa-client trace4eu-tsa-client:x.y.z
```
For example:
```sh
docker run -d -v /data/trace4eu/tsa-client/config/application.yml:/app/config/application.yml -p 8080:8080 --name trace4eu-tsa-client trace4eu-tsa-client:0.4.9
```
Once the container is up and running you can access to the documentation:

http://localhost:8080/trace4eu/tsa-client/swagger-ui/index.html

Or download the openapi specification:

http://localhost:8080/trace4eu/tsa-client/v3/api-docs
