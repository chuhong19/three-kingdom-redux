-- liquibase formatted sql

-- changeset three-kingdom-team:014-set-match-event-tx-default
-- comment: Ensure match_event_tx.tx_id auto uses the sequence

ALTER TABLE match_event_tx
    ALTER COLUMN tx_id SET DEFAULT nextval('match_event_tx_id_seq');

-- rollback ALTER TABLE match_event_tx ALTER COLUMN tx_id DROP DEFAULT;

