package com.demo.shopapp.domain.order.repository;

import com.demo.shopapp.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // tìm các đơn hàng của 1 user nào đó
    List<Order> findOrderByUserId(Long userId);

    Page<Order> findAllByActiveIsTrue(Pageable pageable);

    Optional<Order> findByPaymentId(String paymentId);


    // null or empty will select all
    @Query("SELECT o FROM Order o WHERE" +
            ":keyWord IS NULL OR :keyWord = '' OR " +
            " o.address LIKE %:keyWord% OR " +
            " o.phoneNumber LIKE %:keyWord% OR " +
            " o.email LIKE %:keyWord% OR " +
            " o.fullName LIKE %:keyWord% OR " +
            " o.note LIKE %:keyWord% OR " +
            " o.shippingMethod LIKE %:keyWord%" )
//    @Query(value = "SELECT * FROM orders o WHERE " +
//            "(:keyWord IS NULL OR :keyWord = '' OR " +
//            "o.address LIKE CONCAT('%', :keyWord, '%') OR " +
//            "o.phone_number LIKE CONCAT('%', :keyWord, '%') OR " +
//            "o.email LIKE CONCAT('%', :keyWord, '%') OR " +
//            "o.note LIKE CONCAT('%', :keyWord, '%') OR " +
//            "o.status LIKE CONCAT('%', :keyWord, '%') OR " +
//            "o.shipping_method LIKE CONCAT('%', :keyWord, '%') OR " +
//            "o.payment_method LIKE CONCAT('%', :keyWord, '%'))",
//            countQuery = "SELECT COUNT(*) FROM orders o WHERE " +
//                    "(:keyWord IS NULL OR :keyWord = '' OR " +
//                    "o.address LIKE CONCAT('%', :keyWord, '%') OR " +
//                    "o.phone_number LIKE CONCAT('%', :keyWord, '%') OR " +
//                    "o.email LIKE CONCAT('%', :keyWord, '%') OR " +
//                    "o.full_name LIKE CONCAT('%', :keyWord, '%') OR " +
//                    "o.note LIKE CONCAT('%', :keyWord, '%') OR " +
//                    "o.status LIKE CONCAT('%', :keyWord, '%') OR " +
//                    "o.shipping_method LIKE CONCAT('%', :keyWord, '%') OR " +
//                    "o.payment_method LIKE CONCAT('%', :keyWord, '%'))",
//            nativeQuery = true)
    Page<Order> findByKeyWord(@Param("keyWord") String keyWord, Pageable pageable);

}



//    @Query("select p from Product p where" +
//            "(:categoryId IS NULL OR :categoryId = 0 OR p.category.id = :categoryId)" +
//            "AND(:keyWord IS NULL OR :keyWord = ''OR p.name LIKE %:keyWord% OR p.description LIKE %:keyWord%)")


//
//@Query(value = "SELECT * FROM products p " +
//        "WHERE (:categoryId IS NULL OR :categoryId = 0 OR p.category_id = :categoryId) " +
//        "AND (:keyWord IS NULL OR :keyWord = '' OR p.name LIKE CONCAT('%', :keyWord, '%') " +
//        "OR p.description LIKE CONCAT('%', :keyWord, '%'))",
//        countQuery = "SELECT COUNT(*) FROM products p " +
//                "WHERE (:categoryId IS NULL OR :categoryId = 0 OR p.category_id = :categoryId) " +
//                "AND (:keyWord IS NULL OR :keyWord = '' OR p.name LIKE CONCAT('%', :keyWord, '%') " +
//                "OR p.description LIKE CONCAT('%', :keyWord, '%'))",



//
//Create PROCEDURE SearchOrders
//        @keyWord NVARCHAR(255) = NULL
//        As
//BEGIN
//SET NOCOUNT ON;
//
//SELECT *
//FROM orders o
//        WHERE
//@keyWord IS NULL OR @keyWord = '' OR
//o.address LIKE '%' + @keyWord + '%' OR
//o.phone_number LIKE '%' + @keyWord + '%' OR
//o.email LIKE '%' + @keyWord + '%' OR
//o.fullname LIKE '%' + @keyWord + '%' OR
//o.note LIKE '%' + @keyWord + '%' OR
//o.status LIKE '%' + @keyWord + '%' OR
//o.shipping_method LIKE '%' + @keyWord + '%' OR
//o.payment_method LIKE '%' + @keyWord + '%';
//END;
//
//EXEC SearchOrders;



//SELECT definition
//FROM sys.sql_modules
//WHERE object_id = OBJECT_ID('SearchOrders');
