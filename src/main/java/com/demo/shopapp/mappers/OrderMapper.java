package com.demo.shopapp.mappers;


import com.demo.shopapp.dtos.request.OrderDTO;
import com.demo.shopapp.entities.Order;
import com.demo.shopapp.dtos.responses.order.OrderResponse;
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
