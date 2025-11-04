package com.example.three_kingdom_backend.room;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.three_kingdom_backend.config.security.AuthUser;
import com.example.three_kingdom_backend.util.response.StandardResponse;
import com.example.three_kingdom_backend.room.RoomDTO;

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
}
