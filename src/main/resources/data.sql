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

INSERT INTO tb_business_unit (id, business_name) VALUES
(1, 'Human Capital Management'),
(2, 'Corporate Services'),
(3, 'Executive Office'),
(4, 'Technology Services'),
(5, 'Enterprise Applications (ERP & SAP)'),
(6, 'Sales & Marketing'),
(7, 'Customer Experience & Support'),
(8, 'Finance & Procurement'),
(9, 'Business Consulting & Strategy'),
(10, 'Other')
ON CONFLICT (id) DO NOTHING;
 


-- =========================================
-- DEPARTMENTS
-- =========================================
INSERT INTO tb_departments
(id, department_name, business_unit_id, dept_code, user_departments, sr_departments)
VALUES

-- SR Departments
(1, 'Talent Acquisition', 1, 'TA', FALSE, TRUE),
(2, 'Recruiting Operations', 1, 'RO', FALSE, TRUE),
(3, 'Human Resources', 1, 'HR', FALSE, TRUE),
(4, 'HR Business Partnering', 1, 'HRBP', FALSE, TRUE),
(5, 'Compensation & Benefits', 1, 'CB', FALSE, TRUE),

(6, 'Business Operations', 2, 'BO', FALSE, TRUE),
(7, 'Administration', 2, 'ADM', FALSE, TRUE),
(8, 'Legal & Compliance', 2, 'LC', FALSE, TRUE),

(9, 'Executive Leadership', 3, 'EL', FALSE, TRUE),
(10, 'Corporate Strategy', 3, 'CS', FALSE, TRUE),

(11, 'Software Development', 4, 'SD', FALSE, TRUE),
(12, 'Information Technology', 4, 'IT', FALSE, TRUE),
(13, 'UI/UX Design', 4, 'UIUX', FALSE, TRUE),
(14, 'Quality Assurance (QA)', 4, 'QA', FALSE, TRUE),
(15, 'DevOps & Cloud Engineering', 4, 'DCE', FALSE, TRUE),
(16, 'Data Engineering', 4, 'DE', FALSE, TRUE),
(17, 'Data Analytics', 4, 'DA', FALSE, TRUE),
(18, 'Cyber Security', 4, 'CYB', FALSE, TRUE),
(19, 'Infrastructure Management', 4, 'IM', FALSE, TRUE),

(20, 'SAP Functional', 5, 'SAPF', FALSE, TRUE),
(21, 'SAP Technical', 5, 'SAPT', FALSE, TRUE),
(22, 'Salesforce CRM', 5, 'SFDC', FALSE, TRUE),

(23, 'Sales', 6, 'SAL', FALSE, TRUE),
(24, 'Marketing', 6, 'MKT', FALSE, TRUE),
(25, 'Digital Marketing', 6, 'DM', FALSE, TRUE),
(26, 'Business Development', 6, 'BD', FALSE, TRUE),

(27, 'Customer Support', 7, 'CSUP', FALSE, TRUE),
(28, 'Technical Support', 7, 'TS', FALSE, TRUE),
(29, 'Implementation Services', 7, 'IS', FALSE, TRUE),

(30, 'Finance', 8, 'FIN', FALSE, TRUE),
(31, 'Accounting', 8, 'ACC', FALSE, TRUE),
(32, 'Payroll', 8, 'PAY', FALSE, TRUE),

(33, 'Business Analysis', 9, 'BA', FALSE, TRUE),
(34, 'Product Management', 9, 'PM', FALSE, TRUE),

(35, 'Other', 10, 'OTH', FALSE, TRUE),

-- User Creation Departments
(36, 'Business Department', NULL, 'BD', TRUE, FALSE),
(37, 'Recruiting Operations', NULL, 'RO', TRUE, FALSE),
(38, 'Talent Acquisition (TA)', NULL, 'TA', TRUE, FALSE),
(39, 'Human Resources (HR)', NULL, 'HR', TRUE, FALSE),
(40, 'Finance', NULL, 'FIN', TRUE, FALSE),
(41, 'Executive Office', NULL, 'EO', TRUE, FALSE),
(42, 'IT & System Administration', NULL, 'ITSA', TRUE, FALSE),
(43, 'Corporate Administration', NULL, 'CA', TRUE, FALSE),
(44, 'Employee Services', NULL, 'ES', TRUE, FALSE)

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