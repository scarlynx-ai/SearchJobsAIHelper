# SearchJobsAIHelper 🤖📱

**SearchJobsAIHelper** es una aplicación multiplataforma construida con **Kotlin Multiplatform (KMP)** y **Compose Multiplatform** diseñada específicamente para **Android Developers** que buscan vacantes **100% remotas** de alto valor a nivel global, con asistencia de Inteligencia Artificial para el cálculo de compatibilidad (*AI Match Score*), diagnóstico de brechas técnicas (*Skill Gaps*) y generación de postulaciones personalizadas.

---

## 🚀 Flujo de Ciclo de Vida del Proyecto

Este proyecto se rige por el nuevo **Framework Cuadri-Agente**:

```
[0. ideation-strategist] ➔ [1. ux-designer] ➔ [2. feature-developer] ➔ [3. qa-validator]
        (Completado)           (Completado)              (Próximo)                (Final)
```

1. **Fase 0 (Incepción & Estrategia):** Completada por el agente `ideation-strategist`. Documento de producto y matriz de requerimientos disponible.
2. **Fase 1 (Diseño UX/UI & Prototipado):** Completada por el agente `ux-designer` bajo la clasificación `[NUEVO]`.
3. **Fase 2 (Construcción KMP):** A cargo de `feature-developer` (implementación de arquitectura limpia MVI, Compose Multiplatform, SQLDelight y Ktor).
4. **Fase 3 (Calidad & Pruebas):** A cargo de `qa-validator`.

---

## 🎯 Características Principales

* **Priorización Regional Predeterminada ("Perú First"):**
  * Presentación inicial jerárquica: Ofertas 100% remotas originadas o con contratación directa en **Perú** (empresas locales y transnacionales con nómina/entidad en Perú bajo huso horario PET / UTC-5).
  * Continuación con vacantes de **Américas (LatAm y EE. UU.)** con compatibilidad de timezone directa y contratos B2B/EOR (Deel).
  * Cierre con ofertas asíncronas globales (Worldwide).
  * Bonus de afinidad regional en el cálculo del *AI Match Score*.
* **Filtros por Stack Técnico Android:**
  * Jetpack Compose & Compose Multiplatform.
  * Kotlin Coroutines & Asynchronous Flow.
  * Kotlin Multiplatform (KMP).
  * Clean Architecture & MVI / MVVM.
  * Inyección de Dependencias (Hilt / Koin).
  * Persistencia Local (Room / SQLDelight).
* **Filtro Estricto 100% Remoto:** Exclusión automática de esquemas híbridos o presenciales encubiertos; segmentación por zonas horarias (Worldwide, US EST/PST, LatAm, Europa) y modalidades contractuales (B2B Contractor / EOR).
* **AI Match Score (0 - 100%):** Algoritmo de ponderación semántica entre las habilidades del desarrollador y los requerimientos de la oferta.
* **Skill Gap Diagnosis:** Resalta de inmediato qué herramientas faltan y sugiere guías de preparación rápida.
* **Generador de Pitch de Postulación con IA:** Redacción en 1 clic de cartas de presentación y mensajes directos para reclutadores optimizados para ATS.
* **Application Tracker:** Seguimiento de postulaciones en formato pipeline (Guardado, Postulado, Entrevista, Oferta).

---

## 📱 Prototipo Interactivo

El prototipo funcional de alta fidelidad está disponible como artefacto interactivo en el sistema:
* Archivo: `prototype.html`
* Especificación completa de diseño: `ux_specification.md`
* Brief de Ideación: `ideation_product_brief.md`
