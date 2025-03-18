package com.example.ShopApp_BE.Service.Impl;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.UnauthorizedAccessException;
import com.example.ShopApp_BE.DTO.CommentDTO;
import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import com.example.ShopApp_BE.Model.Entity.CommentEntity;
import com.example.ShopApp_BE.Model.Entity.ProductEntity;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Model.Response.CommentResponse;
import com.example.ShopApp_BE.Repository.CommentRepository;
import com.example.ShopApp_BE.Repository.ProductRepository;
import com.example.ShopApp_BE.Repository.UserRepository;
import com.example.ShopApp_BE.Service.CommentService;
import com.example.ShopApp_BE.Utils.MessageKeys;
import com.example.ShopApp_BE.Utils.UploadImages;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    @Override
    public CommentEntity createComment(CommentDTO commentDTO, String phoneNumber) throws Exception {
        UserEntity userEntity = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        ProductEntity productEntity = productRepository.findById(commentDTO.getProductId())
                .orElseThrow(() -> new NotFoundException(MessageKeys.PRODUCT_NOT_FOUND));
        CommentEntity commentEntity = modelMapper.map(commentDTO, CommentEntity.class);
        commentEntity.setUserEntity(userEntity);
        commentEntity.setProductEntity(productEntity);
        commentEntity.setImageUrl(UploadImages.uploadImage(commentDTO.getImage()));
        return commentRepository.save(commentEntity);
    }

    @Override
    public CommentResponse getById(Long id) throws Exception {
        CommentEntity commentEntity = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageKeys.COMMENT_NOT_FOUND));
        return CommentResponse.fromCommentEntity(commentEntity);
    }

    @Override
    public Page<CommentResponse> getByProduct(Long productId, Pageable pageable) {
        Page<CommentEntity> commentPage = commentRepository.findByProductEntity_Id(productId, pageable);
        return commentPage.map(CommentResponse::fromCommentEntity);
    }

    @Override
    public Page<CommentResponse> getByUser(String phoneNumber, Pageable pageable) throws NotFoundException {
        UserEntity userEntity = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        Page<CommentEntity> commentPage = commentRepository.findByUserEntity_Id(userEntity.getId(), pageable);
        return commentPage.map(CommentResponse::fromCommentEntity);
    }

    @Override
    public void deleteById(Long id, String phoneNumber) throws NotFoundException {
        UserEntity userEntity = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USER_ID_NOT_FOUND));
        CommentEntity commentEntity = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageKeys.COMMENT_NOT_FOUND));
        if(!commentEntity.getUserEntity().getId().equals(userEntity.getId())){
            throw new UnauthorizedAccessException(MessageKeys.UNAUTHORIZED);
        }
        commentRepository.delete(commentEntity);
    }
}
