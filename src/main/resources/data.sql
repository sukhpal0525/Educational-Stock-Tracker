SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `User`;

INSERT INTO `User` (`UserID`, `Username`, `Password`) VALUES
(1, 'admin', '$2a$10$AOYMzvkn8MISJsQR3Q0kXO0ZmFMJXQO6mw7IH/p2PfpJO.zCfrLlK'),
-- Test User    (Password: test)
(2, 'test', '$2a$12$yx2YbPbRSvI/1aCJXV7SeOe9y51tIqWtOEhJCm5kSLCDwOCZsSNKO');