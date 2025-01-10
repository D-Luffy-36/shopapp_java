package com.demo.shopapp.controllers;

import com.demo.shopapp.dtos.OrderDTO;
import com.demo.shopapp.dtos.OrderDetailDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("${api.prefix}/order_details")
public class OrderDetailController {
    @GetMapping()
    public ResponseEntity<String> list(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        return
                ResponseEntity.ok(
                        "page = " + page + " limit = " + limit
                );
    }

    // lấy 1 order detail từ id
    @GetMapping("/{id}") // id -> path variable lấy id động
    public ResponseEntity<String> getOrderDetailById(@PathVariable long id) {
        return
                ResponseEntity.ok(
                        "orders_detail with:  " + id
                );
    }

    // lấy order detail từ order
    @GetMapping("/order/{id}") // id -> path variable lấy id động
    public ResponseEntity<String> detail(@Valid @PathVariable long id) {
        return
                ResponseEntity.ok(
                        "orders details : " + id
                );
    }

    @PostMapping()
    public ResponseEntity<String> create(@Valid @RequestBody OrderDetailDTO orderDetailDTO,
                                         BindingResult result) {
        try{
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError -> FieldError.getDefaultMessage())
                        .toList();
                return ResponseEntity.badRequest().body("error = " + errorMessages.toString());
            }
            return ResponseEntity.ok("create orders detail: " + orderDetailDTO.toString());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    // nếu tham số truyền vào là 1 object
    public ResponseEntity<String> update(
            @PathVariable long id,
            @Valid @RequestBody OrderDetailDTO newOrderDetailDTO) {

        return
                ResponseEntity.ok(
                        "update order detail: " + newOrderDetailDTO.toString());
    }

    // delete soft => active false
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        return
                ResponseEntity.ok("delete order: " + id);
    }
}
