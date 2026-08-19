ALTER TABLE businesses
    ADD COLUMN IF NOT EXISTS bank_account_number VARCHAR(20),
    ADD COLUMN IF NOT EXISTS ifsc_code VARCHAR(11),
    ADD COLUMN IF NOT EXISTS account_holder_name VARCHAR(100),
    ADD COLUMN IF NOT EXISTS bank_name VARCHAR(100),
    ADD COLUMN IF NOT EXISTS razorpay_linked_account_id VARCHAR(255);