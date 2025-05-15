package com.demo.shopapp.domain.order.mappers;


import com.demo.shopapp.domain.order.dto.OrderDTO;
import com.demo.shopapp.domain.order.entity.Order;
import com.demo.shopapp.domain.order.dto.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")

public interface OrderMapper {
    // Ánh xạ từ OrderDTO sang Order
    Order toOrder(OrderDTO orderDTO);

    // Ánh xạ từ Order sang OrderResponse
    @Mapping(source = "user.id" , target = "userId")
    OrderResponse toOrderResponse(Order order);

    @Mapping(target = "id", ignore = true) // Không ghi đè id
    void updateOrderFromDTO(OrderDTO orderDTO, @MappingTarget Order order);
}
