package com.example.three_kingdom_backend.match.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.example.three_kingdom_backend.match.KingdomInfo;
import com.example.three_kingdom_backend.match.Match;
import com.example.three_kingdom_backend.match.MatchDetail;
import com.example.three_kingdom_backend.match.enums.EnumAllianceMarker;
import com.example.three_kingdom_backend.match.enums.EnumCriteria;
import com.example.three_kingdom_backend.match.enums.EnumKingdom;
import com.example.three_kingdom_backend.match.enums.EnumPhase;

/**
 * Đóng gói toàn bộ logic khởi tạo {@link MatchDetail} và các
 * {@link KingdomInfo}
 * mặc định cho một trận đấu mới. Lớp này giúp {@link MatchService} gọn gàng hơn
 * và cho phép tái sử dụng/tinh chỉnh cấu hình khởi tạo ở một nơi duy nhất.
 */
@Component
public class MatchDetailBuilder {

    /**
     * Build các thực thể chi tiết trận và thông tin các nước dựa trên các giá trị
     * khởi tạo mặc định của game.
     *
     * @param match header trận đã được tạo nhưng chưa persist detail/kingdom info
     * @return gói dữ liệu chứa {@link MatchDetail} và danh sách {@link KingdomInfo}
     */
    public MatchDetailBuildResult buildFor(Match match) {
        Objects.requireNonNull(match, "match");

        KingdomInfo wei = createDefaultKingdom(EnumKingdom.WEI);
        KingdomInfo shu = createDefaultKingdom(EnumKingdom.SHU);
        KingdomInfo wu = createDefaultKingdom(EnumKingdom.WU);

        MatchDetail detail = new MatchDetail();
        detail.setMatch(match);
        detail.setRoundNumber(1);
        detail.setKingMarker(EnumCriteria.ADMIN);
        detail.setPopulationMarker(EnumCriteria.COMBAT);
        detail.setPhase(EnumPhase.RECRUIT);
        detail.setAllianceMarker(EnumAllianceMarker.TRAIN);
        detail.setFirstKingdom(EnumKingdom.WEI);
        detail.setSecondKingdom(EnumKingdom.SHU);
        detail.setThirdKingdom(EnumKingdom.WU);
        detail.setWeiKingdomInfo(wei);
        detail.setShuKingdomInfo(shu);
        detail.setWuKingdomInfo(wu);

        return new MatchDetailBuildResult(detail, List.of(wei, shu, wu));
    }

    private KingdomInfo createDefaultKingdom(EnumKingdom kingdom) {
        KingdomInfo info = new KingdomInfo();
        info.setKingdom(kingdom);
        info.setGold(0);
        info.setRice(0);
        info.setPopulationSupportToken(0);
        info.setUnTrainedTroops(0);
        info.setTrainedTroops(0);
        info.setSpear(0);
        info.setCrossbow(0);
        info.setHorse(0);
        info.setVessel(0);
        info.setRedCard(0);
        info.setYellowCard(0);
        info.setTotalGeneral(0);
        info.setStationGeneral(0);
        info.setUnusedGeneral(0);
        info.setFlippedMarket(0);
        info.setFlippedFarm(0);
        info.setDevelopedMarket(0);
        info.setDevelopedFarm(0);
        info.setMarketFlagVP(0);
        info.setFarmFlagVP(0);
        info.setMarketFlagNoVP(0);
        info.setFarmFlagNoVP(0);
        info.setMilitaryVictoryPoints(0);
        info.setEconomicLevel(0);
        info.setTribalLevel(0);
        info.setRankLevel(0);
        info.setWuBorderLevel(0);
        info.setShuBorderLevel(0);
        info.setWeiBorderLevel(0);
        info.setStationTroops(0);
        info.setIsEmperorToken(Boolean.FALSE);
        return info;
    }

    /**
     * Kết quả builder: một {@link MatchDetail} kèm theo danh sách kingdom info
     * cần persist.
     *
     * @param detail   chi tiết trận đấu chưa persist
     * @param kingdoms các thực thể kingdom info tương ứng
     */
    public record MatchDetailBuildResult(MatchDetail detail, List<KingdomInfo> kingdoms) {

        public MatchDetailBuildResult {
            Objects.requireNonNull(detail, "detail");
            Objects.requireNonNull(kingdoms, "kingdoms");
        }
    }
}
