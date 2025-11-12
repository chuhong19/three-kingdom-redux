package com.example.three_kingdom_backend.room;

import lombok.Data;

@Data
public class StartGameRequest {
    private Long weiPlayerId;
    private Long shuPlayerId;
    private Long wuPlayerId;
}
