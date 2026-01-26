DROP VIEW IF EXISTS wsPRYLINE;
DROP TABLE IF EXISTS PYRALINE;
DROP TABLE IF EXISTS Lugar;
DROP TABLE IF EXISTS TipoAlerta;
DROP TABLE IF EXISTS Usuario;

CREATE TABLE Lugar (IdLugar INTEGER PRIMARY KEY AUTOINCREMENT, Nombre VARCHAR(30) NOT NULL UNIQUE, Estado CHAR(1) NOT NULL DEFAULT 'A', FechaCreacion DATETIME NOT NULL DEFAULT (datetime('now','localtime')));

CREATE TABLE TipoAlerta (IdTipoAlerta INTEGER PRIMARY KEY AUTOINCREMENT, Nombre VARCHAR(30) NOT NULL UNIQUE, Estado CHAR(1) NOT NULL DEFAULT 'A');

CREATE TABLE Usuario (IdUsuario INTEGER PRIMARY KEY AUTOINCREMENT, Email VARCHAR(50) NOT NULL UNIQUE, Password VARCHAR(50) NOT NULL, Estado CHAR(1) NOT NULL DEFAULT 'A');

CREATE TABLE PYRALINE (IdPYRALINE INTEGER PRIMARY KEY AUTOINCREMENT, IdLugar INTEGER NOT NULL REFERENCES Lugar(IdLugar), IdTipoAlerta INTEGER NOT NULL REFERENCES TipoAlerta(IdTipoAlerta), Temperatura DECIMAL(5,2) NOT NULL, Estado CHAR(1) NOT NULL DEFAULT 'A', FechaHora DATETIME NOT NULL DEFAULT (datetime('now','localtime')), FechaModifica DATETIME NOT NULL DEFAULT (datetime('now','localtime')));

CREATE TRIGGER tr_PYRALINE_Update AFTER UPDATE ON PYRALINE FOR EACH ROW BEGIN UPDATE PYRALINE SET FechaModifica = datetime('now','localtime') WHERE IdPYRALINE = OLD.IdPYRALINE; END;

CREATE VIEW wsPRYLINE AS SELECT P.IdPYRALINE, L.Nombre AS Lugar, T.Nombre AS TipoAlerta, P.IdTipoAlerta, P.Temperatura, P.Estado, P.FechaHora, P.FechaModifica FROM PYRALINE P JOIN Lugar L ON P.IdLugar = L.IdLugar JOIN TipoAlerta T ON P.IdTipoAlerta = T.IdTipoAlerta WHERE P.Estado = 'A';

INSERT INTO Lugar (Nombre) VALUES ('Laboratorio Central');
INSERT INTO Lugar (Nombre) VALUES ('Zona de Servidores');
INSERT INTO TipoAlerta (Nombre) VALUES ('Exceso de Temperatura');
INSERT INTO TipoAlerta (Nombre) VALUES ('Falla de Sensor');
INSERT INTO TipoAlerta (Nombre) VALUES ('Estado Normal');
INSERT INTO Usuario (Email, Password, Estado) VALUES ('mateo.rios01@epn.edu.ec', 'admin1234', 'A');