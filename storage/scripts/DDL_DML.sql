-- 1. LIMPIEZA DE OBJETOS
DROP VIEW IF EXISTS wsPRYLINE;
DROP TABLE IF EXISTS PYRALINE;
DROP TABLE IF EXISTS Usuario;
DROP TABLE IF EXISTS Lugar;
DROP TABLE IF EXISTS TipoAlerta;
DROP TABLE IF EXISTS EstadoAlerta;

-- 2. TABLA: Lugar (Sincronizada con LugarDTO)
CREATE TABLE Lugar (
    IdLugar         INTEGER PRIMARY KEY AUTOINCREMENT,
    Nombre          VARCHAR(30) NOT NULL UNIQUE,
    Descripcion     VARCHAR(100), -- Requerido por DTO, permitido NULL para tus inserts
    Estado          CHAR(1) NOT NULL DEFAULT 'A',
    FechaCreacion   DATETIME NOT NULL DEFAULT (datetime('now','localtime'))
);

-- 3. TABLA: TipoAlerta (Sincronizada con campos de TipoAlerta y PYRALINEDTO)
CREATE TABLE TipoAlerta (
    IdTipoAlerta    INTEGER PRIMARY KEY AUTOINCREMENT,
    Nombre          VARCHAR(30) NOT NULL UNIQUE,
    Descripcion     VARCHAR(100), -- Requerido por DTO
    Estado          CHAR(1) NOT NULL DEFAULT 'A',
    FechaCreacion   DATETIME NOT NULL DEFAULT (datetime('now','localtime')),
    FechaModifica   DATETIME NOT NULL DEFAULT (datetime('now','localtime'))
);

-- 4. TABLA: EstadoAlerta (Sincronizada con EstadoAlertaDTO)
CREATE TABLE EstadoAlerta (
    IdEstadoAlerta  INTEGER PRIMARY KEY AUTOINCREMENT,
    Nombre          VARCHAR(30) NOT NULL UNIQUE,
    Descripcion     VARCHAR(100),
    FechaCreacion   DATETIME NOT NULL DEFAULT (datetime('now','localtime')),
    FechaModifica   DATETIME NOT NULL DEFAULT (datetime('now','localtime'))
);

-- 5. TABLA: Usuario (Sincronizada con UsuarioDTO)
CREATE TABLE Usuario (
    IdUsuario       INTEGER PRIMARY KEY AUTOINCREMENT,
    Email           VARCHAR(50) NOT NULL UNIQUE,
    Password        VARCHAR(50) NOT NULL,
    Estado          CHAR(1) NOT NULL DEFAULT 'A'
);

-- 6. TABLA: PYRALINE (Sincronizada con PYRALINEDTO)
CREATE TABLE PYRALINE (
    IdPYRALINE      INTEGER PRIMARY KEY AUTOINCREMENT,
    IdLugar         INTEGER NOT NULL REFERENCES Lugar(IdLugar),
    IdTipoAlerta    INTEGER NOT NULL REFERENCES TipoAlerta(IdTipoAlerta),
    Temperatura     DECIMAL(5,2) NOT NULL,
    Estado          CHAR(1) NOT NULL DEFAULT 'A',
    FechaHora       DATETIME NOT NULL DEFAULT (datetime('now','localtime')),
    FechaModifica   DATETIME NOT NULL DEFAULT (datetime('now','localtime'))
);

-- 7. TRIGGERS: Auditoría automática
CREATE TRIGGER tr_PYRALINE_Update AFTER UPDATE ON PYRALINE 
BEGIN 
    UPDATE PYRALINE SET FechaModifica = datetime('now','localtime') WHERE IdPYRALINE = OLD.IdPYRALINE; 
END;

CREATE TRIGGER tr_TipoAlerta_Update AFTER UPDATE ON TipoAlerta 
BEGIN 
    UPDATE TipoAlerta SET FechaModifica = datetime('now','localtime') WHERE IdTipoAlerta = OLD.IdTipoAlerta; 
END;

-- 8. VISTA: wsPRYLINE (Alias exactos para PYRALINEDTO)
CREATE VIEW wsPRYLINE AS 
SELECT 
    P.IdPYRALINE, 
    P.IdLugar,
    L.Nombre        AS LugarNombre,       -- Mapeado a private String LugarNombre
    T.Nombre        AS TipoAlertaNombre,  -- Mapeado a private String TipoAlertaNombre
    P.IdTipoAlerta, 
    P.Temperatura, 
    P.Estado, 
    P.FechaHora, 
    P.FechaModifica 
FROM PYRALINE P 
JOIN Lugar L ON P.IdLugar = L.IdLugar 
JOIN TipoAlerta T ON P.IdTipoAlerta = T.IdTipoAlerta 
WHERE P.Estado = 'A';

-- 9. INSERCIÓN DE DATOS (Solo los solicitados)
INSERT INTO Lugar (Nombre) VALUES ('Laboratorio Central');
INSERT INTO Lugar (Nombre) VALUES ('Zona de Servidores');

INSERT INTO TipoAlerta (Nombre) VALUES ('Exceso de Temperatura');
INSERT INTO TipoAlerta (Nombre) VALUES ('Falla de Sensor');
INSERT INTO TipoAlerta (Nombre) VALUES ('Estado Normal');

INSERT INTO Usuario (Email, Password, Estado) VALUES ('pyraline', 'admin1234', 'A');