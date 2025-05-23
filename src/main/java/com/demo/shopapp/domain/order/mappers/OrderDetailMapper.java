package com.demo.shopapp.domain.order.mappers;

import com.demo.shopapp.domain.order.dto.OrderDetailDTO;

import com.demo.shopapp.domain.order.entity.OrderDetail;
import com.demo.shopapp.domain.order.dto.OrderDetailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    OrderDetail toOrderDetail(OrderDetailDTO orderDetailDTO);// Ánh xạ từ OrderDetail sang OrderDetailResponse
//    @Mapping(source = "order.id", target = "orderId")               // Lấy orderId từ đối tượng Order
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.thumbnail", target = "productThumbnail")
    OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail);

    @Mapping(target = "id", ignore = true) // Không ghi đè id
    void updateOrderDetailFromOrderDTO(OrderDetailDTO orderDetailDTO, @MappingTarget OrderDetail orderDetail);

}

