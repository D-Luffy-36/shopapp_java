package com.demo.shopapp.domain.order.service;

import com.demo.shopapp.domain.order.entity.Order;
import com.demo.shopapp.domain.order.entity.OrderDetail;
import com.demo.shopapp.domain.order.entity.OrderStatus;
import com.demo.shopapp.domain.payment.entity.Payment;
import com.demo.shopapp.domain.product.entity.Product;
import com.demo.shopapp.domain.user.entity.User;
import com.demo.shopapp.domain.order.dto.OrderDTO;
import com.demo.shopapp.shared.exceptions.DataNotFoundException;
import com.demo.shopapp.domain.order.mappers.OrderMapper;
import com.demo.shopapp.domain.order.repository.OrderDetailRepository;
import com.demo.shopapp.domain.order.repository.OrderRepository;
import com.demo.shopapp.domain.product.repository.ProductRepository;
import com.demo.shopapp.domain.user.repository.UserRepository;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final OrderDetailRepository orderDetailRepository;

    @Transactional
    @Override
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        User existingUser = this.userRepository.findById(orderDTO.getUserId()).orElseThrow(
                () -> new DataNotFoundException("User not found with id: " + orderDTO.getUserId())
        );

        // conver OrderDto -> Order
        Order newOrder = this.orderMapper.toOrder(orderDTO);
        newOrder.setUser(existingUser);
        newOrder.setActive(true);
        newOrder.setStatus(Payment.PaymentStatus.PENDING);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setTotalMoney(orderDTO.getTotalMoney());

        newOrder.setShippingDate(LocalDateTime.now());

        newOrder = this.orderRepository.save(newOrder);

        List<OrderDetail> orderDetails = new ArrayList<>();

        for(int i = 0; i < orderDTO.getCartItems().size(); i++) {
            Product product  = this.productRepository.findById(orderDTO.getCartItems().get(i).getProductId())
                    .orElseThrow(() -> new DataNotFoundException("Product not found"));

            Integer quantity = orderDTO.getCartItems().get(i).getQuantity();
            Double price = product.getPrice() * quantity;

            OrderDetail newOrderDetail = OrderDetail.builder()
                    .order(newOrder)
                    .product(product)
                    .numberOfProduct(quantity)
                    .unitPrice(product.getPrice())
                    .price(price)
                    .build();
            orderDetails.add(newOrderDetail);
        }
        orderDetailRepository.saveAll(orderDetails);
        return newOrder;
    }


    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    public Order getOrderById(long id) {
        return this.orderRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Order not found with id: " + id));
    }

    public Long findOrderIdByPaymentId(String paymentId) {
        Optional<Order> orderOptional = orderRepository.findByPaymentId(
                paymentId
        );
        return orderOptional.map(Order::getId).orElse(null);  // Trả về orderId nếu tìm thấy, hoặc null nếu không
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

    @Transactional
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
            existingOrder.setStatus(Payment.PaymentStatus.PENDING);
        }else {
            existingOrder.setStatus(Payment.PaymentStatus.valueOf(orderDTO.getStatus()));
        }

        return this.orderRepository.save(
                existingOrder
        );
    }


    @Transactional
    @Override
    public void deleteOrder(long id) {
        Order existingOrder = this.orderRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Order not found"));
        existingOrder.setActive(false);
        this.orderRepository.save(existingOrder);
    }

    @Override
    public List<Order> getOrdersByUserId(long id) {
        if(this.userRepository.findById(id).isPresent()) {
            return this.orderRepository.findOrderByUserId(id)
                    .stream()
                    .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                    .toList();
        }
        return Collections.emptyList();
    }

    @Override
    public Page<Order> searchOrders(String keyWord, int page, int limit) {
        if (page <= 0) {
            page = 1; // Đặt giá trị mặc định
        }
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        return this.orderRepository.findByKeyWord(keyWord, pageable);
    }



    public void updateOrderStatustPay(Long orderId, Payment.PaymentStatus status) {
        // Tìm kiếm đơn hàng theo orderId
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        // Cập nhật trạng thái thanh toán cho đơn hàng
        order.setStatus(status);  // Giả sử Order entity có trường `paymentStatus`

        // Lưu đơn hàng đã cập nhật lại
        orderRepository.save(order);
    }
}
