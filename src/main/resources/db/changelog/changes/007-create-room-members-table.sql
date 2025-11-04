-- liquibase formatted sql

-- changeset three-kingdom-team:007-create-room-members
-- comment: Create room_members join table

CREATE TABLE room_members (
    id        BIGSERIAL PRIMARY KEY,
    room_id   BIGINT NOT NULL,
    user_id   BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE room_members
  ADD CONSTRAINT fk_room_members_room
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE;

ALTER TABLE room_members
  ADD CONSTRAINT fk_room_members_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Mỗi user chỉ được vào một room đúng 1 lần
ALTER TABLE room_members
  ADD CONSTRAINT uk_room_members_room_user UNIQUE (room_id, user_id);

-- Index phục vụ truy vấn phổ biến
CREATE INDEX idx_room_members_room_id ON room_members(room_id);
CREATE INDEX idx_room_members_user_id ON room_members(user_id);

-- rollback
-- rollback DROP TABLE room_members;

