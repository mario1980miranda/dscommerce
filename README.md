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
