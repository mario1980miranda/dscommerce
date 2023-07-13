package com.devsuperior.dscommerce.tests;

import com.devsuperior.dscommerce.entities.Category;

public class CategoryFactory {

	public static Category createCategory() {
		return new Category(1L, "Category Name");
	}
	
	public static Category createCatetory(final Long id, final String name) {
		return new Category(id, name);
	}
	
	
}
