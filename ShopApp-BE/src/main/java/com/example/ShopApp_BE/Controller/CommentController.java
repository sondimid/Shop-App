package com.example.ShopApp_BE.Controller;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.DTO.CommentDTO;
import com.example.ShopApp_BE.Model.Response.CommentResponse;
import com.example.ShopApp_BE.Model.Response.PageResponse;
import com.example.ShopApp_BE.Service.CommentService;
import com.example.ShopApp_BE.Utils.JwtTokenUtils;
import com.example.ShopApp_BE.Utils.MessageKeys;
import com.example.ShopApp_BE.Utils.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("${api.prefix}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("")
    public ResponseEntity<?> createComment(@RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization,
                                            @ModelAttribute CommentDTO commentDTO) throws Exception {

        String email = jwtTokenUtils.extractEmail(authorization.substring(7), TokenType.ACCESS);
        commentService.createComment(commentDTO, email);
        return ResponseEntity.accepted().body(MessageKeys.CREATE_COMMENT_SUCCESS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable("id") Long id) throws Exception {
        return ResponseEntity.ok().body(commentService.getById(id));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getCommentsByUser(@RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization,
                                               @RequestParam(name = "page", defaultValue = "0") Integer page,
                                               @RequestParam(name = "limit", defaultValue = "5") Integer limit) throws NotFoundException {
        String email = jwtTokenUtils.extractEmail(authorization.substring(7), TokenType.ACCESS);
        Pageable pageable = PageRequest.of(page, limit);
        Page<CommentResponse> comments = commentService.getByUser(email, pageable);
        return ResponseEntity.ok().body(PageResponse.builder()
                .pageSize(comments.getSize())
                .pageNumber(comments.getNumber())
                .totalElements(comments.getTotalElements())
                .totalPages(comments.getTotalPages())
                .content(comments.getContent()).build());
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<?> getCommentsByProduct(@PathVariable("productId") Long productId,
                                                  @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                  @RequestParam(name = "limit", defaultValue = "5") Integer limit){
        Pageable pageable = PageRequest.of(page, limit);
        Page<CommentResponse> commentResponsePage = commentService.getByProduct(productId, pageable);
        return ResponseEntity.ok().body(PageResponse.builder()
                .content(commentResponsePage.getContent())
                .totalPages(commentResponsePage.getTotalPages())
                .totalElements(commentResponsePage.getTotalElements())
                .pageNumber(commentResponsePage.getNumber())
                .pageSize(commentResponsePage.getSize())
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") Long id,
                                           @RequestHeader(MessageKeys.AUTHORIZATION_HEADER) String authorization) throws NotFoundException {
        String email = jwtTokenUtils.extractEmail(authorization.substring(7), TokenType.ACCESS);
        commentService.deleteById(id, email);
        return ResponseEntity.accepted().body(MessageKeys.DELETE_COMMENT_SUCCESS);
    }
}
