package com.example.ShopApp_BE.Service;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.DTO.CommentDTO;
import com.example.ShopApp_BE.Model.Entity.CommentEntity;
import com.example.ShopApp_BE.Model.Response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentEntity createComment(CommentDTO commentDTO, String phoneNumber) throws Exception;

    CommentResponse getById(Long id) throws Exception;

    Page<CommentResponse> getByProduct(Long productId, Pageable pageable);

    Page<CommentResponse> getByUser(String phoneNumber, Pageable pageable) throws NotFoundException;

    void deleteById(Long id, String phoneNumber) throws NotFoundException;
}
