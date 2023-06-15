# dscommerce

## Framework details

- Spring Web MVC
- Spring Data JPA
- H2 Database

## Maven plugin pom.xml
```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-resources-plugin</artifactId>
	<version>3.1.0</version> <!--$NO-MVN-MAN-VER$ -->
</plugin>
```

## Spring profiles

properties :

```
spring.profiles.active=**test**
spring.jpa.open-in-view=false
```

yaml :

```yaml
spring:
  profiles:
    active:
      - test
  jpa:
    open-in-view: false
```

## H2 Database connetion

properties :

```
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.defer-datasource-initialization=true
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

yaml :

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
    database-platform: org.hibernate.dialect.H2Dialect
    defer-datasource-initialization: true
```

## JPA Mapping
### Recommendation Instant Mapping
```java
@Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
private Instant moment;
```
### N-1
```java
public class Order {
	...
	@ManyToOne
	@JoinColumn(name = "client_id")
	private User client;
```
```java
public class User {
	...
	@OneToMany(mappedBy = "client")
	private List<Order> orders = new ArrayList<>();
```
### 1-1
```java
public class Payment {
	...
	@OneToOne
	@MapsId
	private Order order;
```
```java
public class Order {
	...
	@OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
	private Payment payment;
```
### N-N
```java
public class Product {
	...
	@ManyToMany
	@JoinTable(name = "tb_product_category",
	joinColumns = @JoinColumn(name = "product_id"),
	inverseJoinColumns = @JoinColumn(name = "category_id"))
	private Set<Category> categories = new HashSet<>();
```
```java
public class Category {
	...
	@ManyToMany(mappedBy = "categories")
	private Set<Product> products = new HashSet<>();
```
### N-N with class association
```java
@Embeddable
public class OrderItemPK {
	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;
	...
```
```java
@Entity
@Table(name = "tb_order_item")
public class OrderItem {
	@EmbeddedId
	private OrderItemPK id = new OrderItemPK();
	private Integer quantity;
	private Double price;
	
	public OrderItem() {
	}
	public OrderItem(Order order, Product product, Integer quantity, Double price) {
		id.setOrder(order);
		id.setProduct(product);
		this.quantity = quantity;
		this.price = price;
	}
	public Order getOrder() {
		return id.getOrder();
	}
	public void setOrder(Order order) {
		id.setOrder(order);
	}
	...
```
```java
public class Order {
	...
	@OneToMany(mappedBy = "id.order")
	private Set<OrderItem> items = new HashSet<>();
	...
	public Set<OrderItem> getItems() {
		return items;
	}
	public List<Product> getProducts() {
		return items.stream().map(x -> x.getProduct()).toList();
	}
```
```java
public class Product {
	...
	@OneToMany(mappedBy = "id.product")
	private Set<OrderItem> items = new HashSet<>();
	...
	public Set<OrderItem> getItems() {
		return items;
	}
	public List<Order> getOrders() {
		return items.stream().map(x -> x.getOrder()).toList();
	}
```
## Seed

## Service and Repository

> @Service

> @Transational(readOnly=true)

> @JPARepository<T, ID>

> @Transactional(propagation = Propagation.SUPPORTS)

On doit l'utiliser pour capturer les exceptions qui ne sont pas liés au framework spring, par exemple h2.JDBC...Exception, avec le "Propagation.SUPPORTS" le DataIntegrityViolationException du Spring sera capture e on pourra le traiter dans le ExceptionHandler.

## DTO

## CRUD


### Create / POST

> @PostMapping

> @RequestBody

> ResponseEntity

```java
URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(dto.getId())
				.toUri();
return ResponseEntity.created(location).body(dto);
```

### Read / GET

> @GetMapping

> @Pageable

Exemplos de parametros : ?size=12&page=0&sort=name,desc

> @GetMapping("/{id}")

> @PathVariable

## Exceptions com Controller Advice

Criar uma classe que vai interceptar todas as exceptions de determinadas classes customizadas. Por exemplo : 

```java
@ControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<CustomError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
		final HttpStatus status = HttpStatus.NOT_FOUND;
		final CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
}
```

> **ResourceNotFoundException.class** é nossa classe de erro customizada que extende RuntimeException, podemos tratar outros tipos de exception dentro desta classe.

> **CustomError** classe criada com os mesmo atributos de um exception normal lançada por um Controller em um erro 500

```json
{
    "timestamp": "2023-06-05T19:42:18.945+00:00",
    "status": 500,
    "error": "Internal Server Error",
    "path": "/products/100"
}
```

## Bean Validation

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

[https://jakarta.ee/specifications/bean-validation/3.0/](https://jakarta.ee/specifications/bean-validation/3.0/)

[https://jakarta.ee/specifications/bean-validation/3.0/apidocs/](https://jakarta.ee/specifications/bean-validation/3.0/apidocs/)

[https://javaee.github.io/tutorial/bean-validation.html](https://javaee.github.io/tutorial/bean-validation.html)

# Spring Security

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

## Oauth2

[https://oauth.net/2/](https://oauth.net/2/)

- Dependencias AuthorizationServer e ResourceServer

```xml
<dependency>
	<groupId>org.springframework.security</groupId>
	<artifactId>spring-security-oauth2-authorization-server</artifactId>
</dependency>

<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

O Spring Security disponibiliza um caminho padrao no path para geracao do token JWT

POST > host:port/oauth2/token

- Auth Server
	- app credentials + user credentials | token (signature, claims, expiration)
- Resource Server
	- resource URI + token | resource access
- JWT
- CORS (Configurar quais hosts podem acessar o backend)


## Exemplos no projeto

com.devsuperior.demo.config.AuthorizationServerConfig

com.devsuperior.demo.config.ResourceServerConfig

## Controle de acesso por perfil e rota

> @PreAuthorize("hasRole('ROLE_ADMIN')")

> @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")

