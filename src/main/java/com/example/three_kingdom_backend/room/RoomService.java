package com.example.three_kingdom_backend.room;

import com.example.three_kingdom_backend.user.User;
import com.example.three_kingdom_backend.room.CreateRoomRequest;
import com.example.three_kingdom_backend.room.STATUS;
import com.example.three_kingdom_backend.room.Room;
import com.example.three_kingdom_backend.room.RoomRepository;
import com.example.three_kingdom_backend.room.RoomDTO;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import com.example.three_kingdom_backend.util.response.StandardResponse;
import com.example.three_kingdom_backend.user.UserRepository;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public RoomService(RoomRepository roomRepository, UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    public StandardResponse<RoomDTO> createRoom(CreateRoomRequest request, Long ownerId) {

        User owner = userRepository.findById(ownerId).orElseThrow(() -> new RuntimeException("User not found"));

        Room room = new Room();
        room.setPassword(request.getPassword());
        room.setDescription(request.getDescription());
        room.setStatus(STATUS.OPENING);
        room.setOwner(owner);
        var saved = roomRepository.save(room);

        return StandardResponse.create("200", RoomDTO.fromEntity(saved));
    }

    public StandardResponse<RoomDTO> getRoomById(Long id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
        return StandardResponse.create("200", RoomDTO.fromEntity(room));
    }

    public StandardResponse<List<RoomDTO>> getRoomsByStatus(STATUS status) {
        List<RoomDTO> rooms = roomRepository.findByStatus(status).stream()
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
        return StandardResponse.create("200", rooms);
    }
}
