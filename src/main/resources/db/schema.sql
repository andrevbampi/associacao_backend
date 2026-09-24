-- Estrutura do banco 'associacao' (MariaDB). Apenas documentação: não é executado automaticamente.
-- Gerado com: mariadb-dump --no-data --skip-comments associacao

CREATE TABLE `pessoa` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tipo` int(11) NOT NULL DEFAULT 1 COMMENT '1 = física; 2 = jurídica',
  `nome` varchar(240) NOT NULL,
  `documento` varchar(30) NOT NULL,
  `datanascimento` date DEFAULT NULL,
  `telefone` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `endereco` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
CREATE TABLE `status_membro` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
CREATE TABLE `usuario` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(100) NOT NULL,
  `senha` varchar(100) DEFAULT NULL,
  `idpessoa` int(11) NOT NULL,
  `ativo` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `usuario_unique` (`login`),
  UNIQUE KEY `usuario_unique_1` (`idpessoa`),
  CONSTRAINT `usuario_pessoa_FK` FOREIGN KEY (`idpessoa`) REFERENCES `pessoa` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
CREATE TABLE `membro` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idpessoa` int(11) NOT NULL,
  `ativo` tinyint(1) NOT NULL,
  `idstatus` int(11) NOT NULL,
  `datainclusao` date NOT NULL,
  `datasaida` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `membro_unique` (`idpessoa`),
  KEY `membro_status_membro_FK` (`idstatus`),
  CONSTRAINT `membro_pessoa_FK` FOREIGN KEY (`idpessoa`) REFERENCES `pessoa` (`id`),
  CONSTRAINT `membro_status_membro_FK` FOREIGN KEY (`idstatus`) REFERENCES `status_membro` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
CREATE TABLE `marco_membro` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(255) NOT NULL,
  `data` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


