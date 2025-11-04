package com.example.three_kingdom_backend.room;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByOwnerId(Long ownerId);

    List<Room> findByStatus(STATUS status);
}