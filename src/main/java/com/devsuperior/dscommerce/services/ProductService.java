package com.devsuperior.dscommerce.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.ProductRepository;

@Service
public class ProductService {

	private ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(final Long id) {
		final Optional<Product> optionalProduct = this.productRepository.findById(id);
		final Product entity = optionalProduct.get();
		ProductDTO dto = new ProductDTO(entity);
		return dto;
	}
}