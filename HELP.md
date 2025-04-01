# Getting Started

### Business Overview

Wallet-APP is a monolithic application with several functionalities. Ideally those functionalities should be split into isolates microservices
in order to reduce complexity and improve performance.
The Wallet-APP support the following flows:
- Users
  - JWT for authentication
- Accounting
  - Balance partitions
  - Bank Accounts
- Balances
  - Historical
  - Near Real-time
- Withdrawals
- Deposits
- Transfers

### Technical Overview

Wallet-APP was developed in Java 21 + Spring-boot. Its includes the following tools:
- Swagger + Open API
- JWT
- JPA
- MySQL 8
- Lombok

Design rules:
- Each operation is translated to a movement entity. The movement is immutable, so the data can not be updated in the future.
- The movements are associated to the operation itself and one balance.
- The balance itself is the partition + currency.
- Each balance is associated to one account. And the account can use several balances, defined as partitions. The balance also defines the currency.
- One account can support several currencies, each currency associated to 1 balance.
- The balance partition allow to think in different boxes of transactions. Ex: AVAILABLE-ARS, BLOCKED-ARS, IN_TRANSIT-ARS, IN_RESERVE-ARS. Etc
- Based on transaction status, the movements can be moved between partitions. Ex: Payment with a settlement period. It must impact first on BLOCKED partition and then move it to AVAILABLE partition.
- Event oriented, composed by (Events - Snapshots - Projections)
- Event represent an immutable transaction.
- Snapshot represent the accumulated amount of one balance in a unit of time. In this case for 1 day (closed day). So, every day there is a process building the snapshots for all balances.
- Projection represent the accumulated amount near real-time of one balance. This is a cron process running every 5 seconds.
- Optimistic Locking for projection updates in order to avoid data leaks.
- Idempotency is applied in all tables, like: movements (uid is a field composed to make this event idempotent)
- In order to apply idempotency, some tables has an uid field with a unique constraint.
- Every time a new movement is created, the service enqueue an event into an inner-queue. Then other process pull that event and update the projection.

#### Database model
![plot](../wallet-app/images/db-model.png)

#### Balance modeling
![plot](../wallet-app/images/main-entities.png)

### Swagger (http://localhost:8080/swagger-ui/index.html#/)

#### Login
![plot](../wallet-app/images/login.png)

![plot](../wallet-app/images/authenticate.png)
#### Get Account
```
curl -X 'GET' \
  'http://localhost:8080/accounts' \
  -H 'accept: */*' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsdWlzIiwiaWF0IjoxNzQzNTIyMzQ5LCJleHAiOjE3NDM1MjU5NDl9.W8Yu7l-301spSVN4i5p3JjwMRcffN0eqdT6EtUWg2z8'
```
Response
```
{
  "id": 1,
  "userId": 1,
  "name": "wallet account",
  "createdDate": "2025-03-28T21:00:00",
  "updatedDate": "2025-03-28T21:00:00",
  "status": "ACTIVE",
  "balances": [
    {
      "id": 1,
      "accountId": 1,
      "uid": "AVAILABLE-ARS-1",
      "currency": "ARS",
      "partition": "AVAILABLE",
      "createdDate": "2025-03-28T21:00:00"
    },
    {
      "id": 2,
      "accountId": 1,
      "uid": "AVAILABLE-USD-1",
      "currency": "USD",
      "partition": "AVAILABLE",
      "createdDate": "2025-03-28T21:00:00"
    },
    {
      "id": 6,
      "accountId": 1,
      "uid": "AVAILABLE-BRL-1",
      "currency": "BRL",
      "partition": "AVAILABLE",
      "createdDate": "2025-03-28T21:00:00"
    },
    {
      "id": 7,
      "accountId": 1,
      "uid": "AVAILABLE-COP-1",
      "currency": "COP",
      "partition": "AVAILABLE",
      "createdDate": "2025-03-31T18:19:17.826029"
    }
  ]
}
```
#### Get Balance
```
curl -X 'GET' \
'http://localhost:8080/accounts/balances' \
-H 'accept: */*' \
-H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsdWlzIiwiaWF0IjoxNzQzNTIyMzQ5LCJleHAiOjE3NDM1MjU5NDl9.W8Yu7l-301spSVN4i5p3JjwMRcffN0eqdT6EtUWg2z8'
```
Response
```
[
  {
    "balanceUid": "AVAILABLE-ARS-14",
    "amount": 193500,
    "createdDate": "2025-03-31T23:31:26",
    "updatedDate": "2025-04-01T00:29:03",
    "type": "PROJECTION"
  },
  {
    "balanceUid": "AVAILABLE-USD-14",
    "amount": 13450,
    "createdDate": "2025-03-31T23:31:21",
    "updatedDate": "2025-03-31T23:31:21",
    "type": "PROJECTION"
  }
]
```
### Reference Documentation

For further reference, please consider the following sections:

* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.4/maven-plugin)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.4.4/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.4/reference/web/servlet.html)

### Guides

The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the
parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.