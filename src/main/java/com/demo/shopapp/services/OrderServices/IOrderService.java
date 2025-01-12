package com.demo.shopapp.services.OrderServices;


import com.demo.shopapp.dtos.OrderDTO;
import com.demo.shopapp.dtos.ProductDTO;
import com.demo.shopapp.dtos.ProductImageDTO;
import com.demo.shopapp.entities.Order;
import com.demo.shopapp.entities.Product;
import com.demo.shopapp.entities.ProductImage;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IOrderService {
    Order createOrder(OrderDTO orderDTO);
    Order getOrderById(long id);
    Page<Order> getAllOrders(int page, int limit);
    Order updateOrder(long id, OrderDTO orderDTO);
    void deleteOrder(long id);
}

//
//Product createProduct(ProductDTO productDTO) throws Exception;
//Page<Product> getAllProducts(int page, int limit);
//
//Product getProductById(Long id) throws Exception;
//Product updateProduct(Long id, ProductDTO productDTO) throws Exception;
//void deleteProduct(Long id) throws Exception;
//ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO);
