package org.zerock.cleanaido_admin_back.order.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_number", unique = true, nullable = false)
    private Long orderNumber;

    @Column(name = "customer_id", length = 255, nullable = false)
    private String customerId;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Column(name = "delivery_address", nullable = false, length = 255)
    private String deliveryAddress;

    @Column(name = "delivery_message", length = 255)
    private String deliveryMessage;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "tracking_number", length = 255)
    private String trackingNumber;

    @Column(name = "order_status", length = 50)
    private String orderStatus;

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public void addOrderDetail(OrderDetail orderDetail) {
        this.orderDetails.add(orderDetail);
        orderDetail.setOrder(this);
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
