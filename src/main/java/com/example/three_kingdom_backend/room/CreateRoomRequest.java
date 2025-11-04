package com.example.three_kingdom_backend.room;

import lombok.Data;

@Data
public class CreateRoomRequest {
    private String password;
    private String description;
}
