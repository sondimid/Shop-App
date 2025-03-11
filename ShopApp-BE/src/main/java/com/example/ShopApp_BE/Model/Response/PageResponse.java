package com.example.ShopApp_BE.Model.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Builder
public class PageResponse<T>  {
    List<T> content;
    Integer pageNumber;
    Integer pageSize;
    Long totalElements;
    Integer totalPages;
}
