package com.gestion.alojamientos.repository;

import com.gestion.alojamientos.model.Chat;
import com.gestion.alojamientos.model.users.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepo extends JpaRepository<Chat, Long>, JpaSpecificationExecutor<Chat> {

}
