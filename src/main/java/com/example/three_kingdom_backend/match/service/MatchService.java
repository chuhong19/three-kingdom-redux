package com.example.three_kingdom_backend.match.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.three_kingdom_backend.match.Match;
import com.example.three_kingdom_backend.match.MatchDTO;
import com.example.three_kingdom_backend.match.MatchDetail;
import com.example.three_kingdom_backend.match.enums.EnumKingdom;
import com.example.three_kingdom_backend.match.enums.MatchStatus;
import com.example.three_kingdom_backend.room.Room;
import com.example.three_kingdom_backend.room.RoomRepository;
import com.example.three_kingdom_backend.user.User;
import com.example.three_kingdom_backend.user.UserRepository;
import com.example.three_kingdom_backend.util.response.StandardResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final MatchDetailRepository matchDetailRepository;
    private final KingdomInfoRepository kingdomInfoRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final MatchDetailBuilder matchDetailBuilder;

    @Transactional
    public StandardResponse<MatchDTO> initMatch(Long roomId, Long weiPlayerId, Long shuPlayerId, Long wuPlayerId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));

        User weiPlayer = loadUser(weiPlayerId);
        User shuPlayer = loadUser(shuPlayerId);
        User wuPlayer = loadUser(wuPlayerId);

        Match match = new Match();
        match.setRoom(room);
        match.setWeiPlayer(weiPlayer);
        match.setShuPlayer(shuPlayer);
        match.setWuPlayer(wuPlayer);
        match.setStatus(MatchStatus.IN_PROGRESS);
        match.setCurrentTurn(EnumKingdom.WEI);
        match.setWeiTurn(true);
        match.setShuTurn(false);
        match.setWuTurn(false);
        match.setActivePlayers(new ArrayList<>(List.of(weiPlayer, shuPlayer, wuPlayer)));

        Match savedMatch = matchRepository.save(match);

        MatchDetailBuilder.MatchDetailBuildResult buildResult = matchDetailBuilder.buildFor(savedMatch);
        kingdomInfoRepository.saveAll(buildResult.kingdoms());

        MatchDetail savedDetail = matchDetailRepository.save(buildResult.detail());

        return StandardResponse.create("200", "Match initialized successfully",
                MatchDTO.from(savedMatch, savedDetail));
    }

    private User loadUser(Long userId) {
        Objects.requireNonNull(userId, "userId");
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}
