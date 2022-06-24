# gerimedica




Start:
The following guides illustrate how to use some features concretely:

Run: $ mvn spring-boot:run

url: http://localhost Port: 8080

#### Scheme APIs:

| Http Method   | Path                                    | Description                 |
|-------------	|---------------------------------------- |---------------------------	|
| POST          | /api/v1/upload/                         | Upload csv file in multipart|
| GET           | /api/v1/fetch/{code}/                   | Fetch csv data by code      |
| GET           | /api/v1/fetch/all/                      | Fetch all data              |
| DELETE        | /api/v1/delete/all/                     | Delete all data             |
