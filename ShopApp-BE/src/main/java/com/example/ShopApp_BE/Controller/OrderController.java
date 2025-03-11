package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.DTO.OrderDTO;
import com.example.ShopApp_BE.DTO.OrderDetailDTO;
import com.example.ShopApp_BE.Service.OrderService;
import com.example.ShopApp_BE.Utils.JwtTokenUtils;
import com.example.ShopApp_BE.Utils.MessageKeys;
import com.example.ShopApp_BE.Utils.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('USER_ROLE')")
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDTO,
                                         @RequestHeader("Authorization") String authorization){
        try {
            orderService.createOrder(orderDTO,
                    jwtTokenUtils.extractPhoneNumber(authorization.substring(7), TokenType.ACCESS));
            return ResponseEntity.accepted().body(MessageKeys.CREATE_ORDER_SUCCESS);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> updateOrder(@RequestBody OrderDTO orderDTO,
                                         @PathVariable("id") Long orderId,
                                         @RequestHeader("Authorization") String authorization){
        try{
            orderService.updateOrder(orderDTO, orderId,
                    jwtTokenUtils.extractPhoneNumber(authorization.substring(7), TokenType.ACCESS));
            return ResponseEntity.accepted().body(MessageKeys.UPDATE_SUCCESS);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getAllOrders(@RequestHeader("Authorization") String authorization,
                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "limit", defaultValue = "5") Integer limit){
        try{
            Pageable pageable = PageRequest.of(page, limit);
            return ResponseEntity.ok().body(orderService.
                    getOrderByUser(jwtTokenUtils.extractPhoneNumber(authorization.substring(7), TokenType.ACCESS),pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
