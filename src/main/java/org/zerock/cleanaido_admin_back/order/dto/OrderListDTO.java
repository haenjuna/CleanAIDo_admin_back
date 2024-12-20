package org.zerock.cleanaido_admin_back.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.cleanaido_admin_back.order.entity.Order;
import org.zerock.cleanaido_admin_back.order.entity.OrderDetail;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.zerock.cleanaido_admin_back.order.entity.QOrderDetail.orderDetail;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderListDTO {

    private Long orderNumber;
    private String customerId;
    private String phoneNumber;
    private String deliveryAddress;
    private Integer totalPrice;
    private LocalDateTime orderDate;
    private String trackingNumber;
    private String orderStatus;
    private List<Long> productNumbers;
    private List<Integer> quantities; // 각 제품의 수량 리스트
    private List<Integer> prices;    // 각 제품의 가격 리스트

    public OrderListDTO(Order order) {
        this.orderNumber = order.getOrderNumber();
        this.customerId = order.getCustomerId();
        this.phoneNumber = order.getPhoneNumber();
        this.deliveryAddress = order.getDeliveryAddress();
        this.totalPrice = order.getTotalPrice();
        this.orderDate = order.getOrderDate();
        this.trackingNumber = order.getTrackingNumber();
        this.orderStatus = order.getOrderStatus();

        // Product의 pno 추출
        this.productNumbers = order.getOrderDetails().stream()
                .map(orderDetail -> orderDetail.getProduct().getPno())
                .collect(Collectors.toList());

        // 수량 추출
        this.quantities = order.getOrderDetails().stream()
                .map(OrderDetail::getQuantity)
                .collect(Collectors.toList());

        // 가격 추출
        this.prices = order.getOrderDetails().stream()
                .map(OrderDetail::getPrice)
                .collect(Collectors.toList());
    }
}