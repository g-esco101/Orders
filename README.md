# Orders

A REST service that manages Orders from a company. It uses HATEOAS to simplify
the creation of links to the resources that it offers.

Currently, the app is hosted on azure. The swagger user interface can be accessed here:
https://orders-rest-app.azurewebsites.net/swagger-ui/

## Technology

- Java 11
- Spring Boot
- Maven
- Intellij IDEA

### Spring Dependencies

- Web
- JPA
- H2
- HATEOAS
- Lombok

## To Run

Open the project using Intellij IDEA. The application can be launched several ways:
- In [OrdersApplication.java](src/main/java/com/goviesco/orders/OrdersApplication.java) right-click in the main method
and select Run from your IDE

- Spring Initializr uses maven wrapper so type this in the Terminal:
./mvnw clean spring-boot:run

- Alternatively using your installed maven version type this in the Terminal:
mvn clean spring-boot:run

Spring Boot will run the CommandLineRunner bean in [LoadDatabase.java](src/main/java/com/goviesco/orders/LoadDatabase.java) once the application 
context is loaded. This runner will request a copy of [OrderRepository.java](src/main/java/com/goviesco/orders/repository/OrderRepository.java), and use it to 
create and store several orders. The H2 database console can be accessed at http://localhost:8080/h2-console. The username is sa and 
the password is password.
The datasource properties are in [application.properties](src/main/resources/application.properties)

The api can be accessed via the swagger user interface at http://localhost:8080/swagger-ui/

Alternatively, Postman or Curl can be used to consume the service. Below is a sample of the JSON for a POST request. 
Note that old clients can still use "name": "Marie Curie" instead of using both the fields "firstName": "Marie" and "lastName": "Curie".
More on this later in the section Evolution of the API. 

POST for new clients:

{
"firstName": "Marie",
"lastName": "Curie",
"address": {
"address1": "4545 Wilshire Blvd",
"address2": "Apt 3",
"city": "Los Angeles",
"state": "CA",
"zip": "90025"
},
"orderLines": [
{
"brand": "LG",
"model": "Phone",
"cost": "1200",
"quantity": 1
}
],
"tax": "100",
"shipping": "200",
"total": "1500"
}

POST for old clients:

{
"name": "Marie Curie",
"address": {
"address1": "4545 Wilshire Blvd",
"address2": "Apt 3",
"city": "Los Angeles",
"state": "CA",
"zip": "90025"
},
"orderLines": [
{
"brand": "LG",
"model": "Phone",
"cost": "1200",
"quantity": 1
}
],
"tax": "100",
"shipping": "200",
"total": "1500"
}

The aforementioned POSTs will return the Hypertext Application Language (HAL) document below. Note how it contains the "firstName" and "lastName" fields, as well
as the "name" field.

{
"id": 4,
"status": "PROCESSING",
"firstName": "Marie",
"lastName": "Curie",
"address": {
"id": 4,
"address1": "4545 Wilshire Blvd",
"address2": "Apt 3",
"city": "Los Angeles",
"state": "CA",
"zip": "90025"
},
"orderLines": [
{
"id": 7,
"brand": "LG",
"model": "Phone",
"cost": "1200",
"quantity": 1
}
],
"tax": "100",
"shipping": "200",
"total": "1500",
"name": "Marie Curie",
"_links": {
"self": {
"href": "http://localhost:8080/orders/4"
},
"orders": {
"href": "http://localhost:8080/orders"
},
"cancel": {
"href": "http://localhost:8080/orders/4/cancel"
},
"complete": {
"href": "http://localhost:8080/orders/4/complete"
}
}
}

Note that cancel and complete are PUT requests.

## About REST?
Representational State Transfer (i.e. REST) is an architectural style that defines constraints and properties based on HTTP.
In applications, the data is the state, the JSON is the representational state, and HTTP is the transfer.

### Creating Links

Spring HATEOAS’s RepresentationModelAssembler interface is implemented in [OrderModelAssembler.java](src/main/java/com/goviesco/orders/assembler/OrderModelAssembler.java) to simplify link creation. The abstract method
toModel is implemented to map an [Order.java](src/main/java/com/goviesco/orders/entity/Order.java) to an EntityModel<Order>, which contains an Order's relevant links. Also, the default method
toCollectionModel is used by the [OrderController](src/main/java/com/goviesco/orders/controller/OrderController.java) to compose the CollectionModel, which is a collection of EntityModels,
for the readAll method in OrderController. The readAll method also adds a link for the aggregate root, making it more RESTful.

### Evolution of the API

REST is composed of architectural constraints that make the application resilient. Meaning that upgrades to the service 
will not cause clients to fail.

To demonstrate this, initially [Order.java](src/main/java/com/goviesco/orders/entity/Order.java) had only a name field, but later the need for firstName and lastName fields arose.
Simply replacing name with firstName and lastName will break clients that use the name field. 
To accommodate this change, we simply add new fields to the JSON representation. We don't remove any fields. Although 
information is duplicated, it serves the purpose of supporting both new and old clients and does not require old clients
to upgrade immediately.

To process data in both the new and old ways, the [Order.java](src/main/java/com/goviesco/orders/entity/Order.java) entity was updated by replacing name with firstName 
and lastName and adding a virtual getter and a virtual setter for name. The virtual getter and setter use firstName 
and lastName to get and set values for name. This change results in changes to the table in the database:
The name column is replaced by the firstName and lastName columns. Note: the same endpoints can be used for both 
new and old clients.

### State Transitions and HATEOAS

An [Order.java](src/main/java/com/goviesco/orders/entity/Order.java) goes through a series of state transitions from the time it is created to the
time it is either completed or canceled. This is modeled with the enum [Status.java](src/main/java/com/goviesco/orders/enumeration/Status.java): Status.PROCESSING, Status.COMPLETED, and Status.CANCELED.
An [Order.java](src/main/java/com/goviesco/orders/entity/Order.java) with Status.PROCESSING can transition to either Status.COMPLETED or Status.CANCELED. An [Order.java](src/main/java/com/goviesco/orders/entity/Order.java) with Status.COMPLETED
or Status.CANCELED cannot transition states. 

Hypermedia As The Engine Of Application State (HATEOAS) allows the clients to be given links to valid actions.
For example, when cancel and complete are valid actions, links to them are dynamically included in the response by the 
[OrderModelAssembler.java](src/main/java/com/goviesco/orders/assembler/OrderModelAssembler.java). This allows clients to
not have to parse the payload, and decouples state based actions from the payload of data. Also, clients can show users
buttons to these actions only when they are valid, which mitigates the risk of the server and its clients having errors
in regard to state transitions.

If clients use HAL and can read links instead of reading the data of plain old JSON, they can give up the need for domain
knowledge about the Orders service, which mitigates coupling between client and server.     

## Testing
The [OrderControllerTests](src/test/java/com/goviesco/orders/OrderControllerTests.java) tests the
[OrderController](src/main/java/com/goviesco/orders/controller/OrderController.java), including the hypermedia and exceptions.

## Entity and Relationships
There are three entities: [Order.java](src/main/java/com/goviesco/orders/entity/Order.java), 
[OrderLine.java](src/main/java/com/goviesco/orders/entity/OrderLine.java), and 
[Address.java](src/main/java/com/goviesco/orders/entity/Address.java). 
```java
public class Order {
    ...
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> orderLines;
    ...
```
An [Order.java](src/main/java/com/goviesco/orders/entity/Order.java) ships to only one [Address.java](src/main/java/com/goviesco/orders/entity/Address.java). This is a unidirectional, one-to-one relationship. The @OneToOne annotation used
in [Order.java](src/main/java/com/goviesco/orders/entity/Order.java) will create a join column named ADDRESS_ID in the ORDERS table. ADDRESS_ID stores the foreign key, i.e. the 
primary key or the Id of the [Address.java](src/main/java/com/goviesco/orders/entity/Address.java). 

An [Order.java](src/main/java/com/goviesco/orders/entity/Order.java) contains many [OrderLines](src/main/java/com/goviesco/orders/entity/OrderLine.java). This is a unidirectional, one-to-many relationship. The @OneToMany annotation used
in [Order.java](src/main/java/com/goviesco/orders/entity/Order.java) will create a join table named ORDERS_ORDER_LINES. Its primary key is made up of two foreign keys: the primary key or Id of the
[Order.java](src/main/java/com/goviesco/orders/entity/Order.java) and the primary key or Id of the [OrderLine.java](src/main/java/com/goviesco/orders/entity/OrderLine.java).

The configurations of these relationships use CascadeType.ALL and orphanRemoval is true. CascadeType.All 
indicates that all operations, i.e. MERGE, PERSIST, REFRESH, REMOVE, and DETACH, must be cascaded to the target 
of the association. orphanRemoval is true indicates that the remove operation will be applied to entities that 
have been removed from the relationship. For example, in the [OrderController's](src/main/java/com/goviesco/orders/controller/OrderController.java)
update method, this configuration ensures that the old address and old orderLines are removed from their respective tables.
Note that the [OrderLines](src/main/java/com/goviesco/orders/entity/OrderLine.java) are not reassigned, because it would throw a persistence exception. Instead, the list is modified
```java
    @PutMapping("/orders/{id}")
    public ResponseEntity<?> update(@RequestBody Order newOrder, @PathVariable Long id) {
        Order updatedOrder = repository.findById(id)
                .map(order -> {
                    order.setStatus(newOrder.getStatus());
                    order.setFirstName(newOrder.getFirstName());
                    order.setLastName(newOrder.getLastName());
                    order.setAddress(newOrder.getAddress());
                    // Note: the existing list is modified; reassigning it would lead to a persistence exception.
                    order.getOrderLines().clear();
                    order.getOrderLines().addAll(newOrder.getOrderLines());
                    return repository.save(order);
                })
                .orElseThrow(() -> new OrderNotFoundException(id));
        ...
```

## Enum Persistence

JPA 2.1 provides features that simplify persisting enums and makes the persistence of 
enums flexible to change.

Prior to JPA 2.1, the most common way to map an enum value to and from its database
representation was to use the @Enumerated annotation, but updating the enum can result in problems. 

If we annotate the enum field in an entity with @Enumerated(EnumType.ORDINAL), JPA will call Enum.ordinal()
and use the return value to persist the entity in the database. E.g., Status.PROCESSING.ordinal()
returns 0. When we persist the ordinal value in the database, a problem arises if we add a value in the middle or change the enum's
order. In this case, we would have to update all the database records. 

If we annotate the enum field in an entity with @Enumerated(EnumType.STRING), JPA will call Enum.name()
and use the return value to persist the entity in the database. E.g., Status.PROCESSING.name()
returns PROCESSING. When we persist the name value in the database, we can change the order and add new enums, but 
changing the name of an enum will break the database data. Also, it uses more space than necessary.

If we persist the enum with JPA callback methods by using the @PostLoad and @PrePersist events, the enum can be 
mapped back and forth within these methods. This would entail having two attributes in the entity: one that is mapped to 
a database value; and the other transient and is mapped to a real enum value, which is used by the business logic. This 
solution.

To overcome the shortcomings of the aforementioned solutions, JPA 2.1 offers an API that 
converts an entity field to its database value and vice versa: javax.persistence.AttributeConverter and annotate it 
with @Converter(autoApply = true). Setting the autoApply value to true will have JPA automatically apply the 
conversion to all mapped fields. This gives flexibility to safely add new enum value, change the existing values, and
saves space -- without disrupting the persisted data.  

```java
public enum Status {

    PROCESSING("PROC"), COMPLETED("COMP"), CANCELED("CAN");

    private String dbData;

    private Status(String dbData) {
        this.dbData = dbData;
    }

    public String getDbColumn() {
        return this.dbData;
    }
}
```
```java
@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDbColumn();
    }

    @Override
    public Status convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return Stream.of(Status.values())
                .filter(val -> val.getDbColumn().equals(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
```
```java
public class Order {
    ...
    @Column(length = 4) // Note: @Size and @Length are used to validate the size of a field. @Column is used to control DDL statements.
    private Status status;
    ...
}    

```
