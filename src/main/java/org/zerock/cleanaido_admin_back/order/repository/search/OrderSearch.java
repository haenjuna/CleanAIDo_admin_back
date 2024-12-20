package org.zerock.cleanaido_admin_back.order.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.cleanaido_admin_back.common.dto.PageRequestDTO;
import org.zerock.cleanaido_admin_back.common.dto.PageResponseDTO;
import org.zerock.cleanaido_admin_back.order.dto.OrderListDTO;
import org.zerock.cleanaido_admin_back.order.dto.OrderDetailListDTO;

import java.util.List;

public interface OrderSearch {
    Page<OrderListDTO> listBySeller(String sellerId, List<String> statuses, Pageable pageable);
    Page<OrderListDTO> list(List<String> statuses, Pageable pageable);
    Page<OrderListDTO> searchByOrderNumber(String keyword, List<String> statuses, Pageable pageable);
    Page<OrderListDTO> searchByProductNumber(String keyword, List<String> statuses, Pageable pageable);
    Page<OrderListDTO> searchByCustomerId(String keyword, List<String> statuses, Pageable pageable);
    Page<OrderListDTO> searchByPhoneNumber(String keyword, List<String> statuses, Pageable pageable);
    Page<OrderListDTO> searchByTrackingNumber(String keyword, List<String> statuses, Pageable pageable);
    PageResponseDTO<OrderDetailListDTO> getOrderDetailList(String sellerId, Long orderNum, PageRequestDTO pageRequestDTO);
}
