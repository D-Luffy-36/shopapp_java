package com.demo.shopapp.repositorys;

import com.demo.shopapp.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // tìm các đơn hàng của 1 user nào đó
    List<Order> findOrderByUserId(Long userId);

    Page<Order> findAllByActiveIsTrue(Pageable pageable);


    Page<Order> findAll(Pageable pageable);


}
