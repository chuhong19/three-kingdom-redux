package com.example.three_kingdom_backend.match.store;

import com.example.three_kingdom_backend.util.Auditable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "match_events", indexes = {
        @Index(name = "idx_events_match_id", columnList = "match_id"),
        @Index(name = "idx_events_match_tx", columnList = "match_id,tx_id"),
        @Index(name = "idx_events_type", columnList = "type")
})
@IdClass(MatchEventKey.class)
@Data
@EqualsAndHashCode(callSuper = false)
public class MatchEvent extends Auditable {

    @Id
    @Column(name = "match_id", nullable = false)
    private Long matchId;

    @Id
    @Column(name = "seq", nullable = false)
    private Long seq; // ← PK thành phần (cùng match_id)

    @Column(name = "tx_id", nullable = false)
    private Long txId;

    @Column(name = "type", nullable = false, length = 64)
    private String type;

    @Column(name = "payload_jsonb", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String payloadJson; // đặt tên rõ payloadJson
}
