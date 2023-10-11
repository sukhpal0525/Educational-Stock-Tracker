SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE `WebUser`;

-- Users
INSERT INTO `WebUser` (`UserID`, `UserName`, `Password`) VALUES
-- Admin Account (Password = 'admin')
(1, 'Admin', '$2a$10$AOYMzvkn8MISJsQR3Q0kXO0ZmFMJXQO6mw7IH/p2PfpJO.zCfrLlK'),
-- Test Account  (Password = 'admin')
(2, 'Test User', '$2a$10$AOYMzvkn8MISJsQR3Q0kXO0ZmFMJXQO6mw7IH/p2PfpJO.zCfrLlK');
