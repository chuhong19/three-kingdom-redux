package com.example.three_kingdom_backend.match.store;

import com.example.three_kingdom_backend.match.enums.EnumKingdom;
import com.example.three_kingdom_backend.util.Auditable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity
@Table(name = "match_event_tx", indexes = {
                @Index(name = "idx_tx_match_id", columnList = "match_id"),
                @Index(name = "idx_tx_created_at", columnList = "created_at")
}, uniqueConstraints = {
                @UniqueConstraint(name = "uq_match_event_tx_tx_id", columnNames = { "tx_id" }),
                @UniqueConstraint(name = "uq_match_event_tx_idem_per_match", columnNames = { "match_id",
                                "idempotency_key" })
})
@Data
@EqualsAndHashCode(callSuper = false)
public class MatchEventTx extends Auditable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "match_id", nullable = false)
        private Long matchId;

        @Column(name = "tx_id", nullable = false, insertable = false, updatable = false)
        @Generated(GenerationTime.INSERT)
        private Long txId; // dùng sequence trong DB cho chuẩn

        @Column(name = "command_type", nullable = false, length = 64)
        private String commandType;

        @Enumerated(EnumType.STRING)
        @Column(name = "actor_kingdom", nullable = false, length = 8)
        private EnumKingdom actorKingdom;

        @Column(name = "round_number")
        private Integer roundNumber;

        @Column(name = "phase", length = 32)
        private String phase;

        @Column(name = "idempotency_key", length = 64)
        private String idempotencyKey;
}
