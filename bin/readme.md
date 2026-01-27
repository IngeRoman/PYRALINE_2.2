App
│
├── App                ← Capa de presentación (UI/UX)
│   ├── DesktopApp              ← App gráfica (Desktop)
│   │   ├── ViewModels
│   │   │   └── MainViewModel
│   │   └── Views
│   │
│   └── ConsoleApp              ← App de terminal (usuario final)
│       └── SistemaRuso.java    ← Punto de entrada
│
├── BusinessLogic      ← Domain: Lógica de negocio
│   ├── Interfaces              ← Contratos de comportamiento / servicios
│   ├── Services                ← Reglas de negocio
│   └── Entities                ← Domain Models / Entidades del negocio
│
├── DataAccess         ← Persistencia
│   ├── Interfaces              ← Contratos (Repositories / DAOs)
│   ├── DAOs                    ← Implementaciones
│   ├── DTOs  
                  ← Objetos de transporte
│   └── Helpers                 ← Mappers / utilidades DB
│
└── Infrastructure     ← implementa la interacción con sistemas externos (DB, APIs, archivos, etc.).
    ├── Config.java                  ← Configuración
    ├── Logging.java                 ← Logs
    ├── resources/              (IMG, XML, JSON de referencia)/(recursos empaquetados en el JAR)
    └── Tools                   ← Utilidades comunes
│
│── app.properties     (parámetros generales, cadena de conexión, usuario, password)

│
└── Tests                        ← Pruebas unitarias e integración
    ├── DomainTests
    ├── ApplicationTests
    └── InfrastructureTests