package com.demo.shopapp.services.order;

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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        OrderMapper orderMapper) {

        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        User existingUser = this.userRepository.findById(orderDTO.getUserId()).orElseThrow(
                () -> new DataNotFoundException("User not found with id: " + orderDTO.getUserId())
        );

        // conver OrderDto -> Order
        Order newOrder = this.orderMapper.toOrder(orderDTO);
        newOrder.setUser(existingUser);
        newOrder.setActive(true);
        newOrder.setStatus(OrderStatus.PENDING);
        newOrder.setOrderDate(LocalDateTime.now());
        // check shipping date >= ngay hom nay

        // đoạn này cần xử lí phức tạp

        newOrder.setShippingDate(LocalDateTime.now());

        return this.orderRepository.save(newOrder);
    }

    @Override
    public Order getOrderById(long id) {
        return this.orderRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Order not found with id: " + id));
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
    public Order updateOrder(long id, OrderDTO orderDTO) throws Exception{
        Order existingOrder = this.orderRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Order not found"));
        User existingUser = this.userRepository.findById(orderDTO.getUserId()).orElseThrow(() -> new DataNotFoundException("User not found with id: " + orderDTO.getUserId()));

        this.orderMapper.updateOrderFromDTO(orderDTO, existingOrder);

        existingOrder.setUser(existingUser);

        LocalDateTime shippingDate = orderDTO.getShippingDate() == null
                ?  LocalDateTime.now()
                : orderDTO.getShippingDate();

        // nếu ngày shipping date hợp lệ thì set
        if(shippingDate.isAfter(LocalDateTime.now()) && shippingDate.isAfter(existingOrder.getOrderDate())) {
            existingOrder.setShippingDate(shippingDate);
        } else {
            // Xử lý trường hợp ngày giao hàng không hợp lệ, có thể ném ngoại lệ
            throw new Exception("Delivery date must be in the future and after the order date.");
        }

        // Lưu lại đối tượng đã cập nhật
        if(orderDTO.getStatus() == null || orderDTO.getStatus().isEmpty() || !OrderStatus.VALID_STATUSES.contains(orderDTO.getStatus())){
            existingOrder.setStatus(OrderStatus.PENDING);
        }else {
            existingOrder.setStatus(orderDTO.getStatus());
        }

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

    @Override
    public List<Order> getOrdersByUserId(long id) {
        if(this.userRepository.findById(id).isPresent()) {
            return this.orderRepository.findOrderByUserId(id);
        }
        return Collections.emptyList();
    }
}
