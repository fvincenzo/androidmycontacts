-- phpMyAdmin SQL Dump
-- version 2.11.3deb1ubuntu1.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generato il: 16 Nov, 2008 at 06:58 PM
-- Versione MySQL: 5.0.51
-- Versione PHP: 5.2.4-2ubuntu5.3

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `news`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `friends`
--

CREATE TABLE IF NOT EXISTS `friends` (
  `id` int(11) NOT NULL auto_increment,
  `user` varchar(256) NOT NULL,
  `friend` varchar(256) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Dump dei dati per la tabella `friends`
--


-- --------------------------------------------------------

--
-- Struttura della tabella `pendings`
--

CREATE TABLE IF NOT EXISTS `pendings` (
  `id` int(11) NOT NULL auto_increment,
  `user` varchar(256) NOT NULL,
  `pending` varchar(256) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Dump dei dati per la tabella `pendings`
--


