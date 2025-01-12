package com.demo.shopapp.controllers;

import com.demo.shopapp.dtos.OrderDTO;
import com.demo.shopapp.entities.Order;

import com.demo.shopapp.mappers.OrderMapper;
import com.demo.shopapp.responses.ListOrderResponse;
import com.demo.shopapp.responses.OrderResponse;
import com.demo.shopapp.services.OrderServices.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }
    @GetMapping()
    public ResponseEntity<?> list(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        Page<Order> orders =  this.orderService.getAllOrdersWithActive(page, limit);

        ListOrderResponse listOrderResponse =  ListOrderResponse.builder()
                .orders(orders.getContent()
                        .stream()
                        .map(order -> orderMapper.toOrderResponse(order))
                        .toList())
                .totalPages(orders.getTotalPages())
                        .build();

        return ResponseEntity.ok(listOrderResponse);
    }

    @GetMapping("/users/{userId}") // id -> path variable lấy id động
    public ResponseEntity<?> getOrdersByUserId(@PathVariable long userId) {
        try{
            return ResponseEntity.ok(
                    orderService.getOrdersByUserId(userId).stream().map(orderMapper::toOrderResponse)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping()
    public ResponseEntity<?> create(@Valid @RequestBody OrderDTO orderDTO, BindingResult result) {

        try{
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError -> FieldError.getDefaultMessage())
                        .toList();

                return ResponseEntity.badRequest().body("error = " + errorMessages.toString());
            }
            Order newOrder = this.orderService.createOrder(orderDTO);

//            OrderResponse.fromOrder(newOrder);

            OrderResponse newOrderResponse = orderMapper.toOrderResponse(newOrder);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Order created successfully",
                    "data", newOrderResponse
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    // nếu tham số truyền vào là 1 object
    public ResponseEntity<?> update(
            @PathVariable long id,
            @RequestBody OrderDTO orderDTO) {
        try{
            Order updatedOrder = this.orderService.updateOrder(id, orderDTO);
            OrderResponse updatedOrderResponse = orderMapper.toOrderResponse(updatedOrder);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Order updated successfully",
                    "data", updatedOrderResponse
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // delete soft => active false
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
       try{
           this.orderService.deleteOrder(id);
           return ResponseEntity.ok(Map.of(
                   "status", "success",
                   "message", "Order deleted successfully with id: " + id
           ));
       }catch (Exception e) {
           return ResponseEntity.badRequest().body(e.getMessage());
       }

    }


}
