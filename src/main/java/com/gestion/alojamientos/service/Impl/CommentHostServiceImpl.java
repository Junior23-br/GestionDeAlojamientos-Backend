package com.gestion.alojamientos.service.Impl;


import com.gestion.alojamientos.dto.accommodation.CommentHost.CommentHostCreateDTO;
import com.gestion.alojamientos.dto.accommodation.CommentHost.CommentHostDTO;
import com.gestion.alojamientos.dto.accommodation.CommentHost.DeleteCommentHostDTO;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.model.message.CommentHost;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.model.users.Host;
import com.gestion.alojamientos.repository.accomodation.CommentHostRepo;
import com.gestion.alojamientos.repository.user.GuestRepository;
import com.gestion.alojamientos.repository.user.HostRepo;
import com.gestion.alojamientos.service.CommentHostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.gestion.alojamientos.mapper.accomodation.CommentHostMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para gestionar los comentarios hacia los anfitriones (CommentHost).
 *
 * Proporciona operaciones CRUD básicas y validaciones de entidades relacionadas (Guest y Host).
 */
@Service
@RequiredArgsConstructor
public class CommentHostServiceImpl implements CommentHostService {

    private final CommentHostRepo commentHostRepository;
    private final GuestRepository guestRepository;
    private final HostRepo hostRepository;
    private final CommentHostMapper mapper;

    // =========================
    // CREATE
    // =========================
    @Override
    public CommentHostDTO createComment(CommentHostCreateDTO dto) throws ElementNotFoundException {
        Guest sender = guestRepository.findById(dto.senderId())
                .orElseThrow(() -> new ElementNotFoundException("No se encontró el huésped con ID: " + dto.senderId()));

        Host receiver = hostRepository.findById(dto.receiverId())
                .orElseThrow(() -> new ElementNotFoundException("No se encontró el anfitrión con ID: " + dto.receiverId()));

        CommentHost comment = CommentHost.builder()
                .sender(sender)
                .receiver(receiver)
                .content(dto.content())
                .build();

        CommentHost saved = commentHostRepository.save(comment);
        return mapper.toDto(saved);
    }

    // =========================
    // READ
    // =========================
    @Override
    public CommentHostDTO getCommentById(Long id) throws ElementNotFoundException {
        CommentHost comment = commentHostRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("Comentario no encontrado con ID: " + id));
        return mapper.toDto(comment);
    }
    @Override
    public List<CommentHostDTO> getCommentsBySender(Long senderId) {
        return commentHostRepository.findByReceiverId(senderId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    @Override
    public List<CommentHostDTO> getCommentsByReceiver(Long receiverId) {
        return commentHostRepository.findByReceiverId(receiverId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentHostDTO> getAllComments() {
        return commentHostRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }


    // =========================
    // UPDATE
    // =========================


    // =========================
    // DELETE
    // =========================
    @Override
    public void deleteComment(DeleteCommentHostDTO dto) throws ElementNotFoundException {
        CommentHost comment = commentHostRepository.findById(dto.id())
                .orElseThrow(() -> new ElementNotFoundException("Comentario no encontrado con ID: " + dto.id()));
        commentHostRepository.delete(comment);
    }

    @Override
    public List<CommentHost> findBySender_Id(Long senderId) {
        return List.of();
    }

    @Override
    public List<CommentHost> findByReceiver_Id(Long receiverId) {
        return List.of();
    }

}