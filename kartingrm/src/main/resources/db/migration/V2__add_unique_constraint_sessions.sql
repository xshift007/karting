ALTER TABLE sessions
    ADD CONSTRAINT uk_session_unique
        UNIQUE (session_date, start_time, end_time);
