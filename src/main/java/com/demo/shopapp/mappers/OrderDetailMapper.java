package com.demo.shopapp.mappers;

import com.demo.shopapp.dtos.OrderDetailDTO;

import com.demo.shopapp.entities.OrderDetail;
import com.demo.shopapp.responses.orderDetail.OrderDetailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    OrderDetail toOrderDetail(OrderDetailDTO orderDetailDTO);

    // Ánh xạ từ OrderDetail sang OrderDetailResponse
    @Mapping(source = "order.id", target = "orderId")               // Lấy orderId từ đối tượng Order
    @Mapping(source = "product.name", target = "productName")
    OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail);

    @Mapping(target = "id", ignore = true) // Không ghi đè id
    void updateOrderDetailFromOrderDTO(OrderDetailDTO orderDetailDTO, @MappingTarget OrderDetail orderDetail);

}

