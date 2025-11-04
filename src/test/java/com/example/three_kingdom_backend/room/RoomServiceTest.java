package com.example.three_kingdom_backend.room;

import com.example.three_kingdom_backend.user.User;
import com.example.three_kingdom_backend.user.UserRepository;
import com.example.three_kingdom_backend.util.response.StandardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoomService Tests")
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomMemberRepository roomMemberRepository;

    @InjectMocks
    private RoomService roomService;

    private User owner;
    private User member;
    private Room room;
    private CreateRoomRequest createRoomRequest;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setUsername("owner");
        owner.setEmail("owner@example.com");
        owner.setPassword("password");

        member = new User();
        member.setId(2L);
        member.setUsername("member");
        member.setEmail("member@example.com");
        member.setPassword("password");

        room = new Room();
        room.setId(1L);
        room.setOwner(owner);
        room.setPassword("roompassword");
        room.setDescription("Test room");
        room.setStatus(STATUS.OPENING);

        createRoomRequest = new CreateRoomRequest();
        createRoomRequest.setPassword("roompassword");
        createRoomRequest.setDescription("Test room");
    }

    @Test
    @DisplayName("Create room successfully")
    void testCreateRoom_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> {
            Room savedRoom = invocation.getArgument(0);
            savedRoom.setId(1L);
            return savedRoom;
        });
        when(roomMemberRepository.save(any(RoomMember.class))).thenReturn(new RoomMember());

        // When
        StandardResponse<RoomDTO> response = roomService.createRoom(createRoomRequest, 1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("200");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getDescription()).isEqualTo("Test room");
        assertThat(response.getData().getStatus()).isEqualTo(STATUS.OPENING);
        assertThat(response.getData().getOwnerId()).isEqualTo(1L);
        assertThat(response.getData().getOwnerUsername()).isEqualTo("owner");

        verify(userRepository, times(1)).findById(1L);
        verify(roomRepository, times(1)).save(any(Room.class));
        verify(roomMemberRepository, times(1)).save(any(RoomMember.class));

        // Verify room was created with correct properties
        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(roomRepository).save(roomCaptor.capture());
        Room savedRoom = roomCaptor.getValue();
        assertThat(savedRoom.getPassword()).isEqualTo("roompassword");
        assertThat(savedRoom.getDescription()).isEqualTo("Test room");
        assertThat(savedRoom.getStatus()).isEqualTo(STATUS.OPENING);
        assertThat(savedRoom.getOwner()).isEqualTo(owner);
    }

    @Test
    @DisplayName("Create room fails when owner not found")
    void testCreateRoom_OwnerNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> roomService.createRoom(createRoomRequest, 1L));

        assertThat(exception.getMessage()).isEqualTo("User not found");
        verify(userRepository, times(1)).findById(1L);
        verify(roomRepository, never()).save(any(Room.class));
        verify(roomMemberRepository, never()).save(any(RoomMember.class));
    }

    @Test
    @DisplayName("Create room with null password and description")
    void testCreateRoom_WithNullFields() {
        // Given
        createRoomRequest.setPassword(null);
        createRoomRequest.setDescription(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> {
            Room savedRoom = invocation.getArgument(0);
            savedRoom.setId(1L);
            return savedRoom;
        });
        when(roomMemberRepository.save(any(RoomMember.class))).thenReturn(new RoomMember());

        // When
        StandardResponse<RoomDTO> response = roomService.createRoom(createRoomRequest, 1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("200");
        assertThat(response.getData()).isNotNull();

        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(roomRepository).save(roomCaptor.capture());
        Room savedRoom = roomCaptor.getValue();
        assertThat(savedRoom.getPassword()).isNull();
        assertThat(savedRoom.getDescription()).isNull();
    }

    @Test
    @DisplayName("Get room by id successfully")
    void testGetRoomById_Success() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        // When
        StandardResponse<RoomDTO> response = roomService.getRoomById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("200");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getId()).isEqualTo(1L);
        assertThat(response.getData().getDescription()).isEqualTo("Test room");
        assertThat(response.getData().getStatus()).isEqualTo(STATUS.OPENING);

        verify(roomRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Get room by id fails when room not found")
    void testGetRoomById_RoomNotFound() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> roomService.getRoomById(1L));

        assertThat(exception.getMessage()).isEqualTo("Room not found");
        verify(roomRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Get rooms by status successfully")
    void testGetRoomsByStatus_Success() {
        // Given
        Room room1 = new Room();
        room1.setId(1L);
        room1.setStatus(STATUS.OPENING);
        room1.setOwner(owner);

        Room room2 = new Room();
        room2.setId(2L);
        room2.setStatus(STATUS.OPENING);
        room2.setOwner(owner);

        List<Room> rooms = Arrays.asList(room1, room2);
        when(roomRepository.findByStatus(STATUS.OPENING)).thenReturn(rooms);

        // When
        StandardResponse<List<RoomDTO>> response = roomService.getRoomsByStatus(STATUS.OPENING);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("200");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData()).hasSize(2);

        verify(roomRepository, times(1)).findByStatus(STATUS.OPENING);
    }

    @Test
    @DisplayName("Join room successfully with correct password")
    void testJoinRoom_Success_WithPassword() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(2L)).thenReturn(Optional.of(member));
        when(roomMemberRepository.findByRoomIdAndUserId(1L, 2L)).thenReturn(Optional.empty());
        when(roomMemberRepository.save(any(RoomMember.class))).thenReturn(new RoomMember());

        // When
        StandardResponse<RoomDTO> response = roomService.joinRoom(1L, "roompassword", 2L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("200");
        assertThat(response.getMessage()).isEqualTo("Joined room successfully");
        assertThat(response.getData()).isNotNull();

        verify(roomRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verify(roomMemberRepository, times(1)).findByRoomIdAndUserId(1L, 2L);
        verify(roomMemberRepository, times(1)).save(any(RoomMember.class));
    }

    @Test
    @DisplayName("Join room successfully without password (public room)")
    void testJoinRoom_Success_NoPassword() {
        // Given
        room.setPassword(null);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(2L)).thenReturn(Optional.of(member));
        when(roomMemberRepository.findByRoomIdAndUserId(1L, 2L)).thenReturn(Optional.empty());
        when(roomMemberRepository.save(any(RoomMember.class))).thenReturn(new RoomMember());

        // When
        StandardResponse<RoomDTO> response = roomService.joinRoom(1L, null, 2L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("200");
        assertThat(response.getMessage()).isEqualTo("Joined room successfully");

        verify(roomMemberRepository, times(1)).save(any(RoomMember.class));
    }

    @Test
    @DisplayName("Join room fails when room not found")
    void testJoinRoom_RoomNotFound() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> roomService.joinRoom(1L, "password", 2L));

        assertThat(exception.getMessage()).isEqualTo("Room not found");
        verify(roomRepository, times(1)).findById(1L);
        verify(roomMemberRepository, never()).save(any(RoomMember.class));
    }

    @Test
    @DisplayName("Join room fails when user not found")
    void testJoinRoom_UserNotFound() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> roomService.joinRoom(1L, "password", 2L));

        assertThat(exception.getMessage()).isEqualTo("User not found");
        verify(roomRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verify(roomMemberRepository, never()).save(any(RoomMember.class));
    }

    @Test
    @DisplayName("Join room fails when user is already a member")
    void testJoinRoom_AlreadyMember() {
        // Given
        RoomMember existingMember = new RoomMember();
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(2L)).thenReturn(Optional.of(member));
        when(roomMemberRepository.findByRoomIdAndUserId(1L, 2L)).thenReturn(Optional.of(existingMember));

        // When
        StandardResponse<RoomDTO> response = roomService.joinRoom(1L, "roompassword", 2L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("400");
        assertThat(response.getMessage()).isEqualTo("User is already a member of this room");
        assertThat(response.getData()).isNotNull();

        verify(roomMemberRepository, times(1)).findByRoomIdAndUserId(1L, 2L);
        verify(roomMemberRepository, never()).save(any(RoomMember.class));
    }

    @Test
    @DisplayName("Join room fails with invalid password")
    void testJoinRoom_InvalidPassword() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(2L)).thenReturn(Optional.of(member));
        when(roomMemberRepository.findByRoomIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

        // When
        StandardResponse<RoomDTO> response = roomService.joinRoom(1L, "wrongpassword", 2L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("401");
        assertThat(response.getMessage()).isEqualTo("Invalid password");
        assertThat(response.getData()).isNull();

        verify(roomMemberRepository, times(1)).findByRoomIdAndUserId(1L, 2L);
        verify(roomMemberRepository, never()).save(any(RoomMember.class));
    }

    @Test
    @DisplayName("Join room with empty password when room has password")
    void testJoinRoom_EmptyPassword_WhenRoomHasPassword() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(2L)).thenReturn(Optional.of(member));
        when(roomMemberRepository.findByRoomIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

        // When
        StandardResponse<RoomDTO> response = roomService.joinRoom(1L, "", 2L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("401");
        assertThat(response.getMessage()).isEqualTo("Invalid password");

        verify(roomMemberRepository, never()).save(any(RoomMember.class));
    }

    @Test
    @DisplayName("Leave room successfully")
    void testLeaveRoom_Success() {
        // Given
        RoomMember roomMember = new RoomMember();
        roomMember.setRoom(room);
        roomMember.setUser(member);
        when(roomMemberRepository.findByRoomIdAndUserId(1L, 2L)).thenReturn(Optional.of(roomMember));
        doNothing().when(roomMemberRepository).delete(any(RoomMember.class));

        // When
        StandardResponse<RoomDTO> response = roomService.leaveRoom(1L, 2L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("200");
        assertThat(response.getMessage()).isEqualTo("Left room successfully");
        assertThat(response.getData()).isNull(); // createMessage returns null data

        verify(roomMemberRepository, times(1)).findByRoomIdAndUserId(1L, 2L);
        verify(roomMemberRepository, times(1)).delete(roomMember);
    }

    @Test
    @DisplayName("Leave room fails when user is not a member")
    void testLeaveRoom_NotMember() {
        // Given
        when(roomMemberRepository.findByRoomIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> roomService.leaveRoom(1L, 2L));

        assertThat(exception.getMessage()).isEqualTo("User is not a member of this room");
        verify(roomMemberRepository, times(1)).findByRoomIdAndUserId(1L, 2L);
        verify(roomMemberRepository, never()).delete(any(RoomMember.class));
    }

    @Test
    @DisplayName("Create room adds owner as member automatically")
    void testCreateRoom_OwnerAddedAsMember() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> {
            Room savedRoom = invocation.getArgument(0);
            savedRoom.setId(1L);
            return savedRoom;
        });
        when(roomMemberRepository.save(any(RoomMember.class))).thenReturn(new RoomMember());

        // When
        roomService.createRoom(createRoomRequest, 1L);

        // Then
        ArgumentCaptor<RoomMember> memberCaptor = ArgumentCaptor.forClass(RoomMember.class);
        verify(roomMemberRepository).save(memberCaptor.capture());
        RoomMember savedMember = memberCaptor.getValue();
        assertThat(savedMember.getUser()).isEqualTo(owner);
    }
}
