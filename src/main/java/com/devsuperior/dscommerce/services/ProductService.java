package com.devsuperior.dscommerce.services;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscommerce.dto.CategoryDTO;
import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.CategoryRepository;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.DatabaseException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

	private ProductRepository productRepository;
	
	private CategoryRepository categoryRepository;

	public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(final Long id) {
		final Optional<Product> optionalProduct = this.productRepository.findById(id);
		final Product entity = optionalProduct.orElseThrow(() -> new ResourceNotFoundException());
		ProductDTO dto = new ProductDTO(entity);
		return dto;
	}

	@Transactional(readOnly = true)
	public Page<ProductDTO> findAll(Pageable pageable) {
		final Page<Product> result = this.productRepository.findAll(pageable);
		this.productRepository.searchProductsWithCategories(result.stream().collect(Collectors.toList()));
		return result.map(product -> new ProductDTO(product));
	}

	@Transactional
	public ProductDTO insert(final ProductDTO dto) {
		Product entity = new Product();
		this.copyDtoToEntity(dto, entity);
		entity = this.productRepository.save(entity);
		return new ProductDTO(entity);
	}

	@Transactional
	public ProductDTO update(final Long id, final ProductDTO dto) {
		try {
			Product entity = this.productRepository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			this.productRepository.save(entity);
			return new ProductDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException();
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(final Long id) {
		if (!this.productRepository.existsById(id)) {
			throw new ResourceNotFoundException();
		}
		try {
			this.productRepository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Échec d'intégrité référentielle");
		}
	}

	private void copyDtoToEntity(final ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setPrice(dto.getPrice());
		entity.setImgUrl(dto.getImgUrl());
		
		for (CategoryDTO catDto : dto.getCategories()) {
			Category catEntity = this.categoryRepository.getReferenceById(catDto.getId());
			entity.getCategories().add(catEntity);
		}
	}
}
