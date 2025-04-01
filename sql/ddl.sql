CREATE DATABASE `wallet_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

-- wallet_db.accounts definition
CREATE TABLE `accounts` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `user_id` bigint NOT NULL,
                            `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                            `created_date` datetime(6) DEFAULT NULL,
                            `updated_date` datetime(6) DEFAULT NULL,
                            `status` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_account_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- wallet_db.balance_projection definition
CREATE TABLE `balance_projection` (
                                      `balance_uid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                                      `amount` decimal(38,2) DEFAULT NULL,
                                      `last_movement_id` bigint DEFAULT NULL,
                                      `created_date` datetime DEFAULT NULL,
                                      `updated_date` datetime DEFAULT NULL,
                                      `version` bigint DEFAULT NULL,
                                      PRIMARY KEY (`balance_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- wallet_db.deposit definition
CREATE TABLE `deposit` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `account_id` bigint DEFAULT NULL,
                           `amount` decimal(38,2) DEFAULT NULL,
                           `bank_account_number` varchar(255) DEFAULT NULL,
                           `created_date` datetime(6) DEFAULT NULL,
                           `currency` varchar(255) DEFAULT NULL,
                           `comment` varchar(255) DEFAULT NULL,
                           `uid` varchar(255) DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `deposit_unique_uid` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- wallet_db.movement_snapshots definition
CREATE TABLE `movement_snapshots` (
                                      `id` bigint NOT NULL AUTO_INCREMENT,
                                      `uid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                                      `balance_uid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                                      `amount` decimal(38,2) DEFAULT NULL,
                                      `created_date` date DEFAULT NULL,
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `uk_movement_snapshot_uid` (`uid`),
                                      KEY `idx_movement_snapshot_balance_uid` (`balance_uid`),
                                      KEY `idx_movement_snapshot_created_date` (`created_date`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Tabla para snapshots de movimientos';

-- wallet_db.movements definition
CREATE TABLE `movements` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `uid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `balance_uid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `amount` decimal(38,2) DEFAULT NULL,
                             `created_date` datetime(6) DEFAULT NULL,
                             `reference_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                             `reference_id` bigint NOT NULL,
                             `reference_date` datetime(6) DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_movement_uid` (`uid`),
                             KEY `idx_movement_balance_uid` (`balance_uid`),
                             KEY `idx_movement_reference` (`reference_type`,`reference_id`),
                             KEY `idx_movement_balance_uid_created_date` (`balance_uid`,`created_date`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- wallet_db.transfer definition
CREATE TABLE `transfer` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `amount` decimal(38,2) DEFAULT NULL,
                            `comment` varchar(255) DEFAULT NULL,
                            `created_date` datetime(6) DEFAULT NULL,
                            `currency` varchar(255) DEFAULT NULL,
                            `fee` decimal(38,2) DEFAULT NULL,
                            `from_account_id` bigint DEFAULT NULL,
                            `to_account_id` bigint DEFAULT NULL,
                            `uid` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `transfer_unique_uid` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- wallet_db.withdraw definition
CREATE TABLE `withdraw` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `account_id` bigint DEFAULT NULL,
                            `amount` decimal(38,2) DEFAULT NULL,
                            `bank_account_number` varchar(255) DEFAULT NULL,
                            `created_date` datetime(6) DEFAULT NULL,
                            `currency` varchar(255) DEFAULT NULL,
                            `fee` decimal(38,2) DEFAULT NULL,
                            `comment` varchar(255) DEFAULT NULL,
                            `uid` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `withdraw_unique_uid` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- wallet_db.account_balances definition
CREATE TABLE `account_balances` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `account_id` bigint NOT NULL,
                                    `uid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                                    `currency` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                    `created_date` datetime(6) DEFAULT NULL,
                                    `balance_partition` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `uk_account_balance_uid` (`uid`),
                                    KEY `fk_account_balance_account` (`account_id`),
                                    CONSTRAINT `fk_account_balance_account` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- wallet_db.users definition
CREATE TABLE `users` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `user_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                         `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `last_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         `created_date` datetime(6) DEFAULT NULL,
                         `updated_date` datetime(6) DEFAULT NULL,
                         `user_id` bigint DEFAULT NULL,
                         `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_user_username` (`user_name`),
                         UNIQUE KEY `UK6efs5vmce86ymf5q7lmvn2uuf` (`user_id`),
                         KEY `idx_user_name` (`name`),
                         KEY `idx_user_last_name` (`last_name`),
                         CONSTRAINT `FKhsyk09m70jfb8lejf57fvt7ng` FOREIGN KEY (`user_id`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Tabla de usuarios del sistema';