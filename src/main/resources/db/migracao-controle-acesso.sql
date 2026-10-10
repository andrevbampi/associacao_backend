-- Controle de acesso por grupos e permissões (executar UMA vez antes de subir a nova versão da API).
-- Ao iniciar, a API sincroniza a tabela `permissao` com o catálogo do código e, se não houver
-- nenhum grupo, cria os grupos padrão e coloca todos os usuários existentes em "Administrador".

CREATE TABLE `grupo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `ativo` tinyint(1) NOT NULL DEFAULT 1,
  `administrador` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `grupo_unique` (`nome`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE `permissao` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `codigo` varchar(100) NOT NULL,
  `modulo` varchar(100) NOT NULL,
  `descricao` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `permissao_unique` (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE `grupo_permissao` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idgrupo` int(11) NOT NULL,
  `idpermissao` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `grupo_permissao_unique` (`idgrupo`,`idpermissao`),
  KEY `grupo_permissao_permissao_FK` (`idpermissao`),
  CONSTRAINT `grupo_permissao_grupo_FK` FOREIGN KEY (`idgrupo`) REFERENCES `grupo` (`id`),
  CONSTRAINT `grupo_permissao_permissao_FK` FOREIGN KEY (`idpermissao`) REFERENCES `permissao` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE `usuario_grupo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idusuario` int(11) NOT NULL,
  `idgrupo` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `usuario_grupo_unique` (`idusuario`,`idgrupo`),
  KEY `usuario_grupo_grupo_FK` (`idgrupo`),
  CONSTRAINT `usuario_grupo_usuario_FK` FOREIGN KEY (`idusuario`) REFERENCES `usuario` (`id`),
  CONSTRAINT `usuario_grupo_grupo_FK` FOREIGN KEY (`idgrupo`) REFERENCES `grupo` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE `usuario_permissao` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idusuario` int(11) NOT NULL,
  `idpermissao` int(11) NOT NULL,
  `efeito` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `usuario_permissao_unique` (`idusuario`,`idpermissao`),
  KEY `usuario_permissao_permissao_FK` (`idpermissao`),
  CONSTRAINT `usuario_permissao_usuario_FK` FOREIGN KEY (`idusuario`) REFERENCES `usuario` (`id`),
  CONSTRAINT `usuario_permissao_permissao_FK` FOREIGN KEY (`idpermissao`) REFERENCES `permissao` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- `login` guarda o login de quem fez a alteração (continua legível mesmo se o usuário for excluído).
CREATE TABLE `auditoria_acesso` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `datahora` datetime NOT NULL,
  `idusuario` int(11) DEFAULT NULL,
  `login` varchar(100) NOT NULL,
  `acao` varchar(30) NOT NULL,
  `entidade` varchar(30) NOT NULL,
  `identidade` int(11) DEFAULT NULL,
  `descricao` varchar(1000) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `auditoria_acesso_usuario_FK` (`idusuario`),
  KEY `auditoria_acesso_datahora_IDX` (`datahora`),
  CONSTRAINT `auditoria_acesso_usuario_FK` FOREIGN KEY (`idusuario`) REFERENCES `usuario` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
