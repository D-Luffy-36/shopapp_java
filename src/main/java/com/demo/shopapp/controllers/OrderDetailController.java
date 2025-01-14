package com.demo.shopapp.controllers;

import com.demo.shopapp.dtos.OrderDTO;
import com.demo.shopapp.dtos.OrderDetailDTO;
import com.demo.shopapp.entities.OrderDetail;
import com.demo.shopapp.mappers.OrderDetailMapper;
import com.demo.shopapp.responses.ResponseObject;
import com.demo.shopapp.responses.order.OrderResponse;
import com.demo.shopapp.responses.orderDetail.OrderDetailResponse;
import com.demo.shopapp.services.order.OrderService;
import com.demo.shopapp.services.orderdetail.OrderDetailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("${api.prefix}/order_details")
public class OrderDetailController {
    private final OrderDetailService orderDetailService;
    private final OrderService orderService;
    private final OrderDetailMapper orderDetailMapper;

    public OrderDetailController(OrderDetailService orderDetailService,
                                 OrderService orderService,
                                 OrderDetailMapper orderDetailMapper) {
        this.orderDetailService = orderDetailService;
        this.orderService = orderService;
        this.orderDetailMapper = orderDetailMapper;
    }

    @PostMapping()
    public ResponseObject<OrderDetailResponse> create(@Valid @RequestBody OrderDetailDTO orderDetailDTO,
                                                      BindingResult result) {
        try{
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError -> FieldError.getDefaultMessage())
                        .toList();

                return ResponseObject.<OrderDetailResponse>builder()
                        .message(errorMessages.toString())
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
            }

            OrderDetail newOrderDetail = this.orderDetailService.create(orderDetailDTO);
            OrderDetailResponse newOrderDetailResponse = this.orderDetailMapper.toOrderDetailResponse(newOrderDetail);
            return  ResponseObject.<OrderDetailResponse>builder()
                    .message("created orderdetail succesfully")
                    .status(HttpStatus.CREATED)
                    .data(newOrderDetailResponse)
                    .build();

        } catch (Exception e) {
            return ResponseObject.<OrderDetailResponse>builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

    }


    // lấy 1 order detail từ id
    @GetMapping("/{id}")
    public ResponseObject<OrderDetailResponse> getOrderDetailById(@PathVariable long id) {
        try{
            OrderDetail orderDetail = this.orderDetailService.getOrderDetailById(id);
            OrderDetailResponse orderDetailResponse = this.orderDetailMapper.toOrderDetailResponse(orderDetail);
            return  ResponseObject.<OrderDetailResponse>builder()
                    .message("succesfully")
                    .status(HttpStatus.OK)
                    .data(orderDetailResponse)
                    .build();
        } catch (Exception e) {
            return ResponseObject.<OrderDetailResponse>builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    // lấy order detail từ order
    @GetMapping("/order/{id}") // id -> path variable lấy id động
    public ResponseObject<List<OrderDetailResponse>> getOrderDetailsByOrderId(@Valid @PathVariable long id) {
        try{
            List<OrderDetailResponse> orderDetailResponses = this.orderDetailService
                    .getOrderDetailsByOrderId(id)
                    .stream()
                    .map(orderDetailMapper::toOrderDetailResponse)
                    .toList();

            return  ResponseObject.<List<OrderDetailResponse>>builder()
                    .message("succesfully")
                    .status(HttpStatus.OK)
                    .data(orderDetailResponses)
                    .build();
        }catch (Exception e) {
            return  ResponseObject.<List<OrderDetailResponse>>builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

    }



    @PutMapping("/{id}")
    // nếu tham số truyền vào là 1 object
    public ResponseObject<OrderDetailResponse> update(
            @PathVariable long id,
            @Valid @RequestBody OrderDetailDTO newOrderDetailDTO) {
        try{
            OrderDetail existingOrderDetail = this.orderDetailService.update(id, newOrderDetailDTO);
            OrderDetailResponse orderDetailResponse = this.orderDetailMapper.toOrderDetailResponse(existingOrderDetail);
            return  ResponseObject.<OrderDetailResponse>builder()
                    .message("succesfully")
                    .status(HttpStatus.OK)
                    .data(orderDetailResponse)
                    .build();

        } catch (Exception e) {
            return ResponseObject.<OrderDetailResponse>builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseObject<OrderDetailResponse> delete(@PathVariable long id) {
        try{
            this.orderDetailService.delete(id);
            return  ResponseObject.<OrderDetailResponse>builder()
                    .message("deleted orderdetail with id: " + id)
                    .status(HttpStatus.OK)
                    .build();
        } catch (Exception e) {
            return  ResponseObject.<OrderDetailResponse>builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }
}
