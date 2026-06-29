-- Migration pour bases existantes créées avant l'ajout de username
ALTER TABLE banks ADD COLUMN IF NOT EXISTS username TEXT NOT NULL DEFAULT 'unknown';
ALTER TABLE sct_transaction ADD COLUMN IF NOT EXISTS username TEXT NOT NULL DEFAULT 'unknown';

UPDATE banks SET username = lower(player::text) WHERE username = 'unknown';
UPDATE sct_transaction SET username = lower(player::text) WHERE username = 'unknown';
