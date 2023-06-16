package com.devsuperior.dscommerce.services;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscommerce.dto.OrderDTO;
import com.devsuperior.dscommerce.entities.Order;
import com.devsuperior.dscommerce.entities.OrderItem;
import com.devsuperior.dscommerce.entities.OrderStatus;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.repositories.OrderItemRepository;
import com.devsuperior.dscommerce.repositories.OrderRepository;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;

@Service
public class OrderService {

	private OrderRepository orderRepository;
	private OrderItemRepository orderItemRepository;
	private UserService userService;
	private ProductRepository productRepository;

	// @formatter:off
	public OrderService(OrderRepository orderRepository, 
						OrderItemRepository orderItemRepository,
						UserService userService, 
						ProductRepository productRepository) {
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.userService = userService;
		this.productRepository = productRepository;
	}
	// @formatter:on


	@Transactional(readOnly = true)
	public OrderDTO findById(final Long id) {
		final Optional<Order> optionalOrder = this.orderRepository.findById(id);
		final Order entity = optionalOrder.orElseThrow(() -> new ResourceNotFoundException());
		OrderDTO dto = new OrderDTO(entity);
		return dto;
	}

	@Transactional
	public OrderDTO insert(OrderDTO orderDTO) {
		Order order = new Order();
		order.setMoment(Instant.now());
		order.setStatus(OrderStatus.WAITTING_PAYMENT);
		
		final User user = this.userService.autheticated();
		order.setClient(user);
		
		order.getItems().addAll(orderDTO.getItems().stream().map(orderItemDTO -> {
			Product product = this.productRepository.getReferenceById(orderItemDTO.getProductId());
			return new OrderItem(order, product, orderItemDTO.getQuantity(), product.getPrice());
		}).collect(Collectors.toList()));
		
		this.orderRepository.save(order);
		this.orderItemRepository.saveAll(order.getItems());
		
		return new OrderDTO(order);
	}

}
