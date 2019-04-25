-- 本文件存放初始化数据

-- password is 'admin_kunming'
INSERT INTO client (client_id, password, client_name, create_time)
SELECT 'admin', '83cfb7f0dda5f0efc09d0d90bf80c025', '默认管理员', CURRENT_TIMESTAMP() 
From dual 
WHERE NOT EXISTS (Select 1 From client WHERE client_id = 'admin') limit 1;


INSERT INTO client_roles (`client_id`, `service_name`, `role`, create_time)
SELECT 'admin', 'SYS', 'SYS_ADMIN', CURRENT_TIMESTAMP()
From dual 
WHERE NOT EXISTS (Select 1 From client_roles WHERE client_id='admin') LIMIT 1;

-- ROLES
INSERT INTO roles (`role_id`, `role_display_name`, create_time)
SELECT 'SYS_ADMIN', '系统管理员', CURRENT_TIMESTAMP()
From dual 
WHERE NOT EXISTS (Select 1 From roles WHERE role_id='SYS_ADMIN') LIMIT 1;

INSERT INTO roles (`role_id`, `role_display_name`, create_time)
SELECT 'GUEST', '访客', CURRENT_TIMESTAMP()
From dual 
WHERE NOT EXISTS (Select 1 From roles WHERE role_id='GUEST') LIMIT 1;

INSERT INTO roles (`role_id`, `role_display_name`, create_time)
SELECT 'MAINTAINER', '数据维护人员', CURRENT_TIMESTAMP()
From dual 
WHERE NOT EXISTS (Select 1 From roles WHERE role_id='MAINTAINER') LIMIT 1;

