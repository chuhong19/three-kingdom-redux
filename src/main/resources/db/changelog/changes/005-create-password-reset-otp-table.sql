-- liquibase formatted sql

-- changeset three-kingdom-team:005-create-password-reset-otp-table
-- comment: Create password_reset_otp table for password reset functionality

CREATE TABLE password_reset_otp (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    code_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,
    max_attempts INTEGER NOT NULL DEFAULT 5,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_password_reset_otp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_password_reset_otp_user_id ON password_reset_otp (user_id);
CREATE INDEX idx_password_reset_otp_expires_at ON password_reset_otp (expires_at);
CREATE INDEX idx_password_reset_otp_used ON password_reset_otp (used);

DELETE FROM password_reset_otp WHERE used=true OR expires_at < now() - interval '1 day';
