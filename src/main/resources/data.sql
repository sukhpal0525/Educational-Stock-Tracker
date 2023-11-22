SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `User`;

INSERT INTO `User` (`UserID`, `Username`, `Password`) VALUES

    (1, 'admin', '$2a$10$AOYMzvkn8MISJsQR3Q0kXO0ZmFMJXQO6mw7IH/p2PfpJO.zCfrLlK'),

    -- Test User    (Password: test)
    (2, 'test', '$2a$12$Dc4CevIaPk6RxmW/xM.ef.mWXn.svcAdiTJ0lNnxjwMtra.uLeu1C');