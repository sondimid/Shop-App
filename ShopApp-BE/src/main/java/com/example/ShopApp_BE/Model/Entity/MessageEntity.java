package com.example.ShopApp_BE.Model.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "messages")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity extends AbstractEntity {

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserEntity sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private UserEntity recipient;
}
