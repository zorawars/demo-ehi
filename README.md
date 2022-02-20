# Demo Patient Application
Spring Boot application that allows API access to Patient information using REST over HTTP.

## Building the project
Run the below maven command to run all tests, generate the OpenAPI YAML file using Springdoc, code coverage using Jacoco, and build the application jar file.
```shell
$ ./mvnw clean verify
```

## Running the application locally
Use the maven `spring-boot-plugin` to run the application locally.
```shell
$ ./mvnw spring-boot:run
```

Once the application comes up it is available at http://localhost:8080/ehi.

- Swagger UI with the OpenAPI definition - http://localhost:8080/ehi/swagger-ui/index.html
- H2 console - http://localhost:8080/ehi/h2-console. Access H2 database as `user=sa` and `password=password`. See [application.properties](src/main/resources/application.properties).

## REST Endpoints to access Patient information
- Get all patients, or search for patients using first name - https://localhost:8080/ehi/patients
- Look up a patient, say with ID `6` - https://localhost:8080/ehi/patients/6
- Add a new patient - https://localhost:8080/ehi/patients

### OpenAPI 3.0 Documentation
The OpenAPI YAML file documenting the REST API is generated and available under the project root at [springdoc-ehi-patient.yaml](springdoc-ehi-patient.yaml).
This can be imported into Postman.


### Curl commands
* Get all patients, by default 50 at a time.
```shell
$ curl -X 'GET' 'http://localhost:8080/ehi/patients'
```
* Get all patients whose first name contains `Ste`.
```shell
$ curl -X 'GET' 'http://localhost:8080/ehi/patients?first_name=Ste'
```
* Get first 5 patients.
```shell
$ curl -X 'GET' 'http://localhost:8080/ehi/patients?size=5'
$ curl -X 'GET' 'http://localhost:8080/ehi/patients?page=0&size=5'
```
* Get patient with `ID=6`.
```shell
$ curl -X 'GET' 'http://localhost:8080/ehi/patients/6'
```
* Add new patient.
```shell
$ curl -X 'POST' 'http://localhost:8080/ehi/patients' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1986-01-01"
}'
```
