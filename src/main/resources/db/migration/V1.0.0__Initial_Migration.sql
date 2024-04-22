CREATE TABLE IF NOT EXISTS `transactions` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `dateTime` DATETIME NOT NULL,
    `stockTicker` VARCHAR(64) NOT NULL,
    `quantity` INT NOT NULL,
    `purchasePrice` DECIMAL(10, 2) NOT NULL,
    `totalCost` DECIMAL(10, 2) NOT NULL,
    `transactionType` VARCHAR(64) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `User`(`UserID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    );

CREATE TABLE IF NOT EXISTS `User` (
    `UserID` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `Username` VARCHAR(255) NOT NULL UNIQUE,
    `Password` VARCHAR(255) NOT NULL,
    `isAdmin` TINYINT(1) NOT NULL DEFAULT 0,
    `balance` DECIMAL(10, 2) NOT NULL DEFAULT 10000.00
    );

CREATE TABLE IF NOT EXISTS `Role` (
    `RoleID` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `name` ENUM('USER', 'ADMIN') NOT NULL
    );

CREATE TABLE IF NOT EXISTS `User_Roles` (
    `UserID` BIGINT UNSIGNED,
    `RoleID` BIGINT UNSIGNED,
    PRIMARY KEY (`UserID`, `RoleID`),
    FOREIGN KEY (`UserID`) REFERENCES `User`(`UserID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    FOREIGN KEY (`RoleID`) REFERENCES `Role`(`RoleID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    );

CREATE TABLE IF NOT EXISTS `Portfolio` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `totalCost` DECIMAL(10, 2) NOT NULL,
    `totalValue` DECIMAL(10, 2) NOT NULL,
    `totalChangePercent` DECIMAL(5, 2) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `User`(`UserID`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    );

CREATE TABLE IF NOT EXISTS `PortfolioItem` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `portfolio_id` BIGINT UNSIGNED NOT NULL,
    `stock_ticker` VARCHAR(10) NOT NULL,
    `quantity` INT NOT NULL,
    `purchasePrice` DECIMAL(10, 2) NOT NULL,
    `isEditing` TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`portfolio_id`) REFERENCES `Portfolio`(`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    FOREIGN KEY (`stock_ticker`) REFERENCES `PortfolioStock`(`ticker`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    );

CREATE TABLE IF NOT EXISTS `PortfolioStock` (
    `ticker` VARCHAR(10) NOT NULL,
    `name` VARCHAR(64) NOT NULL,
    `currentPrice` DECIMAL(10, 2) NOT NULL,
    `sector` VARCHAR(64) NULL,
    `fiftyTwoWeekHigh` DECIMAL(10, 2) NULL,
    `fiftyTwoWeekLow` DECIMAL(10, 2) NULL,
    `regularMarketChangePercent` DECIMAL(5, 2) NULL,
    PRIMARY KEY (`ticker`)
    );
