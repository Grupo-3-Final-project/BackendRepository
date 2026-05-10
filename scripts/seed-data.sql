-- Seed Data for Parque de Atracciones
-- This script populates the database with demo data for development and testing

-- Users (Admin, Employee, Regular Users)
INSERT INTO users (first_name, last_name, dni, email, phone, birth_date, password_hash, role, created_at, updated_at) VALUES
('Admin', 'User', '00000000A', 'admin@parque.com', '600000000', '1980-01-01', '$2a$10$slYQmyNdGzin7olVlAzJuuK5C2sP7L.1kPCTe8rGfkE9B2R5B4S4i', 'ADMIN', NOW(), NOW()),
('Manager', 'Principal', '11111111B', 'manager@parque.com', '600000001', '1985-05-15', '$2a$10$slYQmyNdGzin7olVlAzJuuK5C2sP7L.1kPCTe8rGfkE9B2R5B4S4i', 'MANAGER', NOW(), NOW()),
('Employee', 'Taquilla', '22222222C', 'employee@parque.com', '600000002', '1990-03-20', '$2a$10$slYQmyNdGzin7olVlAzJuuK5C2sP7L.1kPCTe8rGfkE9B2R5B4S4i', 'EMPLOYEE', NOW(), NOW()),
('Juan', 'García', '33333333D', 'juan.garcia@example.com', '600000003', '1992-07-10', '$2a$10$slYQmyNdGzin7olVlAzJuuK5C2sP7L.1kPCTe8rGfkE9B2R5B4S4i', 'USER', NOW(), NOW()),
('María', 'López', '44444444E', 'maria.lopez@example.com', '600000004', '1995-11-25', '$2a$10$slYQmyNdGzin7olVlAzJuuK5C2sP7L.1kPCTe8rGfkE9B2R5B4S4i', 'USER', NOW(), NOW()),
('Carlos', 'Martínez', '55555555F', 'carlos.martinez@example.com', '600000005', '1988-02-14', '$2a$10$slYQmyNdGzin7olVlAzJuuK5C2sP7L.1kPCTe8rGfkE9B2R5B4S4i', 'USER', NOW(), NOW()),
('Ana', 'Sánchez', '66666666G', 'ana.sanchez@example.com', '600000006', '1999-09-30', '$2a$10$slYQmyNdGzin7olVlAzJuuK5C2sP7L.1kPCTe8rGfkE9B2R5B4S4i', 'USER', NOW(), NOW()),
('David', 'Navarro', '77777777H', 'david.navarro@example.com', '600000007', '1993-12-05', '$2a$10$slYQmyNdGzin7olVlAzJuuK5C2sP7L.1kPCTe8rGfkE9B2R5B4S4i', 'USER', NOW(), NOW()),
('Laura', 'Rodríguez', '88888888I', 'laura.rodriguez@example.com', '600000008', '1996-08-18', '$2a$10$slYQmyNdGzin7olVlAzJuuK5C2sP7L.1kPCTe8rGfkE9B2R5B4S4i', 'USER', NOW(), NOW()),
('Pedro', 'Gómez', '99999999J', 'pedro.gomez@example.com', '600000009', '1991-06-12', '$2a$10$slYQmyNdGzin7olVlAzJuuK5C2sP7L.1kPCTe8rGfkE9B2R5B4S4i', 'USER', NOW(), NOW());

-- Hotels
INSERT INTO hotels (name, address, city, phone, email, total_rooms, occupied_rooms, nightly_rate, description, created_at, updated_at) VALUES
('Hotel Paradise', 'Calle Principal 123', 'Madrid', '913000001', 'info@paradise.com', 100, 45, 150.00, 'Hotel 5 estrellas en el corazón de Madrid', NOW(), NOW()),
('Hotel Central', 'Avenida Real 456', 'Madrid', '913000002', 'info@central.com', 80, 30, 120.00, 'Hotel céntrico con excelentes servicios', NOW(), NOW()),
('Hotel Express', 'Calle Secundaria 789', 'Madrid', '913000003', 'info@express.com', 60, 20, 85.00, 'Hotel económico y cómodo', NOW(), NOW()),
('Hotel Premium Plus', 'Paseo de la Castellana 1000', 'Madrid', '913000004', 'info@premiumplus.com', 120, 60, 200.00, 'Lujo y comodidad sin compromiso', NOW(), NOW()),
('Hotel Familiar', 'Calle del Parque 111', 'Madrid', '913000005', 'info@familiar.com', 50, 25, 95.00, 'Ideal para familias con niños', NOW(), NOW());

-- Attractions
INSERT INTO attractions (name, category, description, min_age, max_age, capacity, current_visitors, daily_cost, status, created_at, updated_at) VALUES
('Montaña Rusa X', 'THRILL', 'La montaña rusa más emocionante del parque. Velocidad máxima: 120 km/h', 12, 100, 50, 25, 15.00, 'OPEN', NOW(), NOW()),
('Zona Infantil Mágica', 'FAMILY', 'Atracciones seguras y divertidas para los más pequeños', 3, 10, 100, 50, 8.00, 'OPEN', NOW(), NOW()),
('Piratas del Caribe', 'FAMILY', 'Viaje en barca temático por las aguas caribeñas', 5, 90, 80, 40, 12.00, 'OPEN', NOW(), NOW()),
('Torre de Caída Libre', 'THRILL', 'Caída libre desde 60 metros. Solo para valientes', 14, 100, 30, 15, 18.00, 'OPEN', NOW(), NOW()),
('Circo de Trapecistas', 'SHOW', 'Espectáculo acrobático en vivo', 0, 100, 200, 150, 10.00, 'OPEN', NOW(), NOW()),
('Laberinto Encantado', 'FAMILY', 'Laberinto interactivo con efectos especiales', 4, 100, 60, 30, 5.00, 'OPEN', NOW(), NOW()),
('Tranvía Temático', 'TRANSPORT', 'Paseo en tranvía por todo el parque', 0, 100, 40, 20, 6.00, 'OPEN', NOW(), NOW()),
('Casa del Terror', 'THRILL', 'Casa embrujada con efectos de realidad virtual', 10, 100, 50, 25, 14.00, 'OPEN', NOW(), NOW()),
('Simulador de Vuelo', 'THRILL', 'Experiencia realista de pilotaje', 8, 100, 20, 10, 20.00, 'MAINTENANCE', NOW(), NOW()),
('Restaurante Temático', 'FOOD', 'Comida y bebida en ambiente pirata', 0, 100, 200, 100, 25.00, 'OPEN', NOW(), NOW());

-- Bookings (Reservas de hoteles)
INSERT INTO bookings (user_id, hotel_id, check_in_date, check_out_date, num_guests, total_price, status, notes, created_at, updated_at) VALUES
(4, 1, DATE_ADD(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 2, 300.00, 'CONFIRMED', 'Habitación con vista al parque', NOW(), NOW()),
(5, 2, DATE_ADD(CURDATE(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 12 DAY), 3, 360.00, 'CONFIRMED', 'Dos habitaciones adyacentes', NOW(), NOW()),
(6, 3, DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), 1, 85.00, 'CONFIRMED', 'Habitación económica', NOW(), NOW()),
(7, 1, DATE_ADD(CURDATE(), INTERVAL 15 DAY), DATE_ADD(CURDATE(), INTERVAL 18 DAY), 4, 600.00, 'PENDING', 'Grupo familiar grande', NOW(), NOW()),
(8, 4, DATE_ADD(CURDATE(), INTERVAL 7 DAY), DATE_ADD(CURDATE(), INTERVAL 9 DAY), 2, 400.00, 'CONFIRMED', 'Suite premium', NOW(), NOW()),
(9, 5, DATE_ADD(CURDATE(), INTERVAL 12 DAY), DATE_ADD(CURDATE(), INTERVAL 14 DAY), 5, 475.00, 'CONFIRMED', 'Habitaciones múltiples para familia', NOW(), NOW()),
(10, 2, DATE_ADD(CURDATE(), INTERVAL 20 DAY), DATE_ADD(CURDATE(), INTERVAL 22 DAY), 2, 240.00, 'PENDING', 'Aniversario', NOW(), NOW());

-- Offers (Ofertas especiales)
INSERT INTO offers (name, description, discount_percentage, start_date, end_date, min_age, max_age, created_at, updated_at) VALUES
('Verano Joven', 'Descuento para jóvenes de 18-25 años', 15, DATE_ADD(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 60 DAY), 18, 25, NOW(), NOW()),
('Familia Plus', 'Pack familiar con descuento especial', 20, DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 90 DAY), 0, 100, NOW(), NOW()),
('Senior Activo', 'Entrada especial para mayores de 60', 25, DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 180 DAY), 60, 100, NOW(), NOW()),
('Niños Menores', 'Descuento para niños menores de 10 años', 30, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 365 DAY), 3, 9, NOW(), NOW()),
('Weekend Combo', 'Oferta fin de semana: hotel + entrada', 18, DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 180 DAY), 0, 100, NOW(), NOW());

-- Employees
INSERT INTO employees (first_name, last_name, dni, email, phone, position, hire_date, salary, department, status, created_at, updated_at) VALUES
('Miguel', 'Fernández', '12121212K', 'miguel.fernandez@parque.com', '650000001', 'MANAGER', '2020-01-15', 2500.00, 'ADMINISTRATION', 'ACTIVE', NOW(), NOW()),
('Isabel', 'Jiménez', '13131313L', 'isabel.jimenez@parque.com', '650000002', 'SUPERVISOR', '2019-06-01', 2000.00, 'OPERATIONS', 'ACTIVE', NOW(), NOW()),
('Fernando', 'Díaz', '14141414M', 'fernando.diaz@parque.com', '650000003', 'OPERATOR', '2021-03-10', 1500.00, 'ATTRACTIONS', 'ACTIVE', NOW(), NOW()),
('Elena', 'Ruiz', '15151515N', 'elena.ruiz@parque.com', '650000004', 'CASHIER', '2021-09-15', 1300.00, 'TICKETING', 'ACTIVE', NOW(), NOW()),
('Javier', 'Moreno', '16161616O', 'javier.moreno@parque.com', '650000005', 'MAINTENANCE', '2020-05-20', 1600.00, 'MAINTENANCE', 'ACTIVE', NOW(), NOW());

-- Shifts (Turnos de empleados)
INSERT INTO shifts (employee_id, shift_date, shift_type, start_time, end_time, created_at, updated_at) VALUES
(3, CURDATE(), 'MORNING', '08:00:00', '14:00:00', NOW(), NOW()),
(3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'EVENING', '14:00:00', '21:00:00', NOW(), NOW()),
(4, CURDATE(), 'EVENING', '14:00:00', '21:00:00', NOW(), NOW()),
(4, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'MORNING', '08:00:00', '14:00:00', NOW(), NOW()),
(5, CURDATE(), 'NIGHT', '21:00:00', '06:00:00', NOW(), NOW()),
(5, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'MORNING', '08:00:00', '14:00:00', NOW(), NOW());

-- Maintenance Records (Mantenimiento de atracciones)
INSERT INTO maintenance (attraction_id, maintenance_date, maintenance_type, description, technician_id, duration_minutes, status, notes, created_at, updated_at) VALUES
(1, CURDATE(), 'PREVENTIVE', 'Revisión de frenos y sistemas de seguridad', 5, 120, 'COMPLETED', 'Todos los sistemas en perfecto estado', NOW(), NOW()),
(4, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'CORRECTIVE', 'Reparación de motor principal', 5, 240, 'SCHEDULED', 'Esperando piezas de reemplazo', NOW(), NOW()),
(8, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'PREVENTIVE', 'Limpieza y lubricación de piezas móviles', 5, 90, 'SCHEDULED', 'Mantenimiento regular', NOW(), NOW()),
(9, CURDATE(), 'CORRECTIVE', 'Problemas con el sistema de realidad virtual', 5, 180, 'IN_PROGRESS', 'Software siendo actualizado', NOW(), NOW()),
(2, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'PREVENTIVE', 'Inspección de estructuras y seguridad', 5, 150, 'SCHEDULED', 'Revisión trimestral', NOW(), NOW());
