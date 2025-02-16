package com.demo.shopapp.repositorys;

import com.demo.shopapp.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Boolean existsByName(String name);
    Optional<Product> findById(long id);

    Page<Product> findAll(Pageable pageable);


//    @Query("select p from Product p where" +
//            "(:categoryId IS NULL OR :categoryId = 0 OR p.category.id = :categoryId)" +
//            "AND(:keyWord IS NULL OR :keyWord = ''OR p.name LIKE %:keyWord% OR p.description LIKE %:keyWord%)")

    @Query(value = "SELECT * FROM products p " +
            "WHERE (:categoryId IS NULL OR :categoryId = 0 OR p.category_id = :categoryId) " +
            "AND (:keyWord IS NULL OR :keyWord = '' OR p.name LIKE CONCAT('%', :keyWord, '%') " +
            "OR p.description LIKE CONCAT('%', :keyWord, '%'))",
            countQuery = "SELECT COUNT(*) FROM products p " +
                    "WHERE (:categoryId IS NULL OR :categoryId = 0 OR p.category_id = :categoryId) " +
                    "AND (:keyWord IS NULL OR :keyWord = '' OR p.name LIKE CONCAT('%', :keyWord, '%') " +
                    "OR p.description LIKE CONCAT('%', :keyWord, '%'))",
            nativeQuery = true)
    Page<Product> searchProducts(@Param("keyWord") String keyWord,
                                 @Param("categoryId") Long categoryId,
                                 Pageable pageable);
    @Query(value = "SELECT p.id, p.name, p.price, p.thumbnail FROM products AS p WHERE p.id IN (:ids) ", nativeQuery = true)
    List<Object[]> findProductByIds(@Param("ids") List<Long> ids);
}


//:categoryId IS NULL: Điều kiện này kiểm tra nếu giá trị của categoryId được truyền vào là NULL.
//Nếu đúng, tất cả các sản phẩm sẽ được lấy mà không phân biệt categoryId.

//:categoryId = 0: Điều kiện này kiểm tra nếu categoryId bằng 0.
//Thường thì categoryId = 0 được dùng để có thể lấy tất cả sản phẩm mà không có sự phân biệt về danh mục.
//Nếu categoryId là 0, mọi sản phẩm sẽ được trả về.

//p.category_id = :categoryId:
//Điều kiện này sẽ chỉ trả về các sản phẩm có category_id bằng với giá trị của categoryId được truyền vào.
