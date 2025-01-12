package com.demo.shopapp.services.OrderServices;

import com.demo.shopapp.dtos.OrderDTO;
import com.demo.shopapp.entities.Order;
import com.demo.shopapp.entities.OrderStatus;
import com.demo.shopapp.entities.User;
import com.demo.shopapp.exceptions.DataNotFoundException;
import com.demo.shopapp.mappers.OrderMapper;
import com.demo.shopapp.repositorys.OrderRepository;
import com.demo.shopapp.repositorys.UserRepository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
                        OrderMapper orderMapper) {

        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public Order createOrder(OrderDTO orderDTO) {
        User existingUser = this.userRepository.findById(orderDTO.getUserId()).orElseThrow(
                () -> new DataNotFoundException("User not found")
        );

        // conver OrderDto -> Order
        Order newOrder = this.orderMapper.toOrder(orderDTO, userRepository);

        newOrder.setActive(true);
        newOrder.setStatus(OrderStatus.PENDING);



        return this.orderRepository.save(newOrder);
    }

    @Override
    public Order getOrderById(long id) {
        return null;
    }


    public Page<Order> getAllOrdersWithActive(int page, int limit) {
        if (page <= 0) {
            page = 1; // Đặt giá trị mặc định
        }

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());

        return this.orderRepository.findAllByActiveIsTrue(pageable);
    }

    @Override
    public Page<Order> getAllOrders(int page, int limit) {
        return null;
    }

    @Override
    public Order updateOrder(long id, OrderDTO orderDTO) {
        Order existingOrder = this.orderRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Order not found"));
        this.orderMapper.updateOrderFromDTO(orderDTO, existingOrder);
        // Lưu lại đối tượng đã cập nhật
        existingOrder.setStatus(OrderStatus.PENDING);
        existingOrder.setActive(true);
        existingOrder.setShippingDate(orderDTO.getShippingDate());

        return this.orderRepository.save(
                existingOrder
        );
    }


    @Override
    public void deleteOrder(long id) {
        Order existingOrder = this.orderRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Order not found"));
        existingOrder.setActive(false);
        this.orderRepository.save(existingOrder);
    }
}
