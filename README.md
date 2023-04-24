[![Run Integration Tests](https://github.com/SpringStoreOrg/com-boot-user-service-it/actions/workflows/build-and-test.yaml/badge.svg)](https://github.com/SpringStoreOrg/com-boot-user-service-it/actions/workflows/build-and-test.yaml) [![Maven](https://badgen.net/badge/icon/maven?icon=maven&label)](https://https://maven.apache.org/)
[![Open Source? Yes!](https://badgen.net/badge/Open%20Source%20%3F/Yes%21/blue?icon=github)](https://github.com/Naereen/badges/)

# Spring Store API Test Project

This is a Java / Maven / Rest Assured / TestNG  automation project.

The project "user-service-it" is a test automation project dedicated to test the API of the Microservice "user-service" part of the https://www.springwebstore.com app.

## REST API
- Each line of the scenario is tied to backend code that actually executes the line (step).

## Verify JSON GET Request

Testing a simple response containing some JSON data.

- Request URL: https://spring-store-gateway-service.herokuapp.com/user/?email=jellofirsthand@gaa1iler.site
- Request Method: GET
- Response Content-Type: application/json
- Response Body:
```json
{
  "id": 134,
  "firstName": "test_firstName",
  "lastName": "test_lastName",
  "phoneNumber": "0740000000",
  "email": "jellofirsthand@gaa1iler.site",
  "deliveryAddress": "street, no. 1",
  "roles": [
    "ACCESS",
    "CREATE_ORDER"
  ],
  "userFavorites": null,
  "activated": true
}
```
- Status Code: 200 OK

## Request not found
- Request URL: https://spring-store-gateway-service.herokuapp.com/user/?email=test@gaa1iler.site
- Request Method: GET
- Response Body:
```json
{
  
}
```
- Status Code: 404 Not Found