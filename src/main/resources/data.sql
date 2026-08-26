-- =========================================
-- MODULE
-- =========================================
INSERT INTO tb_module
(id, module_id, parent_id, module_name, created_date, created_by, updated_by, updated_date)
VALUES
(1, 1, 0, 'Demand', '2026-04-22', 'admin', NULL, NULL),
(2, 2, 0, 'Supply', '2026-04-22', 'admin', NULL, NULL),
(3, 3, 0, 'System & Admins', '2026-04-22', 'admin', NULL, NULL),
(4, 4, 1, 'My Srs', '2026-04-22', 'admin', NULL, NULL),
(5, 5, 2, 'Kanban', '2026-04-22', 'admin', NULL, NULL),
(6, 6, 2, 'Hiring Dashboard', '2026-04-22', 'admin', NULL, NULL),
(7, 7, 2, 'Job Details', '2026-04-22', 'admin', NULL, NULL),
(8, 8, 3, 'Users', '2026-04-22', 'admin', NULL, NULL),
(9, 9, 3, 'Roles & Permissions', '2026-04-22', 'admin', NULL, NULL),
(10, 10, 0, 'My Approval', '2026-05-07', 'admin', NULL, NULL),
(11, 11, 10, 'SR Approvals', '2026-05-07', 'admin', NULL, NULL),
(12, 12, 10, 'Hierarchy Approvals', '2026-05-07', 'admin', NULL, NULL),
(13, 13, 3, 'Approval Chain Configuration', '2026-05-07', 'admin', NULL, NULL),
(14, 14, 1, 'Job Requisitions', '2026-05-20', 'admin', NULL, NULL),
(15, 15, 1, 'Recruiter Assignment management', '2026-05-21', 'admin', NULL, NULL),
(16, 16, 2, 'My Job Assignments', '2026-05-21', 'admin', NULL, NULL),
(17, 17, 1, 'Interview Plan Config', '2026-05-28', 'admin', NULL, NULL),
(18, 18, 10, 'Interview Plan Approvals', '2026-05-28', 'admin', NULL, NULL),
(19, 19, 1, 'Assign Interviewers', '2026-04-22', 'admin', NULL, NULL),
(20, 20, 2, 'My Interview Requests', '2026-06-09', 'admin', NULL, NULL),
(21, 21, 22, 'AI Interview Zone', '2026-06-23', 'admin', NULL, NULL),
(22, 22, 0, 'Candidate Management', '2026-06-23', 'admin', NULL, NULL),
(23, 23, 22, 'In Person Interview', '2026-07-07', 'admin', NULL, NULL),
(24, 24, 22, 'Interview Pipeline', '2026-07-07', 'admin', NULL, NULL),
(25, 25, 22, 'Offer Management', '2026-07-15', 'admin', NULL, NULL),
(26, 26, 10, 'Offer Approvals', '2026-08-05', 'admin', NULL, NULL),
(27, 27, 0, 'My DashBoards', '2026-08-07', 'admin', NULL, NULL),
(28, 28, 27, 'HM DashBoard', '2026-08-07', 'admin', NULL, NULL),
(29, 29, 27, 'Recruiter DashBoard', '2026-08-07', 'admin', NULL, NULL),
(30, 30, 27, 'Recruiters Performance DashBoard', '2026-08-07', 'admin', NULL, NULL)
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

---Role Administrator
INSERT INTO tb_role
(id, role_id, role_name, department_id, created_date, created_by, updated_by, updated_date, business_unit_id, description)
VALUES
(1, 1, 'Administrator', NULL, '2026-08-26', 'admin', NULL, NULL, NULL,
 'Administrator role with full permissions ')
ON CONFLICT (id) DO NOTHING;

---Administrator permissions
INSERT INTO tb_permission
(id, permission_id, module_id, role_id, can_create, can_view, can_edit, can_delete, can_export, created_date, created_by, updated_by, updated_date)
VALUES
(1, 1, 1, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(2, 2, 2, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(3, 3, 3, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(4, 4, 4, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(5, 5, 5, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(6, 6, 6, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(7, 7, 7, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(8, 8, 8, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(9, 9, 9, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(10, 10, 10, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(11, 11, 11, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(12, 12, 12, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(13, 13, 13, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(14, 14, 14, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(15, 15, 15, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(16, 16, 16, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(17, 17, 17, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(18, 18, 18, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(19, 19, 19, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(20, 20, 20, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(21, 21, 21, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(22, 22, 22, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(23, 23, 23, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(24, 24, 24, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(25, 25, 25, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(26, 26, 26, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(27, 27, 27, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(28, 28, 28, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(29, 29, 29, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL),
(30, 30, 30, 1, true, true, true, true, false, '2026-08-26', 'admin', NULL, NULL)
ON CONFLICT (id) DO NOTHING;


-- =========================================
-- ROLE
-- =========================================

-- INSERT INTO tb_role
-- (id, role_id, role_name, department_id, created_date, created_by,
--  updated_by, updated_date, business_unit_id, description)

-- VALUES

-- (1, 1, 'Department Head', 5, '2026-04-22', 'admin', NULL, NULL, 2, 'Leads Business Operations'),
-- (2, 2, 'Team Lead', 5, '2026-04-22', 'admin', NULL, NULL, 2, 'Manages team operations'),
-- (3, 3, 'Talent Acquisition Specialist', 1, '2026-04-22', 'admin', NULL, NULL, 1, 'Handles hiring process'),
-- (4, 4, 'Recruiting Operations', 2, '2026-04-22', 'admin', NULL, NULL, 1, 'Manages recruitment workflow'),
-- (5, 5, 'HR', 3, '2026-04-22', 'admin', NULL, NULL, 1, 'HR generalist role')

-- ON CONFLICT (id) DO NOTHING;


-- =========================================
-- PERMISSION
-- =========================================

-- INSERT INTO tb_permission
-- (id, permission_id, module_id, role_id,
--  can_create, can_view, can_edit, can_delete,
--  created_date, created_by)

-- VALUES

-- (1, 1, 1, 1, TRUE, TRUE, TRUE, TRUE, '2026-04-22', 'admin'),
-- (2, 2, 2, 1, TRUE, TRUE, TRUE, TRUE, '2026-04-22', 'admin'),
-- (3, 3, 3, 2, FALSE, TRUE, TRUE, FALSE, '2026-04-22', 'admin'),
-- (4, 4, 4, 2, FALSE, TRUE, FALSE, FALSE, '2026-04-22', 'admin')

-- ON CONFLICT (id) DO NOTHING;

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

---Role
INSERT INTO tb_user
(
    id,
    user_id,
    user_type_id,
    first_name,
    last_name,
    employee_id,
    email,
    mobile_number,
    alternate_contact,
    employment_type_id,
    department_id,
    business_unit_id,
    password,
    pin,
    updated_by,
    updated_at,
    active,
    deactivated,
    username,
    failed_attempts,
    account_locked,
    lock_time,
    force_password_reset,
    password_updated_at,
    candidate_id,
    pin_updated_at,
    first_time_web_login,
    first_time_mobile_login
)
VALUES
(
    1,
    1,
    1,
    'sai',
    'narasimha',
    1,
    'spmproject66@gmail.com',
    '9876543210',
    NULL,
    1,
    1,
    1,
    '$2a$10$Z2wbNTAQF76MaL4un0RI/.8oFU/ZB/QD0e0W6IkSPz.XyTULwoz72',
    '$2a$10$/jqt2hrOvl0j5Hzi4t1DHeHk3Cz5rr.XfQpzErRE1ujzXqt74xaiC',
    NULL,
    NULL,
    true,
    false,
    'sainarasimha',
    0,
    false,
    NULL,
    false,
    NULL,
    NULL,
    NULL,
    true,
    true
)
ON CONFLICT (id) DO NOTHING;


INSERT INTO tb_assign_roles
(
    id,
    assign_role_id,
    user_id,
    role_id,
    assigned_by,
    assigned_at
)
VALUES
(
    1,
    1,
    1,
    1,
    'admin',
    '2026-08-26'
)
ON CONFLICT (id) DO NOTHING;

---functionality

INSERT INTO tb_functionality
(id, functionality_name, is_chain_created)
VALUES
(1, 'SR_Approvals', false),
(2, 'Supply Module', false),
(3, 'Interview Plan', false),
(4, 'Offer Plan', false)
ON CONFLICT (id) DO NOTHING;