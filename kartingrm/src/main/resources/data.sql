-- 1) Limpieza de tablas existentes (orden respetando FKs)
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS participants;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS visits;
DROP TABLE IF EXISTS holidays;
DROP TABLE IF EXISTS clients;
DROP TABLE IF EXISTS tariff_config;

-- 2) Creación de tablas

-- clientes
CREATE TABLE clients (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         full_name VARCHAR(255) NOT NULL,
                         email VARCHAR(255) NOT NULL UNIQUE,
                         phone VARCHAR(50),
                         birth_date DATE,
                         total_visits INT NOT NULL DEFAULT 0,
                         registration_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- visitas (para cliente frecuente)
CREATE TABLE visits (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        client_id BIGINT NOT NULL,
                        visit_date DATE NOT NULL,
                        CONSTRAINT fk_visit_client FOREIGN KEY (client_id)
                            REFERENCES clients(id) ON DELETE CASCADE
);

-- feriados
CREATE TABLE holidays (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          date DATE NOT NULL UNIQUE,
                          name VARCHAR(255) NOT NULL
);

-- configuración de tarifas
CREATE TABLE tariff_config (
                               rate VARCHAR(50) PRIMARY KEY,
                               price DECIMAL(19,2) NOT NULL,
                               minutes INT NOT NULL
);

-- sesiones
CREATE TABLE sessions (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          session_date DATE NOT NULL,
                          start_time TIME NOT NULL,
                          end_time TIME NOT NULL,
                          capacity INT NOT NULL,
                          CONSTRAINT uk_session UNIQUE (session_date, start_time, end_time)
);

-- reservas
CREATE TABLE reservations (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              reservation_code VARCHAR(100) NOT NULL UNIQUE,
                              client_id BIGINT NOT NULL,
                              session_id BIGINT NOT NULL,
                              duration INT NOT NULL,
                              participants INT NOT NULL,
                              rate_type VARCHAR(50) NOT NULL,
                              base_price DECIMAL(19,2) NOT NULL,
                              discount_percentage DECIMAL(5,2),
                              final_price DECIMAL(19,2) NOT NULL,
                              status VARCHAR(50) NOT NULL,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT fk_reservation_client FOREIGN KEY (client_id)
                                  REFERENCES clients(id),
                              CONSTRAINT fk_reservation_session FOREIGN KEY (session_id)
                                  REFERENCES sessions(id)
);

-- participantes de cada reserva
CREATE TABLE participants (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              full_name VARCHAR(255),
                              email VARCHAR(255),
                              birthday BOOLEAN,
                              reservation_id BIGINT,
                              CONSTRAINT fk_participant_reservation FOREIGN KEY (reservation_id)
                                  REFERENCES reservations(id) ON DELETE CASCADE
);

-- pagos
CREATE TABLE payments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          reservation_id BIGINT NOT NULL UNIQUE,
                          payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          payment_method VARCHAR(50),
                          vat_percentage DECIMAL(5,2) NOT NULL,
                          vat_amount DECIMAL(19,2) NOT NULL,
                          final_amount_incl_vat DECIMAL(19,2) NOT NULL,
                          CONSTRAINT fk_payment_reservation FOREIGN KEY (reservation_id)
                              REFERENCES reservations(id)
);

-- 3) Datos iniciales

INSERT INTO tariff_config (rate, price, minutes) VALUES
                                                     ('LAP_10', 15000, 30),
                                                     ('LAP_15', 20000, 35),
                                                     ('LAP_20', 25000, 40),
                                                     ('WEEKEND', 20000, 35),
                                                     ('HOLIDAY', 25000, 40);

INSERT INTO holidays (date, name) VALUES
                                      ('2025-09-18', 'Fiestas Patrias'),
                                      ('2025-12-25', 'Navidad');
