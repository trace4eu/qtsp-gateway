# TRACE4EU-TSA-CLIENT

Java application built with Spring Boot framework
It provides two methods:
- generate a timestamp token given a data
- verify a timestamp token from the original data

The application is already deployed in the Trace4eu dev environment.  
Swagger file can be accessed: https://api-dev.trace4eu.eu/trace4eu/tsa-client/swagger-ui/index.html#/.  
The openapi file descriptor is also available: https://api-dev.trace4eu.eu/trace4eu/tsa-client/v3/api-docs

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
    endpoint: https://freetsa.org/tsr
    cert: |-
      -----BEGIN CERTIFICATE-----
      MIIIATCCBemgAwIBAgIJAMHphhYNqOmCMA0GCSqGSIb3DQEBDQUAMIGVMREwDwYD
      VQQKEwhGcmVlIFRTQTEQMA4GA1UECxMHUm9vdCBDQTEYMBYGA1UEAxMPd3d3LmZy
      ZWV0c2Eub3JnMSIwIAYJKoZIhvcNAQkBFhNidXNpbGV6YXNAZ21haWwuY29tMRIw
      EAYDVQQHEwlXdWVyemJ1cmcxDzANBgNVBAgTBkJheWVybjELMAkGA1UEBhMCREUw
      HhcNMTYwMzEzMDE1NzM5WhcNMjYwMzExMDE1NzM5WjCCAQkxETAPBgNVBAoTCEZy
      ZWUgVFNBMQwwCgYDVQQLEwNUU0ExdjB0BgNVBA0TbVRoaXMgY2VydGlmaWNhdGUg
      ZGlnaXRhbGx5IHNpZ25zIGRvY3VtZW50cyBhbmQgdGltZSBzdGFtcCByZXF1ZXN0
      cyBtYWRlIHVzaW5nIHRoZSBmcmVldHNhLm9yZyBvbmxpbmUgc2VydmljZXMxGDAW
      BgNVBAMTD3d3dy5mcmVldHNhLm9yZzEiMCAGCSqGSIb3DQEJARYTYnVzaWxlemFz
      QGdtYWlsLmNvbTESMBAGA1UEBxMJV3VlcnpidXJnMQswCQYDVQQGEwJERTEPMA0G
      A1UECBMGQmF5ZXJuMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAtZEE
      jE5IbzTp3Ahif8I3UWIjaYS4LLEwvv9RfPw4+EvOXGWodNqyYhrgvOfjNWPg7ek0
      /V+IIxWfB4SICCJ0YMHtiCYXBvQoEzQ1nfu4G9E1P8F5YQrxqMjIZdwA6iOzqJvm
      vQO6hansgn1gVlkF4i1qWE7ROArhUCgM7jl+mKAS84BGQAeGJEO8B3y5X0Ia8xcS
      2Wg8223/uvPIululZq5SPUWdYXc0bU2EDieIa3wBxbiQ14ouJ7uo3S+aKBLhV9Yv
      khxlliVIBp3Nt9Bt4YHeDpVw1m+HIgzii2KKtVkG8+4MIQ9wUej0hYr4uaktCeRq
      8tnLpb/PrRaM32BEkaSwZgOxFMr3Ax8GXn7u+lPFdfNJDAWdLjLdx2rE1MTHEGg7
      l/0b5ZG8YQVRhtiPmgORswe2+R7ZVNqjb5rNah4Uqi5K3xdGS1TbGNu2/+MAgCRl
      RzcENs5Od7rl3m/g8/nW5/++tGHnlOkvsJUfiq5hpBLM6bIQdGNci+MnrhoPa0pk
      brD4RjvGO/hFUwQ10Z6AJRHsn2bDSWlS2L7LabCqTUxB9gUV/n3LuJMZzdpZumrq
      S+POrnGOb8tszX25/FC7FbEvNmWwqjByicLm3UsRHOSLotnv21prmlBgaTNPs09v
      x64zDws0IIqsgN8yZv3ZBGWHa6LLiY2VBTFbbnsCAwEAAaOCAdswggHXMAkGA1Ud
      EwQCMAAwHQYDVR0OBBYEFG52C3tOT5zhYMptLOknoqKUs3c3MB8GA1UdIwQYMBaA
      FPpVDYw0ZlFDTPfns6dsla965qSXMAsGA1UdDwQEAwIGwDAWBgNVHSUBAf8EDDAK
      BggrBgEFBQcDCDBjBggrBgEFBQcBAQRXMFUwKgYIKwYBBQUHMAKGHmh0dHA6Ly93
      d3cuZnJlZXRzYS5vcmcvdHNhLmNydDAnBggrBgEFBQcwAYYbaHR0cDovL3d3dy5m
      cmVldHNhLm9yZzoyNTYwMDcGA1UdHwQwMC4wLKAqoCiGJmh0dHA6Ly93d3cuZnJl
      ZXRzYS5vcmcvY3JsL3Jvb3RfY2EuY3JsMIHGBgNVHSAEgb4wgbswgbgGAQAwgbIw
      MwYIKwYBBQUHAgEWJ2h0dHA6Ly93d3cuZnJlZXRzYS5vcmcvZnJlZXRzYV9jcHMu
      aHRtbDAyBggrBgEFBQcCARYmaHR0cDovL3d3dy5mcmVldHNhLm9yZy9mcmVldHNh
      X2Nwcy5wZGYwRwYIKwYBBQUHAgIwOxo5RnJlZVRTQSB0cnVzdGVkIHRpbWVzdGFt
      cGluZyBTb2Z0d2FyZSBhcyBhIFNlcnZpY2UgKFNhYVMpMA0GCSqGSIb3DQEBDQUA
      A4ICAQClyUTixvrAoU2TCn/QoLFytB/BSDw+lXxoorzZuXZPGpUBYf1yRy1Bpe7S
      d3hiA7VCIkD7OibN4XYIe2+xAR30zBniVxqkoFEQlmXpTEb1C9Kt7mrEE34lGyWj
      navaRRUV2P+eByCejsILeHT34aDt58AJN/6EozT4syZc7S2O2d9hOWWDZ3/rOCwe
      47I+bqXwXfMN57n4kAXSUmb2EvOci09tq6bXv7rBljK5Bjcyn1Km8GahDkPqqB+E
      mmxf4/6LXqIydfaH8gUuUC6mwwdipmjM4Hhx3Y6X4xW7qSniVYmXegoxLOlsUQax
      Q3x3nys2GxgoiPPuiiNDdPoGPpVhkmJ/fEMQc5ZdEmCSjroAnoA0Ka4yTPlvBCNU
      83vKWv3cefeTRqs4i/x58B3JhhJU6mzBKZQQdrg9IFVvO+UTJoN/KHb3gzs3Dnw9
      QQUjgn1PU0AMciGNdSKf8QxviJOpo6HAxCu0yJjBPfQcf2VztPxWUVlxphCnsNKF
      fIIlqfsgTqzsouiXGqGvh4hqKuPHL+CgquhCmAp3vvFrkhFUWAkNmCtZRmA3ZOda
      CtPRFFS5mG9ni5q2r+hJcDOuOr/U60O3vJ3uaIFZSeZIFYKoLnhSd/IoIQfv45Ag
      DgUIrLjqguolBSdvPJ2io9O0rTi7+IQr2jb8JEgpH1WNwC3R4A==
      -----END CERTIFICATE-----
    ca-cert: |-
      -----BEGIN CERTIFICATE-----
      MIIH/zCCBeegAwIBAgIJAMHphhYNqOmAMA0GCSqGSIb3DQEBDQUAMIGVMREwDwYD
      VQQKEwhGcmVlIFRTQTEQMA4GA1UECxMHUm9vdCBDQTEYMBYGA1UEAxMPd3d3LmZy
      ZWV0c2Eub3JnMSIwIAYJKoZIhvcNAQkBFhNidXNpbGV6YXNAZ21haWwuY29tMRIw
      EAYDVQQHEwlXdWVyemJ1cmcxDzANBgNVBAgTBkJheWVybjELMAkGA1UEBhMCREUw
      HhcNMTYwMzEzMDE1MjEzWhcNNDEwMzA3MDE1MjEzWjCBlTERMA8GA1UEChMIRnJl
      ZSBUU0ExEDAOBgNVBAsTB1Jvb3QgQ0ExGDAWBgNVBAMTD3d3dy5mcmVldHNhLm9y
      ZzEiMCAGCSqGSIb3DQEJARYTYnVzaWxlemFzQGdtYWlsLmNvbTESMBAGA1UEBxMJ
      V3VlcnpidXJnMQ8wDQYDVQQIEwZCYXllcm4xCzAJBgNVBAYTAkRFMIICIjANBgkq
      hkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAtgKODjAy8REQ2WTNqUudAnjhlCrpE6ql
      mQfNppeTmVvZrH4zutn+NwTaHAGpjSGv4/WRpZ1wZ3BRZ5mPUBZyLgq0YrIfQ5Fx
      0s/MRZPzc1r3lKWrMR9sAQx4mN4z11xFEO529L0dFJjPF9MD8Gpd2feWzGyptlel
      b+PqT+++fOa2oY0+NaMM7l/xcNHPOaMz0/2olk0i22hbKeVhvokPCqhFhzsuhKsm
      q4Of/o+t6dI7sx5h0nPMm4gGSRhfq+z6BTRgCrqQG2FOLoVFgt6iIm/BnNffUr7V
      DYd3zZmIwFOj/H3DKHoGik/xK3E82YA2ZulVOFRW/zj4ApjPa5OFbpIkd0pmzxzd
      EcL479hSA9dFiyVmSxPtY5ze1P+BE9bMU1PScpRzw8MHFXxyKqW13Qv7LWw4sbk3
      SciB7GACbQiVGzgkvXG6y85HOuvWNvC5GLSiyP9GlPB0V68tbxz4JVTRdw/Xn/XT
      FNzRBM3cq8lBOAVt/PAX5+uFcv1S9wFE8YjaBfWCP1jdBil+c4e+0tdywT2oJmYB
      BF/kEt1wmGwMmHunNEuQNzh1FtJY54hbUfiWi38mASE7xMtMhfj/C4SvapiDN837
      gYaPfs8x3KZxbX7C3YAsFnJinlwAUss1fdKar8Q/YVs7H/nU4c4Ixxxz4f67fcVq
      M2ITKentbCMCAwEAAaOCAk4wggJKMAwGA1UdEwQFMAMBAf8wDgYDVR0PAQH/BAQD
      AgHGMB0GA1UdDgQWBBT6VQ2MNGZRQ0z357OnbJWveuaklzCBygYDVR0jBIHCMIG/
      gBT6VQ2MNGZRQ0z357OnbJWveuakl6GBm6SBmDCBlTERMA8GA1UEChMIRnJlZSBU
      U0ExEDAOBgNVBAsTB1Jvb3QgQ0ExGDAWBgNVBAMTD3d3dy5mcmVldHNhLm9yZzEi
      MCAGCSqGSIb3DQEJARYTYnVzaWxlemFzQGdtYWlsLmNvbTESMBAGA1UEBxMJV3Vl
      cnpidXJnMQ8wDQYDVQQIEwZCYXllcm4xCzAJBgNVBAYTAkRFggkAwemGFg2o6YAw
      MwYDVR0fBCwwKjAooCagJIYiaHR0cDovL3d3dy5mcmVldHNhLm9yZy9yb290X2Nh
      LmNybDCBzwYDVR0gBIHHMIHEMIHBBgorBgEEAYHyJAEBMIGyMDMGCCsGAQUFBwIB
      FidodHRwOi8vd3d3LmZyZWV0c2Eub3JnL2ZyZWV0c2FfY3BzLmh0bWwwMgYIKwYB
      BQUHAgEWJmh0dHA6Ly93d3cuZnJlZXRzYS5vcmcvZnJlZXRzYV9jcHMucGRmMEcG
      CCsGAQUFBwICMDsaOUZyZWVUU0EgdHJ1c3RlZCB0aW1lc3RhbXBpbmcgU29mdHdh
      cmUgYXMgYSBTZXJ2aWNlIChTYWFTKTA3BggrBgEFBQcBAQQrMCkwJwYIKwYBBQUH
      MAGGG2h0dHA6Ly93d3cuZnJlZXRzYS5vcmc6MjU2MDANBgkqhkiG9w0BAQ0FAAOC
      AgEAaK9+v5OFYu9M6ztYC+L69sw1omdyli89lZAfpWMMh9CRmJhM6KBqM/ipwoLt
      nxyxGsbCPhcQjuTvzm+ylN6VwTMmIlVyVSLKYZcdSjt/eCUN+41K7sD7GVmxZBAF
      ILnBDmTGJmLkrU0KuuIpj8lI/E6Z6NnmuP2+RAQSHsfBQi6sssnXMo4HOW5gtPO7
      gDrUpVXID++1P4XndkoKn7Svw5n0zS9fv1hxBcYIHPPQUze2u30bAQt0n0iIyRLz
      aWuhtpAtd7ffwEbASgzB7E+NGF4tpV37e8KiA2xiGSRqT5ndu28fgpOY87gD3ArZ
      DctZvvTCfHdAS5kEO3gnGGeZEVLDmfEsv8TGJa3AljVa5E40IQDsUXpQLi8G+UC4
      1DWZu8EVT4rnYaCw1VX7ShOR1PNCCvjb8S8tfdudd9zhU3gEB0rxdeTy1tVbNLXW
      99y90xcwr1ZIDUwM/xQ/noO8FRhm0LoPC73Ef+J4ZBdrvWwauF3zJe33d4ibxEcb
      8/pz5WzFkeixYM2nsHhqHsBKw7JPouKNXRnl5IAE1eFmqDyC7G/VT7OF669xM6hb
      Ut5G21JE4cNK6NNucS+fzg1JPX0+3VhsYZjj7D5uljRvQXrJ8iHgr/M6j2oLHvTA
      I2MLdq2qjZFDOCXsxBxJpbmLGBx9ow6ZerlUxzws2AWv2pk=
      -----END CERTIFICATE-----

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
