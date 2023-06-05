package com.devsuperior.dscommerce.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.services.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

	private ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping("/{id}")
	public ProductDTO findById(@PathVariable final Long id) {
		final ProductDTO dto = this.productService.findById(id);
		return dto;
	}
	
	@GetMapping
	public Page<ProductDTO> findAll(Pageable pageable) {
		return this.productService.findAll(pageable);
	}
	
	@PostMapping
	public ProductDTO insert(@RequestParam final ProductDTO dto) {
		return this.productService.insert(dto);
	}
}
