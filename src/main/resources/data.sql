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

INSERT INTO tb_role (
    id,
    business_unit_id,
    created_by,
    created_date,
    department_id,
    description,
    role_id,
    role_name
) VALUES
(1, 2, 'admin', '2026-04-22', 5, 'Leads Business Operations', 1, 'Department Head'),
(2, 2, 'admin', '2026-04-22', 5, 'Manages team operations', 2, 'Team Lead'),
(3, 1, 'admin', '2026-04-22', 1, 'Handles hiring process', 3, 'Talent Acquisition Specialist'),
(4, 1, 'admin', '2026-04-22', 2, 'Manages recruitment workflow', 4, 'Recruiting Operations'),
(5, 1, 'admin', '2026-04-22', 3, 'HR generalist role', 5, 'HR'),
(6, 1, 'admin', '2026-04-22', 3, 'Handles compensation & benefits', 6, 'Total Rewards'),
(7, 1, 'admin', '2026-04-22', 4, 'Strategic HR role', 7, 'HR Strategic Partner'),
(8, 3, 'admin', '2026-04-22', 7, 'Chief Executive Officer', 8, 'CEO'),
(9, 3, 'admin', '2026-04-22', 7, 'Chief Operating Officer', 9, 'COO'),
(10, 3, 'admin', '2026-04-22', 7, 'Chief HR Officer', 10, 'CHRO'),
(11, 4, 'admin', '2026-04-22', 8, 'Manages IT platforms', 11, 'Administrator'),
(12, 4, 'admin', '2026-04-22', 9, 'Handles backend development', 12, 'Backend Developer'),
(13, 4, 'admin', '2026-04-22', 9, 'Handles frontend UI', 13, 'Frontend Developer'),
(14, 4, 'admin', '2026-04-22', 10, 'Designs user interfaces', 14, 'UI Designer'),
(15, 4, 'admin', '2026-04-22', 10, 'Improves user experience', 15, 'UX Researcher'),
(16, 4, 'admin', '2026-04-22', 11, 'Ensures quality testing', 16, 'QA Engineer'),
(17, 4, 'admin', '2026-04-22', 11, 'Automation testing', 17, 'Automation Tester'),
(18, 4, 'admin', '2026-04-22', 12, 'Handles CI/CD pipelines', 18, 'DevOps Engineer'),
(19, 4, 'admin', '2026-04-22', 12, 'Designs cloud solutions', 19, 'Cloud Architect'),
(20, 4, 'admin', '2026-04-22', 13, 'Analyzes business data', 20, 'Data Analyst'),
(21, 4, 'admin', '2026-04-22', 13, 'Builds data pipelines', 21, 'Data Engineer'),
(22, 6, 'admin', '2026-04-22', 16, 'Handles sales', 22, 'Sales Executive'),
(23, 6, 'admin', '2026-04-22', 16, 'Manages clients', 23, 'Account Manager'),
(24, 6, 'admin', '2026-04-22', 16, 'Leads sales team', 24, 'Sales Manager'),
(25, 9, 'admin', '2026-04-22', 21, 'Analyzes requirements', 25, 'Business Analyst'),
(26, 9, 'admin', '2026-04-22', 21, 'Handles functional specs', 26, 'Functional Analyst'),
(27, 5, 'admin', '2026-04-22', 14, 'Handles SAP modules', 27, 'SAP Consultant'),
(28, 5, 'admin', '2026-04-22', 15, 'ABAP development', 28, 'SAP ABAP Developer'),
(29, 5, 'admin', '2026-04-22', 15, 'System integrations', 29, 'Integration Specialist'),
(30, 7, 'admin', '2026-04-22', 18, 'Customer support', 30, 'Support Executive'),
(31, 7, 'admin', '2026-04-22', 18, 'Handles tickets', 31, 'Helpdesk'),
(32, 6, 'admin', '2026-04-22', 17, 'Online marketing', 32, 'Digital Marketing'),
(33, 6, 'admin', '2026-04-22', 17, 'Manages brand', 33, 'Brand Manager'),
(34, 8, 'admin', '2026-04-22', 19, 'Financial analysis', 34, 'Financial Analyst'),
(35, 8, 'admin', '2026-04-22', 19, 'Handles accounts', 35, 'Accountant'),
(36, 8, 'admin', '2026-04-22', 20, 'Handles procurement', 36, 'Procurement Specialist'),
(37, 8, 'admin', '2026-04-22', 20, 'Manages vendors', 37, 'Vendor Manager'),
(38, 2, 'admin', '2026-04-22', 6, 'Provides legal advice', 38, 'Legal Advisor'),
(39, 2, 'admin', '2026-04-22', 6, 'Ensures compliance', 39, 'Compliance Officer'),
(40, 2, 'admin', '2026-05-01', 2, 'Full access role', 40, 'Managementt Head'),
(41, 3, 'admin', '2026-05-04', 7, 'Executive', 41, 'Executive'),
(42, 1, 'admin', '2026-05-04', 1, 'this role belongs to Talent Team', 42, 'Talent Team'),
(43, 3, 'admin', '2026-05-04', 7, 'Leader', 43, 'Leader'),
(44, 1, 'admin', '2026-05-06', 1, 'Intern Role', 44, 'TA intern'),
(46, 4, 'admin', '2026-04-22', 8, 'System Admin', 45, 'System Admin')
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


-- =========================================
-- Functionality
-- =========================================


INSERT INTO tb_functionality (id, functionality_name, is_chain_created)
VALUES 
(2, 'SR_Approvals', false),
(3, 'Supply Module', false)
ON CONFLICT (id) DO NOTHING;