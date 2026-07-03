CREATE TABLE `user` (
    `user_id` INT(50) NOT NULL,
    `full_name` VARCHAR(50) NOT NULL,
    `mobile_number` VARCHAR(13) NOT NULL,
    `pin` VARCHAR(255) NOT NULL,
    `balance` DOUBLE NOT NULL,
    PRIMARY KEY (`user_id`),
    UNIQUE (`mobile_number`)
);

CREATE TABLE `transaction` (
    `transaction_id` INT(50) NOT NULL,
    `transaction_type` VARCHAR(20) NOT NULL,
    `amount` DOUBLE NOT NULL,
    `details` VARCHAR(50) NOT NULL,
    `date` DATE NOT NULL,
    `user_id` INT(50) NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
);

COMMIT;