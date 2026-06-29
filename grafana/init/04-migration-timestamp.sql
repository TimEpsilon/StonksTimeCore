-- Migration pour bases existantes : colonnes temporelles en TIMESTAMPTZ(3) ISO avec millisecondes

-- banks : DATE -> TIMESTAMPTZ(3)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'banks'
          AND column_name = 'time'
          AND data_type = 'date'
    ) THEN
        ALTER TABLE banks
            ALTER COLUMN time TYPE TIMESTAMPTZ(3)
            USING (time::timestamp AT TIME ZONE 'UTC');
    END IF;
END $$;

-- sct_transaction : hour BIGINT -> time TIMESTAMPTZ(3)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'sct_transaction'
          AND column_name = 'hour'
    ) THEN
        ALTER TABLE sct_transaction RENAME COLUMN hour TO time;
        ALTER TABLE sct_transaction
            ALTER COLUMN time TYPE TIMESTAMPTZ(3)
            USING to_timestamp(time * 3600) AT TIME ZONE 'UTC';
    END IF;
END $$;
