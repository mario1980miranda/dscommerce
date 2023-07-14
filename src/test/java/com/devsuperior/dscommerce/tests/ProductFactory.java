package com.devsuperior.dscommerce.tests;

import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.entities.Product;

public class ProductFactory {

	public static Product createProduct() {
		final Category category = CategoryFactory.createCategory();
		Product product = new Product(1L, "Smart TV", "Lorem ipsum dolor sit amet, conse", 69.99, "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg");
		product.getCategories().add(category);
		return product;
	}
	
	public static Product createProduct(final String name) {
		Product product = createProduct();
		product.setName(name);
		return product;
	}
}
