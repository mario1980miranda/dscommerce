package com.devsuperior.dscommerce.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.tests.ProductFactory;
import com.devsuperior.dscommerce.utils.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TokenUtil tokenUtil;

	@Autowired
	private ObjectMapper objectMapper;

	private String productName;
	private String adminToken, clientToken, invalidToken;
	private String adminUsername, adminPassword, clientUsername, clientPassword;

	private Product product;
	private ProductDTO productDTO;

	@BeforeEach
	void setUp() throws Exception {

		adminUsername = "alex@gmail.com";
		adminPassword = "123456";
		clientUsername = "maria@gmail.com";
		clientPassword = "123456";

		productName = "Macbook";

		product = ProductFactory.createNewProduct();
		productDTO = new ProductDTO(product);

		adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
		clientToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, clientPassword);
		invalidToken = adminToken + "invalidate.token";
	}

	@Test
	public void findAllShouldReturnPageWhenNameParamEmpty() throws Exception {

		ResultActions result = mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.content[0].id").value(1L));
		result.andExpect(jsonPath("$.content[0].name").value("The Lord of the Rings"));
		result.andExpect(jsonPath("$.content[0].price").value(90.5));
		result.andExpect(jsonPath("$.content[0].imgUrl").value(
				"https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"));
	}

	@Test
	public void findAllShouldReturnPageWhenNameParamIsNotEmpty() throws Exception {

		ResultActions result = mockMvc
				.perform(get("/products?name={productName}", productName).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.content[0].id").value(3L));
		result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		result.andExpect(jsonPath("$.content[0].price").value(1250.0));
		result.andExpect(jsonPath("$.content[0].imgUrl").value(
				"https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg"));
	}

	@Test
	public void insertShouldReturnProductDTOCreatedWhenAdminLogged() throws Exception {

		final String jsonBody = objectMapper.writeValueAsString(productDTO);

		// @formatter:off
		ResultActions result = mockMvc.perform(post("/products")
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print());
		// @formatter:on

		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").value(26L));
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidName() throws Exception {

		product.setName("ab");
		productDTO = new ProductDTO(product);
		
		final String jsonBody = objectMapper.writeValueAsString(productDTO);

		// @formatter:off
		ResultActions result = mockMvc.perform(post("/products")
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print());
		// @formatter:on

		result.andExpect(status().isUnprocessableEntity());
	}
}
