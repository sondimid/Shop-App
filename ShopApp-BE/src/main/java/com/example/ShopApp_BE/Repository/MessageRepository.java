package com.example.ShopApp_BE.Repository;

import com.example.ShopApp_BE.Model.Entity.MessageEntity;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Utils.ConfixSql;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    Optional<List<MessageEntity>> findByRoomId(Long roomId);

    @Query(ConfixSql.Message.SEARCH_BY_CREATED_AT)
    List<MessageEntity> findLatest();

}
