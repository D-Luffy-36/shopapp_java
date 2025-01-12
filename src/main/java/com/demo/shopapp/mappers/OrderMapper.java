package com.demo.shopapp.mappers;


import com.demo.shopapp.dtos.OrderDTO;
import com.demo.shopapp.entities.Order;
import com.demo.shopapp.entities.User;
import com.demo.shopapp.repositorys.UserRepository;
import com.demo.shopapp.responses.OrderResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "userId", target = "user")
    Order toOrder(OrderDTO orderDTO,@Context UserRepository userRepository);

    default User map(Long userId, @Context UserRepository userRepository) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).orElseThrow(()-> new RuntimeException("cant not find user")); // Tìm User từ database
    }

    @Mapping(source = "user.id", target = "userId")
    OrderResponse toOrderResponse(Order order);

    // Phương thức ánh xạ từ OrderDTO vào Order, cập nhật các trường
    void updateOrderFromDTO(OrderDTO orderDTO, @MappingTarget Order order);
}
