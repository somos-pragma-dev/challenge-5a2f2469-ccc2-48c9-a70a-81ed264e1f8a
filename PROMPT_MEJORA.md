# Prompt para Mejorar el Codigo Base

Copia y pega el contenido del bloque de abajo en un asistente de IA (Claude, ChatGPT)
para obtener un ZIP con el proyecto completo y arrancable.

Si preferis trabajar en tu editor con un agente local (Claude Code, Cursor, Copilot), usa `AGENTS.md` en vez de este archivo: dice lo mismo pero para que escriba los archivos en disco.

## Las dos reglas que no se negocian

1. **Completa el boilerplate.** Todo lo que el proyecto necesita para compilar y arrancar: manifiesto de dependencias, punto de entrada, configuracion, capa de interfaz, y las capas del patron arquitectonico declarado. Eso es andamiaje y es tu trabajo.
2. **NO resuelvas el reto.** Los entregables de las fases son el trabajo de la persona. El hueco pedagogico se deja como esta: el proyecto arranca, pero lo que el reto pide implementar NO esta implementado.

Dicho de otra forma: si algo impide compilar, arreglalo. Si algo es logica de negocio incompleta, validaciones ausentes, un secreto hardcodeado o un patron mejorable, dejalo exactamente como esta — es lo que la persona tiene que encontrar.

## Lo que le falta a este proyecto

Esto NO lo tenes que adivinar: salio de comparar el proyecto contra la arquitectura declarada del reto y de un analisis estatico del codigo. Completalo TODO.

### Archivos que la arquitectura del reto declara y no estan

Creálos con implementacion real, en la capa que les corresponde:

- `src/main/java/com/pragma/riskengine/circuitbreaker/DynamicCircuitBreaker.java`
- `src/main/java/com/pragma/riskengine/killswitch/KillSwitchPolicy.java`
- `src/main/java/com/pragma/riskengine/compliance/MiFIDIITracer.java`

### Referencias colgando en el codigo que si esta

Cada una rompe la compilacion:

- `src/main/java/com/pragma/riskengine/Main.java` — `com.pragma.riskengine.circuitbreaker.DynamicCircuitBreaker`: El import com.pragma.riskengine.circuitbreaker.DynamicCircuitBreaker usa un paquete propio del proyecto pero ningun archivo generado declara ese tipo. Falta generar la clase/interfaz, o el import esta mal escrito.
- `src/main/java/com/pragma/riskengine/Main.java` — `com.pragma.riskengine.killswitch.KillSwitchPolicy`: El import com.pragma.riskengine.killswitch.KillSwitchPolicy usa un paquete propio del proyecto pero ningun archivo generado declara ese tipo. Falta generar la clase/interfaz, o el import esta mal escrito.
- `src/main/java/com/pragma/riskengine/Main.java` — `com.pragma.riskengine.compliance.MiFIDIITracer`: El import com.pragma.riskengine.compliance.MiFIDIITracer usa un paquete propio del proyecto pero ningun archivo generado declara ese tipo. Falta generar la clase/interfaz, o el import esta mal escrito.
- `src/main/java/com/pragma/riskengine/var/VaRIntradayCalculator.java` — `RiskEvent`: El import com.pragma.riskengine.RiskEvent no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- `src/test/java/com/pragma/riskengine/var/VaRIntradayCalculatorTest.java` — `RiskEvent`: El import com.pragma.riskengine.RiskEvent no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- `src/main/java/com/pragma/riskengine/var/VaRIntradayCalculator.java` — `InstrumentVaRState.getLock`: Se invoca `getLock` sobre `InstrumentVaRState`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/riskengine/var/VaRIntradayCalculator.java` — `InstrumentVaRState.updateVaR`: Se invoca `updateVaR` sobre `InstrumentVaRState`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/riskengine/var/VaRIntradayCalculator.java` — `InstrumentVaRState.setLastCalculationTime`: Se invoca `setLastCalculationTime` sobre `InstrumentVaRState`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/riskengine/var/VaRIntradayCalculator.java` — `InstrumentVaRState.incrementCalculationCount`: Se invoca `incrementCalculationCount` sobre `InstrumentVaRState`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/riskengine/limits/RiskLimitsService.java` — `InstrumentLimits.getMaxExposure`: Se invoca `getMaxExposure` sobre `InstrumentLimits`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/riskengine/limits/RiskLimitsService.java` — `PositionState.getNetPosition`: Se invoca `getNetPosition` sobre `PositionState`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/riskengine/limits/RiskLimitsService.java` — `DailyPnL.getDailyLoss`: Se invoca `getDailyLoss` sobre `DailyPnL`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/riskengine/limits/RiskLimitsService.java` — `PositionState.getTotalExposure`: Se invoca `getTotalExposure` sobre `PositionState`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/riskengine/replay/DeterministicReplayEngine.java` — `EventSession.addEvent`: Se invoca `addEvent` sobre `EventSession`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/riskengine/replay/DeterministicReplayEngine.java` — `EventSession.getEvents`: Se invoca `getEvents` sobre `EventSession`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/main/java/com/pragma/riskengine/replay/DeterministicReplayEngine.java` — `ReplayScheduler.schedule`: Se invoca `schedule` sobre `ReplayScheduler`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/riskengine/var/VaRIntradayCalculatorTest.java` — `VaRIntradayCalculator.calculateVar`: Se invoca `calculateVar` sobre `VaRIntradayCalculator`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/riskengine/var/VaRIntradayCalculatorTest.java` — `VaRIntradayCalculator.calculatePortfolioVaR`: Se invoca `calculatePortfolioVaR` sobre `VaRIntradayCalculator`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/riskengine/var/VaRIntradayCalculatorTest.java` — `VaRIntradayCalculator.updatePosition`: Se invoca `updatePosition` sobre `VaRIntradayCalculator`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/riskengine/var/VaRIntradayCalculatorTest.java` — `VaRIntradayCalculator.getCurrentVar`: Se invoca `getCurrentVar` sobre `VaRIntradayCalculator`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/riskengine/var/VaRIntradayCalculatorTest.java` — `VaRIntradayCalculator.onVolatilityUpdate`: Se invoca `onVolatilityUpdate` sobre `VaRIntradayCalculator`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/riskengine/var/VaRIntradayCalculatorTest.java` — `VaRIntradayCalculator.setTimeHorizonMinutes`: Se invoca `setTimeHorizonMinutes` sobre `VaRIntradayCalculator`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- `src/test/java/com/pragma/riskengine/var/VaRIntradayCalculatorTest.java` — `VaRIntradayCalculator.calculateTotalExposure`: Se invoca `calculateTotalExposure` sobre `VaRIntradayCalculator`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.

## Como saber que terminaste

```bash
el comando de build o arranque canonico del stack elegido
```

Ese comando corriendo sin errores es la definicion de "listo".

---

```
## Briefing del reto (autoridad)
Este bloque manda sobre los archivos adjuntos. El stack y el rol salen de AQUÍ, no de un topic genérico ni de markdown placeholder.

### Contexto técnico original
Sistema master-l2 que evalúa el riesgo de cada orden antes de enviarla al exchange, en menos de 500 microsegundos p99. Consume feed de market data (nivel 2 orderbook + trades), mantiene un modelo de VaR intraday, aplica límites por trader/estrategia/instrumento en tiempo real, y dispara circuit breakers dinámicos que bloquean nuevas órdenes cuando la exposición supera thresholds calibrados por volatilidad. Debe justificar el uso de estructuras lock-free vs mutex, la elección entre C++ vs Rust vs Java LMAX Disruptor, cómo garantiza la consistencia entre múltiples risk engines corriendo en paralelo (consensus vs sharding por instrument), la política de kill switch cuando detecta un algoritmo que se comporta de forma anómala, y la estrategia de replay determinístico para post-mortem de incidents. Incluye compliance con regulaciones MiFID II para trazabilidad de decisiones de riesgo.

### Reto
- Tema: TEST-CT
- Seniority: master-l2
- Tipo: practical
- Título: Motor de Risk Scoring en Tiempo Real con Circuit Breakers Dinámicos
- Tiempo estimado: 4 semanas

### Fases (trabajo del HUMANO — PROHIBIDO completarlas)
No implementes estos entregables. Dejalos como hueco pedagógico. El asistente solo materializa el proyecto arrancable para que el participante pueda trabajar.
- Fase 1: Consumo y Procesamiento de Market Data — objetivo: Implementar la funcionalidad para consumir y procesar el feed de market data en tiempo real. — entregable (NO resolver): Sistema que consume y procesa el feed de market data en tiempo real.
- Fase 2: Modelo de VaR Intraday y Límites de Riesgo — objetivo: Implementar el modelo de VaR intraday y aplicar límites de riesgo por trader/estrategia/instrumento. — entregable (NO resolver): Sistema que implementa el modelo de VaR intraday y aplica límites de riesgo en tiempo real.
- Fase 3: Circuit Breakers Dinámicos y Kill Switch — objetivo: Implementar circuit breakers dinámicos y la política de kill switch para bloquear nuevas órdenes y detener algoritmos anómalos. — entregable (NO resolver): Sistema que implementa circuit breakers dinámicos y la política de kill switch en tiempo real.
- Fase 4: Compliance con Regulaciones MiFID II — objetivo: Implementar la trazabilidad de decisiones de riesgo de acuerdo con las regulaciones MiFID II. — entregable (NO resolver): Sistema que implementa la trazabilidad de decisiones de riesgo de acuerdo con las regulaciones MiFID II.

Eres un asistente experto en análisis, corrección y generación de archivos de cualquier tipo:
código fuente, documentación, hojas de cálculo, documentos Word, configuraciones, entre otros.
Voy a enviarte una cadena de texto que contiene uno o más archivos. Cada archivo está delimitado por un marcador con el siguiente formato:
// === ARCHIVO: ruta/del/archivo.extension ===
o también puede aparecer como:
## === ARCHIVO: ruta/del/archivo.extension ===
Lo que sigue al marcador puede ser:

El contenido real del archivo (código, texto, YAML, etc.)
Una descripción en lenguaje natural de lo que debe contener el archivo


TU TAREA
PASO 0 — ¿Esto es un proyecto o una carcasa?
Antes de extraer archivos, leé el Briefing (si está) y diagnosticá el adjunto.

Es CARCASA si ocurre CUALQUIERA de estas:
- No hay manifiesto de dependencias del stack del briefing (manifest.json de VTEX IO / package.json / pom.xml / build.gradle / requirements.txt / go.mod / *.tf / *.csproj, según corresponda)
- Hay un "binario" que en realidad es un comentario ("no puede ser mostrado como texto plano", placeholder .fig/.docx vacío)
- Los markdowns ya completan entregables de fases posteriores ("se implementó fade-in", lista de áreas ya resuelta)

Si es CARCASA:
- MATERIALIZÁ un proyecto que arranca en el stack del briefing (VTEX IO Store Framework, Angular, Terraform, pytest, Nest, etc.). Incluí manifiesto, punto de entrada y capa de interfaz reales.
- NO copies los markdowns de "solución" como si fueran el producto. Son ruido de generación.
- NO resuelvas las fases del briefing (están marcadas PROHIBIDO). Dejá el hueco pedagógico: el flujo existe, las microinteracciones/calidad/infra que el reto pide NO están hechas.
- Después seguí al PASO 5 (ZIP).

Si es un proyecto REAL (manifiesto + código que compila o arranca):
- Seguí PASO 1 en adelante. 🔴 compilación sí. 🟡 pedagógico no.

PASO 1 — Detección y extracción
Identifica todos los archivos presentes en la cadena. Para cada archivo extrae:

Su ruta completa (ej: src/main/java/com/pragma/Service.java)
Su contenido o descripción

PASO 2 — Clasificación por tipo
Clasifica cada archivo en una de estas categorías:
A) Código fuente (Java, Python, TypeScript, JavaScript, Kotlin, etc.)
B) Configuración / documentación (YAML, properties, Markdown, JSON, txt, etc.)
C) Excel (.xlsx, .xls, .csv)
D) Word (.docx, .doc)
E) Otro tipo de archivo binario o especial
PASO 3 — Clasificación de errores en código fuente

Objetivo prioritario: que el proyecto compile. No corrijas flujo de negocio ni lógica funcional.

Antes de modificar cualquier archivo de código fuente, clasifica cada problema encontrado en una de estas dos categorías:
🔴 ERROR DE COMPILACIÓN — corregir siempre
Son errores que impiden que el proyecto arranque, sin valor pedagógico:

Import faltante o incorrecto
Clase, método o variable referenciada que no existe en ningún archivo del proyecto
Error de sintaxis
Anotación con atributos inválidos
Dependencia ausente en pom.xml, package.json, etc.
Archivo referenciado que no existe y debe ser creado con implementación mínima

→ CORREGIR estos errores.
🟡 PROBLEMA FUNCIONAL O DE CALIDAD — preservar siempre
Son problemas que no impiden compilar. Pueden ser intencionales para el aprendizaje:

Clave secreta hardcodeada ("secret", "password123")
API deprecada que funciona pero tiene reemplazo moderno
Lógica de negocio incorrecta o incompleta
Código redundante o de baja legibilidad
Falta de validaciones en flujo de negocio
Patrones de diseño incorrectos pero funcionales
Concurrencia no segura
Configuración funcional pero no óptima

→ PRESERVAR tal cual. No corregir, no mejorar, no comentar.
PASO 4 — Procesamiento según tipo de archivo
Tipo A — Código fuente
Aplica únicamente las correcciones clasificadas como 🔴 ERROR DE COMPILACIÓN.
No alteres ningún elemento clasificado como 🟡 PROBLEMA FUNCIONAL O DE CALIDAD.
Si falta un archivo referenciado, créalo con la implementación mínima necesaria para compilar.
Tipo B — Configuración / documentación
Extrae el contenido tal cual, sin modificaciones salvo errores evidentes de sintaxis
(ej: YAML mal indentado).
Tipo C — Excel (.xlsx)
Si viene con contenido real, genera el archivo respetando ese contenido.
Si viene con descripción en lenguaje natural, genera un archivo Excel funcional con:

Fila de encabezados en negrita con color de fondo distintivo
Columnas con ancho ajustado al contenido
Tipos de dato correctos por columna
Validaciones si la descripción lo indica
Hojas nombradas descriptivamente si hay más de una
Filas de ejemplo si no hay datos reales

Tipo D — Word (.docx)
Si viene con contenido real, genera el archivo respetando ese contenido.
Si viene con descripción en lenguaje natural, genera un documento Word funcional con:

Estilos de título (Título 1, Título 2) para jerarquía de secciones
Fuente legible (Calibri o equivalente), tamaño 11-12pt para cuerpo
Márgenes estándar
Tabla de contenido si tiene múltiples secciones
Tablas con encabezados en negrita si aplica

Tipo E — Otro
Genera el archivo con el contenido o estructura más apropiada según la descripción.
PASO 5 — Exportación en ZIP
Empaqueta todos los archivos en un único archivo ZIP descargable respetando exactamente
la estructura de rutas indicada por los marcadores.
El ZIP debe incluir:

Archivos de código con únicamente los errores de compilación corregidos
Archivos de configuración y documentación sin cambios
Archivos nuevos creados para resolver dependencias de compilación faltantes
Archivos Excel y Word generados desde descripción

IMPORTANTE: El ZIP debe estar listo para descargar al finalizar. No preguntes si el usuario
quiere generarlo. Simplemente genera el archivo y proporciona el enlace de descarga; No debes desplegar en el chat el resumen de lo que arreglaste al Zip, solo entregalo.

REGLAS IMPORTANTES

No omitas ningún archivo aunque no tenga errores ni modificaciones
Respeta los nombres y rutas exactas indicadas por los marcadores
Si un archivo no tiene marcador claro, infiere el nombre desde su contenido
Si la cadena contiene solo documentación, placeholders o binarios fake, NO la reproduzcas:
aplicá PASO 0 (materializar el proyecto del briefing). Reproducir la carcasa es un fallo.
No agregues texto después del enlace de descarga del ZIP
No preguntes si el usuario quiere el ZIP: simplemente generalo siempre
Si detectas que falta un archivo de configuración necesario para compilar
(pom.xml, package.json, requirements.txt, build.gradle, etc.), créalo e inclúyelo
inferiendo su contenido desde los imports y frameworks detectados en el código
Nunca corrijas problemas 🟡 aunque parezcan obvios o fáciles de mejorar.
El participante que recibirá este proyecto los debe encontrar y resolver él mismo.


INPUT
Aquí está la cadena con los archivos:

// === ARCHIVO: pom.xml ===
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.pragma</groupId>
    <artifactId>risk-engine</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>Risk Engine</name>
    <description>Real-time Risk Scoring Engine with Dynamic Circuit Breakers</description>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <disruptor.version>4.0.0</disruptor.version>
        <resilience4j.version>2.1.0</resilience4j.version>
        <commons-math3.version>3.6.1</commons-math3.version>
        <junit.version>5.10.0</junit.version>
        <mockito.version>5.5.0</mockito.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.lmax</groupId>
            <artifactId>disruptor</artifactId>
            <version>${disruptor.version}</version>
        </dependency>

        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-core</artifactId>
            <version>${resilience4j.version}</version>
        </dependency>

        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-circuitbreaker</artifactId>
            <version>${resilience4j.version}</version>
        </dependency>

        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-retry</artifactId>
            <version>${resilience4j.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-math3</artifactId>
            <version>${commons-math3.version}</version>
        </dependency>

        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.9</version>
        </dependency>

        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.4.11</version>
        </dependency>

        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>${mockito.version}</version>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-junit-jupiter</artifactId>
            <version>${mockito.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.1.2</version>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>com.pragma.riskengine.Main</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

// === ARCHIVO: src/main/java/com/pragma/riskengine/Main.java ===
package com.pragma.riskengine;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.pragma.riskengine.marketdata.MarketDataEventHandler;
import com.pragma.riskengine.var.VaRIntradayCalculator;
import com.pragma.riskengine.limits.RiskLimitsService;
import com.pragma.riskengine.circuitbreaker.DynamicCircuitBreaker;
import com.pragma.riskengine.killswitch.KillSwitchPolicy;
import com.pragma.riskengine.compliance.MiFIDIITracer;
import com.pragma.riskengine.sharding.InstrumentShardingStrategy;
import com.pragma.riskengine.replay.DeterministicReplayEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final int RING_BUFFER_SIZE = 1 << 16;
    private static final int RING_BUFFER_POW2 = 65536;

    private final Disruptor<RiskEvent> disruptor;
    private final RingBuffer<RiskEvent> ringBuffer;
    private final AtomicBoolean isRunning;

    private final MarketDataEventHandler marketDataHandler;
    private final VaRIntradayCalculator varCalculator;
    private final RiskLimitsService limitsService;
    private final DynamicCircuitBreaker circuitBreaker;
    private final KillSwitchPolicy killSwitchPolicy;
    private final MiFIDIITracer complianceTracer;
    private final InstrumentShardingStrategy shardingStrategy;
    private final DeterministicReplayEngine replayEngine;

    public Main() {
        this.isRunning = new AtomicBoolean(false);

        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        this.marketDataHandler = new MarketDataEventHandler();
        this.varCalculator = new VaRIntradayCalculator();
        this.limitsService = new RiskLimitsService();
        this.circuitBreaker = new DynamicCircuitBreaker();
        this.killSwitchPolicy = new KillSwitchPolicy();
        this.complianceTracer = new MiFIDIITracer();
        this.shardingStrategy = new InstrumentShardingStrategy();
        this.replayEngine = new DeterministicReplayEngine();

        this.disruptor = new Disruptor<>
            (RiskEvent::new, RING_BUFFER_POW2, threadFactory,
             ProducerType.MULTI, new BlockingWaitStrategy());

        disruptor.handleEventsWith(marketDataHandler)
            .then(varCalculator)
            .then(limitsService)
            .then(circuitBreaker)
            .then(killSwitchPolicy)
            .then(complianceTracer);

        this.ringBuffer = disruptor.getRingBuffer();

        logger.info("Risk Engine inicializado con RingBuffer de {} elementos", RING_BUFFER_POW2);
    }

    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            disruptor.start();
            logger.info("Risk Engine iniciado y escuchando eventos");
        } else {
            logger.warn("Risk Engine ya se encuentra en ejecucion");
        }
    }

    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            disruptor.shutdown();
            logger.info("Risk Engine detenido");
        } else {
            logger.warn("Risk Engine no esta en ejecucion");
        }
    }

    public void publishEvent(RiskEvent event) {
        if (!isRunning.get()) {
            logger.error("No se puede publicar evento: Risk Engine no esta inicializado");
            return;
        }

        long sequence = ringBuffer.next();
        try {
            RiskEvent storedEvent = ringBuffer.get(sequence);
            storedEvent.copyFrom(event);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public VaRIntradayCalculator getVarCalculator() {
        return varCalculator;
    }

    public RiskLimitsService getLimitsService() {
        return limitsService;
    }

    public DynamicCircuitBreaker getCircuitBreaker() {
        return circuitBreaker;
    }

    public KillSwitchPolicy getKillSwitchPolicy() {
        return killSwitchPolicy;
    }

    public MiFIDIITracer getComplianceTracer() {
        return complianceTracer;
    }

    public InstrumentShardingStrategy getShardingStrategy() {
        return shardingStrategy;
    }

    public DeterministicReplayEngine getReplayEngine() {
        return replayEngine;
    }

    public static void main(String[] args) {
        Main engine = new Main();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Recibida senial de cierre, deteniendo Risk Engine...");
            engine.stop();
        }));

        engine.start();

        logger.info("Risk Engine listo para procesar eventos de riesgo");
    }
}

// === ARCHIVO: src/main/java/com/pragma/riskengine/RiskEvent.java ===
package com.pragma.riskengine;

import java.time.Instant;

public final class RiskEvent {

    private String eventId;
    private String instrumentId;
    private String traderId;
    private String strategyId;
    private EventType eventType;
    private double price;
    private double quantity;
    private double volatility;
    private double var;
    private double exposure;
    private RiskDecision decision;
    private Instant timestamp;
    private long nanoTimestamp;
    private String correlationId;

    public RiskEvent() {
        this.timestamp = Instant.now();
        this.nanoTimestamp = System.nanoTime();
    }

    public void copyFrom(RiskEvent other) {
        this.eventId = other.eventId;
        this.instrumentId = other.instrumentId;
        this.traderId = other.traderId;
        this.strategyId = other.strategyId;
        this.eventType = other.eventType;
        this.price = other.price;
        this.quantity = other.quantity;
        this.volatility = other.volatility;
        this.var = other.var;
        this.exposure = other.exposure;
        this.decision = other.decision;
        this.timestamp = other.timestamp;
        this.nanoTimestamp = other.nanoTimestamp;
        this.correlationId = other.correlationId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    public String getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(String strategyId) {
        this.strategyId = strategyId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    public double getVar() {
        return var;
    }

    public void setVar(double var) {
        this.var = var;
    }

    public double getExposure() {
        return exposure;
    }

    public void setExposure(double exposure) {
        this.exposure = exposure;
    }

    public RiskDecision getDecision() {
        return decision;
    }

    public void setDecision(RiskDecision decision) {
        this.decision = decision;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public long getNanoTimestamp() {
        return nanoTimestamp;
    }

    public void setNanoTimestamp(long nanoTimestamp) {
        this.nanoTimestamp = nanoTimestamp;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public enum EventType {
        MARKET_DATA,
        ORDER_SUBMITTED,
        ORDER_FILLED,
        ORDER_CANCELLED,
        POSITION_UPDATE,
        LIMIT_CHECK,
        CIRCUIT_BREAKER_TRIGGERED,
        KILL_SWITCH_ACTIVATED
    }

    public enum RiskDecision {
        APPROVED,
        REJECTED,
        REVIEW_REQUIRED,
        CIRCUIT_BREAKER_OPEN,
        KILL_SWITCH_ACTIVATED,
        PENDING
    }
}

// === ARCHIVO: src/main/java/com/pragma/riskengine/marketdata/MarketDataEventHandler.java ===
package com.pragma.riskengine.marketdata;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.pragma.riskengine.Main;
import com.pragma.riskengine.RiskEvent;
import com.pragma.riskengine.RiskEvent.EventType;
import com.pragma.riskengine.RiskEvent.RiskDecision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class MarketDataEventHandler implements EventHandler<RiskEvent> {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataEventHandler.class);
    private static final int ORDER_BOOK_LEVELS = 10;
    private static final double MIN_PRICE_TICK = 0.01;
    private static final double MAX_SPREAD_BPS = 50.0;
    private static final long MICROS_TO_NANOS = 1_000L;

    private final Main main;
    private final AtomicLong lastOrderBookUpdate = new AtomicLong(0L);
    private final LongAdder eventsProcessed = new LongAdder();
    private final LongAdder totalLatencyMicros = new LongAdder();
    private volatile double lastBestBid = 0.0;
    private volatile double lastBestAsk = Double.MAX_VALUE;
    private volatile double lastMidPrice = 0.0;
    private volatile double lastVolatility = 0.0;
    private volatile boolean orderBookStale = false;

    public MarketDataEventHandler(Main main) {
        this.main = main;
    }

    @Override
    public void onEvent(RiskEvent event, long sequence, boolean endOfBatch) {
        long startNanos = System.nanoTime();
        try {
            switch (event.getEventType()) {
                case ORDER_BOOK_UPDATE -> processOrderBookUpdate(event);
                case TRADE -> processTrade(event);
                case ORDER_SUBMISSION -> processOrderSubmission(event);
                default -> logger.warn("Tipo de evento desconocido: {}", event.getEventType());
            }
            lastOrderBookUpdate.set(System.nanoTime());
        } catch (Exception e) {
            logger.error("Error procesando evento {}: {}", event.getEventId(), e.getMessage(), e);
            event.setDecision(RiskDecision.REJECTED);
        } finally {
            long latencyMicros = (System.nanoTime() - startNanos) / MICROS_TO_NANOS;
            eventsProcessed.increment();
            totalLatencyMicros.add(latencyMicros);
            if (eventsProcessed.sum() % 10000 == 0) {
                logPerformanceMetrics();
            }
        }
    }

    private void processOrderBookUpdate(RiskEvent event) {
        double bid = extractBestBid(event);
        double ask = extractBestAsk(event);
        double spread = calculateSpread(bid, ask);
        if (spread > MAX_SPREAD_BPS * lastMidPrice / 10000) {
            orderBookStale = true;
            logger.warn("Order book spread anomalous para instrumento {}: {} bps", 
                event.getInstrumentId(), spread / lastMidPrice * 10000);
        } else {
            orderBookStale = false;
        }
        lastBestBid = bid;
        lastBestAsk = ask;
        lastMidPrice = (bid + ask) / 2.0;
        double newVolatility = calculateVolatilityFromBook(bid, ask);
        event.setPrice(lastMidPrice);
        event.setVolatility(newVolatility);
        lastVolatility = newVolatility;
        if (orderBookStale) {
            event.setDecision(RiskDecision.WARNED);
        } else {
            event.setDecision(RiskDecision.APPROVED);
        }
        event.setTimestamp(Instant.now());
        event.setNanoTimestamp(System.nanoTime());
    }

    private void processTrade(RiskEvent event) {
        double tradePrice = event.getPrice();
        double tradeQuantity = event.getQuantity();
        if (tradePrice <= 0 || tradeQuantity <= 0) {
            logger.warn("Trade inválido para instrumento {}: precio={}, cantidad={}",
                event.getInstrumentId(), tradePrice, tradeQuantity);
            event.setDecision(RiskDecision.REJECTED);
            return;
        }
        double priceDeviation = Math.abs(tradePrice - lastMidPrice) / lastMidPrice;
        if (priceDeviation > 0.05) {
            logger.warn("Trade con desviación significativa: instrumento={}, desviación={}%",
                event.getInstrumentId(), priceDeviation * 100);
        }
        double exposureImpact = tradePrice * tradeQuantity;
        event.setExposure(exposureImpact);
        event.setDecision(RiskDecision.APPROVED);
        event.setTimestamp(Instant.now());
        event.setNanoTimestamp(System.nanoTime());
    }

    private void processOrderSubmission(RiskEvent event) {
        double orderPrice = event.getPrice();
        double orderQuantity = event.getQuantity();
        if (orderQuantity > getMaxOrderSize(event.getInstrumentId())) {
            logger.warn("Orden excede tamaño máximo: instrumento={}, cantidad={}, máximo={}",
                event.getInstrumentId(), orderQuantity, getMaxOrderSize(event.getInstrumentId()));
            event.setDecision(RiskDecision.REJECTED);
            return;
        }
        if (orderBookStale) {
            logger.warn("Orden rechazada por order book stale: instrumento={}", event.getInstrumentId());
            event.setDecision(RiskDecision.WARNED);
            return;
        }
        double orderValue = orderPrice * orderQuantity;
        event.setExposure(orderValue);
        event.setDecision(RiskDecision.APPROVED);
        event.setTimestamp(Instant.now());
        event.setNanoTimestamp(System.nanoTime());
    }

    private double extractBestBid(RiskEvent event) {
        return event.getPrice() > 0 ? event.getPrice() * 0.9995 : 100.0;
    }

    private double extractBestAsk(RiskEvent event) {
        return event.getPrice() > 0 ? event.getPrice() * 1.0005 : 100.01;
    }

    private double calculateSpread(double bid, double ask) {
        if (bid <= 0 || ask <= 0 || ask <= bid) return Double.MAX_VALUE;
        return (ask - bid) / lastMidPrice * 10000;
    }

    private double calculateVolatilityFromBook(double bid, double ask) {
        double spread = Math.abs(ask - bid);
        double volatilityEstimate = spread / lastMidPrice * Math.sqrt(252 * 6.5 * 390);
        return Math.max(volatilityEstimate, 0.01);
    }

    private double getMaxOrderSize(String instrumentId) {
        return 1_000_000.0;
    }

    private void logPerformanceMetrics() {
        double avgLatency = (double) totalLatencyMicros.sum() / eventsProcessed.sum();
        logger.info("MarketDataHandler - Eventos: {}, Latencia avg: {} μs",
            eventsProcessed.sum(), String.format("%.2f", avgLatency));
    }

    public double getLastMidPrice() {
        return lastMidPrice;
    }

    public double getLastVolatility() {
        return lastVolatility;
    }

    public boolean isOrderBookStale() {
        return orderBookStale;
    }

    public long getEventsProcessed() {
        return eventsProcessed.sum();
    }
}

// === ARCHIVO: src/main/java/com/pragma/riskengine/var/VaRIntradayCalculator.java ===
package com.pragma.riskengine.var;

import com.pragma.riskengine.Main;
import com.pragma.riskengine.RiskEvent;
import com.pragma.riskengine.RiskEvent.EventType;
import com.pragma.riskengine.RiskEvent.RiskDecision;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.StampedLock;

public class VaRIntradayCalculator {

    private static final Logger logger = LoggerFactory.getLogger(VaRIntradayCalculator.class);
    private static final double DEFAULT_CONFIDENCE_LEVEL = 0.99;
    private static final double DEFAULT_HOLDING_PERIOD_HOURS = 1.0;
    private static final int MAX_PRICE_HISTORY = 1000;
    private static final double MIN_VOLATILITY = 0.01;
    private static final double MAX_VOLATILITY = 5.0;

    private final Main main;
    private final NormalDistribution normalDistribution;
    private final ConcurrentHashMap<String, InstrumentVaRState> instrumentStates;
    private final ConcurrentHashMap<String, double[]> priceHistories;

    public VaRIntradayCalculator(Main main) {
        this.main = main;
        this.normalDistribution = new NormalDistribution();
        this.instrumentStates = new ConcurrentHashMap<>();
        this.priceHistories = new ConcurrentHashMap<>();
    }

    public double calculateVaR(String instrumentId, double exposure, double volatility, Duration timeWindow) {
        InstrumentVaRState state = instrumentStates.computeIfAbsent(instrumentId, 
            k -> new InstrumentVaRState());
        StampedLock lock = state.getLock();
        long stamp = lock.writeLock();
        try {
            double effectiveVolatility = clampVolatility(volatility);
            double scaledVolatility = effectiveVolatility * Math.sqrt(timeWindow.toHours() / DEFAULT_HOLDING_PERIOD_HOURS);
            double var99 = calculateParametricVaR(exposure, scaledVolatility, DEFAULT_CONFIDENCE_LEVEL);
            state.updateVaR(var99);
            state.setLastCalculationTime(Instant.now());
            state.incrementCalculationCount();
            logger.debug("VaR calculado para instrumento {}: exposición={}, volatilidad={}, VaR99={}",
                instrumentId, exposure, effectiveVolatility, var99);
            return var99;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public double calculateVaRHistorical(String instrumentId, double exposure, int lookbackDays) {
        double[] prices = priceHistories.get(instrumentId);
        if (prices == null || prices.length < 2) {
            logger.warn("Sin historial suficiente para VaR histórico: instrumento {}", instrumentId);
            return calculateVaRDefault(exposure);
        }
        double[] returns = calculateReturns(prices);
        double[] sortedReturns = returns.clone();
        java.util.Arrays.sort(sortedReturns);
        int varIndex = (int) Math.ceil((1 - DEFAULT_CONFIDENCE_LEVEL) * sortedReturns.length);
        double varReturn = -sortedReturns[Math.min(varIndex, sortedReturns.length - 1)];
        double historicalVaR = exposure * varReturn;
        InstrumentVaRState state = instrumentStates.get(instrumentId);
        if (state != null) {
            state.updateVaR(historicalVaR);
        }
        return historicalVaR;
    }

    public void updatePriceHistory(String instrumentId, double price) {
        priceHistories.compute(instrumentId, (key, existing) -> {
            if (existing == null) {
                return new double[]{price};
            }
            if (existing.length >= MAX_PRICE_HISTORY) {
                System.arraycopy(existing, 1, existing, 0, existing.length - 1);
                existing[existing.length - 1] = price;
                return existing;
            } else {
                double[] updated = new double[existing.length + 1];
                System.arraycopy(existing, 0, updated, 0, existing.length);
                updated[existing.length] = price;
                return updated;
            }
        });
    }

    public double getCurrentVaR(String instrumentId) {
        InstrumentVaRState state = instrumentStates.get(instrumentId);
        if (state == null) {
            return 0.0;
        }
        StampedLock lock = state.getLock();
        long stamp = lock.tryOptimisticRead();
        double var = state.getCurrentVaR();
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                var = state.getCurrentVaR();
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return var;
    }

    private double calculateParametricVaR(double exposure, double volatility, double confidenceLevel) {
        double zScore = normalDistribution.inverseCumulativeProbability(confidenceLevel);
        double var = exposure * volatility * zScore;
        return Math.max(var, 0.0);
    }

    private double[] calculateReturns(double[] prices) {
        if (prices.length < 2) {
            return new double[0];
        }
        double[] returns = new double[prices.length - 1];
        for (int i = 1; i < prices.length; i++) {
            returns[i - 1] = (prices[i] - prices[i - 1]) / prices[i - 1];
        }
        return returns;
    }

    private double clampVolatility(double volatility) {
        return Math.max(MIN_VOLATILITY, Math.min(MAX_VOLATILITY, volatility));
    }

    private double calculateVaRDefault(double exposure) {
        return exposure * DEFAULT_CONFIDENCE_LEVEL * 0.02;
    }

    public void reset() {
        instrumentStates.clear();
        priceHistories.clear();
        logger.info("VaR calculator reseteado");
    }

    public int getInstrumentCount() {
        return instrumentStates.size();
    }

    private static class InstrumentVaRState {
        private final StampedLock lock = new StampedLock();
        private final AtomicReference<Double> currentVaR = new AtomicReference<>(0.0);
        private volatile Instant lastCalculationTime;
        private volatile long calculationCount = 0;

        public StampedLock getLock() {
            return lock;
        }

        public double getCurrentVaR() {
            return currentVaR.get();
        }

        public void updateVaR(double var) {
            currentVaR.set(var);
        }

        public void setLastCalculationTime(Instant time) {
            this.lastCalculationTime = time;
        }

        public void incrementCalculationCount() {
            calculationCount++;
        }
    }
}

// === ARCHIVO: src/main/java/com/pragma/riskengine/limits/RiskLimitsService.java ===
package com.pragma.riskengine.limits;

import com.pragma.riskengine.Main;
import com.pragma.riskengine.RiskEvent;
import com.pragma.riskengine.RiskEvent.EventType;
import com.pragma.riskengine.RiskEvent.RiskDecision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.StampedLock;

public class RiskLimitsService {

    private static final Logger logger = LoggerFactory.getLogger(RiskLimitsService.class);
    private static final double DEFAULT_POSITION_LIMIT = 10_000_000.0;
    private static final double DEFAULT_TRADER_LIMIT = 50_000_000.0;
    private static final double DEFAULT_STRATEGY_LIMIT = 100_000_000.0;
    private static final double DEFAULT_DAILY_LOSS_LIMIT = 1_000_000.0;
    private static final double DEFAULT_CONCENTRATION_LIMIT = 0.25;
    private static final int MAX_VIOLATIONS_PER_MINUTE = 5;

    private final Main main;
    private final ConcurrentHashMap<String, TraderLimits> traderLimitsMap;
    private final ConcurrentHashMap<String, StrategyLimits> strategyLimitsMap;
    private final ConcurrentHashMap<String, InstrumentLimits> instrumentLimitsMap;
    private final ConcurrentHashMap<String, PositionState> positionStates;
    private final ConcurrentHashMap<String, DailyPnL> dailyPnLs;

    public RiskLimitsService(Main main) {
        this.main = main;
        this.traderLimitsMap = new ConcurrentHashMap<>();
        this.strategyLimitsMap = new ConcurrentHashMap<>();
        this.instrumentLimitsMap = new ConcurrentHashMap<>();
        this.positionStates = new ConcurrentHashMap<>();
        this.dailyPnLs = new ConcurrentHashMap<>();
    }

    public RiskDecision evaluateLimits(RiskEvent event) {
        String traderId = event.getTraderId();
        String strategyId = event.getStrategyId();
        String instrumentId = event.getInstrumentId();
        double exposure = event.getExposure();
        double price = event.getPrice();
        double quantity = event.getQuantity();

        RiskDecision decision = checkTraderLimit(traderId, exposure);
        if (decision == RiskDecision.REJECTED) {
            logLimitViolation("TRADER", traderId, exposure, "límite de trader");
            return decision;
        }

        decision = checkStrategyLimit(strategyId, exposure);
        if (decision == RiskDecision.REJECTED) {
            logLimitViolation("STRATEGY", strategyId, exposure, "límite de estrategia");
            return decision;
        }

        decision = checkInstrumentLimit(instrumentId, exposure);
        if (decision == RiskDecision.REJECTED) {
            logLimitViolation("INSTRUMENT", instrumentId, exposure, "límite de instrumento");
            return decision;
        }

        decision = checkPositionLimit(instrumentId, quantity);
        if (decision == RiskDecision.REJECTED) {
            logLimitViolation("POSITION", instrumentId, quantity, "límite de posición");
            return decision;
        }

        decision = checkConcentrationLimit(traderId, instrumentId, exposure);
        if (decision == RiskDecision.REJECTED) {
            logLimitViolation("CONCENTRATION", traderId, exposure, "límite de concentración");
            return decision;
        }

        decision = checkDailyLossLimit(traderId);
        if (decision == RiskDecision.REJECTED) {
            logLimitViolation("DAILY_LOSS", traderId, 0, "límite de pérdida diaria");
            return decision;
        }

        updatePosition(instrumentId, quantity, price);
        return RiskDecision.APPROVED;
    }

    private RiskDecision checkTraderLimit(String traderId, double exposure) {
        TraderLimits limits = traderLimitsMap.computeIfAbsent(traderId, 
            k -> new TraderLimits(DEFAULT_TRADER_LIMIT));
        double currentExposure = getTraderExposure(traderId);
        if (currentExposure + exposure > limits.getMaxExposure()) {
            return RiskDecision.REJECTED;
        }
        return RiskDecision.APPROVED;
    }

    private RiskDecision checkStrategyLimit(String strategyId, double exposure) {
        StrategyLimits limits = strategyLimitsMap.computeIfAbsent(strategyId,
            k -> new StrategyLimits(DEFAULT_STRATEGY_LIMIT));
        double currentExposure = getStrategyExposure(strategyId);
        if (currentExposure + exposure > limits.getMaxExposure()) {
            return RiskDecision.REJECTED;
        }
        return RiskDecision.APPROVED;
    }

    private RiskDecision checkInstrumentLimit(String instrumentId, double exposure) {
        InstrumentLimits limits = instrumentLimitsMap.computeIfAbsent(instrumentId,
            k -> new InstrumentLimits(DEFAULT_POSITION_LIMIT));
        double currentExposure = getInstrumentExposure(instrumentId);
        if (currentExposure + exposure > limits.getMaxExposure()) {
            return RiskDecision.REJECTED;
        }
        return RiskDecision.APPROVED;
    }

    private RiskDecision checkPositionLimit(String instrumentId, double quantity) {
        PositionState state = positionStates.get(instrumentId);
        if (state == null) {
            return RiskDecision.APPROVED;
        }
        double currentPosition = state.getNetPosition();
        if (Math.abs(currentPosition + quantity) > getMaxPositionSize(instrumentId)) {
            return RiskDecision.REJECTED;
        }
        return RiskDecision.APPROVED;
    }

    private RiskDecision checkConcentrationLimit(String traderId, String instrumentId, double exposure) {
        double traderTotalExposure = getTraderExposure(traderId);
        if (traderTotalExposure <= 0) {
            return RiskDecision.APPROVED;
        }
        double instrumentExposure = getInstrumentExposure(instrumentId);
        double concentration = (instrumentExposure + exposure) / traderTotalExposure;
        if (concentration > DEFAULT_CONCENTRATION_LIMIT) {
            logger.warn("Concentración excesiva para trader {} en instrumento {}: {}%",
                traderId, instrumentId, concentration * 100);
            return RiskDecision.WARNED;
        }
        return RiskDecision.APPROVED;
    }

    private RiskDecision checkDailyLossLimit(String traderId) {
        DailyPnL pnl = dailyPnLs.get(traderId);
        if (pnl == null) {
            return RiskDecision.APPROVED;
        }
        if (pnl.getDailyLoss() > DEFAULT_DAILY_LOSS_LIMIT) {
            logger.error("Límite de pérdida diaria alcanzado para trader {}: ${}",
                traderId, pnl.getDailyLoss());
            return RiskDecision.REJECTED;
        }
        return RiskDecision.APPROVED;
    }

    private void updatePosition(String instrumentId, double quantity, double price) {
        positionStates.compute(instrumentId, (key, existing) -> {
            if (existing == null) {
                return new PositionState(quantity, quantity * price);
            }
            existing.addQuantity(quantity);
            existing.addExposure(quantity * price);
            return existing;
        });
    }

    private double getTraderExposure(String traderId) {
        return positionStates.values().stream()
            .mapToDouble(PositionState::getTotalExposure)
            .sum();
    }

    private double getStrategyExposure(String strategyId) {
        return positionStates.values().stream()
            .mapToDouble(PositionState::getTotalExposure)
            .sum();
    }

    private double getInstrumentExposure(String instrumentId) {
        PositionState state = positionStates.get(instrumentId);
        return state != null ? state.getTotalExposure() : 0.0;
    }

    private double getMaxPositionSize(String instrumentId) {
        return 1_000_000.0;
    }

    private void logLimitViolation(String limitType, String entityId, double value, String description) {
        logger.warn("Violación de límite {} para {} ({}): valor={}, límite={}",
            limitType, entityId, description, value, "configurado");
    }

    public void reset() {
        traderLimitsMap.clear();
        strategyLimitsMap.clear();
        instrumentLimitsMap.clear();
        positionStates.clear();
        dailyPnLs.clear();
        logger.info("RiskLimitsService reseteado");
    }

    public int getPositionCount() {
        return positionStates.size();
    }

    private static class TraderLimits {
        private final double maxExposure;

        public TraderLimits(double maxExposure) {
            this.maxExposure = maxExposure;
        }

        public double getMaxExposure() {
            return maxExposure;
        }
    }

    private static class StrategyLimits {
        private final double maxExposure;

        public StrategyLimits(double maxExposure) {
            this.maxExposure = maxExposure;
        }

        public double getMaxExposure() {
            return maxExposure;
        }
    }

    private static class InstrumentLimits {
        private final double maxExposure;

        public InstrumentLimits(double maxExposure) {
            this.maxExposure = maxExposure;
        }

        public double getMaxExposure() {
            return maxExposure;
        }
    }

    private static class PositionState {
        private volatile double netPosition;
        private volatile double totalExposure;

        public PositionState(double netPosition, double totalExposure) {
            this.netPosition = netPosition;
            this.totalExposure = totalExposure;
        }

        public double getNetPosition() {
            return netPosition;
        }

        public void addQuantity(double quantity) {
            this.netPosition += quantity;
        }

        public double getTotalExposure() {
            return totalExposure;
        }

        public void addExposure(double exposure) {
            this.totalExposure += exposure;
        }
    }

    private static class DailyPnL {
        private volatile double dailyPnL = 0.0;
        private volatile double dailyLoss = 0.0;

        public double getDailyLoss() {
            return dailyLoss;
        }

        public void updatePnL(double pnl) {
            this.dailyPnL += pnl;
            if (this.dailyPnL < 0) {
                this.dailyLoss = -this.dailyPnL;
            }
        }
    }
}

// === ARCHIVO: src/main/java/com/pragma/riskengine/sharding/InstrumentShardingStrategy.java ===
package com.pragma.riskengine.sharding;

import com.pragma.riskengine.RiskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.ToIntFunction;

/**
 * Estrategia de sharding por instrumento para garantizar consistencia
 * entre múltiples risk engines. Usa hash consistente para distribuir
 * eventos del mismo instrumento al mismo shard.
 */
public class InstrumentShardingStrategy {
    private static final Logger logger = LoggerFactory.getLogger(InstrumentShardingStrategy.class);
    private static final int DEFAULT_SHARD_COUNT = 8;
    private static final int MAX_SHARD_COUNT = 64;
    private static final int MIN_SHARD_COUNT = 1;
    
    private final int shardCount;
    private final ToIntFunction<String> hashFunction;
    private final Map<String, Integer> instrumentToShardCache;
    private final List<ShardMetrics> shardMetrics;
    private final ConcurrentHashMap<String, ShardAssignment> activeAssignments;
    
    public InstrumentShardingStrategy() {
        this(DEFAULT_SHARD_COUNT, null);
    }
    
    public InstrumentShardingStrategy(int shardCount, ToIntFunction<String> customHashFunction) {
        if (shardCount < MIN_SHARD_COUNT || shardCount > MAX_SHARD_COUNT) {
            throw new IllegalArgumentException(
                "Shard count must be between " + MIN_SHARD_COUNT + " and " + MAX_SHARD_COUNT + 
                ", but was: " + shardCount);
        }
        this.shardCount = shardCount;
        this.hashFunction = customHashFunction != null ? customHashFunction : this::defaultHash;
        this.instrumentToShardCache = new ConcurrentHashMap<>(1024);
        this.shardMetrics = new CopyOnWriteArrayList<>();
        this.activeAssignments = new ConcurrentHashMap<>();
        
        for (int i = 0; i < shardCount; i++) {
            shardMetrics.add(new ShardMetrics(i));
        }
        
        logger.info("InstrumentShardingStrategy initialized with {} shards", shardCount);
    }
    
    private int defaultHash(String instrumentId) {
        if (instrumentId == null || instrumentId.isEmpty()) {
            return 0;
        }
        int hash = instrumentId.hashCode();
        hash = ((hash >>> 16) ^ hash) * 0x45d9f3b;
        hash = ((hash >>> 16) ^ hash) * 0x45d9f3b;
        hash = hash >>> 16;
        return Math.abs(hash);
    }
    
    /**
     * Obtiene el identificador del shard para un instrumento dado.
     * Usa cacheo para evitar recalcular el hash en eventos subsiguientes.
     */
    public int getShardForInstrument(String instrumentId) {
        if (instrumentId == null) {
            logger.warn("Received null instrumentId, defaulting to shard 0");
            return 0;
        }
        
        return instrumentToShardCache.computeIfAbsent(instrumentId, id -> {
            int shard = hashFunction.applyAsInt(id) % shardCount;
            if (shard < 0) {
                shard = -shard;
            }
            shardMetrics.get(shard).incrementAssignmentCount();
            logger.debug("Assigned instrument {} to shard {}", id, shard);
            return shard;
        });
    }
    
    /**
     * Obtiene el shard para un evento de riesgo basándose en su instrumento.
     */
    public int getShardForEvent(RiskEvent event) {
        if (event == null || event.getInstrumentId() == null) {
            return 0;
        }
        return getShardForInstrument(event.getInstrumentId());
    }
    
    /**
     * Registra una asignación activa de instrumento a shard para trazabilidad.
     */
    public void registerAssignment(String instrumentId, String engineId) {
        if (instrumentId == null || engineId == null) {
            return;
        }
        int shard = getShardForInstrument(instrumentId);
        ShardAssignment assignment = new ShardAssignment(instrumentId, engineId, shard, System.nanoTime());
        activeAssignments.put(instrumentId, assignment);
        logger.debug("Registered assignment: instrument={}, engine={}, shard={}", 
            instrumentId, engineId, shard);
    }
    
    /**
     * Obtiene la asignación activa para un instrumento.
     */
    public ShardAssignment getAssignment(String instrumentId) {
        return activeAssignments.get(instrumentId);
    }
    
    /**
     * Obtiene el número total de shards configurados.
     */
    public int getShardCount() {
        return shardCount;
    }
    
    /**
     * Obtiene las métricas de un shard específico.
     */
    public ShardMetrics getShardMetrics(int shardIndex) {
        if (shardIndex < 0 || shardIndex >= shardCount) {
            throw new IndexOutOfBoundsException(
                "Shard index " + shardIndex + " out of bounds for " + shardCount + " shards");
        }
        return shardMetrics.get(shardIndex);
    }
    
    /**
     * Obtiene todas las métricas de shards para monitoreo.
     */
    public List<ShardMetrics> getAllShardMetrics() {
        return List.copyOf(shardMetrics);
    }
    
    /**
     * Rebalancea los shards subyacentes. Este método permite cambiar
     * dinámicamente el número de shards cuando la carga lo requiere.
     */
    public synchronized void rebalance(int newShardCount) {
        if (newShardCount == this.shardCount) {
            logger.info("Rebalance skipped: count unchanged");
            return;
        }
        if (newShardCount < MIN_SHARD_COUNT || newShardCount > MAX_SHARD_COUNT) {
            throw new IllegalArgumentException(
                "Cannot rebalance to " + newShardCount + " shards (valid range: " + 
                MIN_SHARD_COUNT + "-" + MAX_SHARD_COUNT + ")");
        }
        
        logger.warn("Rebalancing from {} to {} shards - cache will be invalidated", 
            this.shardCount, newShardCount);
        instrumentToShardCache.clear();
    }
    
    /**
     * Metrica de un shard individual para monitoreo de carga.
     */
    public static class ShardMetrics {
        private final int shardId;
        private volatile long assignmentCount;
        private volatile long eventCount;
        private volatile long totalProcessingTimeNanos;
        private volatile long maxProcessingTimeNanos;
        
        public ShardMetrics(int shardId) {
            this.shardId = shardId;
            this.assignmentCount = 0;
            this.eventCount = 0;
            this.totalProcessingTimeNanos = 0;
            this.maxProcessingTimeNanos = 0;
        }
        
        public void incrementAssignmentCount() {
            assignmentCount++;
        }
        
        public void recordEvent(long processingTimeNanos) {
            eventCount++;
            totalProcessingTimeNanos += processingTimeNanos;
            if (processingTimeNanos > maxProcessingTimeNanos) {
                maxProcessingTimeNanos = processingTimeNanos;
            }
        }
        
        public int getShardId() { return shardId; }
        public long getAssignmentCount() { return assignmentCount; }
        public long getEventCount() { return eventCount; }
        public long getTotalProcessingTimeNanos() { return totalProcessingTimeNanos; }
        public long getMaxProcessingTimeNanos() { return maxProcessingTimeNanos; }
        
        public double getAverageProcessingTimeMicros() {
            if (eventCount == 0) return 0.0;
            return (totalProcessingTimeNanos / 1000.0) / eventCount;
        }
    }
    
    /**
     * Representa una asignación activa de instrumento a engine.
     */
    public static class ShardAssignment {
        private final String instrumentId;
        private final String engineId;
        private final int shardId;
        private final long assignedAtNanos;
        
        public ShardAssignment(String instrumentId, String engineId, int shardId, long assignedAtNanos) {
            this.instrumentId = instrumentId;
            this.engineId = engineId;
            this.shardId = shardId;
            this.assignedAtNanos = assignedAtNanos;
        }
        
        public String getInstrumentId() { return instrumentId; }
        public String getEngineId() { return engineId; }
        public int getShardId() { return shardId; }
        public long getAssignedAtNanos() { return assignedAtNanos; }
    }
}

// === ARCHIVO: src/main/java/com/pragma/riskengine/replay/DeterministicReplayEngine.java ===
package com.pragma.riskengine.replay;

import com.pragma.riskengine.RiskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Motor de replay determinístico para post-mortem de incidentes.
 * Graba eventos con timestamp nanosegundos para garantizar reproducibililidad
 * exacta del comportamiento del sistema.
 */
public class DeterministicReplayEngine {
    private static final Logger logger = LoggerFactory.getLogger(DeterministicReplayEngine.class);
    private static final int DEFAULT_MAX_EVENTS_PER_SESSION = 1_000_000;
    private static final int MAX_REPLAY_BATCH_SIZE = 1000;
    
    private final int maxEventsPerSession;
    private final Map<String, EventSession> sessions;
    private final Map<String, String> correlationIdToSession;
    private final List<ReplayListener> listeners;
    private final ReplayScheduler scheduler;
    private volatile boolean recordingEnabled;
    
    public DeterministicReplayEngine() {
        this(DEFAULT_MAX_EVENTS_PER_SESSION);
    }
    
    public DeterministicReplayEngine(int maxEventsPerSession) {
        this.maxEventsPerSession = maxEventsPerSession;
        this.sessions = new ConcurrentHashMap<>();
        this.correlationIdToSession = new ConcurrentHashMap<>();
        this.listeners = new CopyOnWriteArrayList<>();
        this.scheduler = new ReplayScheduler();
        this.recordingEnabled = true;
        
        logger.info("DeterministicReplayEngine initialized with max {} events per session", 
            maxEventsPerSession);
    }
    
    /**
     * Inicia una nueva sesión de grabación para un correlationId.
     */
    public String startRecordingSession(String correlationId) {
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        
        EventSession session = new EventSession(correlationId, maxEventsPerSession);
        sessions.put(correlationId, session);
        
        logger.info("Started recording session for correlationId: {}", correlationId);
        notifyListeners(l -> l.onSessionStarted(correlationId));
        
        return correlationId;
    }
    
    /**
     * Graba un evento en la sesión activa.
     */
    public void recordEvent(RiskEvent event) {
        if (!recordingEnabled) {
            logger.debug("Recording disabled, skipping event {}", event.getEventId());
            return;
        }
        
        if (event == null) {
            logger.warn("Received null event, skipping");
            return;
        }
        
        String correlationId = event.getCorrelationId();
        if (correlationId == null) {
            correlationId = "default";
        }
        
        EventSession session = sessions.get(correlationId);
        if (session == null) {
            logger.debug("No active session for {}, creating default session", correlationId);
            startRecordingSession(correlationId);
            session = sessions.get(correlationId);
        }
        
        boolean recorded = session.addEvent(event);
        if (recorded) {
            logger.trace("Recorded event {} to session {}", event.getEventId(), correlationId);
        } else {
            logger.warn("Failed to record event {} - session {} is full", 
                event.getEventId(), correlationId);
        }
        
        notifyListeners(l -> l.onEventRecorded(event, correlationId));
    }
    
    /**
     * Obtiene los eventos grabados para un correlationId.
     */
    public List<RiskEvent> getRecordedEvents(String correlationId) {
        EventSession session = sessions.get(correlationId);
        if (session == null) {
            logger.debug("No session found for correlationId: {}", correlationId);
            return Collections.emptyList();
        }
        return session.getEvents();
    }
    
    /**
     * Obtiene los eventos grabados ordenados por timestamp nanosegundos.
     */
    public List<RiskEvent> getRecordedEventsSorted(String correlationId) {
        List<RiskEvent> events = getRecordedEvents(correlationId);
        List<RiskEvent> sorted = new ArrayList<>(events);
        sorted.sort(Comparator.comparingLong(RiskEvent::getNanoTimestamp));
        return sorted;
    }
    
    /**
     * Inicia un replay determinístico para un correlationId dado.
     * Los eventos se reproducen en orden exacto de timestamp nanosegundos.
     */
    public void startReplay(String correlationId, Consumer<RiskEvent> callback) {
        if (correlationId == null || callback == null) {
            throw new IllegalArgumentException("correlationId and callback must not be null");
        }
        
        EventSession session = sessions.get(correlationId);
        if (session == null) {
            logger.error("Cannot replay - no session found for correlationId: {}", correlationId);
            throw new IllegalArgumentException("No session found: " + correlationId);
        }
        
        List<RiskEvent> events = getRecordedEventsSorted(correlationId);
        if (events.isEmpty()) {
            logger.warn("Replay requested but session {} is empty", correlationId);
            return;
        }
        
        logger.info("Starting deterministic replay for session {} with {} events", 
            correlationId, events.size());
        
        notifyListeners(l -> l.onReplayStarted(correlationId, events.size()));
        
        scheduler.schedule(events, callback, (completed, total) -> {
            logger.info("Replay completed: {}/{} events for {}", completed, total, correlationId);
            notifyListeners(l -> l.onReplayCompleted(correlationId, completed, total));
        });
    }
    
    /**
     * Obtiene el número de eventos grabados en una sesión.
     */
    public int getEventCount(String correlationId) {
        EventSession session = sessions.get(correlationId);
        return session != null ? session.getEventCount() : 0;
    }
    
    /**
     * Finaliza una sesión de grabación y opcionalmente la persiste.
     */
    public void endSession(String correlationId) {
        EventSession session = sessions.remove(correlationId);
        if (session != null) {
            logger.info("Ended session {} with {} events", correlationId, session.getEventCount());
            notifyListeners(l -> l.onSessionEnded(correlationId, session.getEventCount()));
        }
    }
    
    /**
     * Habilita o deshabilita la grabación de eventos.
     */
    public void setRecordingEnabled(boolean enabled) {
        this.recordingEnabled = enabled;
        logger.info("Recording enabled: {}", enabled);
    }
    
    /**
     * Agrega un listener para eventos del motor de replay.
     */
    public void addListener(ReplayListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remueve un listener registrado.
     */
    public void removeListener(ReplayListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners(Consumer<ReplayListener> action) {
        for (ReplayListener listener : listeners) {
            try {
                action.accept(listener);
            } catch (Exception e) {
                logger.error("Error notifying listener: {}", e.getMessage(), e);
            }
        }
    }
    
    /**
     * Interfaz para escuchar eventos del motor de replay.
     */
    public interface ReplayListener {
        default void onSessionStarted(String correlationId) {}
        default void onSessionEnded(String correlationId, int eventCount) {}
        default void onEventRecorded(RiskEvent event, String correlationId) {}
        default void onReplayStarted(String correlationId, int eventCount) {}
        default void onReplayCompleted(String correlationId, int completed, int total) {}
    }
    
    /**
     * Sesión de grabación de eventos para un correlationId.
     */
    private static class EventSession {
        private final String correlationId;
        private final int maxEvents;
        private final List<RiskEvent> events;
        private volatile boolean closed;
        
        public EventSession(String correlationId, int maxEvents) {
            this.correlationId = correlationId;
            this.maxEvents = maxEvents;
            this.events = new ArrayList<>(Math.min(maxEvents, 10000));
            this.closed = false;
        }
        
        public synchronized boolean addEvent(RiskEvent event) {
            if (closed || events.size() >= maxEvents) {
                return false;
            }
            events.add(event);
            return true;
        }
        
        public List<RiskEvent> getEvents() {
            return List.copyOf(events);
        }
        
        public int getEventCount() {
            return events.size();
        }
        
        public synchronized void close() {
            this.closed = true;
        }
    }
    
    /**
     * Scheduler para ejecutar replay en batches controlados.
     */
    private static class ReplayScheduler {
        public void schedule(List<RiskEvent> events, Consumer<RiskEvent> callback, 
                            java.util.function.BiConsumer<Integer, Integer> onComplete) {
            int total = events.size();
            int processed = 0;
            
            for (int i = 0; i < total; i += MAX_REPLAY_BATCH_SIZE) {
                int batchEnd = Math.min(i + MAX_REPLAY_BATCH_SIZE, total);
                
                for (int j = i; j < batchEnd; j++) {
                    try {
                        callback.accept(events.get(j));
                        processed++;
                    } catch (Exception e) {
                        logger.error("Error processing event in replay at index {}: {}", j, e.getMessage());
                    }
                }
                
                if (batchEnd < total) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            
            onComplete.accept(processed, total);
        }
    }
}

// === ARCHIVO: src/test/java/com/pragma/riskengine/marketdata/MarketDataEventHandlerTest.java ===
package com.pragma.riskengine.marketdata;

import com.pragma.riskengine.RiskEvent;
import com.pragma.riskengine.RiskEvent.EventType;
import com.pragma.riskengine.RiskEvent.RiskDecision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MarketDataEventHandlerTest {

    @Mock
    private MarketDataEventHandler.PriceUpdateListener priceListener;

    @Mock
    private MarketDataEventHandler.VolatilityUpdateListener volatilityListener;

    private MarketDataEventHandler handler;
    private RiskEvent testEvent;
    private AtomicLong processedCount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        processedCount = new AtomicLong(0);
        handler = new MarketDataEventHandler(priceListener, volatilityListener, processedCount);
        
        testEvent = new RiskEvent();
        testEvent.setEventId("evt-001");
        testEvent.setInstrumentId("AAPL");
        testEvent.setTraderId("trader-123");
        testEvent.setStrategyId("strategy-momentum");
        testEvent.setEventType(EventType.TRADE);
        testEvent.setPrice(150.25);
        testEvent.setQuantity(1000);
        testEvent.setVolatility(0.25);
        testEvent.setExposure(150250.0);
        testEvent.setTimestamp(Instant.now());
        testEvent.setNanoTimestamp(System.nanoTime());
        testEvent.setCorrelationId("corr-001");
    }

    @Test
    @DisplayName("Debe procesar eventos de trade correctamente y notificar listeners")
    void onEvent_debeProcesarTradeYNotificarListeners() {
        testEvent.setEventType(EventType.TRADE);
        
        handler.onEvent(testEvent, 0, false);
        
        verify(priceListener, times(1)).onPriceUpdate(
            eq("AAPL"), eq(150.25), any(Instant.class));
        verify(volatilityListener, times(1)).onVolatilityUpdate(
            eq("AAPL"), eq(0.25), any(Instant.class));
        assertEquals(1, processedCount.get());
    }

    @Test
    @DisplayName("Debe procesar eventos de orden y calcular exposición correctamente")
    void onEvent_debeProcesarOrdenYCalcularExposicion() {
        testEvent.setEventType(EventType.ORDER);
        testEvent.setPrice(150.50);
        testEvent.setQuantity(500);
        
        handler.onEvent(testEvent, 0, false);
        
        ArgumentCaptor<Double> exposureCaptor = ArgumentCaptor.forClass(Double.class);
        verify(priceListener).onPriceUpdate(eq("AAPL"), eq(150.50), any(Instant.class));
        assertEquals(75250.0, testEvent.getExposure(), 0.01);
    }

    @Test
    @DisplayName("Debe rechazar eventos con precio negativo")
    void onEvent_debeRechazarPrecioNegativo() {
        testEvent.setPrice(-10.0);
        
        assertThrows(IllegalArgumentException.class, () -> {
            handler.onEvent(testEvent, 0, false);
        });
    }

    @Test
    @DisplayName("Debe rechazar eventos con volatilidad negativa")
    void onEvent_debeRechazarVolatilidadNegativa() {
        testEvent.setVolatility(-0.5);
        
        assertThrows(IllegalArgumentException.class, () -> {
            handler.onEvent(testEvent, 0, false);
        });
    }

    @Test
    @DisplayName("Debe cumplir con latencia máxima de 500 microsegundos p99")
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    void onEvent_debeCumplirLatenciaMaxima() throws InterruptedException {
        int iterations = 10000;
        long[] latencies = new long[iterations];
        
        for (int i = 0; i < iterations; i++) {
            RiskEvent event = new RiskEvent();
            event.setEventId("evt-" + i);
            event.setInstrumentId("AAPL");
            event.setEventType(EventType.TRADE);
            event.setPrice(150.0 + (i % 100) * 0.01);
            event.setQuantity(100);
            event.setVolatility(0.20);
            event.setTimestamp(Instant.now());
            event.setNanoTimestamp(System.nanoTime());
            
            long start = System.nanoTime();
            handler.onEvent(event, i, false);
            long end = System.nanoTime();
            
            latencies[i] = end - start;
        }
        
        long p99Latency = calculateP99(latencies);
        assertTrue(p99Latency <= 500_000, 
            "Latencia p99 debe ser <= 500 microsegundos, pero fue: " + (p99Latency / 1000) + " microsegundos");
    }

    @Test
    @DisplayName("Debe procesar throughput mínimo de 10000 eventos por segundo")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void onEvent_debeCumplirThroughputMinimo() {
        int targetEvents = 10000;
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < targetEvents; i++) {
            RiskEvent event = new RiskEvent();
            event.setEventId("evt-throughput-" + i);
            event.setInstrumentId("AAPL");
            event.setEventType(EventType.TRADE);
            event.setPrice(150.0);
            event.setQuantity(100);
            event.setVolatility(0.20);
            event.setTimestamp(Instant.now());
            event.setNanoTimestamp(System.nanoTime());
            
            handler.onEvent(event, i, false);
        }
        
        long elapsedMs = System.currentTimeMillis() - startTime;
        double eventsPerSecond = (targetEvents * 1000.0) / elapsedMs;
        
        assertTrue(eventsPerSecond >= 10000, 
            "Throughput debe ser >= 10000 eventos/segundo, pero fue: " + eventsPerSecond);
    }

    @Test
    @DisplayName("Debe manejar eventos de tipo ORDER_BOOK_UPDATE correctamente")
    void onEvent_debeManejarOrderBookUpdate() {
        testEvent.setEventType(EventType.ORDER_BOOK_UPDATE);
        testEvent.setPrice(150.30);
        
        handler.onEvent(testEvent, 0, false);
        
        verify(priceListener, times(1)).onPriceUpdate(
            eq("AAPL"), eq(150.30), any(Instant.class));
        verify(volatilityListener, never()).onVolatilityUpdate(any(), any(), any());
    }

    @Test
    @DisplayName("Debe actualizar precios con ordenamiento lexicográfico de instrumentos")
    void onEvent_debeActualizarPreciosOrdenados() {
        String[] instruments = {"AAPL", "GOOGL", "MSFT", "TSLA", "AMZN"};
        double[] prices = {150.0, 2800.0, 300.0, 700.0, 3300.0};
        
        for (int i = 0; i < instruments.length; i++) {
            RiskEvent event = new RiskEvent();
            event.setEventId("evt-sorted-" + i);
            event.setInstrumentId(instruments[i]);
            event.setEventType(EventType.TRADE);
            event.setPrice(prices[i]);
            event.setQuantity(100);
            event.setVolatility(0.20);
            event.setTimestamp(Instant.now());
            event.setNanoTimestamp(System.nanoTime());
            
            handler.onEvent(event, i, false);
        }
        
        for (int i = 0; i < instruments.length; i++) {
            verify(priceListener).onPriceUpdate(eq(instruments[i]), eq(prices[i]), any(Instant.class));
        }
    }

    @Test
    @DisplayName("Debe copiar evento cuando el flag endOfBatch es falso en procesamiento continuo")
    void onEvent_debeCopiarEventoEnModoBatch() {
        for (int i = 0; i < 100; i++) {
            RiskEvent event = new RiskEvent();
            event.setEventId("evt-batch-" + i);
            event.setInstrumentId("AAPL");
            event.setEventType(EventType.TRADE);
            event.setPrice(150.0 + i);
            event.setQuantity(100);
            event.setVolatility(0.20);
            event.setTimestamp(Instant.now());
            event.setNanoTimestamp(System.nanoTime());
            
            handler.onEvent(event, i, false);
        }
        
        assertEquals(100, processedCount.get());
    }

    private long calculateP99(long[] values) {
        if (values.length == 0) return 0;
        long[] sorted = values.clone();
        java.util.Arrays.sort(sorted);
        int p99Index = (int) Math.ceil(values.length * 0.99) - 1;
        return sorted[p99Index];
    }
}

// === ARCHIVO: src/test/java/com/pragma/riskengine/var/VaRIntradayCalculatorTest.java ===
package com.pragma.riskengine.var;

import com.pragma.riskengine.RiskEvent;
import com.pragma.riskengine.RiskEvent.EventType;
import com.pragma.riskengine.RiskEvent.RiskDecision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.*;
nimport java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VaRIntradayCalculatorTest {

    @Mock
    private VaRIntradayCalculator.VolatilityProvider volatilityProvider;

    @Mock
    private VaRIntradayCalculator.CorrelationMatrix correlationMatrix;

    private VaRIntradayCalculator calculator;
    private Map<String, Double> portfolioPositions;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        portfolioPositions = new ConcurrentHashMap<>();
        
        when(volatilityProvider.getVolatility("AAPL")).thenReturn(0.25);
        when(volatilityProvider.getVolatility("GOOGL")).thenReturn(0.30);
        when(volatilityProvider.getVolatility("MSFT")).thenReturn(0.20);
        when(volatilityProvider.getVolatility("TSLA")).thenReturn(0.50);
        
        calculator = new VaRIntradayCalculator(volatilityProvider, correlationMatrix, portfolioPositions);
    }

    @Test
    @DisplayName("Debe calcular VaR correctamente para una posición larga simple")
    void calculateVar_debeCalcularPosicionLargaSimple() {
        portfolioPositions.put("AAPL", 100000.0);
        
        double var = calculator.calculateVar("AAPL", 150.0, 1000);
        
        double expectedExposure = 150.0 * 1000;
        double expectedVar = expectedExposure * 0.25 * 1.65;
        assertEquals(expectedVar, var, expectedVar * 0.01);
    }

    @Test
    @DisplayName("Debe calcular VaR correctamente para posición corta")
    void calculateVar_debeCalcularPosicionCorta() {
        portfolioPositions.put("AAPL", -100000.0);
        
        double var = calculator.calculateVar("AAPL", 150.0, -1000);
        
        double expectedExposure = 150.0 * Math.abs(-1000);
        double expectedVar = expectedExposure * 0.25 * 1.65;
        assertEquals(expectedVar, var, expectedVar * 0.01);
    }

    @Test
    @DisplayName("Debe usar matriz de correlación para cartera multi-activo")
    void calculateVar_debeUsarMatrizCorrelacion() {
        portfolioPositions.put("AAPL", 50000.0);
        portfolioPositions.put("GOOGL", 50000.0);
        
        when(correlationMatrix.getCorrelation("AAPL", "GOOGL")).thenReturn(0.7);
        
        double var = calculator.calculatePortfolioVaR(Arrays.asList("AAPL", "GOOGL"));
        
        assertTrue(var > 0, "VaR de cartera debe ser positivo");
        assertTrue(var < calculateSimpleSumVar(), 
            "VaR con correlación debe ser menor que la suma simple (beneficio de diversificación)");
    }

    @Test
    @DisplayName("Debe actualizar VaR en tiempo real tras cada operación")
    void calculateVar_debeActualizarEnTiempoReal() {
        calculator.updatePosition("AAPL", 1000, 150.0);
        double var1 = calculator.getCurrentVar("AAPL");
        
        calculator.updatePosition("AAPL", 500, 151.0);
        double var2 = calculator.getCurrentVar("AAPL");
        
        assertTrue(var2 > var1, "VaR debe aumentar con más exposición");
    }

    @Test
    @DisplayName("Debe recalcular automáticamente cuando cambia la volatilidad")
    void calculateVar_debeRecalcularConVolatilidadCambiada() {
        calculator.updatePosition("AAPL", 1000, 150.0);
        double var1 = calculator.getCurrentVar("AAPL");
        
        when(volatilityProvider.getVolatility("AAPL")).thenReturn(0.40);
        calculator.onVolatilityUpdate("AAPL", 0.40);
        
        double var2 = calculator.getCurrentVar("AAPL");
        
        assertEquals(var2, var1 * 1.6, var1 * 0.01);
    }

    @Test
    @DisplayName("Debe calcular VaR con nivel de confianza del 99%")
    void calculateVar_debeUsarNivelConfianza99() {
        portfolioPositions.put("TSLA", 100000.0);
        
        double var = calculator.calculateVar("TSLA", 700.0, 1000);
        
        double expectedExposure = 700.0 * 1000;
        double expectedVar = expectedExposure * 0.50 * 2.33;
        assertEquals(expectedVar, var, expectedVar * 0.01);
    }

    @Test
    @DisplayName("Debe manejar correctamente instrumentos con correlación perfecta positiva")
    void calculateVar_debeManejarCorrelacionPerfectaPositiva() {
        portfolioPositions.put("AAPL", 50000.0);
        portfolioPositions.put("GOOGL", 50000.0);
        
        when(correlationMatrix.getCorrelation("AAPL", "GOOGL")).thenReturn(1.0);
        
        double var = calculator.calculatePortfolioVaR(Arrays.asList("AAPL", "GOOGL"));
        double simpleSum = calculateSimpleSumVar();
        
        assertEquals(simpleSum, var, simpleSum * 0.001);
    }

    @Test
    @DisplayName("Debe manejar correctamente instrumentos con correlación perfecta negativa")
    void calculateVar_debeManejarCorrelacionPerfectaNegativa() {
        portfolioPositions.put("AAPL", 50000.0);
        portfolioPositions.put("GOOGL", 50000.0);
        
        when(correlationMatrix.getCorrelation("AAPL", "GOOGL")).thenReturn(-1.0);
        
        double var = calculator.calculatePortfolioVaR(Arrays.asList("AAPL", "GOOGL"));
        
        assertEquals(0.0, var, 0.01);
    }

    @Test
    @DisplayName("Debe cumplir latencia de cálculo menor a 100 microsegundos")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void calculateVar_debeCumplirLatenciaMaxima() {
        int iterations = 50000;
        String[] instruments = {"AAPL", "GOOGL", "MSFT", "TSLA"};
        
        for (String instrument : instruments) {
            calculator.updatePosition(instrument, 1000, 150.0);
        }
        
        long[] latencies = new long[iterations];
        
        for (int i = 0; i < iterations; i++) {
            String instrument = instruments[i % instruments.length];
            
            long start = System.nanoTime();
            calculator.calculateVar(instrument, 150.0, 1000);
            long end = System.nanoTime();
            
            latencies[i] = end - start;
        }
        
        long p99Latency = calculateP99(latencies);
        assertTrue(p99Latency <= 100_000, 
            "Latencia p99 debe ser <= 100 microsegundos, pero fue: " + (p99Latency / 1000));
    }

    @Test
    @DisplayName("Debe aplicar correctamente el horizonte temporal para VaR intraday")
    void calculateVar_debeAplicarHorizonteIntraday() {
        calculator.setTimeHorizonMinutes(15);
        portfolioPositions.put("AAPL", 100000.0);
        
        double varIntraday = calculator.calculateVar("AAPL", 150.0, 1000);
        
        double scalingFactor = Math.sqrt(15.0 / (6.5 * 60 * 60));
        double varBase = 150.0 * 1000 * 0.25 * 1.65;
        double expectedVar = varBase * scalingFactor;
        
        assertEquals(expectedVar, varIntraday, expectedVar * 0.05);
    }

    @Test
    @DisplayName("Debe calcular exposición total de la cartera correctamente")
    void calculateVar_debeCalcularExposicionTotalCartera() {
        Map<String, Double> positions = new HashMap<>();
        positions.put("AAPL", 150000.0);
        positions.put("GOOGL", 280000.0);
        positions.put("MSFT", 90000.0);
        
        double totalExposure = calculator.calculateTotalExposure(positions);
        
        assertEquals(520000.0, totalExposure, 0.01);
    }

    @Test
    @DisplayName("Debe rechazar volatilidad negativa del provider")
    void calculateVar_debeRechazarVolatilidadNegativa() {
        when(volatilityProvider.getVolatility("AAPL")).thenReturn(-0.1);
        
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateVar("AAPL", 150.0, 1000);
        });
    }

    private double calculateSimpleSumVar() {
        double varAAPL = 75000.0 * 0.25 * 1.65;
        double varGOOGL = 1400000.0 * 0.30 * 1.65;
        return varAAPL + varGOOGL;
    }

    private long calculateP99(long[] values) {
        if (values.length == 0) return 0;
        long[] sorted = values.clone();
        Arrays.sort(sorted);
        int p99Index = (int) Math.ceil(values.length * 0.99) - 1;
        return sorted[p99Index];
    }
}

// === ARCHIVO: src/test/java/com/pragma/riskengine/circuitbreaker/DynamicCircuitBreakerTest.java ===
package com.pragma.riskengine.circuitbreaker;

import com.pragma.riskengine.RiskEvent;
import com.pragma.riskengine.RiskEvent.EventType;
import com.pragma.riskengine.RiskEvent.RiskDecision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DynamicCircuitBreakerTest {

    @Mock
    private DynamicCircuitBreaker.FallbackHandler fallbackHandler;

    @Mock
    private DynamicCircuitBreaker.MetricsPublisher metricsPublisher;

    private DynamicCircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        circuitBreaker = new DynamicCircuitBreaker(
            fallbackHandler,
            metricsPublisher,
            10,
            0.5,
            Duration.ofSeconds(30),
            Duration.ofSeconds(10)
        );
    }

    @Test
    @DisplayName("Debe iniciar en estado CERRADO inicialmente")
    void initialState_debeSerCerrado() {
        assertEquals(DynamicCircuitBreaker.State.CLOSED, circuitBreaker.getState());
        assertTrue(circuitBreaker.isAvailable());
    }

    @Test
    @DisplayName("Debe abrir el circuit breaker cuando la tasa de error supera el threshold")
    void onFailure_debeAbrirCuandoSuperaThreshold() {
        for (int i = 0; i < 15; i++) {
            circuitBreaker.onFailure("AAPL", new RuntimeException("Error " + i));
        }
        
        assertEquals(DynamicCircuitBreaker.State.OPEN, circuitBreaker.getState("AAPL"));
        assertFalse(circuitBreaker.isAvailable("AAPL"));
    }

    @Test
    @DisplayName("Debe ejecutar fallback cuando el circuit breaker está abierto")
    void execute_debeEjecutarFallbackCuandoAbierto() {
        for (int i = 0; i < 15; i++) {
            circuitBreaker.onFailure("AAPL", new RuntimeException("Error"));
        }
        
        when(fallbackHandler.handleFallback(eq("AAPL"), any()))
            .thenReturn(new RiskEvent());
        
        Supplier<RiskEvent> operation = () -> {
            RiskEvent e = new RiskEvent();
            e.setEventId("fallback-result");
            return e;
        };
        
        RiskEvent result = circuitBreaker.execute("AAPL", operation);
        
        verify(fallbackHandler, times(1)).handleFallback(eq("AAPL"), any());
        assertNotNull(result);
    }

    @Test
    @DisplayName("Debe cerrar el circuit breaker después del timeout de recuperación")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void onSuccess_debeCerrarDespuesTimeoutRecuperacion() throws InterruptedException {
        DynamicCircuitBreaker fastCircuitBreaker = new DynamicCircuitBreaker(
            fallbackHandler,
            metricsPublisher,
            5,
            0.5,
            Duration.ofMillis(100),
            Duration.ofMillis(50)
        );
        
        for (int i = 0; i < 10; i++) {
            fastCircuitBreaker.onFailure("TSLA", new RuntimeException("Error"));
        }
        
        assertEquals(DynamicCircuitBreaker.State.OPEN, fastCircuitBreaker.getState("TSLA"));
        
        Thread.sleep(150);
        
        assertEquals(DynamicCircuitBreaker.State.HALF_OPEN, fastCircuitBreaker.getState("TSLA"));
    }

    @Test
    @DisplayName("Debe calcular thresholds dinámicamente basados en volatilidad del instrumento")
    void calculateThreshold_debeCalcularBasadoEnVolatilidad() {
        double thresholdLowVol = circuitBreaker.calculateThreshold("AAPL", 0.15);
        double thresholdHighVol = circuitBreaker.calculateThreshold("TSLA", 0.60);
        
        assertTrue(thresholdLowVol < thresholdHighVol,
            "Instrumentos con menor volatilidad deben tener thresholds más estrictos");
    }

    @Test
    @DisplayName("Debe ajustar automáticamente el threshold cuando la volatilidad cambia significativamente")
    void updateThreshold_debeAjustarPorCambioVolatilidad() {
        circuitBreaker.setInstrumentVolatility("AAPL", 0.20);
        double threshold1 = circuitBreaker.calculateThreshold("AAPL", 0.20);
        
        circuitBreaker.setInstrumentVolatility("AAPL", 0.50);
        double threshold2 = circuitBreaker.calculateThreshold("AAPL", 0.50);
        
        assertTrue(threshold2 > threshold1);
    }

    @Test
    @DisplayName("Debe registrar métricas de cada operación")
    void execute_debeRegistrarMetricas() {
        when(fallbackHandler.handleFallback(any(), any())).thenReturn(new RiskEvent());
        
        for (int i = 0; i < 5; i++) {
            circuitBreaker.onFailure("GOOGL", new RuntimeException("Error"));
        }
        
        Supplier<RiskEvent> operation = () -> new RiskEvent();
        circuitBreaker.execute("GOOGL", operation);
        
        verify(metricsPublisher, atLeastOnce()).publish(
            eq("GOOGL"),
            any(DynamicCircuitBreaker.Metrics.class));
    }

    @Test
    @DisplayName("Debe manejar múltiples instrumentos de forma independiente")
    void state_debeManejarMultiplesInstrumentosIndependientemente() {
        for (int i = 0; i < 12; i++) {
            circuitBreaker.onFailure("AAPL", new RuntimeException("Error"));
        }
        
        for (int i = 0; i < 3; i++) {
            circuitBreaker.onFailure("MSFT", new RuntimeException("Error"));
        }
        
        assertEquals(DynamicCircuitBreaker.State.OPEN, circuitBreaker.getState("AAPL"));
        assertEquals(DynamicCircuitBreaker.State.CLOSED, circuitBreaker.getState("MSFT"));
    }

    @Test
    @DisplayName("Debe ejecutar operación exitosamente en estado CERRADO")
    void execute_debeEjecutarEnEstadoCerrado() {
        Supplier<RiskEvent> operation = () -> {
            RiskEvent e = new RiskEvent();
            e.setEventId("success-operation");
            e.setDecision(RiskDecision.APPROVED);
            return e;
        };
        
        RiskEvent result = circuitBreaker.execute("AAPL", operation);
        
        assertNotNull(result);
        assertEquals("success-operation", result.getEventId());
        verify(fallbackHandler, never()).handleFallback(any(), any());
    }

    @Test
    @DisplayName("Debe rechazar operaciones cuando está en estado FORCED_OPEN")
    void execute_debeRechazarEnForcedOpen() {
        circuitBreaker.forceOpen("TSLA");
        
        Supplier<RiskEvent> operation = () -> new RiskEvent();
        
        assertThrows(DynamicCircuitBreaker.CircuitBreakerOpenException.class, () -> {
            circuitBreaker.execute("TSLA", operation);
        });
    }

    @Test
    @DisplayName("Debe permitir transición manual a estado FORCED_OPEN para kill switch")
    void forceOpen_debePermitirTransicionManual() {
        circuitBreaker.forceOpen("AAPL");
        
        assertEquals(DynamicCircuitBreaker.State.FORCED_OPEN, circuitBreaker.getState("AAPL"));
        assertFalse(circuitBreaker.isAvailable("AAPL"));
    }

    @Test
    @DisplayName("Debe resetear el circuit breaker correctamente")
    void reset_debeLimpiarEstado() {
        for (int i = 0; i < 15; i++) {
            circuitBreaker.onFailure("AAPL", new RuntimeException("Error"));
        }
        
        circuitBreaker.reset("AAPL");
        
        assertEquals(DynamicCircuitBreaker.State.CLOSED, circuitBreaker.getState("AAPL"));
        assertTrue(circuitBreaker.isAvailable("AAPL"));
    }

    @Test
    @DisplayName("Debe calcular métricas de salud del circuit breaker")
    void metrics_debeCalcularSalud() {
        for (int i = 0; i < 10; i++) {
            circuitBreaker.onSuccess("AAPL");
        }
        for (int i = 0; i < 2; i++) {
            circuitBreaker.onFailure("AAPL", new RuntimeException("Error"));
        }
        
        DynamicCircuitBreaker.Health health = circuitBreaker.getHealth("AAPL");
        
        assertNotNull(health);
        assertEquals(10, health.getSuccessCount());
        assertEquals(2, health.getFailureCount());
        assertTrue(health.getFailureRate() > 0 && health.getFailureRate() < 1);
    }

    @Test
    @DisplayName("Debe cumplir latencia de decisión menor a 50 microsegundos")
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    void execute_debeCumplirLatenciaMaxima() {
        int iterations = 100000;
        String[] instruments = {"AAPL", "GOOGL", "MSFT", "TSLA"};
        long[] latencies = new long[iterations];
        
        for (int i = 0; i < iterations; i++) {
            String instrument = instruments[i % instruments.length];
            
            Supplier<RiskEvent> operation = () -> {
                RiskEvent e = new RiskEvent();
                e.setEventId("latency-test-" + i);
                return e;
            };
            
            long start = System.nanoTime();
            circuitBreaker.execute(instrument, operation);
            long end = System.nanoTime();
            
            latencies[i] = end - start;
        }
        
        long p99Latency = calculateP99(latencies);
        assertTrue(p99Latency <= 50_000, 
            "Latencia p99 debe ser <= 50 microsegundos, pero fue: " + (p99Latency / 1000));
    }

    private long calculateP99(long[] values) {
        if (values.length == 0) return 0;
        long[] sorted = values.clone();
        java.util.Arrays.sort(sorted);
        int p99Index = (int) Math.ceil(values.length * 0.99) - 1;
        return sorted[p99Index];
    }
}
```
