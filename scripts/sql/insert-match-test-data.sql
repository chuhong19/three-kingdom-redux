-- Test data seed for `/api/matches/{id}/commands`
-- Usage (assuming `psql` is installed and `DATABASE_URL` is configured):
--   psql "$DATABASE_URL" -f scripts/sql/insert-match-test-data.sql

-- Username: wei_player
-- Password: password

DO $$
DECLARE
    v_hashed_password CONSTANT TEXT := '$2b$12$M3aWblUA25vpqBhMEcKkpulfEk8KvyGUA0KUa9kBpXqQXpVoOAsYi';
    v_match_id CONSTANT BIGINT := 1001;
    v_room_id BIGINT := 5001;
    v_owner_id BIGINT := 9001;
    v_wei_id BIGINT := 9002;
    v_shu_id BIGINT := 9003;
    v_wu_id BIGINT := 9004;
    v_wei_kingdom_id BIGINT := 2001;
    v_shu_kingdom_id BIGINT := 2002;
    v_wu_kingdom_id BIGINT := 2003;
BEGIN
    -- Clean up existing data for deterministic re-runs
    DELETE FROM match_events WHERE match_id = v_match_id;
    DELETE FROM match_event_tx WHERE match_id = v_match_id;
    DELETE FROM match_details WHERE match_id = v_match_id;
    DELETE FROM match_active_players WHERE match_id = v_match_id;
    DELETE FROM matches WHERE id = v_match_id;
    DELETE FROM kingdom_info WHERE id IN (v_wei_kingdom_id, v_shu_kingdom_id, v_wu_kingdom_id);
    DELETE FROM rooms WHERE id = v_room_id;
    DELETE FROM users WHERE id IN (v_owner_id, v_wei_id, v_shu_id, v_wu_id);

    -- Seed users (password hash corresponds to plain text "password")
    INSERT INTO users (id, username, email, password, created_at, updated_at)
    VALUES
        (v_owner_id, 'room_owner', 'owner@example.com', v_hashed_password, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (v_wei_id, 'wei_player', 'wei@example.com', v_hashed_password, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (v_shu_id, 'shu_player', 'shu@example.com', v_hashed_password, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (v_wu_id, 'wu_player', 'wu@example.com', v_hashed_password, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (id) DO UPDATE
        SET username = EXCLUDED.username,
            email = EXCLUDED.email,
            password = EXCLUDED.password,
            updated_at = CURRENT_TIMESTAMP;

    -- Seed a room for the match (password is also BCrypt hashed "password")
    INSERT INTO rooms (id, description, password, status, owner_id, created_at, updated_at)
    VALUES
        (v_room_id, 'Demo match room', v_hashed_password, 'PLAYING', v_owner_id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (id) DO UPDATE
        SET description = EXCLUDED.description,
            password = EXCLUDED.password,
            status = EXCLUDED.status,
            owner_id = EXCLUDED.owner_id,
            updated_at = CURRENT_TIMESTAMP;

    -- Seed match record
    INSERT INTO matches (
        id, room_id, wei_player_id, shu_player_id, wu_player_id,
        status, current_turn, is_wei_turn, is_shu_turn, is_wu_turn,
        created_at, updated_at
    ) VALUES (
        v_match_id, v_room_id, v_wei_id, v_shu_id, v_wu_id,
        'IN_PROGRESS', 'WEI', true, false, false,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    )
    ON CONFLICT (id) DO UPDATE
        SET room_id = EXCLUDED.room_id,
            wei_player_id = EXCLUDED.wei_player_id,
            shu_player_id = EXCLUDED.shu_player_id,
            wu_player_id = EXCLUDED.wu_player_id,
            status = EXCLUDED.status,
            current_turn = EXCLUDED.current_turn,
            is_wei_turn = EXCLUDED.is_wei_turn,
            is_shu_turn = EXCLUDED.is_shu_turn,
            is_wu_turn = EXCLUDED.is_wu_turn,
            updated_at = CURRENT_TIMESTAMP;

    -- Active players participating in the match
    INSERT INTO match_active_players (match_id, user_id) VALUES
        (v_match_id, v_wei_id),
        (v_match_id, v_shu_id),
        (v_match_id, v_wu_id)
    ON CONFLICT (match_id, user_id) DO NOTHING;

    -- Kingdom info for each faction
    INSERT INTO kingdom_info (
        id, kingdom, gold, rice, population_support_token, untrained_troops,
        trained_troops, spear, crossbow, horse, vessel, red_card, yellow_card,
        total_general, station_general, unused_general, flipped_market,
        flipped_farm, developed_market, developed_farm, market_flag_vp,
        farm_flag_vp, market_flag_no_vp, farm_flag_no_vp, military_victory_points,
        economic_level, tribal_level, rank_level, wu_border_level, shu_border_level,
        wei_border_level, station_troops, is_emperor_token, created_at, updated_at
    ) VALUES
        (v_wei_kingdom_id, 'WEI', 10, 8, 3, 2, 4, 1, 1, 1, 0, 0, 0, 5, 2, 3, 1, 0, 1, 0, 0, 0, 0, 0, 2, 1, 0, 1, 0, 1, 2, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (v_shu_kingdom_id, 'SHU', 9, 7, 2, 3, 3, 1, 0, 1, 0, 0, 0, 4, 1, 3, 0, 1, 0, 1, 0, 0, 0, 0, 2, 1, 1, 1, 1, 0, 1, 0, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        (v_wu_kingdom_id, 'WU', 11, 9, 4, 1, 5, 0, 1, 1, 1, 0, 0, 6, 2, 4, 0, 1, 1, 1, 0, 0, 0, 0, 3, 1, 1, 2, 1, 1, 1, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (id) DO UPDATE
        SET kingdom = EXCLUDED.kingdom,
            gold = EXCLUDED.gold,
            rice = EXCLUDED.rice,
            population_support_token = EXCLUDED.population_support_token,
            untrained_troops = EXCLUDED.untrained_troops,
            trained_troops = EXCLUDED.trained_troops,
            spear = EXCLUDED.spear,
            crossbow = EXCLUDED.crossbow,
            horse = EXCLUDED.horse,
            vessel = EXCLUDED.vessel,
            red_card = EXCLUDED.red_card,
            yellow_card = EXCLUDED.yellow_card,
            total_general = EXCLUDED.total_general,
            station_general = EXCLUDED.station_general,
            unused_general = EXCLUDED.unused_general,
            flipped_market = EXCLUDED.flipped_market,
            flipped_farm = EXCLUDED.flipped_farm,
            developed_market = EXCLUDED.developed_market,
            developed_farm = EXCLUDED.developed_farm,
            market_flag_vp = EXCLUDED.market_flag_vp,
            farm_flag_vp = EXCLUDED.farm_flag_vp,
            market_flag_no_vp = EXCLUDED.market_flag_no_vp,
            farm_flag_no_vp = EXCLUDED.farm_flag_no_vp,
            military_victory_points = EXCLUDED.military_victory_points,
            economic_level = EXCLUDED.economic_level,
            tribal_level = EXCLUDED.tribal_level,
            rank_level = EXCLUDED.rank_level,
            wu_border_level = EXCLUDED.wu_border_level,
            shu_border_level = EXCLUDED.shu_border_level,
            wei_border_level = EXCLUDED.wei_border_level,
            station_troops = EXCLUDED.station_troops,
            is_emperor_token = EXCLUDED.is_emperor_token,
            updated_at = CURRENT_TIMESTAMP;

    -- Match detail referencing the kingdom info records
    INSERT INTO match_details (
        id, match_id, round_number, king_marker, population_marker, phase,
        alliance_marker, first_kingdom, second_kingdom, third_kingdom,
        wei_kingdom_info_id, shu_kingdom_info_id, wu_kingdom_info_id,
        created_at, updated_at
    ) VALUES (
        3001, v_match_id, 1, 'ADMIN', 'COMBAT', 'RECRUIT',
        'TRAIN', 'WEI', 'SHU', 'WU',
        v_wei_kingdom_id, v_shu_kingdom_id, v_wu_kingdom_id,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    )
    ON CONFLICT (id) DO UPDATE
        SET match_id = EXCLUDED.match_id,
            round_number = EXCLUDED.round_number,
            king_marker = EXCLUDED.king_marker,
            population_marker = EXCLUDED.population_marker,
            phase = EXCLUDED.phase,
            alliance_marker = EXCLUDED.alliance_marker,
            first_kingdom = EXCLUDED.first_kingdom,
            second_kingdom = EXCLUDED.second_kingdom,
            third_kingdom = EXCLUDED.third_kingdom,
            wei_kingdom_info_id = EXCLUDED.wei_kingdom_info_id,
            shu_kingdom_info_id = EXCLUDED.shu_kingdom_info_id,
            wu_kingdom_info_id = EXCLUDED.wu_kingdom_info_id,
            updated_at = CURRENT_TIMESTAMP;

    -- Reset sequences so future inserts continue from the max id
    PERFORM setval('users_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM users), 1));
    PERFORM setval('rooms_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM rooms), 1));
    PERFORM setval('matches_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM matches), 1));
    PERFORM setval('kingdom_info_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM kingdom_info), 1));
    PERFORM setval('match_details_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM match_details), 1));
END;
$$;

