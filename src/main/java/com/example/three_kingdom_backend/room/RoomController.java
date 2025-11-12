package com.example.three_kingdom_backend.room;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.three_kingdom_backend.config.security.AuthUser;
import com.example.three_kingdom_backend.util.response.StandardResponse;
import com.example.three_kingdom_backend.room.RoomDTO;
import com.example.three_kingdom_backend.room.JoinRoomRequest;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/create")
    public StandardResponse<RoomDTO> createRoom(@RequestBody CreateRoomRequest request,
            @AuthenticationPrincipal AuthUser me) {
        return roomService.createRoom(request, me.getId());
    }

    @PostMapping("/join/{roomId}")
    public StandardResponse<RoomDTO> joinRoom(
            @PathVariable Long roomId,
            @RequestBody(required = false) JoinRoomRequest request,
            @AuthenticationPrincipal AuthUser me) {
        String password = request != null ? request.getPassword() : null;
        return roomService.joinRoom(roomId, password, me.getId());
    }

    @PostMapping("/leave/{roomId}")
    public StandardResponse<RoomDTO> leaveRoom(@PathVariable Long roomId,
            @AuthenticationPrincipal AuthUser me) {
        return roomService.leaveRoom(roomId, me.getId());
    }

    @PostMapping("/start/{roomId}")
    public StandardResponse<RoomDTO> startGame(@PathVariable Long roomId,
            @RequestBody StartGameRequest request,
            @AuthenticationPrincipal AuthUser me) {
        return roomService.startGame(roomId, me.getId(), request.getWeiPlayerId(), request.getShuPlayerId(),
                request.getWuPlayerId());
    }
}
