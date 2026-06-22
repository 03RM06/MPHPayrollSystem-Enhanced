USE payrollsystem_db;

-- ---------- Lookups ----------
INSERT IGNORE INTO employment_status (status_id, status_name) VALUES
  (1,'PROBATIONARY'),(2,'REGULAR'),(3,'RESIGNED'),(4,'TERMINATED'),(5,'RETIRED');

INSERT IGNORE INTO `position` (position_id, position_name) VALUES
  (1,'CEO'),(2,'HR Manager'),(3,'Finance Officer'),(4,'IT Support'),(5,'Rank and File');

INSERT IGNORE INTO allowance (allowance_id, allowance_name) VALUES
  (1,'Rice Subsidy'),(2,'Phone Allowance'),(3,'Clothing Allowance');

INSERT IGNORE INTO leave_type (leave_type_id, type_name) VALUES
  (1,'Emergency Leave'),(2,'Maternity Leave'),(3,'Paternity Leave'),
  (4,'Sick Leave'),(5,'Vacation Leave');

INSERT IGNORE INTO approval_status (status_id, status_name) VALUES
  (1,'Pending'),(2,'Approved'),(3,'Rejected');

INSERT IGNORE INTO roles (role_id, role_name) VALUES
  (1,'ADMIN'),(2,'HR'),(3,'FINANCE'),(4,'IT'),(5,'EMPLOYEE');

INSERT IGNORE INTO account_status (status_id, status_name) VALUES
  (1,'ACTIVE'),(2,'INACTIVE');

-- ---------- Birthdays / Addresses ----------
INSERT IGNORE INTO birthday (birthday_id, birth_date) VALUES
  (1,'1980-03-12'),(2,'1988-07-25'),(3,'1992-11-03'),(4,'1990-01-18'),(5,'1995-06-30');

INSERT IGNORE INTO address (address_id, full_address) VALUES
  (1,'123 Mabini St, Makati City'),(2,'45 Rizal Ave, Quezon City'),
  (3,'9 Bonifacio Rd, Pasig City'),(4,'12 Aguinaldo St, Taguig City'),
  (5,'78 Luna St, Manila');

-- ---------- Employees ----------
INSERT IGNORE INTO employee
  (employee_id,last_name,first_name,birthday_id,address_id,phone_number,status_id,position_id,supervisor_employee_id) VALUES
  ('E001','Garcia','Manuel',   1,1,'0917-100-0001',2,1,NULL),
  ('E002','Lim','Antonio',     2,2,'0917-100-0002',2,2,'E001'),
  ('E003','Aquino','Bianca',   3,3,'0917-100-0003',2,3,'E001'),
  ('E004','Reyes','Isabella',  4,4,'0917-100-0004',2,4,'E001'),
  ('E005','Santos','Maria',    5,5,'0917-100-0005',2,5,'E002');

-- ---------- Compensation ----------
INSERT IGNORE INTO compensation (employee_id,basic_salary,gross_semi_monthly_rate,hourly_rate) VALUES
  ('E001',90000.00,45000.00,535.71),
  ('E002',52000.00,26000.00,309.52),
  ('E003',45000.00,22500.00,267.86),
  ('E004',35000.00,17500.00,208.33),
  ('E005',25000.00,12500.00,148.81);

-- ---------- Government IDs ----------
INSERT IGNORE INTO employee_government_id
  (employee_id,sss_number,philhealth_number,tin_number,pagibig_number) VALUES
  ('E001','34-1234567-1','110000000001','123-456-001','1210000000001'),
  ('E002','34-1234567-2','110000000002','123-456-002','1210000000002'),
  ('E003','34-1234567-3','110000000003','123-456-003','1210000000003'),
  ('E004','34-1234567-4','110000000004','123-456-004','1210000000004'),
  ('E005','34-1234567-5','110000000005','123-456-005','1210000000005');

-- ---------- Allowances ----------
INSERT IGNORE INTO employee_allowance (employee_id,allowance_id,amount) VALUES
  ('E001',1,1500.00),('E001',2,2000.00),('E001',3,1000.00),
  ('E002',1,1500.00),('E002',2,1000.00),('E002',3,800.00),
  ('E003',1,1500.00),('E003',2,800.00), ('E003',3,800.00),
  ('E004',1,1000.00),('E004',2,500.00), ('E004',3,500.00),
  ('E005',1,1000.00),('E005',2,500.00), ('E005',3,500.00);

-- ---------- User accounts (demo password: password123) ----------
INSERT IGNORE INTO user_account (account_id,username,password_hash,employee_id,status_id) VALUES
  (1,'admin',    SHA2('password123',256),'E001',1),
  (2,'hr',       SHA2('password123',256),'E002',1),
  (3,'finance',  SHA2('password123',256),'E003',1),
  (4,'it',       SHA2('password123',256),'E004',1),
  (5,'employee', SHA2('password123',256),'E005',1);

-- ---------- Role assignments ----------
INSERT IGNORE INTO user_role (account_id,role_id) VALUES
  (1,1),(2,2),(3,3),(4,4),(5,5);
