package com.demo.shopapp.controllers;

import com.demo.shopapp.dtos.request.OrderDTO;
import com.demo.shopapp.entities.Order;

import com.demo.shopapp.mappers.OrderDetailMapper;
import com.demo.shopapp.mappers.OrderMapper;
import com.demo.shopapp.dtos.responses.ResponseObject;
import com.demo.shopapp.dtos.responses.order.ListOrderResponse;
import com.demo.shopapp.dtos.responses.order.OrderResponse;
import com.demo.shopapp.services.order.OrderService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;


    // just admin can see all orders
    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> list(
            @RequestParam (value = "keyWord", required = false, defaultValue = "") String keyWord,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit
    ) {
        Page<Order> orders =  this.orderService.searchOrders(keyWord, page, limit);

        ListOrderResponse listOrderResponse = ListOrderResponse.builder()
                .orders(orders.getContent()
                        .stream()
                        .map(order -> orderMapper.toOrderResponse(order))
                        .toList())
                .totalPages(orders.getTotalPages())
                .build();

        return ResponseEntity.ok(listOrderResponse);
    }

    @GetMapping("/users/{userId}") // id -> path variable lấy id động
    public ResponseObject<?> getOrdersByUserId(@PathVariable long userId) {
        try{
            return ResponseObject.<List<OrderResponse>>builder()
                    .data(
                            this.orderService.getOrdersByUserId(userId)
                                    .stream()
                                    .map(this.orderMapper::toOrderResponse)
                                    .toList()
                    )
                    .status(HttpStatus.OK)
                    .build();

        } catch (Exception e) {
            return  ResponseObject.<OrderResponse>builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @GetMapping("/{id}")
    public ResponseObject<?> getOrderById(@PathVariable long id) {
        try{
            Order order = this.orderService.getOrderById(id);
            OrderResponse orderResponse = this.orderMapper.toOrderResponse(order);

            orderResponse.setOrderDetailResponses(
                    order.getOrderDetails()
                            .stream()
                            .map(orderDetailMapper::toOrderDetailResponse)
                            .toList()
            );
            return   ResponseObject.<OrderResponse>builder()
                        .status(HttpStatus.OK)
                        .data(orderResponse)
                        .build();

        } catch (Exception e) {
            return  ResponseObject.<OrderResponse>builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @PostMapping()
    public ResponseObject<OrderResponse> create(@Valid @RequestBody OrderDTO orderDTO, BindingResult result) {

        try{
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError -> FieldError.getDefaultMessage())
                        .toList();

                return  ResponseObject.<OrderResponse>builder()
                        .message(errorMessages.toString())
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
            }

            Order newOrder = this.orderService.createOrder(orderDTO);
//            OrderResponse.fromOrder(newOrder);
            OrderResponse newOrderResponse = orderMapper.toOrderResponse(newOrder);
            return  ResponseObject.<OrderResponse>builder()
                    .message("succesfully created new order")
                    .status(HttpStatus.CREATED)
                    .data(newOrderResponse)
                    .build();

        } catch (Exception e) {
            return ResponseObject.<OrderResponse>builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
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








