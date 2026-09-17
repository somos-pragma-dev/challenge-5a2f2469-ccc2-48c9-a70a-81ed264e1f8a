# Motor de Risk Scoring en Tiempo Real con Circuit Breakers Dinámicos

Diseña un sistema que evalúa el riesgo de cada orden de trading en menos de 500 microsegundos p99 antes de enviarla al exchange. El sistema consume un feed de market data (nivel 2 orderbook + trades), mantiene un modelo de VaR intraday, aplica límites por trader/estrategia/instrumento en tiempo real, y dispara circuit breakers dinámicos que bloquean nuevas órdenes cuando la exposición supera los thresholds calibrados por volatilidad. Debes justificar la elección de estructuras lock-free vs mutex, la elección entre C++ vs Rust vs Java LMAX Disruptor, garantizar la consistencia entre múltiples risk engines corriendo en paralelo (consensus vs sharding por instrumento), la política de kill switch cuando detecta un algoritmo que se comporta de forma anómala, y la estrategia de replay determinístico para post-mortem de incidents. Incluye compliance con regulaciones MiFID II para trazabilidad de decisiones de riesgo.

## Informacion General

| Campo | Valor |
|-------|-------|
| **Tema** | TEST-CT |
| **Nivel** | master-l2 |
| **Tipo** | practical |
| **Tiempo estimado** | 4 semanas |

## Fases del Reto

### Fase 0: Configuración del Proyecto

**Objetivo:** Obtener el proyecto base funcional enviando el Código Base a un asistente de IA, que lo analizará, corregirá errores y generará un ZIP listo para usar.

**Tiempo estimado:** 15-30 minutos

**Instrucciones:**

- Asegúrate de tener instalado para ejecutar el proyecto: JDK 17+, Maven 3.9+, IDE con soporte Java.
- Copia todo el contenido del campo **Código Base** de este reto — incluyendo el texto de instrucciones que aparece al inicio.
- Abre un asistente de IA (Claude en claude.ai, ChatGPT o Gemini — se recomienda Claude), pega el contenido copiado en el chat y envíalo.
- El asistente analizará los archivos, corregirá errores y generará un archivo ZIP descargable. Descárgalo y extráelo en la carpeta donde quieras trabajar.
- Ejecuta `mvn compile` en la raíz. Si no hay errores, estás listo.

**Entregable:** El proyecto compila/arranca sin errores.

<details>
<summary>Pistas de conocimiento</summary>

- Copia el Código Base completo incluyendo el texto de instrucciones al inicio — esas instrucciones le indican al asistente exactamente qué hacer con los archivos.
- Si el asistente no genera el ZIP automáticamente al terminar el análisis, escríbele: "genera el ZIP ahora".
- Si el proyecto tiene errores al arrancar, comparte el mensaje de error con el mismo asistente para que lo corrija.

</details>

### Fase 1: Consumo y Procesamiento de Market Data

**Objetivo:** Implementar la funcionalidad para consumir y procesar el feed de market data en tiempo real.

**Tiempo estimado:** 1 semana

**Instrucciones:**

- Describir el problema de consumo y procesamiento del feed de market data.
- Identificar los criterios de aceptación para la fase.

**Entregable:** Sistema que consume y procesa el feed de market data en tiempo real.

<details>
<summary>Pistas de conocimiento</summary>

- Considerar la latencia y el throughput requeridos.
- Evaluar la consistencia de los datos recibidos.

</details>

### Fase 2: Modelo de VaR Intraday y Límites de Riesgo

**Objetivo:** Implementar el modelo de VaR intraday y aplicar límites de riesgo por trader/estrategia/instrumento.

**Tiempo estimado:** 1 semana

**Instrucciones:**

- Describir el problema de implementar el modelo de VaR intraday y aplicar límites de riesgo.
- Identificar los criterios de aceptación para la fase.

**Entregable:** Sistema que implementa el modelo de VaR intraday y aplica límites de riesgo en tiempo real.

<details>
<summary>Pistas de conocimiento</summary>

- Considerar la volatilidad y los thresholds para los límites de riesgo.
- Evaluar la precisión y la eficiencia del modelo de VaR.

</details>

### Fase 3: Circuit Breakers Dinámicos y Kill Switch

**Objetivo:** Implementar circuit breakers dinámicos y la política de kill switch para bloquear nuevas órdenes y detener algoritmos anómalos.

**Tiempo estimado:** 1 semana

**Instrucciones:**

- Describir el problema de implementar circuit breakers dinámicos y la política de kill switch.
- Identificar los criterios de aceptación para la fase.

**Entregable:** Sistema que implementa circuit breakers dinámicos y la política de kill switch en tiempo real.

<details>
<summary>Pistas de conocimiento</summary>

- Considerar la exposición y los thresholds calibrados por volatilidad.
- Evaluar la estrategia de replay determinístico para post-mortem de incidents.

</details>

### Fase 4: Compliance con Regulaciones MiFID II

**Objetivo:** Implementar la trazabilidad de decisiones de riesgo de acuerdo con las regulaciones MiFID II.

**Tiempo estimado:** 1 semana

**Instrucciones:**

- Describir el problema de implementar la trazabilidad de decisiones de riesgo.
- Identificar los criterios de aceptación para la fase.

**Entregable:** Sistema que implementa la trazabilidad de decisiones de riesgo de acuerdo con las regulaciones MiFID II.

<details>
<summary>Pistas de conocimiento</summary>

- Considerar los requisitos de trazabilidad y compliance.
- Evaluar la eficiencia y la precisión de la trazabilidad implementada.

</details>

## Dimensiones Evaluadas

- **queEs**: ¿Qué es un circuit breaker dinámico y cómo funciona en el contexto del sistema de risk scoring?
- **paraQueSirve**: ¿Para qué sirve el modelo de VaR intraday en la evaluación del riesgo de las órdenes de trading?
- **comoSeUsa**: ¿Cómo se usa la política de kill switch para detener algoritmos anómalos en el sistema?
- **erroresComunes**: ¿Cuáles son los errores comunes al implementar la trazabilidad de decisiones de riesgo de acuerdo con las regulaciones MiFID II?
- **queDecisionesImplica**: ¿Qué decisiones implica la elección entre C++ vs Rust vs Java LMAX Disruptor para implementar el modelo de VaR?

## Criterios de Evaluacion

- Implementar la funcionalidad para consumir y procesar el feed de market data en tiempo real.
- Implementar el modelo de VaR intraday y aplicar límites de riesgo por trader/estrategia/instrumento.
- Implementar circuit breakers dinámicos y la política de kill switch para bloquear nuevas órdenes y detener algoritmos anómalos.
- Implementar la trazabilidad de decisiones de riesgo de acuerdo con las regulaciones MiFID II.

## Como trabajar con un asistente de IA

Hay dos caminos, elegi uno:

- **AGENTS.md** (recomendado) — instrucciones nativas del repo. Abri esta carpeta con tu agente local (Claude Code, Cursor, Codex, Copilot, Gemini) y las carga solo. Sabe que archivos faltan y con que comando se verifica, y completa el scaffold escribiendo en disco.
- **PROMPT_MEJORA.md** — para copiar y pegar en un chat (claude.ai, ChatGPT). Devuelve un ZIP con el proyecto. Sirve si no tenes un agente en el IDE.

Ninguno de los dos resuelve las fases del reto: eso es tu trabajo.

## Verificacion

El proyecto esta listo para trabajar cuando este comando corre sin errores:

```bash
el comando de build o arranque canonico del stack elegido
```

---

*Reto generado automaticamente por Challenge Generator - Pragma*
