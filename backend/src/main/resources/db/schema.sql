CREATE TABLE IF NOT EXISTS account (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    icon VARCHAR(32) NOT NULL DEFAULT '💳',
    color VARCHAR(16) NOT NULL DEFAULT '#E4F0FA',
    initial_balance DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    is_default TINYINT(1) NOT NULL DEFAULT 0,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_account_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS category (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    type VARCHAR(16) NOT NULL,
    icon VARCHAR(32) NOT NULL,
    color VARCHAR(16) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_category_type_name_deleted (type, name, deleted),
    KEY idx_category_type_deleted_sort (type, deleted, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS budget (
    id BIGINT NOT NULL AUTO_INCREMENT,
    budget_month DATE NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_budget_month (budget_month),
    CONSTRAINT ck_budget_amount_positive CHECK (amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS bill (
    id BIGINT NOT NULL AUTO_INCREMENT,
    type VARCHAR(16) NOT NULL,
    category_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    occurred_at DATETIME NOT NULL,
    note VARCHAR(255) NULL,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_bill_occurred_at_deleted (occurred_at, deleted),
    KEY idx_bill_type_occurred_at_deleted (type, occurred_at, deleted),
    KEY idx_bill_category_occurred_at_deleted (category_id, occurred_at, deleted),
    KEY idx_bill_account_occurred_at_deleted (account_id, occurred_at, deleted),
    CONSTRAINT fk_bill_category FOREIGN KEY (category_id) REFERENCES category(id),
    CONSTRAINT fk_bill_account FOREIGN KEY (account_id) REFERENCES account(id),
    CONSTRAINT ck_bill_amount_positive CHECK (amount > 0),
    CONSTRAINT ck_bill_type CHECK (type IN ('expense', 'income'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO account (id, name, icon, color, initial_balance, is_default) VALUES
    (1, '默认账户', '💳', '#E4F0FA', 0.00, 1);

INSERT IGNORE INTO category (id, name, type, icon, color, sort_order) VALUES
    (1, '三餐', 'expense', '🍚', '#FCE9E3', 10),
    (2, '零食', 'expense', '🍪', '#FFF0D4', 20),
    (3, '服饰', 'expense', '👕', '#F8E3EB', 30),
    (4, '住房', 'expense', '🏠', '#E2F1E9', 40),
    (5, '日用', 'expense', '🧴', '#F5E9D9', 50),
    (6, '护肤', 'expense', '🧖', '#F8E8EE', 60),
    (7, '数码', 'expense', '💻', '#E3EEFB', 70),
    (8, '交通', 'expense', '🚇', '#E4F0FA', 80),
    (9, '人情', 'expense', '🎁', '#FBE6E1', 90),
    (10, '医疗', 'expense', '🩺', '#E4F3EF', 100),
    (11, '通讯', 'expense', '📱', '#E6ECFB', 110),
    (12, '羽毛球', 'expense', '🏸', '#E7F2D9', 120),
    (13, '健身', 'expense', '🏋️', '#E9F1DA', 130),
    (14, '学习', 'expense', '📚', '#EEE6FB', 140),
    (15, '娱乐', 'expense', '🎮', '#F1E7F9', 150),
    (16, '社交', 'expense', '☕', '#FBECD7', 160),
    (17, '旅行', 'expense', '✈️', '#E0F3F3', 170),
    (18, '工资', 'income', '💼', '#E6F3D4', 10),
    (19, '红包', 'income', '🧧', '#FBE2DC', 20),
    (20, '副业', 'income', '🪄', '#E8E4FB', 30),
    (21, '投资', 'income', '📈', '#DDEFF0', 40),
    (22, '意外收入', 'income', '✨', '#FFF0CE', 50);
