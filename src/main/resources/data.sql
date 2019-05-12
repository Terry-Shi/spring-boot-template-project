-- 本文件存放初始化数据

-- password is 'admin_kunming'
INSERT INTO user (username, password, displayname, create_time)
SELECT 'admin', '83cfb7f0dda5f0efc09d0d90bf80c025', '默认管理员', CURRENT_TIMESTAMP() 
From dual 
WHERE NOT EXISTS (Select 1 From user WHERE user_id = 'admin') limit 1;


INSERT INTO user_roles (`user_id`, `role_id`, create_time)
SELECT '1', '1', CURRENT_TIMESTAMP()
From dual 
WHERE NOT EXISTS (Select 1 From user_roles WHERE username='admin') LIMIT 1;

-- ROLES
/**
INSERT INTO roles (`role`, `desc`, create_time)
SELECT 'SYS_ADMIN', '系统管理员', CURRENT_TIMESTAMP()
From dual 
WHERE NOT EXISTS (Select 1 From roles WHERE role_id='SYS_ADMIN') LIMIT 1;

INSERT INTO roles (`role`, `desc`, create_time)
SELECT 'GUEST', '访客', CURRENT_TIMESTAMP()
From dual 
WHERE NOT EXISTS (Select 1 From roles WHERE role='GUEST') LIMIT 1;

INSERT INTO roles (`role`, `desc`, create_time)
SELECT 'MAINTAINER', '数据维护人员', CURRENT_TIMESTAMP()
From dual 
WHERE NOT EXISTS (Select 1 From roles WHERE role='MAINTAINER') LIMIT 1;

*/