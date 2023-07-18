package com.devsuperior.dscommerce.services;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscommerce.dto.OrderDTO;
import com.devsuperior.dscommerce.entities.Order;
import com.devsuperior.dscommerce.entities.OrderItem;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.repositories.OrderItemRepository;
import com.devsuperior.dscommerce.repositories.OrderRepository;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscommerce.tests.OrderFactory;
import com.devsuperior.dscommerce.tests.ProductFactory;
import com.devsuperior.dscommerce.tests.UserFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class OrderServiceTests {

	@InjectMocks
	private OrderService orderService;
	
	@Mock
	private OrderRepository orderRepository;
	
	@Mock
	private AuthService authService;
	
	@Mock
	private UserService userService;
	
	@Mock
	private ProductRepository productRepository;
	
	@Mock
	private OrderItemRepository orderItemRepository;
	
	private Long existingOrderId, nonExistingOrderId;
	private Long existingProductId, nonExistingProducId;
	private User client, admin;
	private Order order;
	private OrderDTO dto;
	private Product product;
	
	@BeforeEach
	void setUp() throws Exception {
		
		existingOrderId = 1L;
		nonExistingOrderId = 2L;
		existingProductId = 1L;
		nonExistingOrderId = 2L;
		
		client = UserFactory.createCustomClientUser(1L, "Bob");
		admin = UserFactory.createCustomAdminUser(2L, "Jeff");
		
		order = OrderFactory.createOrder(client);
		product = ProductFactory.createProduct();
		
		dto = new OrderDTO(order);
		
		Mockito.when(orderRepository.findById(existingOrderId)).thenReturn(Optional.of(order));
		Mockito.when(orderRepository.findById(nonExistingOrderId)).thenReturn(Optional.empty());
		
		Mockito.when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
		Mockito.when(productRepository.getReferenceById(nonExistingProducId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(orderRepository.save(Mockito.any())).thenReturn(order);
		
		Mockito.when(orderItemRepository.saveAll(order.getItems())).thenReturn(new ArrayList<>(order.getItems()));
	}
	
	@Test
	public void findByIdShouldReturnOrderDTOWhenOrderIdExistsAndAdminLogged() {
		
		Mockito.doNothing().when(authService).validateSelfUserOrAdmin(Mockito.any());
		
		final OrderDTO result = orderService.findById(existingOrderId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingOrderId);
	}
	
	@Test
	public void findByIdShouldReturnOrderDTOWhenOrderIdExistsAndSelfClientLogged() {
		
		Mockito.doNothing().when(authService).validateSelfUserOrAdmin(Mockito.any());
		
		final OrderDTO result = orderService.findById(existingOrderId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingOrderId);
	}
	
	@Test
	public void findByIdShouldThrowsForbiddenExceptionWhenOrderIdExistsAndOtherClientLogged() {
		
		Mockito.doThrow(ForbiddenException.class).when(authService).validateSelfUserOrAdmin(Mockito.any());
		
		Assertions.assertThrows(ForbiddenException.class, () -> {
			@SuppressWarnings("unused")
			final OrderDTO result = orderService.findById(existingOrderId);
		});
	}
	
	@Test
	public void findByIdShouldThrowsResourceNotFoundExceptionWhenOrderIdDoesNotExist() {
		
		Mockito.doNothing().when(authService).validateSelfUserOrAdmin(Mockito.any());
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			final OrderDTO result = orderService.findById(nonExistingOrderId);
		});
	}
	
	@Test
	public void insertShouldReturnOrderDTOWhenUserAdminLogged() {
		
		Mockito.when(userService.autheticated()).thenReturn(admin);
		
		final OrderDTO result = orderService.insert(dto);
		
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void insertShouldReturnOrderDTOWhenUserClientLogged() {
		
		Mockito.when(userService.autheticated()).thenReturn(client);
		
		final OrderDTO result = orderService.insert(dto);
		
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void insertShouldThrowUsernameNotFoundExceptionWhenUserNotLogged() {
		
		Mockito.doThrow(UsernameNotFoundException.class).when(userService).autheticated();
		
		order.setClient(new User());
		dto = new OrderDTO(order);
		
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			final OrderDTO result = orderService.insert(dto);
		});
	}
	
	@Test
	public void insertShouldThrowEntityNotFoundExceptionWhenOrderItemIdDoesNotExist() {
		
		Mockito.when(userService.autheticated()).thenReturn(client);
		
		product.setId(nonExistingProducId);
		OrderItem orderItem = new OrderItem(order, product, 2, 50.0);
		order.getItems().add(orderItem);
		
		dto = new OrderDTO(order);
		
		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			final OrderDTO result = orderService.insert(dto);
		});
	}
}
