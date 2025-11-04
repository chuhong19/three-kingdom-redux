-- liquibase formatted sql

-- changeset three-kingdom-team:008-allow-null-description-password-rooms
-- comment: Allow NULL for description and password columns in rooms table

ALTER TABLE rooms
    ALTER COLUMN description DROP NOT NULL;

ALTER TABLE rooms
    ALTER COLUMN password DROP NOT NULL;

-- rollback
-- rollback ALTER TABLE rooms ALTER COLUMN description SET NOT NULL;
-- rollback ALTER TABLE rooms ALTER COLUMN password SET NOT NULL;

