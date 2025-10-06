package com.gestion.alojamientos.repository.message;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gestion.alojamientos.model.message.Message;
import com.gestion.alojamientos.model.users.Guest;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {
    
    /**
     * Encuentra Messages por Chat con paginación
     */
    @Query("SELECT m FROM Message m " +
           "JOIN FETCH m.sender " +
           "JOIN FETCH m.chat " +
           "WHERE m.chat.id = :chatId " +
           "ORDER BY m.createDate DESC")
    Page<Message> findByChatId(@Param("chatId") Long chatId, Pageable pageable);
    
    /**
     * Encuentra Messages entre dos usuarios
     */
    @Query("SELECT m FROM Message m " +
           "JOIN FETCH m.chat " +
           "WHERE (m.sender = :user1 AND m.receiver = :user2) " +
           "OR (m.sender = :user2 AND m.receiver = :user1) " +
           "ORDER BY m.createDate DESC")
    List<Message> findConversationBetweenUsers(@Param("user1") Guest user1, 
                                              @Param("user2") Guest user2);
    
    /**
     * Marcar Messages como leídos
     */
    @Modifying
    @Query("UPDATE Message m SET m.createDate = :readDate WHERE m.id IN :messageIds")
    void markMessagesAsRead(@Param("messageIds") List<Long> messageIds, 
                           @Param("readDate") Date readDate);
}