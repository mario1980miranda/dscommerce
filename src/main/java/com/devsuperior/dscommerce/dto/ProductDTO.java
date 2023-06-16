package com.devsuperior.dscommerce.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.devsuperior.dscommerce.entities.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ProductDTO {

	private Long id;
	@Size(min = 3, max = 80, message = "Le champ NAME doit comporter entre 3 et 80 caractères")
	@NotBlank(message = "Champ requis")
	private String name;
	@Size(min = 10, message = "Le champ DESCRIPTION doit comporter au moins 10 caractères")
	@NotBlank(message = "Champ requis")
	private String description;
	@NotNull(message = "Campo requerido")
	@Positive(message = "Le champ PRICE doit être positive")
	private Double price;
	private String imgUrl;
	
	@NotEmpty(message = "Deve haver ao menos uma categoria")
	private List<CategoryDTO> categories = new ArrayList<>();

	public ProductDTO(Long id, String name, String description, Double price, String imgUrl) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.imgUrl = imgUrl;
	}

	public ProductDTO(final Product entity) {
		id = entity.getId();
		name = entity.getName();
		description = entity.getDescription();
		price = entity.getPrice();
		imgUrl = entity.getImgUrl();
		categories = entity.getCategories().stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Double getPrice() {
		return price;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public List<CategoryDTO> getCategories() {
		return categories;
	}

	
}
