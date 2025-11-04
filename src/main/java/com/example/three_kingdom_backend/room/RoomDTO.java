package com.example.three_kingdom_backend.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    private Long id;
    private Long ownerId;
    private String ownerUsername;
    private String description;
    private STATUS status;
    private Date createdAt;
    private Date updatedAt;

    // Constructor để convert từ Room entity
    public static RoomDTO fromEntity(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setOwnerId(room.getOwner().getId());
        dto.setOwnerUsername(room.getOwner().getUsername());
        dto.setDescription(room.getDescription());
        dto.setStatus(room.getStatus());
        dto.setCreatedAt(room.getCreatedAt());
        dto.setUpdatedAt(room.getUpdatedAt());
        return dto;
    }
}
