-- Base de datos: Proyectos de Inversión Pública - Municipalidad Metropolitana de Lima
SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS bd_proyectos_mml CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bd_proyectos_mml;

DROP TABLE IF EXISTS proyecto;

CREATE TABLE proyecto (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(60)    NOT NULL,
    descripcion     VARCHAR(500)   NULL,
    fecha_inicio    DATE           NOT NULL,
    fecha_fin       DATE           NOT NULL,
    asesor_tecnico  VARCHAR(100)   NOT NULL,
    presupuesto     DECIMAL(14,2)  NOT NULL,
    CONSTRAINT chk_nombre      CHECK (CHAR_LENGTH(nombre) BETWEEN 4 AND 60),
    CONSTRAINT chk_fechas      CHECK (fecha_fin >= fecha_inicio),
    CONSTRAINT chk_presupuesto CHECK (presupuesto > 0)
);

INSERT INTO proyecto (nombre, descripcion, fecha_inicio, fecha_fin, asesor_tecnico, presupuesto) VALUES
('Sistema de Inventario',  'Control de bienes patrimoniales',  '2025-01-15', '2025-03-20', 'Carlos Pérez',  15000),
('Plataforma E-Learning',  'Capacitación virtual a servidores', '2025-02-01', '2025-06-30', 'Andrea Torres', 40000),
('App de Gestión Médica',  'Citas en policlínicos municipales', '2025-03-10', '2025-08-12', 'Luis Ramírez',  55000);
