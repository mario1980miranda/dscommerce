package com.devsuperior.dscommerce.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscommerce.entities.Order;
import com.devsuperior.dscommerce.entities.OrderItem;
import com.devsuperior.dscommerce.entities.OrderStatus;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.tests.ProductFactory;
import com.devsuperior.dscommerce.tests.UserFactory;
import com.devsuperior.dscommerce.utils.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TokenUtil tokenUtil;

	@Autowired
	private ObjectMapper objectMapper;

	private String adminToken, clientToken, invalidToken;
	private String adminUsername, adminPassword, clientUsername, clientPassword;
	private Long existingOrderId, nonExistingOrderId;
	
	private User user;
	private Order order;
	private Product product;
	private OrderItem orderItem;
	
	@BeforeEach
	void setUp() throws Exception {
		
		adminUsername = "alex@gmail.com";
		adminPassword = "123456";
		clientUsername = "maria@gmail.com";
		clientPassword = "123456";
		
		existingOrderId = 1L;
		nonExistingOrderId = 100L;
		
		user = UserFactory.createClientUser();
		order = new Order(null, Instant.now(), OrderStatus.WAITTING_PAYMENT, user, null);
	    product = ProductFactory.createProduct();
	    orderItem = new OrderItem(order, product, 1, 20.0);
	    order.getItems().add(orderItem);
		
		adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
		clientToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, clientPassword);
		invalidToken = adminToken + "invalidate.token";
		
	}
	
	@Test
	public void findByIdShouldReturnOkWhenOrderIdExistsAndAdminLogged() throws Exception {
		
		ResultActions result = mockMvc.perform(
				get("/orders/{id}", existingOrderId)
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnOkWhenOrderIdExistsAndClientLogged() throws Exception {
		
		ResultActions result = mockMvc.perform(
				get("/orders/{id}", existingOrderId)
				.header("Authorization", "Bearer " + clientToken)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnForbiddenWhenOrderIdExistsAndClientLoggedAndOrderDoesNotBelongUser() throws Exception {
		
		Long otherOrderId = 2L;
		
		ResultActions result = mockMvc.perform(
				get("/orders/{id}", otherOrderId)
				.header("Authorization", "Bearer " + clientToken)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isForbidden());
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenOrderIdDoesNotExistAndAdminLogged() throws Exception {
		
		ResultActions result = mockMvc.perform(
				get("/orders/{id}", nonExistingOrderId)
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenOrderIdDoesNotExistAndClientLogged() throws Exception {
		
		ResultActions result = mockMvc.perform(
				get("/orders/{id}", nonExistingOrderId)
				.header("Authorization", "Bearer " + clientToken)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void findByIdShouldReturnUnauthprizedWhenOrderIdExistsAndNotLogged() throws Exception {
		
		ResultActions result = mockMvc.perform(
				get("/orders/{id}", existingOrderId)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenOrderIdDoesNotExistAndInvalidToken() throws Exception {
		
		ResultActions result = mockMvc.perform(
				get("/orders/{id}", existingOrderId)
				.header("Authorization", "Bearer " + invalidToken)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isUnauthorized());
	}
}
