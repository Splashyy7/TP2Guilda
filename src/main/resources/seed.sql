-- ============================================================
-- SEED DATA: API-Guilda
-- Cole este script no console do PostgreSQL
-- Pré-requisito: a aplicação já rodou pelo menos 1x (ddl-auto=update),
-- então as tabelas e sequences já existem.
-- ============================================================

BEGIN;

-- ============================================================
-- SCHEMA: audit
-- ============================================================

-- Organizacoes
INSERT INTO audit.organizacoes (id, nome, ativo, created_at) VALUES
  (1001, 'Guilda Solar',    true, NOW() - INTERVAL '90 days'),
  (1002, 'Ordem da Lua',    true, NOW() - INTERVAL '60 days'),
  (1003, 'Pacto da Aurora', true, NOW() - INTERVAL '30 days');

-- Permissions (code eh unique global; ignora insercao se ja existir)
INSERT INTO audit.permissions (id, code, descricao) VALUES
  (1001, 'AVENTUREIRO_READ',  'Visualizar aventureiros'),
  (1002, 'AVENTUREIRO_WRITE', 'Criar/editar aventureiros'),
  (1003, 'MISSAO_READ',       'Visualizar missoes'),
  (1004, 'MISSAO_WRITE',      'Criar/editar missoes'),
  (1005, 'RELATORIO_READ',    'Acessar relatorios'),
  (1006, 'AUDIT_READ',        'Ler trilhas de auditoria'),
  (1007, 'ADMIN_FULL',        'Administracao total')
ON CONFLICT (code) DO NOTHING;

-- Roles (uma por organizacao para simplificar)
INSERT INTO audit.roles (id, organizacao_id, nome, descricao, created_at) VALUES
  (1001, 1001, 'ADMIN',     'Acesso total na Guilda Solar',     NOW() - INTERVAL '90 days'),
  (1002, 1001, 'OPERADOR',  'Operacional Guilda Solar',         NOW() - INTERVAL '85 days'),
  (1003, 1001, 'VISITANTE', 'Somente leitura Guilda Solar',     NOW() - INTERVAL '85 days'),
  (1004, 1002, 'ADMIN',     'Acesso total na Ordem da Lua',     NOW() - INTERVAL '60 days'),
  (1005, 1002, 'OPERADOR',  'Operacional Ordem da Lua',         NOW() - INTERVAL '58 days'),
  (1006, 1003, 'ADMIN',     'Acesso total no Pacto da Aurora',  NOW() - INTERVAL '30 days');

INSERT INTO audit.role_permissions (role_id, permission_id)
SELECT 1001, id FROM audit.permissions WHERE code IN
  ('AVENTUREIRO_READ','AVENTUREIRO_WRITE','MISSAO_READ','MISSAO_WRITE','RELATORIO_READ','AUDIT_READ','ADMIN_FULL')
UNION ALL
SELECT 1002, id FROM audit.permissions WHERE code IN
  ('AVENTUREIRO_READ','AVENTUREIRO_WRITE','MISSAO_READ','MISSAO_WRITE','RELATORIO_READ')
UNION ALL
SELECT 1003, id FROM audit.permissions WHERE code IN
  ('AVENTUREIRO_READ','MISSAO_READ','RELATORIO_READ')
UNION ALL
SELECT 1004, id FROM audit.permissions WHERE code IN
  ('AVENTUREIRO_READ','AVENTUREIRO_WRITE','MISSAO_READ','MISSAO_WRITE','RELATORIO_READ','AUDIT_READ','ADMIN_FULL')
UNION ALL
SELECT 1005, id FROM audit.permissions WHERE code IN
  ('AVENTUREIRO_READ','MISSAO_READ','RELATORIO_READ')
UNION ALL
SELECT 1006, id FROM audit.permissions WHERE code IN
  ('AVENTUREIRO_READ','AVENTUREIRO_WRITE','MISSAO_READ','MISSAO_WRITE','RELATORIO_READ','AUDIT_READ','ADMIN_FULL');

-- Usuarios
INSERT INTO audit.usuarios
  (id, organizacao_id, nome, email, senha_hash, status, ultimo_login_em, created_at, updated_at) VALUES
  (1001, 1001, 'Aelin Galanodel', 'aelin@solar.gg', '$2a$10$abcdefghijklmnopqrstuv1', 'ATIVO',     NOW() - INTERVAL '1 day',  NOW() - INTERVAL '90 days', NOW()),
  (1002, 1001, 'Brom Forjafogo',  'brom@solar.gg',  '$2a$10$abcdefghijklmnopqrstuv2', 'ATIVO',     NOW() - INTERVAL '2 day',  NOW() - INTERVAL '88 days', NOW()),
  (1003, 1001, 'Cyra Ventoluz',   'cyra@solar.gg',  '$2a$10$abcdefghijklmnopqrstuv3', 'BLOQUEADO', NULL,                       NOW() - INTERVAL '40 days', NOW()),
  (1004, 1002, 'Dren Caligo',     'dren@lua.gg',    '$2a$10$abcdefghijklmnopqrstuv4', 'ATIVO',     NOW() - INTERVAL '3 day',  NOW() - INTERVAL '60 days', NOW()),
  (1005, 1002, 'Elara Selene',    'elara@lua.gg',   '$2a$10$abcdefghijklmnopqrstuv5', 'PENDENTE',  NULL,                       NOW() - INTERVAL '15 days', NOW()),
  (1006, 1003, 'Faun Aurora',     'faun@aurora.gg', '$2a$10$abcdefghijklmnopqrstuv6', 'ATIVO',     NOW() - INTERVAL '5 day',  NOW() - INTERVAL '30 days', NOW());

-- Vinculo user <-> role
INSERT INTO audit.user_roles (usuario_id, role_id) VALUES
  (1001, 1001), -- Aelin = ADMIN Solar
  (1002, 1002), -- Brom  = OPERADOR Solar
  (1003, 1003), -- Cyra  = VISITANTE Solar
  (1004, 1004), -- Dren  = ADMIN Lua
  (1005, 1005), -- Elara = OPERADOR Lua
  (1006, 1006); -- Faun  = ADMIN Pacto

-- API keys
INSERT INTO audit.api_keys
  (id, organizacao_id, nome, key_hash, ativo, created_at, last_used_at) VALUES
  (1001, 1001, 'integracao-relatorios', 'hash-1-solar', true, NOW() - INTERVAL '50 days', NOW() - INTERVAL '1 day'),
  (1002, 1002, 'integracao-painel',     'hash-2-lua',   true, NOW() - INTERVAL '20 days', NULL);

-- ============================================================
-- SCHEMA: aventura
-- ============================================================

-- Missoes
INSERT INTO aventura.missoes
  (id, organizacao_id, titulo, nivel_perigo, status, created_at, data_inicio, data_termino) VALUES
  (1001, 1001, 'Cacada ao Lobo Sombrio',           'BAIXO',   'CONCLUIDA',    NOW() - INTERVAL '40 days', NOW() - INTERVAL '38 days', NOW() - INTERVAL '35 days'),
  (1002, 1001, 'Resgate da Caravana Perdida',      'MEDIO',   'CONCLUIDA',    NOW() - INTERVAL '30 days', NOW() - INTERVAL '28 days', NOW() - INTERVAL '25 days'),
  (1003, 1001, 'Selar o Portal Demoniaco',         'EXTREMO', 'EM_ANDAMENTO', NOW() - INTERVAL '15 days', NOW() - INTERVAL '12 days', NULL),
  (1004, 1001, 'Mapear Floresta dos Sussurros',    'ALTO',    'PLANEJADA',    NOW() - INTERVAL '5 days',  NULL,                       NULL),
  (1005, 1002, 'Investigar Cripta de Selene',      'MEDIO',   'CONCLUIDA',    NOW() - INTERVAL '20 days', NOW() - INTERVAL '18 days', NOW() - INTERVAL '15 days'),
  (1006, 1002, 'Defender Vila de Pedraluz',        'ALTO',    'EM_ANDAMENTO', NOW() - INTERVAL '7 days',  NOW() - INTERVAL '5 days',  NULL),
  (1007, 1002, 'Negociar com a Corte de Espinhos', 'BAIXO',   'CANCELADA',    NOW() - INTERVAL '12 days', NULL,                       NULL),
  (1008, 1003, 'Despertar do Antigo',              'EXTREMO', 'PLANEJADA',    NOW() - INTERVAL '2 days',  NULL,                       NULL);

-- Aventureiros
INSERT INTO aventura.aventureiros
  (id, organizacao_id, cadastrado_por_id, nome, classe, nivel, ativo,
   companheiro_nome, companheiro_especie, companheiro_lealdade,
   created_at, updated_at) VALUES
  (1001, 1001, 1001, 'Thaelin Auroreflexo', 'GUERREIRO', 12, true,  'Faisca',   'DRAGAO_MINIATURA', 90, NOW() - INTERVAL '80 days', NOW()),
  (1002, 1001, 1001, 'Sylras Lumenor',      'MAGO',      15, true,  'Hoo',      'CORUJA',           85, NOW() - INTERVAL '75 days', NOW()),
  (1003, 1001, 1002, 'Korin Pedrabreve',    'CLERIGO',    9, true,  'Argo',     'LOBO',             70, NOW() - INTERVAL '70 days', NOW()),
  (1004, 1001, 1002, 'Vex Sombracorte',     'LADINO',    11, true,  'Mirko',    'CORUJA',           65, NOW() - INTERVAL '60 days', NOW()),
  (1005, 1001, 1001, 'Brielle Ventoarco',   'ARQUEIRO',  13, true,  'Cinder',   'DRAGAO_MINIATURA', 88, NOW() - INTERVAL '55 days', NOW()),
  (1006, 1001, 1002, 'Garran Forjacosta',   'GUERREIRO',  7, false, 'Granito',  'GOLEM',            55, NOW() - INTERVAL '50 days', NOW()),
  (1007, 1002, 1004, 'Lyra Caligae',        'MAGO',      14, true,  'Selene',   'CORUJA',           92, NOW() - INTERVAL '45 days', NOW()),
  (1008, 1002, 1004, 'Drogan Crepus',       'GUERREIRO', 10, true,  'Brutus',   'LOBO',             80, NOW() - INTERVAL '40 days', NOW()),
  (1009, 1002, 1004, 'Mira Estrelharte',    'ARQUEIRO',   8, true,  'Pilo',     'CORUJA',           60, NOW() - INTERVAL '35 days', NOW()),
  (1010, 1002, 1004, 'Saren Ondaprateada',  'CLERIGO',   12, true,  'Talo',     'LOBO',             75, NOW() - INTERVAL '30 days', NOW()),
  (1011, 1002, 1004, 'Yvan Sussurro',       'LADINO',     6, false, 'Pena',     'CORUJA',           50, NOW() - INTERVAL '25 days', NOW()),
  (1012, 1003, 1006, 'Aelar Auroral',       'MAGO',      18, true,  'Helios',   'DRAGAO_MINIATURA', 95, NOW() - INTERVAL '20 days', NOW()),
  (1013, 1003, 1006, 'Borin Pedraviva',     'GUERREIRO', 16, true,  'Granado',  'GOLEM',            85, NOW() - INTERVAL '18 days', NOW()),
  (1014, 1003, 1006, 'Cira Ventospleno',    'ARQUEIRO',  14, true,  'Zafiro',   'DRAGAO_MINIATURA', 82, NOW() - INTERVAL '15 days', NOW());

-- Participacoes em missao
INSERT INTO aventura.participacoes_missao
  (missao_id, aventureiro_id, papel, recompensa_ouro, destaque, created_at) VALUES
  -- Missao 1001 (CONCLUIDA, BAIXO) - Solar
  (1001, 1001, 'LIDER',       500.00, true,  NOW() - INTERVAL '38 days'),
  (1001, 1003, 'SUPORTE',     300.00, false, NOW() - INTERVAL '38 days'),
  (1001, 1005, 'ATAQUE',      400.00, false, NOW() - INTERVAL '38 days'),
  -- Missao 1002 (CONCLUIDA, MEDIO) - Solar
  (1002, 1002, 'LIDER',       900.00, true,  NOW() - INTERVAL '28 days'),
  (1002, 1004, 'EXPLORADOR',  600.00, false, NOW() - INTERVAL '28 days'),
  (1002, 1005, 'ATAQUE',      700.00, false, NOW() - INTERVAL '28 days'),
  (1002, 1001, 'DEFESA',      650.00, false, NOW() - INTERVAL '28 days'),
  -- Missao 1003 (EM_ANDAMENTO, EXTREMO) - Solar
  (1003, 1001, 'LIDER',      1500.00, false, NOW() - INTERVAL '12 days'),
  (1003, 1002, 'ATAQUE',     1300.00, false, NOW() - INTERVAL '12 days'),
  (1003, 1003, 'SUPORTE',    1100.00, false, NOW() - INTERVAL '12 days'),
  (1003, 1005, 'EXPLORADOR', 1200.00, false, NOW() - INTERVAL '12 days'),
  -- Missao 1005 (CONCLUIDA, MEDIO) - Lua
  (1005, 1007, 'LIDER',       800.00, true,  NOW() - INTERVAL '18 days'),
  (1005, 1008, 'DEFESA',      500.00, false, NOW() - INTERVAL '18 days'),
  (1005, 1010, 'SUPORTE',     500.00, false, NOW() - INTERVAL '18 days'),
  -- Missao 1006 (EM_ANDAMENTO, ALTO) - Lua
  (1006, 1007, 'LIDER',      1000.00, false, NOW() - INTERVAL '5 days'),
  (1006, 1008, 'ATAQUE',      900.00, false, NOW() - INTERVAL '5 days'),
  (1006, 1009, 'EXPLORADOR',  800.00, false, NOW() - INTERVAL '5 days'),
  (1006, 1010, 'SUPORTE',     850.00, false, NOW() - INTERVAL '5 days'),
  -- Missao 1008 (PLANEJADA, EXTREMO) - Pacto
  (1008, 1012, 'LIDER',      2000.00, false, NOW() - INTERVAL '2 days'),
  (1008, 1013, 'DEFESA',     1700.00, false, NOW() - INTERVAL '2 days'),
  (1008, 1014, 'EXPLORADOR', 1600.00, false, NOW() - INTERVAL '2 days');

-- ============================================================
-- SCHEMA: audit
-- ============================================================
INSERT INTO audit.audit_entries
  (organizacao_id, actor_user_id, actor_api_key_id, action, entity_schema, entity_name, entity_id,
   occurred_at, ip, user_agent, success) VALUES
  (1001, 1001, NULL, 'CREATE', 'aventura', 'Aventureiro', '1001', NOW() - INTERVAL '80 days', '10.0.0.1', 'curl/8.0',         true),
  (1001, 1001, NULL, 'CREATE', 'aventura', 'Missao',      '1001', NOW() - INTERVAL '40 days', '10.0.0.1', 'curl/8.0',         true),
  (1001, 1002, NULL, 'UPDATE', 'aventura', 'Missao',      '1001', NOW() - INTERVAL '35 days', '10.0.0.2', 'PostmanRuntime/7', true),
  (1001, NULL, 1001, 'EXPORT', 'aventura', 'Missao',      NULL,   NOW() - INTERVAL '1 day',   '10.0.0.9', 'service-bot',      true),
  (1002, 1004, NULL, 'CREATE', 'aventura', 'Aventureiro', '1007', NOW() - INTERVAL '45 days', '10.0.1.1', 'curl/8.0',         true),
  (1002, 1004, NULL, 'LOGIN',  'audit',    'User',        '1004', NOW() - INTERVAL '3 days',  '10.0.1.1', 'Mozilla/5.0',      true),
  (1003, 1006, NULL, 'CREATE', 'aventura', 'Missao',      '1008', NOW() - INTERVAL '2 days',  '10.0.2.1', 'curl/8.0',         true);

SELECT setval('audit.organizacoes_id_seq',  GREATEST((SELECT MAX(id) FROM audit.organizacoes),  1100));
SELECT setval('audit.permissions_id_seq',   GREATEST((SELECT MAX(id) FROM audit.permissions),   1100));
SELECT setval('audit.roles_id_seq',         GREATEST((SELECT MAX(id) FROM audit.roles),         1100));
SELECT setval('audit.usuarios_id_seq',      GREATEST((SELECT MAX(id) FROM audit.usuarios),      1100));
SELECT setval('audit.api_keys_id_seq',      GREATEST((SELECT MAX(id) FROM audit.api_keys),      1100));
SELECT setval('audit.audit_entries_id_seq', GREATEST((SELECT MAX(id) FROM audit.audit_entries), 1100));
SELECT setval('aventura.avent_sequence',    GREATEST((SELECT MAX(id) FROM aventura.aventureiros), 1100));
SELECT setval('aventura.missao_sequence',   GREATEST((SELECT MAX(id) FROM aventura.missoes),    1100));

COMMIT;
