-- MySQL Script generated by MySQL Workbench
-- 12/15/16 11:09:41
-- Model: New Model    Version: 1.0
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema forum_22
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `forum_22` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `forum_22` ;

-- -----------------------------------------------------
-- Table `forum_22`.`t_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `forum_22`.`t_user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NOT NULL COMMENT '用户名，唯一，且不能修改',
  `password` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL COMMENT '注册邮箱',
  `state` TINYINT NOT NULL COMMENT '账号状�' /* comment truncated */ /*�，0 未激活
1 已激活（验证邮箱）
2 已禁用*/,
  `createtime` TIMESTAMP NULL DEFAULT current_timestamp COMMENT '创建时间',
  `avator` VARCHAR(50) NOT NULL COMMENT '用户头像，连接七牛云',
  `tel` VARCHAR(11) NULL COMMENT '账号手机号',
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `forum_22`.`login_log`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `forum_22`.`login_log` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `logintime` TIMESTAMP NOT NULL DEFAULT current_times COMMENT '用户访问时间',
  `ip` VARCHAR(45) NOT NULL COMMENT '用户访问ip',
  `t_user_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_login_log_t_user_idx` (`t_user_id` ASC),
  CONSTRAINT `fk_login_log_t_user`
    FOREIGN KEY (`t_user_id`)
    REFERENCES `forum_22`.`t_user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;