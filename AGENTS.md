# AGENTS.md

Instrucciones para el agente de IA que abra este repositorio (Claude Code, Cursor, Codex, Copilot, Gemini). Se cargan solas: no hay que pegar nada en ningun chat.

## Que es este repositorio

Es el codigo base de un reto de aprendizaje de Pragma: **Motor de Risk Scoring en Tiempo Real con Circuit Breakers Dinámicos**.

| | |
|---|---|
| Tema | TEST-CT |
| Nivel | master-l2 |
| Chapter | Generico |
| Especialidad | Inferido del contexto |
| Stack | Java 21 / LMAX Disruptor 4.0 |
| Patron arquitectonico | reactivo lock-free con sharding por instrumento |
| Tiempo estimado | 4 semanas |

## Tu tarea

Dejar este proyecto en estado **verificable**: que el comando de verificacion corra sin errores. Escribi los archivos en disco, en este repositorio. No generes ZIPs ni archivos adjuntos.

En orden:

1. Corre `el comando de build o arranque canonico del stack elegido` y mira que falla.
2. Completa lo que falte de la lista de abajo: manifiesto de dependencias, punto de entrada, capa de interfaz y las capas del patron declarado.
3. Arregla SOLO los errores que impiden compilar o arrancar.
4. Volve a correr `el comando de build o arranque canonico del stack elegido` hasta que pase.
5. Pará ahí.

## Regla dura: las fases son trabajo del humano

**PROHIBIDO implementar los entregables de las fases.** El valor del reto esta en que la persona los resuelva. Tu trabajo es que tenga un proyecto que arranca; el hueco pedagogico se queda como esta.

No resuelvas nada de esto:

- **Fase 1 — Consumo y Procesamiento de Market Data**: Sistema que consume y procesa el feed de market data en tiempo real.
- **Fase 2 — Modelo de VaR Intraday y Límites de Riesgo**: Sistema que implementa el modelo de VaR intraday y aplica límites de riesgo en tiempo real.
- **Fase 3 — Circuit Breakers Dinámicos y Kill Switch**: Sistema que implementa circuit breakers dinámicos y la política de kill switch en tiempo real.
- **Fase 4 — Compliance con Regulaciones MiFID II**: Sistema que implementa la trazabilidad de decisiones de riesgo de acuerdo con las regulaciones MiFID II.

Distincion operativa:

- **Arreglar** (si): import faltante, tipo que no existe, dependencia sin declarar, error de sintaxis, archivo referenciado que no existe.
- **No tocar** (no): logica de negocio incompleta, validaciones ausentes, secretos hardcodeados, APIs deprecadas que funcionan, concurrencia insegura, patrones mejorables. Eso es lo que la persona tiene que encontrar.

## Lo que falta y tenes que completar

### 1. Archivos que la arquitectura declara (3 de 13)

La propuesta arquitectonica del reto los lista y no llegaron al repo. Crealos con implementacion real, respetando la capa en la que viven:

- [ ] `src/main/java/com/pragma/riskengine/circuitbreaker/DynamicCircuitBreaker.java`
- [ ] `src/main/java/com/pragma/riskengine/killswitch/KillSwitchPolicy.java`
- [ ] `src/main/java/com/pragma/riskengine/compliance/MiFIDIITracer.java`

### 2. Referencias colgando (23)

Salieron de un analisis estatico del codigo que SI esta en el repo. Cada una rompe la compilacion:

- [ ] `src/main/java/com/pragma/riskengine/Main.java` — `com.pragma.riskengine.circuitbreaker.DynamicCircuitBreaker`
      El import com.pragma.riskengine.circuitbreaker.DynamicCircuitBreaker usa un paquete propio del proyecto pero ningun archivo generado declara ese tipo. Falta generar la clase/interfaz, o el import esta mal escrito.
- [ ] `src/main/java/com/pragma/riskengine/Main.java` — `com.pragma.riskengine.killswitch.KillSwitchPolicy`
      El import com.pragma.riskengine.killswitch.KillSwitchPolicy usa un paquete propio del proyecto pero ningun archivo generado declara ese tipo. Falta generar la clase/interfaz, o el import esta mal escrito.
- [ ] `src/main/java/com/pragma/riskengine/Main.java` — `com.pragma.riskengine.compliance.MiFIDIITracer`
      El import com.pragma.riskengine.compliance.MiFIDIITracer usa un paquete propio del proyecto pero ningun archivo generado declara ese tipo. Falta generar la clase/interfaz, o el import esta mal escrito.
- [ ] `src/main/java/com/pragma/riskengine/var/VaRIntradayCalculator.java` — `RiskEvent`
      El import com.pragma.riskengine.RiskEvent no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- [ ] `src/test/java/com/pragma/riskengine/var/VaRIntradayCalculatorTest.java` — `RiskEvent`
      El import com.pragma.riskengine.RiskEvent no se usa en ningun lado del cuerpo del archivo. Se puede eliminar.
- [ ] `src/main/java/com/pragma/riskengine/var/VaRIntradayCalculator.java` — `InstrumentVaRState.getLock`
      Se invoca `getLock` sobre `InstrumentVaRState`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/riskengine/var/VaRIntradayCalculator.java` — `InstrumentVaRState.updateVaR`
      Se invoca `updateVaR` sobre `InstrumentVaRState`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/riskengine/var/VaRIntradayCalculator.java` — `InstrumentVaRState.setLastCalculationTime`
      Se invoca `setLastCalculationTime` sobre `InstrumentVaRState`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/riskengine/var/VaRIntradayCalculator.java` — `InstrumentVaRState.incrementCalculationCount`
      Se invoca `incrementCalculationCount` sobre `InstrumentVaRState`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/riskengine/limits/RiskLimitsService.java` — `InstrumentLimits.getMaxExposure`
      Se invoca `getMaxExposure` sobre `InstrumentLimits`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/riskengine/limits/RiskLimitsService.java` — `PositionState.getNetPosition`
      Se invoca `getNetPosition` sobre `PositionState`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/riskengine/limits/RiskLimitsService.java` — `DailyPnL.getDailyLoss`
      Se invoca `getDailyLoss` sobre `DailyPnL`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/riskengine/limits/RiskLimitsService.java` — `PositionState.getTotalExposure`
      Se invoca `getTotalExposure` sobre `PositionState`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/riskengine/replay/DeterministicReplayEngine.java` — `EventSession.addEvent`
      Se invoca `addEvent` sobre `EventSession`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/riskengine/replay/DeterministicReplayEngine.java` — `EventSession.getEvents`
      Se invoca `getEvents` sobre `EventSession`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/main/java/com/pragma/riskengine/replay/DeterministicReplayEngine.java` — `ReplayScheduler.schedule`
      Se invoca `schedule` sobre `ReplayScheduler`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/riskengine/var/VaRIntradayCalculatorTest.java` — `VaRIntradayCalculator.calculateVar`
      Se invoca `calculateVar` sobre `VaRIntradayCalculator`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/riskengine/var/VaRIntradayCalculatorTest.java` — `VaRIntradayCalculator.calculatePortfolioVaR`
      Se invoca `calculatePortfolioVaR` sobre `VaRIntradayCalculator`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/riskengine/var/VaRIntradayCalculatorTest.java` — `VaRIntradayCalculator.updatePosition`
      Se invoca `updatePosition` sobre `VaRIntradayCalculator`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/riskengine/var/VaRIntradayCalculatorTest.java` — `VaRIntradayCalculator.getCurrentVar`
      Se invoca `getCurrentVar` sobre `VaRIntradayCalculator`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/riskengine/var/VaRIntradayCalculatorTest.java` — `VaRIntradayCalculator.onVolatilityUpdate`
      Se invoca `onVolatilityUpdate` sobre `VaRIntradayCalculator`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/riskengine/var/VaRIntradayCalculatorTest.java` — `VaRIntradayCalculator.setTimeHorizonMinutes`
      Se invoca `setTimeHorizonMinutes` sobre `VaRIntradayCalculator`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.
- [ ] `src/test/java/com/pragma/riskengine/var/VaRIntradayCalculatorTest.java` — `VaRIntradayCalculator.calculateTotalExposure`
      Se invoca `calculateTotalExposure` sobre `VaRIntradayCalculator`, pero esa clase no declara ese metodo. Agregalo con su implementacion real, o usa uno de los que si declara.

### Presentes (11)

- `pom.xml`
- `src/main/java/com/pragma/riskengine/Main.java`
- `src/main/java/com/pragma/riskengine/RiskEvent.java`
- `src/main/java/com/pragma/riskengine/marketdata/MarketDataEventHandler.java`
- `src/main/java/com/pragma/riskengine/var/VaRIntradayCalculator.java`
- `src/main/java/com/pragma/riskengine/limits/RiskLimitsService.java`
- `src/main/java/com/pragma/riskengine/sharding/InstrumentShardingStrategy.java`
- `src/main/java/com/pragma/riskengine/replay/DeterministicReplayEngine.java`
- `src/test/java/com/pragma/riskengine/marketdata/MarketDataEventHandlerTest.java`
- `src/test/java/com/pragma/riskengine/var/VaRIntradayCalculatorTest.java`
- `src/test/java/com/pragma/riskengine/circuitbreaker/DynamicCircuitBreakerTest.java`

### Capas del patron declarado

Cada una tiene que existir como directorio real con al menos un archivo. Codigo plano en la raiz no satisface el patron.

- `src/main/java/com/pragma/riskengine`
- `src/main/java/com/pragma/riskengine/marketdata`
- `src/main/java/com/pragma/riskengine/var`
- `src/main/java/com/pragma/riskengine/limits`
- `src/main/java/com/pragma/riskengine/circuitbreaker`
- `src/main/java/com/pragma/riskengine/killswitch`
- `src/main/java/com/pragma/riskengine/compliance`
- `src/main/java/com/pragma/riskengine/sharding`
- `src/main/java/com/pragma/riskengine/replay`
- `src/test/java/com/pragma/riskengine`

## Verificacion

```bash
el comando de build o arranque canonico del stack elegido
```

Ese comando pasando es la definicion de "terminado" para vos.

## Convenciones que tenes que respetar

- Un solo ecosistema: no declares librerias de otro lenguaje ni mezcles gestores de paquetes.
- Toda libreria que uses tiene que estar declarada en el manifiesto de dependencias.
- Todo import declarado tiene que usarse; todo tipo usado tiene que existir o venir de una dependencia declarada.
- El patron es **reactivo lock-free con sharding por instrumento**: los contratos (interfaces, puertos) los define la capa interna y los implementa la externa, nunca al revés.
- Los archivos que crees llevan implementacion real, no stubs: sin `TODO`, sin cuerpos vacios, sin `// getters y setters`.

## Contexto del candidato

Sirve para calibrar el nivel del codigo, no para resolver las fases.

- Brecha que el reto ataca: Sistema master-l2 que evalúa el riesgo de cada orden antes de enviarla al exchange, en menos de 500 microsegundos p99. Consume feed de market data (nivel 2 orderbook + trades), mantiene un modelo de VaR intraday, aplica límites por trader/estrategia/instrumento en tiempo real, y dispara circuit breakers dinámicos que bloquean nuevas órdenes cuando la exposición supera thresholds calibrados por volatilidad. Debe justificar el uso de estructuras lock-free vs mutex, la elección entre C++ vs Rust vs Java LMAX Disruptor, cómo garantiza la consistencia entre múltiples risk engines corriendo en paralelo (consensus vs sharding por instrument), la política de kill switch cuando detecta un algoritmo que se comporta de forma anómala, y la estrategia de replay determinístico para post-mortem de incidents. Incluye compliance con regulaciones MiFID II para trazabilidad de decisiones de riesgo.

---

*Generado por Challenge Generator — Pragma. `README.md` tiene el enunciado completo del reto para la persona. `PROMPT_MEJORA.md` es la variante para pegar en un chat, si se prefiere ese flujo.*
