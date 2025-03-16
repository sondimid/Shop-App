package com.example.ShopApp_BE.Controller;

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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final JwtTokenUtils jwtTokenUtils;
    private final MailService mailService;

    @PostMapping("")
    @PreAuthorize("hasRole('USER_ROLE')")
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDTO,
                                         @RequestHeader("Authorization") String authorization){
        try {
            OrderEntity orderEntity = orderService.createOrder(orderDTO,
                    jwtTokenUtils.extractPhoneNumber(authorization.substring(7), TokenType.ACCESS));
            mailService.sendEmailOrder(orderEntity);
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
            Page<OrderResponse> pageResponse = orderService.
                    getOrderByUser(jwtTokenUtils.extractPhoneNumber(authorization.substring(7), TokenType.ACCESS),pageable);
            return ResponseEntity.ok().body(PageResponse.builder()
                    .content(Arrays.asList(pageResponse.getContent().toArray()))
                    .totalPages(pageResponse.getTotalPages())
                    .totalElements(pageResponse.getTotalElements())
                    .pageNumber(pageResponse.getNumber())
                    .pageSize(pageResponse.getSize())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateOrderStatus(@PathVariable(name = "orderId") Long orderId,
                                               @RequestBody StatusDTO statusDTO){
        try{
            mailService.sendEmailOrder(orderService.setStatus(orderId, statusDTO.getStatus()));
            return ResponseEntity.accepted().body(MessageKeys.UPDATE_SUCCESS);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> cancelOrder(@PathVariable(name = "orderId") Long orderId,
                                         @RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization){
        try{
            OrderEntity orderEntity = orderService.cancelOrder(jwtTokenUtils.
                    extractPhoneNumber(authorization.substring(7), TokenType.ACCESS), orderId);
            mailService.sendEmailOrder(orderEntity);
            return ResponseEntity.accepted().body(MessageKeys.UPDATE_SUCCESS);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable(name = "orderId") Long orderId,
                                      @RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization){
        try{
            return ResponseEntity.ok().body(orderService.getById(orderId,
                    jwtTokenUtils.extractPhoneNumber(authorization.substring(7), TokenType.ACCESS)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{orderIds}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteOrders(@PathVariable(name = "orderIds") List<Long> orderIds){
        try{
            orderService.deleteOrders(orderIds);
            return ResponseEntity.accepted().body(MessageKeys.DELETE_ORDER_SUCCESS);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{keyword}/keyword")
    public ResponseEntity<?> getOrderByStatus(@PathVariable(name = "keyword") String keyword,
                                              @RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization,
                                              @RequestParam(name = "page", defaultValue = "0") Integer page,
                                              @RequestParam(name = "limit", defaultValue = "5") Integer limit){
        try{
            Pageable pageable = PageRequest.of(page, limit);
            Page<OrderResponse> pageResponse = orderService.getByKeyWord(keyword,
                    jwtTokenUtils.extractPhoneNumber(authorization.substring(7), TokenType.ACCESS),pageable);

            return ResponseEntity.ok().body(PageResponse.builder()
                    .content(Arrays.asList(pageResponse.getContent().toArray()))
                    .pageSize(pageResponse.getSize())
                    .pageNumber(pageResponse.getNumber())
                    .totalElements(pageResponse.getTotalElements())
                    .totalPages(pageResponse.getTotalPages()).build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
