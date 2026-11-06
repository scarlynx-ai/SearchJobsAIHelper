# SearchJobsAIHelper

Plataforma multiplataforma construida con **Kotlin Multiplatform (KMP)** y **Compose Multiplatform** orientada a desarrolladores Android que buscan vacantes de trabajo **100% remotas** en el mercado global.

Integra asistencia de inteligencia artificial para calcular el índice de compatibilidad técnica (*AI Match Score*), diagnosticar vacíos de habilidades (*Skill Gaps*) y generar postulaciones personalizadas y optimizadas para sistemas ATS.

---

## Características Principales

- **Priorización Regional ("Perú First")**: 
  - Clasificación jerárquica inicial que destaca oportunidades remotas originadas o con contratación directa en **Perú** (empresas locales y multinacionales con nómina bajo huso horario PET / UTC-5).
  - Expansión hacia vacantes en **Américas (LatAm y EE. UU.)** con compatibilidad horaria directa y contratos B2B / EOR.
  - Conclusión con posiciones asíncronas a nivel mundial (*Worldwide*).
- **Filtro Estricto 100% Remoto**: Exclusión garantizada de esquemas híbridos o presenciales encubiertos, con segmentación por huso horario y tipo de contrato.
- **Filtros por Stack Android**: Categorización por Jetpack Compose, Kotlin Coroutines, KMP, Clean Architecture, inyección de dependencias (Hilt/Koin) y persistencia local.
- **AI Match Score**: Algoritmo de ponderación semántica que evalúa las habilidades del perfil frente a los requisitos reales de la oferta.
- **Diagnóstico de Brechas Técnicas**: Identificación inmediata de herramientas faltantes con sugerencias de preparación técnica.
- **Generador de Cartas de Postulación**: Redacción en un clic de mensajes directos para reclutadores y cartas de presentación adaptadas a cada rol.
- **Seguimiento de Candidaturas**: Tablero para rastrear el progreso de cada vacante (Guardada, Enviada, Entrevista, Oferta).

---

## Arquitectura y Stack

- **Framework**: Kotlin Multiplatform + Compose Multiplatform
- **Patrón de Presentación**: MVI (Model-View-Intent) con flujo reactivo de datos
- **Networking**: Ktor Client con serialización JSON multiplataforma
- **Persistencia**: SQLDelight / Room KMP para almacenamiento local seguro
- **Fuentes de Ofertas**: Agregadores en vivo (GetOnBrd, Remotive, Jobicy)

---

## Prototipo Interactivo

El prototipo funcional de alta fidelidad se encuentra disponible en el repositorio:
- Archivo de visualización: `prototype.html`
- Especificación de diseño: `ux_specification.md`
- Resumen estratégico: `ideation_product_brief.md`

---

## Autor

Desarrollado por **Scarlynx**.
