package com.gestion.alojamientos.service;

import com.gestion.alojamientos.dto.accommodation.CommentHost.CommentHostCreateDTO;
import com.gestion.alojamientos.dto.accommodation.CommentHost.CommentHostDTO;
import com.gestion.alojamientos.dto.accommodation.CommentHost.CommentHostUpdateDTO;
import com.gestion.alojamientos.dto.accommodation.CommentHost.DeleteCommentHostDTO;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.model.message.CommentHost;

import java.util.List;

public interface CommentHostService {



    // =========================
    // CREATE
    // =========================
    CommentHostDTO createComment(CommentHostCreateDTO dto) throws ElementNotFoundException;

    // =========================
    // READ
    // =========================
    CommentHostDTO getCommentById(Long id) throws ElementNotFoundException;

    List<CommentHostDTO> getCommentsBySender(Long senderId);

    List<CommentHostDTO> getCommentsByReceiver(Long receiverId);

    List<CommentHostDTO> getAllComments();


    // =========================
    // DELETE
    // =========================
    void deleteComment(DeleteCommentHostDTO dto) throws ElementNotFoundException;

    /**
     * Obtiene todos los comentarios enviados por un huésped específico.
     *
     * @param senderId ID del usuario (Guest) que envió los comentarios.
     * @return Lista de comentarios enviados por ese huésped.
     */
    List<CommentHost> findBySender_Id(Long senderId);

    /**
     * Obtiene todos los comentarios dirigidos a un anfitrión específico.
     *
     * @param receiverId ID del Host que recibió los comentarios.
     * @return Lista de comentarios recibidos por ese anfitrión.
     */
    List<CommentHost> findByReceiver_Id(Long receiverId);
}
