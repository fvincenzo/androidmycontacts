-- phpMyAdmin SQL Dump
-- version 2.11.3deb1ubuntu1.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generato il: 10 Nov, 2008 at 11:54 PM
-- Versione MySQL: 5.0.51
-- Versione PHP: 5.2.4-2ubuntu5.3

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `news`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `notizie`
--

CREATE TABLE IF NOT EXISTS `notizie` (
  `id` int(11) NOT NULL auto_increment,
  `titolo` varchar(512) NOT NULL,
  `notizia` varchar(4096) NOT NULL,
  `autore` varchar(256) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=10 ;

--
-- Dump dei dati per la tabella `notizie`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(256) NOT NULL,
  `surname` varchar(256) NOT NULL,
  `user` varchar(256) NOT NULL,
  `password` varchar(256) NOT NULL,
  `photo` varchar(4096) NOT NULL,
  `email` varchar(256) NOT NULL,
  `admin` tinyint(1) NOT NULL,
  `mobile` varchar(256) NOT NULL,
  `work` varchar(256) NOT NULL,
  `home` varchar(256) NOT NULL,
  `im` varchar(256) NOT NULL,
  `geo` varchar(256) NOT NULL,
  `preferred` varchar(256) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `user` (`user`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=13 ;

--
-- Dump dei dati per la tabella `users`
--

INSERT INTO `users` (`id`, `name`, `surname`, `user`, `password`, `photo`, `email`, `admin`, `mobile`, `work`, `home`, `im`, `geo`, `preferred`) VALUES
(12, 'Vincenzo', 'Frascino', 'test', 'test', 'users/BuddyIcon96x96.jpg', 'gabrielknight4@gmail.com', 1, '3280068879', '099881123', '09778773', 'gabrielknight4@inwind.it', '@37.422384,-122.096533', 'MOBILE');

