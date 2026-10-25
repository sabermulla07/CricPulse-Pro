-- =====================================================================
-- CricPulse Pro: Real-Time Cricket Score Management System
-- Database Schema & Initial Seed Data for MySQL 8.0
-- Database Name: cricket_score_db
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `cricket_score_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `cricket_score_db`;

-- Drop existing tables in reverse foreign key order
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `ball_events`;
DROP TABLE IF EXISTS `fall_of_wickets`;
DROP TABLE IF EXISTS `bowling_scorecards`;
DROP TABLE IF EXISTS `batting_scorecards`;
DROP TABLE IF EXISTS `match_innings`;
DROP TABLE IF EXISTS `matches`;
DROP TABLE IF EXISTS `players`;
DROP TABLE IF EXISTS `teams`;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Teams Table
CREATE TABLE `teams` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL UNIQUE,
    `short_code` VARCHAR(10) NOT NULL,
    `primary_color` VARCHAR(20) DEFAULT '#1e3a8a',
    `secondary_color` VARCHAR(20) DEFAULT '#38bdf8',
    `flag_emoji` VARCHAR(20) DEFAULT '🏏',
    `logo_url` VARCHAR(255) DEFAULT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Players Table
CREATE TABLE `players` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `team_id` BIGINT NOT NULL,
    `role` VARCHAR(30) NOT NULL, -- BATSMAN, BOWLER, ALL_ROUNDER, WICKET_KEEPER
    `batting_style` VARCHAR(50) DEFAULT NULL,
    `bowling_style` VARCHAR(50) DEFAULT NULL,
    `jersey_number` INT DEFAULT NULL,
    `is_captain` BOOLEAN DEFAULT FALSE,
    `is_wicket_keeper` BOOLEAN DEFAULT FALSE,
    `matches_played` INT DEFAULT 0,
    `runs_scored` INT DEFAULT 0,
    `highest_score` INT DEFAULT 0,
    `batting_average` DOUBLE DEFAULT 0.0,
    `batting_strike_rate` DOUBLE DEFAULT 0.0,
    `centuries` INT DEFAULT 0,
    `fifties` INT DEFAULT 0,
    `wickets_taken` INT DEFAULT 0,
    `bowling_average` DOUBLE DEFAULT 0.0,
    `bowling_economy` DOUBLE DEFAULT 0.0,
    `best_bowling` VARCHAR(20) DEFAULT '0/0',
    CONSTRAINT `fk_player_team` FOREIGN KEY (`team_id`) REFERENCES `teams`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Matches Table
CREATE TABLE `matches` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `match_title` VARCHAR(200) NOT NULL,
    `series_name` VARCHAR(150) DEFAULT NULL,
    `match_type` VARCHAR(20) DEFAULT 'T20',
    `venue` VARCHAR(150) DEFAULT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'UPCOMING', -- UPCOMING, LIVE, INNINGS_BREAK, COMPLETED, ABANDONED
    `team_a_id` BIGINT NOT NULL,
    `team_b_id` BIGINT NOT NULL,
    `toss_winner` VARCHAR(10) DEFAULT NULL,
    `toss_decision` VARCHAR(10) DEFAULT NULL, -- BAT, BOWL
    `total_overs` INT DEFAULT 20,
    `current_innings` INT DEFAULT 1,
    `target` INT DEFAULT 0,
    `result_summary` VARCHAR(255) DEFAULT NULL,
    `player_of_the_match` VARCHAR(100) DEFAULT NULL,
    `is_simulation_active` BOOLEAN DEFAULT FALSE,
    `simulation_speed_ms` INT DEFAULT 2000,
    `scheduled_at` DATETIME DEFAULT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_match_team_a` FOREIGN KEY (`team_a_id`) REFERENCES `teams`(`id`),
    CONSTRAINT `fk_match_team_b` FOREIGN KEY (`team_b_id`) REFERENCES `teams`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Match Innings Table
CREATE TABLE `match_innings` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `match_id` BIGINT NOT NULL,
    `innings_number` INT NOT NULL, -- 1 or 2
    `batting_team_id` BIGINT NOT NULL,
    `bowling_team_id` BIGINT NOT NULL,
    `total_runs` INT DEFAULT 0,
    `total_wickets` INT DEFAULT 0,
    `overs_bowled` DOUBLE DEFAULT 0.0,
    `legal_balls` INT DEFAULT 0,
    `extras` INT DEFAULT 0,
    `wides` INT DEFAULT 0,
    `no_balls` INT DEFAULT 0,
    `byes` INT DEFAULT 0,
    `leg_byes` INT DEFAULT 0,
    `current_run_rate` DOUBLE DEFAULT 0.0,
    `required_run_rate` DOUBLE DEFAULT 0.0,
    `is_completed` BOOLEAN DEFAULT FALSE,
    CONSTRAINT `fk_innings_match` FOREIGN KEY (`match_id`) REFERENCES `matches`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_innings_bat_team` FOREIGN KEY (`batting_team_id`) REFERENCES `teams`(`id`),
    CONSTRAINT `fk_innings_bowl_team` FOREIGN KEY (`bowling_team_id`) REFERENCES `teams`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Batting Scorecards Table
CREATE TABLE `batting_scorecards` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `innings_id` BIGINT NOT NULL,
    `player_id` BIGINT NOT NULL,
    `batting_order` INT DEFAULT 1,
    `runs` INT DEFAULT 0,
    `balls` INT DEFAULT 0,
    `fours` INT DEFAULT 0,
    `sixes` INT DEFAULT 0,
    `strike_rate` DOUBLE DEFAULT 0.0,
    `is_out` BOOLEAN DEFAULT FALSE,
    `dismissal` VARCHAR(150) DEFAULT 'not out',
    `dismissal_type` VARCHAR(50) DEFAULT NULL,
    `bowler_id` BIGINT DEFAULT NULL,
    `fielder_id` BIGINT DEFAULT NULL,
    `is_on_crease` BOOLEAN DEFAULT FALSE,
    `is_striker` BOOLEAN DEFAULT FALSE,
    CONSTRAINT `fk_batting_innings` FOREIGN KEY (`innings_id`) REFERENCES `match_innings`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_batting_player` FOREIGN KEY (`player_id`) REFERENCES `players`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Bowling Scorecards Table
CREATE TABLE `bowling_scorecards` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `innings_id` BIGINT NOT NULL,
    `player_id` BIGINT NOT NULL,
    `bowling_order` INT DEFAULT 1,
    `overs` DOUBLE DEFAULT 0.0,
    `legal_balls` INT DEFAULT 0,
    `maidens` INT DEFAULT 0,
    `runs_conceded` INT DEFAULT 0,
    `wickets` INT DEFAULT 0,
    `economy` DOUBLE DEFAULT 0.0,
    `dots` INT DEFAULT 0,
    `fours_conceded` INT DEFAULT 0,
    `sixes_conceded` INT DEFAULT 0,
    `wides` INT DEFAULT 0,
    `no_balls` INT DEFAULT 0,
    `is_current_bowler` BOOLEAN DEFAULT FALSE,
    CONSTRAINT `fk_bowling_innings` FOREIGN KEY (`innings_id`) REFERENCES `match_innings`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_bowling_player` FOREIGN KEY (`player_id`) REFERENCES `players`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Fall of Wickets Table
CREATE TABLE `fall_of_wickets` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `innings_id` BIGINT NOT NULL,
    `wicket_number` INT NOT NULL,
    `player_id` BIGINT NOT NULL,
    `score_at_dismissal` INT NOT NULL,
    `over_at_dismissal` VARCHAR(10) NOT NULL,
    CONSTRAINT `fk_fow_innings` FOREIGN KEY (`innings_id`) REFERENCES `match_innings`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_fow_player` FOREIGN KEY (`player_id`) REFERENCES `players`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Ball Events & Commentary Table
CREATE TABLE `ball_events` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `match_id` BIGINT NOT NULL,
    `innings_id` BIGINT NOT NULL,
    `over_number` INT NOT NULL,
    `ball_number_in_over` INT NOT NULL,
    `ball_display` VARCHAR(10) NOT NULL,
    `batsman_id` BIGINT DEFAULT NULL,
    `non_striker_id` BIGINT DEFAULT NULL,
    `bowler_id` BIGINT DEFAULT NULL,
    `runs_scored` INT DEFAULT 0,
    `is_wicket` BOOLEAN DEFAULT FALSE,
    `wicket_type` VARCHAR(50) DEFAULT NULL,
    `out_player_id` BIGINT DEFAULT NULL,
    `is_extra` BOOLEAN DEFAULT FALSE,
    `extra_type` VARCHAR(50) DEFAULT NULL,
    `extra_runs` INT DEFAULT 0,
    `total_ball_runs` INT DEFAULT 0,
    `commentary` TEXT,
    `score_after_ball` VARCHAR(20) DEFAULT NULL,
    `timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_ball_match` FOREIGN KEY (`match_id`) REFERENCES `matches`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_ball_innings` FOREIGN KEY (`innings_id`) REFERENCES `match_innings`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- Initial Seed Data: Teams
-- =====================================================================
INSERT INTO `teams` (`id`, `name`, `short_code`, `primary_color`, `secondary_color`, `flag_emoji`) VALUES
(1, 'India', 'IND', '#1e3a8a', '#38bdf8', '🇮🇳'),
(2, 'Australia', 'AUS', '#b45309', '#facc15', '🇦🇺'),
(3, 'England', 'ENG', '#b91c1c', '#f87171', '🏴󠁧󠁢󠁥󠁮󠁧󠁿'),
(4, 'South Africa', 'SA', '#047857', '#34d399', '🇿🇦')
ON DUPLICATE KEY UPDATE `name`=`name`;

-- Initial Seed Data: Star Players
INSERT INTO `players` (`id`, `name`, `team_id`, `role`, `batting_style`, `bowling_style`, `jersey_number`, `is_captain`, `is_wicket_keeper`, `matches_played`, `runs_scored`, `highest_score`, `batting_average`, `batting_strike_rate`, `wickets_taken`, `bowling_economy`) VALUES
(1, 'Rohit Sharma', 1, 'BATSMAN', 'Right-hand bat', 'Right-arm offbreak', 45, TRUE, FALSE, 158, 4231, 121, 32.5, 140.8, 0, 0.0),
(2, 'Virat Kohli', 1, 'BATSMAN', 'Right-hand bat', 'Right-arm medium', 18, FALSE, FALSE, 125, 4188, 122, 48.7, 137.9, 4, 8.1),
(3, 'Suryakumar Yadav', 1, 'BATSMAN', 'Right-hand bat', 'Right-arm medium', 63, FALSE, FALSE, 72, 2340, 117, 43.3, 168.5, 0, 0.0),
(4, 'Jasprit Bumrah', 1, 'BOWLER', 'Right-hand bat', 'Right-arm fast', 93, FALSE, FALSE, 70, 38, 16, 7.6, 68.0, 89, 6.27),
(5, 'Travis Head', 2, 'BATSMAN', 'Left-hand bat', 'Right-arm offbreak', 62, FALSE, FALSE, 40, 1098, 91, 31.3, 154.2, 3, 8.4),
(6, 'Glenn Maxwell', 2, 'ALL_ROUNDER', 'Right-hand bat', 'Right-arm offbreak', 32, FALSE, FALSE, 106, 2600, 145, 29.5, 155.4, 43, 7.5),
(7, 'Pat Cummins', 2, 'BOWLER', 'Right-hand bat', 'Right-arm fast', 30, FALSE, FALSE, 55, 185, 28, 12.3, 120.0, 66, 7.35),
(8, 'Jos Buttler', 3, 'WICKET_KEEPER', 'Right-hand bat', 'None', 63, TRUE, TRUE, 124, 3264, 101, 35.8, 146.3, 0, 0.0),
(9, 'Adil Rashid', 3, 'BOWLER', 'Right-hand bat', 'Right-arm legbreak', 95, FALSE, FALSE, 114, 115, 22, 6.7, 85.0, 120, 7.38),
(10, 'Heinrich Klaasen', 4, 'BATSMAN', 'Right-hand bat', 'Right-arm offbreak', 45, FALSE, FALSE, 52, 980, 81, 24.5, 147.2, 0, 0.0)
ON DUPLICATE KEY UPDATE `name`=`name`;

-- End of Schema
