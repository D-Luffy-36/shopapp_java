package com.demo.shopapp.services.orderdetail;

import com.demo.shopapp.dtos.request.OrderDetailDTO;
import com.demo.shopapp.entities.OrderDetail;

import java.util.List;

public interface IOrderDetail {
    OrderDetail create(OrderDetailDTO orderDetailDTO) throws Exception;
    OrderDetail update(Long id, OrderDetailDTO orderDetailDTO) throws Exception;
    List<OrderDetail> getOrderDetailsByOrderId(Long orderId) throws Exception;
    OrderDetail getOrderDetailById(Long id) throws Exception;
    void delete(long id) throws Exception;
}

//public interface IOrderService {
//    Order createOrder(OrderDTO orderDTO) throws Exception;
//    Order getOrderById(long id) throws Exception;
//    Page<Order> getAllOrders(int page, int limit);
//    Order updateOrder(long id, OrderDTO orderDTO) throws Exception;
//    void deleteOrder(long id);
//    List<Order> getOrdersByUserId(long id);
//}