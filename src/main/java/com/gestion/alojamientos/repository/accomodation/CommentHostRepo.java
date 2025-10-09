package com.gestion.alojamientos.repository.accomodation;


import com.gestion.alojamientos.model.message.CommentHost;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.model.users.Host;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentHostRepo extends JpaRepository<CommentHost, Long> {

    /**
     * Encuentra todos los comentarios dirigidos a un Host específico.
     */
    @Query("SELECT c FROM CommentHost c " +
            "LEFT JOIN FETCH c.sender " +
            "WHERE c.receiver.id = :hostId")
    List<CommentHost> findByReceiverId(@Param("hostId") Long hostId);

    /**
     * Encuentra todos los comentarios realizados por un Guest específico.
     */
    @Query("SELECT c FROM CommentHost c " +
            "LEFT JOIN FETCH c.receiver " +
            "WHERE c.sender.id = :guestId")
    List<CommentHost> findBySenderId(@Param("guestId") Long guestId);

    /**
     * Busca un comentario específico entre un Guest y un Host.
     * Útil para validar si ya se hizo un comentario.
     */
    @Query("SELECT c FROM CommentHost c " +
            "WHERE c.sender.id = :guestId AND c.receiver.id = :hostId")
    Optional<CommentHost> findBySenderAndReceiver(@Param("guestId") Long guestId,
                                                  @Param("hostId") Long hostId);

    /**
     * Cuenta la cantidad de comentarios que tiene un Host.
     */
    @Query("SELECT COUNT(c) FROM CommentHost c WHERE c.receiver.id = :hostId")
    Long countByHostId(@Param("hostId") Long hostId);

    /**
     * Verifica si un Guest ya ha comentado a un Host específico.
     */
    @Query("SELECT COUNT(c) > 0 FROM CommentHost c " +
            "WHERE c.sender.id = :guestId AND c.receiver.id = :hostId")
    boolean existsByGuestAndHost(@Param("guestId") Long guestId,
                                 @Param("hostId") Long hostId);

    /**
     * Encuentra todos los comentarios con información completa (Guest + Host cargados).
     */
    @Query("SELECT DISTINCT c FROM CommentHost c " +
            "LEFT JOIN FETCH c.sender " +
            "LEFT JOIN FETCH c.receiver")
    List<CommentHost> findAllWithDetails();
}