package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.DTO.OrderDTO;
import com.example.ShopApp_BE.DTO.OrderDetailDTO;
import com.example.ShopApp_BE.DTO.StatusDTO;
import com.example.ShopApp_BE.Model.Entity.OrderEntity;
import com.example.ShopApp_BE.Model.Response.OrderResponse;
import com.example.ShopApp_BE.Model.Response.PageResponse;
import com.example.ShopApp_BE.Service.MailService;
import com.example.ShopApp_BE.Service.OrderService;
import com.example.ShopApp_BE.Utils.JwtTokenUtils;
import com.example.ShopApp_BE.Utils.MessageKeys;
import com.example.ShopApp_BE.Utils.TokenType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.type.PaymentLinkData;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final JwtTokenUtils jwtTokenUtils;
    private final MailService mailService;
    private final PayOS payOS;

    @PutMapping("/success")
    public ResponseEntity<?> createOrder(HttpServletRequest request) throws Exception {
        Cookie cookie = Arrays.stream(request.getCookies())
                .filter(cookie1 -> cookie1.getName().equals(MessageKeys.ORDER_CODE))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(MessageKeys.ORDER_NOT_FOUND));
        Long orderCode = Long.valueOf(cookie.getValue());
        PaymentLinkData paymentLinkData = payOS.getPaymentLinkInformation(orderCode);
        log.info("paymentLinkData: {}", paymentLinkData);
        log.info(paymentLinkData.getStatus());
        if(paymentLinkData.getStatus().equalsIgnoreCase("PAID")) {
            orderService.confirmOrder(Long.valueOf(cookie.getValue()));
            return ResponseEntity.accepted().body(MessageKeys.CREATE_ORDER_SUCCESS);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateOrder(@RequestBody OrderDTO orderDTO,
                                         @PathVariable("id") Long orderId) throws Exception {


        orderService.updateOrder(orderDTO, orderId);
        return ResponseEntity.accepted().body(MessageKeys.UPDATE_SUCCESS);

    }

    @GetMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getAllOrders(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "limit", defaultValue = "20") Integer limit) throws Exception {

        Pageable pageable = PageRequest.of(page, limit);
        Page<OrderResponse> pageResponse = orderService.getOrderByUser(pageable);
        return ResponseEntity.ok().body(PageResponse.builder()
                .content(pageResponse.getContent())
                .totalPages(pageResponse.getTotalPages())
                .totalElements(pageResponse.getTotalElements())
                .pageNumber(pageResponse.getNumber())
                .pageSize(pageResponse.getSize())
                .build());

    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(@PathVariable(name = "orderId") Long orderId,
                                               @RequestBody StatusDTO statusDTO) throws Exception {

        mailService.sendEmailOrder(orderService.setStatus(orderId, statusDTO.getStatus()));
        return ResponseEntity.accepted().body(MessageKeys.UPDATE_SUCCESS);

    }

    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> cancelOrder(@PathVariable(name = "orderId") Long orderId) throws Exception {
        OrderEntity orderEntity = orderService.cancelOrder(orderId);
        return ResponseEntity.accepted().body(MessageKeys.UPDATE_SUCCESS);

    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable(name = "orderId") Long orderId) throws Exception {

        return ResponseEntity.ok().body(orderService.getById(orderId));

    }

    @DeleteMapping("/{orderIds}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteOrders(@PathVariable(name = "orderIds") List<Long> orderIds) throws Exception {

        orderService.deleteOrders(orderIds);
        return ResponseEntity.accepted().body(MessageKeys.DELETE_ORDER_SUCCESS);

    }

    @GetMapping("/{keyword}/keyword")
    public ResponseEntity<?> getOrderByStatus(@PathVariable(name = "keyword") String keyword,
                                              @RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization,
                                              @RequestParam(name = "page", defaultValue = "0") Integer page,
                                              @RequestParam(name = "limit", defaultValue = "10") Integer limit) throws Exception {
        Sort.Direction sort = Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, limit, sort, "createdAt");
        Page<OrderResponse> pageResponse = orderService.getByKeyWord(keyword,pageable);

        return ResponseEntity.ok().body(PageResponse.builder()
                .content(pageResponse.getContent())
                .pageSize(pageResponse.getSize())
                .pageNumber(pageResponse.getNumber())
                .totalElements(pageResponse.getTotalElements())
                .totalPages(pageResponse.getTotalPages()).build());
    }
}
