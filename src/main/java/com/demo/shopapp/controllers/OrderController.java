package com.demo.shopapp.controllers;

import com.demo.shopapp.dtos.OrderDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderController {
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

    @GetMapping("/{userId}") // id -> path variable lấy id động
    public ResponseEntity<String> getOrdersByUserId(@PathVariable long userId) {
        return
                ResponseEntity.ok(
                        "orders of: " + userId
                );
    }




    @PostMapping()
    public ResponseEntity<String> create(@Valid @RequestBody OrderDTO orderDTO, BindingResult result) {

        try{
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError -> FieldError.getDefaultMessage())
                        .toList();
                return ResponseEntity.badRequest().body("error = " + errorMessages.toString());
            }
            return ResponseEntity.ok("create orders: " + orderDTO.toString());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    // nếu tham số truyền vào là 1 object
    public ResponseEntity<String> update(
            @PathVariable long id,
            OrderDTO orderDTO) {

        return
                ResponseEntity.ok(
                        "update category: " + id
                );
    }

    // delete soft => active false
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        return
                ResponseEntity.ok("delete order: " + id);
    }


}
