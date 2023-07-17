package com.devsuperior.dscommerce.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.dto.ProductMinDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.DatabaseException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscommerce.tests.ProductFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	private Long existingProductId, nonExistingProductId, dependentProductId;
	private Product product;
	private String productName;
	private ProductDTO productDTO;
	
	private PageImpl<Product> page;

	@BeforeEach
	void setUp() throws Exception {
		existingProductId = 1L;
		nonExistingProductId = 2L;
		dependentProductId = 3L;
			
		productName = "PlayStation 5";
		
		product = ProductFactory.createProduct(productName);
		productDTO = new ProductDTO(product);
		
		page = new PageImpl<>(List.of(product));
		
		Mockito.when(repository.findById(existingProductId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingProductId)).thenReturn(Optional.empty());
		Mockito.when(repository.searchByName(Mockito.any(), (Pageable)Mockito.any())).thenReturn(page);
		Mockito.when(repository.save(Mockito.any())).thenReturn(product);
		Mockito.when(repository.getReferenceById(existingProductId)).thenReturn(product);
		Mockito.when(repository.getReferenceById(nonExistingProductId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(repository.existsById(existingProductId)).thenReturn(Boolean.TRUE);
		Mockito.when(repository.existsById(dependentProductId)).thenReturn(Boolean.TRUE);
		Mockito.when(repository.existsById(nonExistingProductId)).thenReturn(Boolean.FALSE);
		Mockito.doNothing().when(repository).deleteById(existingProductId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentProductId);
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		
		final ProductDTO result = service.findById(existingProductId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), product.getId());
		Assertions.assertEquals(result.getName(), productName);
	}
	
	@Test
	public void findByIdShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			final ProductDTO result = service.findById(nonExistingProductId);
		});
	}
	
	@Test
	public void findAllShouldReturnPageOfProductMinDTOWhenGivenNameAndPageable() {
		
		Pageable pegeable = PageRequest.of(0, 12);
		String name = "PlayStation 5";
		
		final Page<ProductMinDTO> result = service.findAll(name, pegeable);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getSize(), 1);
		Assertions.assertEquals(result.iterator().next().getName(), product.getName());
	}
	
	@Test
	public void insertShouldReturnProductDTO() {
				
		final ProductDTO result = service.insert(productDTO);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), product.getId());
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() {
		
		final ProductDTO result = service.update(existingProductId, productDTO);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingProductId);
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			final ProductDTO result = service.update(nonExistingProductId, productDTO);
		});
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingProductId);
		});
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingProductId);
		});
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdIsDependent() {
		
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentProductId);
		});
	}
}
