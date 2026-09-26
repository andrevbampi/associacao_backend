-- Estrutura do banco 'associacao' (MariaDB). Apenas documentação: não é executado automaticamente.
-- Gerado com: mariadb-dump --no-data --skip-comments associacao


CREATE TABLE `categoria_financeira` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(100) NOT NULL,
  `tipo` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `categoria_financeira_unique` (`descricao`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
CREATE TABLE `categoria_produto` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
CREATE TABLE `comanda` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idpessoa` int(11) DEFAULT NULL,
  `nometemporario` varchar(120) DEFAULT NULL,
  `dataabertura` datetime NOT NULL,
  `datafechamento` datetime DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `valortotal` decimal(10,2) NOT NULL DEFAULT 0.00,
  `pago` tinyint(1) NOT NULL DEFAULT 0,
  `datapagamento` datetime DEFAULT NULL,
  `formapagamento` varchar(20) DEFAULT NULL,
  `observacao` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `comanda_pessoa_FK` (`idpessoa`),
  CONSTRAINT `comanda_pessoa_FK` FOREIGN KEY (`idpessoa`) REFERENCES `pessoa` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
CREATE TABLE `historico_membro` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idmembro` int(11) NOT NULL,
  `idtipoevento` int(11) NOT NULL,
  `descricao` varchar(255) NOT NULL,
  `data` datetime NOT NULL,
  `idusuarioregistro` int(11) NOT NULL,
  `observacao` varchar(500) DEFAULT NULL,
  `ativo` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `historico_membro_membro_FK` (`idmembro`),
  KEY `historico_membro_tipo_evento_FK` (`idtipoevento`),
  KEY `historico_membro_usuario_FK` (`idusuarioregistro`),
  CONSTRAINT `historico_membro_membro_FK` FOREIGN KEY (`idmembro`) REFERENCES `membro` (`id`),
  CONSTRAINT `historico_membro_tipo_evento_FK` FOREIGN KEY (`idtipoevento`) REFERENCES `tipo_evento` (`id`),
  CONSTRAINT `historico_membro_usuario_FK` FOREIGN KEY (`idusuarioregistro`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
CREATE TABLE `item_comanda` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idcomanda` int(11) NOT NULL,
  `idproduto` int(11) NOT NULL,
  `quantidade` int(11) NOT NULL,
  `precounitario` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `item_comanda_comanda_FK` (`idcomanda`),
  KEY `item_comanda_produto_FK` (`idproduto`),
  CONSTRAINT `item_comanda_comanda_FK` FOREIGN KEY (`idcomanda`) REFERENCES `comanda` (`id`),
  CONSTRAINT `item_comanda_produto_FK` FOREIGN KEY (`idproduto`) REFERENCES `produto` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
CREATE TABLE `lancamento_financeiro` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idcategoriafinanceira` int(11) NOT NULL,
  `tipo` varchar(20) NOT NULL,
  `valor` decimal(10,2) NOT NULL,
  `data` date NOT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `idpessoa` int(11) DEFAULT NULL,
  `idmembro` int(11) DEFAULT NULL,
  `idcomanda` int(11) DEFAULT NULL,
  `idusuario` int(11) NOT NULL,
  `observacao` varchar(500) DEFAULT NULL,
  `pago` tinyint(1) NOT NULL DEFAULT 0,
  `datapagamento` datetime DEFAULT NULL,
  `formapagamento` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `lancamento_financeiro_categoria_FK` (`idcategoriafinanceira`),
  KEY `lancamento_financeiro_pessoa_FK` (`idpessoa`),
  KEY `lancamento_financeiro_membro_FK` (`idmembro`),
  KEY `lancamento_financeiro_comanda_FK` (`idcomanda`),
  KEY `lancamento_financeiro_usuario_FK` (`idusuario`),
  CONSTRAINT `lancamento_financeiro_categoria_FK` FOREIGN KEY (`idcategoriafinanceira`) REFERENCES `categoria_financeira` (`id`),
  CONSTRAINT `lancamento_financeiro_comanda_FK` FOREIGN KEY (`idcomanda`) REFERENCES `comanda` (`id`),
  CONSTRAINT `lancamento_financeiro_membro_FK` FOREIGN KEY (`idmembro`) REFERENCES `membro` (`id`),
  CONSTRAINT `lancamento_financeiro_pessoa_FK` FOREIGN KEY (`idpessoa`) REFERENCES `pessoa` (`id`),
  CONSTRAINT `lancamento_financeiro_usuario_FK` FOREIGN KEY (`idusuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
CREATE TABLE `marco_membro` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(255) NOT NULL,
  `data` datetime NOT NULL,
  PRIMARY KEY (`id`)
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
CREATE TABLE `movimento_estoque` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idproduto` int(11) NOT NULL,
  `tipo` varchar(20) NOT NULL,
  `quantidade` int(11) NOT NULL,
  `estoqueanterior` int(11) NOT NULL,
  `estoqueposterior` int(11) NOT NULL,
  `datahora` datetime NOT NULL,
  `idusuario` int(11) NOT NULL,
  `observacao` varchar(500) DEFAULT NULL,
  `origem` varchar(20) NOT NULL,
  `idorigem` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `movimento_estoque_produto_FK` (`idproduto`),
  KEY `movimento_estoque_usuario_FK` (`idusuario`),
  CONSTRAINT `movimento_estoque_produto_FK` FOREIGN KEY (`idproduto`) REFERENCES `produto` (`id`),
  CONSTRAINT `movimento_estoque_usuario_FK` FOREIGN KEY (`idusuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
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
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
CREATE TABLE `produto` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(150) NOT NULL,
  `preco` decimal(10,2) NOT NULL,
  `precomembro` decimal(10,2) NOT NULL,
  `ativo` tinyint(1) NOT NULL DEFAULT 1,
  `idcategoria` int(11) NOT NULL,
  `estoqueatual` int(11) NOT NULL DEFAULT 0,
  `estoqueminimo` int(11) DEFAULT NULL,
  `controlaestoque` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `produto_categoria_produto_FK` (`idcategoria`),
  CONSTRAINT `produto_categoria_produto_FK` FOREIGN KEY (`idcategoria`) REFERENCES `categoria_produto` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
CREATE TABLE `status_membro` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
CREATE TABLE `tipo_evento` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tipo_evento_unique` (`descricao`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
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
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
