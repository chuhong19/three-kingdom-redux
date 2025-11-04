-- liquibase formatted sql

-- changeset three-kingdom-team:006-create-room-table
-- comment: Create rooms table
CREATE TABLE rooms (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    password    VARCHAR(60)  NOT NULL, -- bcrypt
    status      VARCHAR(20)  NOT NULL,
    owner_id    BIGINT       NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ,
    CONSTRAINT fk_room_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT ck_rooms_status CHECK (status IN ('OPENING','PLAYING','DONE'))
);

CREATE INDEX idx_room_owner_id ON rooms (owner_id);
CREATE INDEX idx_room_status   ON rooms (status);

-- rollback DROP TABLE rooms;
