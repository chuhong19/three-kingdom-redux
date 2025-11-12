package com.example.three_kingdom_backend.match;

import com.example.three_kingdom_backend.match.enums.EnumAllianceMarker;
import com.example.three_kingdom_backend.match.enums.EnumCriteria;
import com.example.three_kingdom_backend.match.enums.EnumKingdom;
import com.example.three_kingdom_backend.match.enums.EnumPhase;
import com.example.three_kingdom_backend.match.enums.MatchStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchDTO {

    private Long id;
    private Long roomId;
    private Long weiPlayerId;
    private Long shuPlayerId;
    private Long wuPlayerId;
    private MatchStatus status;
    private EnumKingdom currentTurn;
    private boolean isWeiTurn;
    private boolean isShuTurn;
    private boolean isWuTurn;

    private Integer roundNumber;
    private EnumCriteria kingMarker;
    private EnumCriteria populationMarker;
    private EnumPhase phase;
    private EnumAllianceMarker allianceMarker;
    private EnumKingdom firstKingdom;
    private EnumKingdom secondKingdom;
    private EnumKingdom thirdKingdom;

    public static MatchDTO from(Match match, MatchDetail detail) {
        MatchDTO dto = new MatchDTO();
        dto.id = match.getId();
        dto.roomId = match.getRoom() != null ? match.getRoom().getId() : null;
        dto.weiPlayerId = match.getWeiPlayer() != null ? match.getWeiPlayer().getId() : null;
        dto.shuPlayerId = match.getShuPlayer() != null ? match.getShuPlayer().getId() : null;
        dto.wuPlayerId = match.getWuPlayer() != null ? match.getWuPlayer().getId() : null;
        dto.status = match.getStatus();
        dto.currentTurn = match.getCurrentTurn();
        dto.isWeiTurn = match.isWeiTurn();
        dto.isShuTurn = match.isShuTurn();
        dto.isWuTurn = match.isWuTurn();

        if (detail != null) {
            dto.roundNumber = detail.getRoundNumber();
            dto.kingMarker = detail.getKingMarker();
            dto.populationMarker = detail.getPopulationMarker();
            dto.phase = detail.getPhase();
            dto.allianceMarker = detail.getAllianceMarker();
            dto.firstKingdom = detail.getFirstKingdom();
            dto.secondKingdom = detail.getSecondKingdom();
            dto.thirdKingdom = detail.getThirdKingdom();
        }
        return dto;
    }

    public static MatchDTO fromEntity(Match match) {
        return from(match, null);
    }
}
