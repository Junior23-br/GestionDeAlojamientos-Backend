package com.gestion.alojamientos.dto.accommodation.CommentHost;


public record CommentHostDTO(
        Long id,
        String content,
        String senderName,
        Long senderId,
        Long receiverId
) {
}
