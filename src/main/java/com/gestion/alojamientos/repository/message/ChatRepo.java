package com.gestion.alojamientos.repository.message;

import java.util.*;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gestion.alojamientos.model.message.Chat;
import com.gestion.alojamientos.model.message.Message;
import com.gestion.alojamientos.model.users.Guest;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

@Repository
public interface ChatRepo extends JpaRepository<Chat, Long>, JpaSpecificationExecutor<Chat> {
     /**
     * Encuentra un Chat por ID con todos sus Messages (JOIN FETCH)
     */
    @Query("SELECT DISTINCT c FROM Chat c " +
           "LEFT JOIN FETCH c.messageList " +
           "WHERE c.id = :chatId")
    Optional<Chat> findByIdWithMessages(@Param("chatId") Long chatId);
    
    /**
     * Encuentra un Chat por ID con Messages y Members
     */
    @Query("SELECT DISTINCT c FROM Chat c " +
           "LEFT JOIN FETCH c.messageList " +
           "LEFT JOIN FETCH c.membersList " +
           "WHERE c.id = :chatId")
    Optional<Chat> findByIdWithMessagesAndMembers(@Param("chatId") Long chatId);
    
    /**
     * Encuentra Chats de un usuario con sus últimos Messages
     */
    @Query("SELECT DISTINCT c FROM Chat c " +
           "LEFT JOIN FETCH c.membersList " +
           "LEFT JOIN FETCH c.messageList m " +
           "WHERE :member MEMBER OF c.membersList " +
           "ORDER BY m.createDate DESC")
    List<Chat> findByMemberWithRecentMessages(@Param("member") Guest member);
    
    // CONSULTAS PAGINADAS CON MESSAGES
    
    /**
     * Encuentra Chats de un usuario paginados con Messages
     */
    @Query("SELECT DISTINCT c FROM Chat c " +
           "LEFT JOIN FETCH c.membersList " +
           "WHERE :member MEMBER OF c.membersList")
    Page<Chat> findByMember(@Param("member") Guest member, Pageable pageable);
    
    /**
     * Encuentra Messages de un Chat paginados
     */
    @Query("SELECT m FROM Message m " +
           "JOIN FETCH m.sender " +
           "WHERE m.chat.id = :chatId " +
           "ORDER BY m.createDate DESC")
    Page<Message> findMessagesByChatId(@Param("chatId") Long chatId, Pageable pageable);
    
    // CONSULTAS ESPECÍFICAS DE MESSAGES
    
    /**
     * Encuentra los últimos N Messages de un Chat
     */
    @Query("SELECT m FROM Message m " +
           "JOIN FETCH m.sender " +
           "WHERE m.chat.id = :chatId " +
           "ORDER BY m.createDate DESC")
    List<Message> findLastMessagesByChatId(@Param("chatId") Long chatId, 
                                          @Param("limit") int limit, 
                                          Pageable pageable);
    
    default List<Message> findLastMessagesByChatId(Long chatId, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createDate").descending());
        return findLastMessagesByChatId(chatId, limit, pageable);
    }
    
    /**
     * Encuentra Messages no leídos de un usuario en un Chat
     */
    @Query("SELECT m FROM Message m " +
           "JOIN FETCH m.sender " +
           "WHERE m.chat.id = :chatId " +
           "AND m.receiver = :receiver " +
           "AND m.createDate > :sinceDate " +
           "ORDER BY m.createDate ASC")
    List<Message> findUnreadMessages(@Param("chatId") Long chatId,
                                    @Param("receiver") Guest receiver,
                                    @Param("sinceDate") Date sinceDate);
    
    // CONSULTAS DE AGREGACIÓN SOBRE MESSAGES
    
    /**
     * Cuenta los Messages de un Chat
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.chat.id = :chatId")
    Long countMessagesByChatId(@Param("chatId") Long chatId);
    
    /**
     * Encuentra la fecha del último Message de un Chat
     */
    @Query("SELECT MAX(m.createDate) FROM Message m WHERE m.chat.id = :chatId")
    Optional<Date> findLastMessageDateByChatId(@Param("chatId") Long chatId);
    
    /**
     * Encuentra Chats con actividad reciente (ordenados por último Message)
     */
    @Query("SELECT c, MAX(m.createDate) as lastActivity FROM Chat c " +
           "LEFT JOIN c.messageList m " +
           "WHERE :member MEMBER OF c.membersList " +
           "GROUP BY c " +
           "ORDER BY lastActivity DESC NULLS LAST")
    Page<Object[]> findChatsByMemberOrderByLastActivity(@Param("member") Guest member, 
                                                       Pageable pageable);
    
    // CONSULTAS CON SPECIFICATIONS PARA BÚSQUEDAS COMPLEJAS
    
    /**
     * Encuentra Chats que contengan texto específico en sus Messages
     */
    default List<Chat> findChatsWithMessageText(String searchText, Guest member) {
        Specification<Chat> spec = Specification.where(hasMember(member))
                                               .and(hasMessageWithText(searchText));
        return findAll(spec);
    }
    
    // SPECIFICATIONS PARA BÚSQUEDAS DINÁMICAS
    
    static Specification<Chat> hasMember(Guest member) {
        return (root, query, criteriaBuilder) -> 
            member == null ? null : criteriaBuilder.isMember(member, root.get("membersList"));
    }
    
    static Specification<Chat> hasMessageWithText(String text) {
        return (root, query, criteriaBuilder) -> {
            if (text == null || text.trim().isEmpty()) return null;
            
            Join<Chat, Message> messages = root.join("messageList", JoinType.INNER);
            return criteriaBuilder.like(
                criteriaBuilder.lower(messages.get("text")), 
                "%" + text.toLowerCase() + "%"
            );
        };
    }
    
    static Specification<Chat> hasMessageAfter(Date date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) return null;
            
            Join<Chat, Message> messages = root.join("messageList", JoinType.INNER);
            return criteriaBuilder.greaterThan(messages.get("createDate"), date);
        };
    }
    
    // CONSULTAS NATIVAS PARA CASOS COMPLEJOS
    
    /**
     * Consulta nativa para estadísticas de Chat
     */
    @Query(value = "SELECT " +
           "c.id as chat_id, " +
           "COUNT(m.id) as message_count, " +
           "MAX(m.create_date) as last_message_date, " +
           "COUNT(DISTINCT cm.user_id) as member_count " +
           "FROM chat c " +
           "LEFT JOIN message m ON c.id = m.chat_id " +
           "LEFT JOIN chat_members cm ON c.id = cm.chat_id " +
           "WHERE c.id = :chatId " +
           "GROUP BY c.id", nativeQuery = true)
    Map<String, Object> getChatStatistics(@Param("chatId") Long chatId);
}
