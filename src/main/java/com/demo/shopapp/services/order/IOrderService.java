package com.demo.shopapp.services.order;


import com.demo.shopapp.dtos.request.OrderDTO;
import com.demo.shopapp.entities.Order;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IOrderService {
    Order createOrder(OrderDTO orderDTO) throws Exception;
    Order getOrderById(long id) throws Exception;
    Page<Order> getAllOrders(int page, int limit);
    Order updateOrder(long id, OrderDTO orderDTO) throws Exception;
    void deleteOrder(long id);
    List<Order> getOrdersByUserId(long id);
    Page<Order> searchOrders(String keyWord, int page, int limit);
}

//
//Product createProduct(ProductDTO productDTO) throws Exception;
//Page<Product> getAllProducts(int page, int limit);
//
//Product getProductById(Long id) throws Exception;
//Product updateProduct(Long id, ProductDTO productDTO) throws Exception;
//void deleteProduct(Long id) throws Exception;
//ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO);
