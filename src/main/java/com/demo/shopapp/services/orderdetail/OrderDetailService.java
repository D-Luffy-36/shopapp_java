package com.demo.shopapp.services.orderdetail;

import com.demo.shopapp.dtos.request.OrderDetailDTO;
import com.demo.shopapp.entities.Order;
import com.demo.shopapp.domain.product.entity.Product;
import com.demo.shopapp.entities.OrderDetail;
import com.demo.shopapp.shared.exceptions.DataNotFoundException;
import com.demo.shopapp.shared.mappers.OrderDetailMapper;
import com.demo.shopapp.shared.mappers.OrderMapper;
import com.demo.shopapp.repositorys.OrderDetailRepository;
import com.demo.shopapp.repositorys.OrderRepository;
import com.demo.shopapp.domain.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class OrderDetailService implements IOrderDetail{
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;

    public OrderDetailService(OrderDetailRepository orderDetailRepository, OrderRepository orderRepository,
                              ProductRepository productRepository, OrderMapper orderMapper,
                              OrderDetailMapper orderDetailMapper) {
        this.orderDetailRepository = orderDetailRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
    }


    @Transactional
    @Override
    public OrderDetail create(OrderDetailDTO orderDetailDTO) {
        Order existingOrder = this.orderRepository.findById(orderDetailDTO.getOrderId()).orElseThrow(
                ()-> new DataNotFoundException("Order not found with id: " + orderDetailDTO.getOrderId()));
        Product existingProduct = this.productRepository.findById(orderDetailDTO.getProductId()).orElseThrow(
                ()-> new DataNotFoundException("Product not found with id: " + orderDetailDTO.getProductId())
        );
        OrderDetail newOrderDetail = this.orderDetailMapper.toOrderDetail(orderDetailDTO);
        newOrderDetail.setOrder(existingOrder);
        newOrderDetail.setProduct(existingProduct);

        return this.orderDetailRepository.save(newOrderDetail);
    }

    @Transactional
    @Override
    public OrderDetail update(Long id, OrderDetailDTO orderDetailDTO) {
        OrderDetail existingOrderDetail = this.orderDetailRepository.findById(id).orElseThrow(
                ()-> new DataNotFoundException("OrderDetail not found with id: " + id)
        );
        Order existingOrder = this.orderRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Order not found with id: " + id));
        Product existingProduct = this.productRepository.findById(orderDetailDTO.getProductId()).orElseThrow(
                () -> new DataNotFoundException("Product not found with id: " + orderDetailDTO.getProductId())
        );

        this.orderDetailMapper.updateOrderDetailFromOrderDTO(orderDetailDTO, existingOrderDetail);
        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setProduct(existingProduct);
        return this.orderDetailRepository.save(existingOrderDetail);
    }

    @Override
    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId) {
        return this.orderDetailRepository.findByOrderId(orderId);
    }

    @Override
    public OrderDetail getOrderDetailById(Long id) throws Exception {
        return this.orderDetailRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Order Detail not found with id: " + id));
    }

    @Transactional
    @Override
    // xóa cứng
    public void delete(long id) {
        this.orderDetailRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Order Detail not found with id: " + id)
        );
        this.orderDetailRepository.deleteById(id);
    }
}
