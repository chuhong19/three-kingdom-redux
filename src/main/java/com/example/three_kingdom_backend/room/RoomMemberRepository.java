package com.example.three_kingdom_backend.room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {
    Optional<RoomMember> findByRoomIdAndUserId(Long roomId, Long userId);

    @Query("SELECT COUNT(*) FROM RoomMember WHERE room.id = :roomId")
    Long countByRoomId(Long roomId);
}
