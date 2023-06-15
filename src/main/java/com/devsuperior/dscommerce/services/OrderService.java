package com.devsuperior.dscommerce.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscommerce.dto.OrderDTO;
import com.devsuperior.dscommerce.entities.Order;
import com.devsuperior.dscommerce.repositories.OrderRepository;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;

@Service
public class OrderService {

	private OrderRepository repository;

	public OrderService(OrderRepository repository) {
		this.repository = repository;
	}

	@Transactional(readOnly = true)
	public OrderDTO findById(final Long id) {
		final Optional<Order> optionalOrder = this.repository.findById(id);
		final Order entity = optionalOrder.orElseThrow(() -> new ResourceNotFoundException());
		OrderDTO dto = new OrderDTO(entity);
		return dto;
	}

}
