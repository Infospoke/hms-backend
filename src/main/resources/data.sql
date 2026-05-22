-- =========================================
-- MODULE
-- =========================================

INSERT INTO tb_module 
(id, module_id, parent_id, module_name, created_date, created_by, updated_by, updated_date)
VALUES
(1, 1, 0, 'Admin', '2026-04-22', 'admin', NULL, NULL),
(2, 2, 0, 'HR', '2026-04-22', 'admin', NULL, NULL),
(3, 3, 1, 'User Management', '2026-04-22', 'admin', NULL, NULL),
(4, 4, 2, 'Employee Management', '2026-04-22', 'admin', NULL, NULL)
ON CONFLICT (id) DO NOTHING;


-- =========================================
-- USER TYPE
-- =========================================

INSERT INTO tb_user_type (id, user_type)
VALUES 
(1, 'Employee'),
(2, 'Manager'),
(3, 'HR'),
(4, 'Admin'),
(5, 'Client')
ON CONFLICT (id) DO NOTHING;


-- =========================================
-- EMPLOYEMENT TYPE
-- =========================================

INSERT INTO tb_employement_type (id, employement_type)
VALUES 
(1, 'Full Time'),
(2, 'Part Time'),
(3, 'Intern'),
(4, 'Contract')
ON CONFLICT (id) DO NOTHING;


-- =========================================
-- BUSINESS UNIT
-- =========================================

INSERT INTO tb_business_unit (id, business_name)
VALUES
(1, 'Human Capital Management'),
(2, 'Corporate Services'),
(3, 'Executive Office'),
(4, 'Technology Services'),
(5, 'Enterprise Applications (ERP & SAP)'),
(6, 'Sales & Marketing'),
(7, 'Customer Experience & Support'),
(8, 'Finance & Procurement'),
(9, 'Business Consulting & Strategy')
ON CONFLICT (id) DO NOTHING;


-- =========================================
-- DEPARTMENTS
-- =========================================

INSERT INTO tb_departments 
(id, department_name, dept_code, business_unit_id)
VALUES

(1, 'Talent Acquisition', 'TAC', 1),
(2, 'Recruiting Operations', 'ROP', 1),
(3, 'Human Resources', 'HRS', 1),
(4, 'HR Business Partnering', 'HBP', 1),

(5, 'Business Operations', 'BOP', 2),
(6, 'Legal & Compliance', 'LGC', 2),

(7, 'Executive Leadership', 'EXL', 3),

(8, 'Information Technology', 'ITT', 4),
(9, 'Software Development', 'SWD', 4),
(10, 'UI/UX Design', 'UXD', 4),
(11, 'Quality Assurance (QA)', 'QAS', 4),
(12, 'DevOps / Cloud Engineering', 'DCE', 4),
(13, 'Data & Analytics', 'DNA', 4),

(14, 'SAP Functional', 'SPF', 5),
(15, 'SAP Technical', 'SPT', 5),

(16, 'Sales', 'SAL', 6),
(17, 'Marketing', 'MKT', 6),

(18, 'Customer Support', 'CSP', 7),

(19, 'Finance', 'FIN', 8),
(20, 'Procurement', 'PRC', 8),

(21, 'Business Analysis', 'BAN', 9)

ON CONFLICT (id) DO NOTHING;


-- =========================================
-- ROLE
-- =========================================

INSERT INTO tb_role
(id, role_id, role_name, department_id, created_date, created_by,
 updated_by, updated_date, business_unit_id, description)

VALUES

(1, 1, 'Department Head', 5, '2026-04-22', 'admin', NULL, NULL, 2, 'Leads Business Operations'),
(2, 2, 'Team Lead', 5, '2026-04-22', 'admin', NULL, NULL, 2, 'Manages team operations'),
(3, 3, 'Talent Acquisition Specialist', 1, '2026-04-22', 'admin', NULL, NULL, 1, 'Handles hiring process'),
(4, 4, 'Recruiting Operations', 2, '2026-04-22', 'admin', NULL, NULL, 1, 'Manages recruitment workflow'),
(5, 5, 'HR', 3, '2026-04-22', 'admin', NULL, NULL, 1, 'HR generalist role')

ON CONFLICT (id) DO NOTHING;


-- =========================================
-- PERMISSION
-- =========================================

INSERT INTO tb_permission
(id, permission_id, module_id, role_id,
 can_create, can_view, can_edit, can_delete,
 created_date, created_by)

VALUES

(1, 1, 1, 1, TRUE, TRUE, TRUE, TRUE, '2026-04-22', 'admin'),
(2, 2, 2, 1, TRUE, TRUE, TRUE, TRUE, '2026-04-22', 'admin'),
(3, 3, 3, 2, FALSE, TRUE, TRUE, FALSE, '2026-04-22', 'admin'),
(4, 4, 4, 2, FALSE, TRUE, FALSE, FALSE, '2026-04-22', 'admin')

ON CONFLICT (id) DO NOTHING;

-- =========================================
-- SENIORITY LEVEL
-- =========================================

INSERT INTO tb_seniority_level (id, seniority_level)
VALUES
(1, 'IC1'),
(2, 'IC2'),
(3, 'IC3'),
(4, 'IC4'),
(5, 'IC5'),
(6, 'IC6'),
(7, 'IC7'),
(8, 'M1'),
(9, 'M2'),
(10, 'M3'),
(11, 'M4'),
(12, 'M5')
ON CONFLICT (id) DO NOTHING;

-- =========================================
-- TRAVEL REQUIREMENTS
-- =========================================

INSERT INTO tb_travel_requirement (id, travel_requirement)
VALUES
(1, 'None'),
(2, '<10%'),
(3, '10-25%'),
(4, '25-50%'),
(5, '50%+')
ON CONFLICT (id) DO NOTHING;