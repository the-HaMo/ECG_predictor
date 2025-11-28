# MEMORIA FASE 3
## DESARROLLO DEL SISTEMA BASADO EN LÓGICA FUZZY

**Integrantes, subgrupo y DNI:**
- Mohammed Amrou Labied Nasser, 1.1, 49857930W
- Sergio García García, 1.1, 49308323A
- José Antonio Nova Robles, 1.2, 49248919P

**Fecha de entrega:** 23/11/2025

---

## INTRODUCCIÓN A LA LÓGICA DIFUSA

A diferencia del sistema Drools que opera con reglas binarias (verdadero/falso), la lógica difusa es un método de razonamiento que permite trabajar con información imprecisa, otorgando grados intermedios de verdad entre el todo y la nada, expresados mediante lenguajes lingüísticos asociados a funciones de pertenencia que definen subconjuntos difusos.

Cada entrada se traduce en grados de pertenencia a cada uno de estos conjuntos difusos y el motor de inferencia combina las reglas para obtener una recomendación final. Aportando robustez, una representación y una interpretación flexible, gradual y más cercana al razonamiento humano del análisis de señales del ECG.

Para medir el grado de pertenencia de una entrada se emplean funciones de pertenencia continuas, tales como la gaussiana, la campana generalizada, la triangular o la trapezoidal. En nuestro sistema difuso se utilizan principalmente las funciones triangulares y trapezoidales. El eje X representa los posibles valores de la variable normalizada, mientras que el eje Y indica el grado de pertenencia al conjunto difuso, en el rango [0,1].

---

## ARQUITECTURA DEL SISTEMA FUZZY

La arquitectura de nuestro sistema es similar a la del sistema inteligente avanzado, con la diferencia principal en la incorporación de reglas borrosas basadas en lógica difusa. Esta modificación introduce dos componentes esenciales: el fuzzificador y el defuzzificador. El primero convierte las variables reales de entrada en conjuntos borrosos definidos sobre su correspondiente universo difuso U, mientras que el segundo transforma los conjuntos borrosos generados por el mecanismo de inferencia (definidos sobre el universo difuso V) en valores reales de salida. A continuación, mostramos un esquema detallado del sistema.

En nuestro proyecto, los procesos de fuzzificación y defuzzificación son gestionados por la librería JFuzzyLogic, para lo cual es necesario tener instalado el archivo .jar correspondiente (referencia 1). La base de reglas borrosas está definida en el archivo `diagnostico_ecg.fcl` (también gestiona la fuzzificación y desfuzzificación), mientras que la evaluación del sistema difuso se realiza mediante la clase `EvaluadorFuzzy`, con el apoyo de la clase auxiliar `AuxiliarFuzzy`. El mecanismo de inferencia se encuentra en la clase main `AnalizadorECG_Fuzzy` por simplicidad.

---

## DISEÑO DE LA BASE DE REGLAS

La mayoría de nuestras funciones de pertenencia utilizadas son triangulares o trapezoidales, debido a su simplicidad, estabilidad numérica y bajo coste computacional. Estas morfologías permiten representar de manera eficaz la progresión fisiológica de los parámetros cardíacos, que normalmente evolucionan de forma suave y continua entre estados normales y patológicos.

Las reglas implementadas siguen exactamente los mismos criterios diagnósticos establecidos en el sistema avanzado, por lo que en este apartado nos centraremos exclusivamente en la morfología de las funciones de pertenencia, explicando qué variables del ECG son relevantes para cada patología y activando las reglas correspondientes.

Una vez detectado el grado de pertenencia, se ejecuta el bloque de reglas asociadas que utiliza operadores and, or, etc., permitiendo la generación de una forma difusa compuesta para poder realizar el proceso de defuzzificación utilizando el método COG (Center of Gravity).

### Análisis del Ritmo Cardíaco

Es necesaria la variable ritmo que mide la frecuencia cardíaca en lpm (latidos por minuto), donde se modelan con funciones triangulares y trapezoidales que representan el dominio desde bradicardia severa a taquicardia severa, con un amplio rango normal.

| Término | Tipo | Rango (lpm) |
|---------|------|-------------|
| Bradicardia muy grave | Trapezoidal | 0-42 |
| Bradicardia grave | Triangular | 38-52 |
| Bradicardia moderada | Triangular | 48-57 |
| Bradicardia leve | Triangular | 54-60 |
| Normal | Trapezoidal | 59-101 |
| Taquicardia leve | Triangular | 100-108 |
| Taquicardia moderada | Triangular | 105-125 |
| Taquicardia grave | Triangular | 120-150 |
| Taquicardia muy grave | Trapezoidal | 145-200 |

Las variables de salida describen el nivel de bradicardia, taquicardia y el estado de salud del ritmo. Todos sus niveles son de forma triangular menos el último, que es trapezoidal.

### Análisis de Isquemia Coronaria

Es necesario detectar las variables de entrada de la amplitud de la onda T y del segmento ST, además de la duración de esta última variable. La primera variable tiene función de pertenencia triangular en todos sus tipos de inversión; la segunda variable también, a excepción del primer nivel "muy elevado" que es trapezoidal; y la última variable es trapezoidal en todos sus niveles.

La variable de salida "riesgo de isquemia" contiene tanto trapezoidales (para abarcar un rango más estable) como triangulares. La descripción de la variable se muestra en la siguiente tabla:

| Término difuso | Tipo | Intervalo |
|----------------|------|-----------|
| Muy leve | Triangular | 0-0.15 |
| Leve | Triangular | 0.1-0.4 |
| Moderado | Triangular | 0.35-0.65 |
| Grave | Triangular | 0.6-0.9 |
| Muy grave | Trapezoidal | 0.85-1.0 |

### Análisis de Hipopotasemia

Como se mencionó en el documento del sistema avanzado, la hipopotasemia comparte las variables correspondientes a la amplitud de la onda T y al segmento ST. En el caso de la primera variable, su función de pertenencia adopta una morfología triangular en todos los niveles, excepto en el nivel denominado "extremadamente invertida", que se representa mediante una función trapezoidal. Por su parte, la variable de entrada asociada al segmento ST mantiene una forma triangular en todos sus niveles.

La variable de salida "probabilidad de hipopotasemia" es igual a la de riesgo de isquemia en cuanto a la función de pertenencia se refiere:

| Término difuso | Tipo | Intervalo |
|----------------|------|-----------|
| Muy leve | Triangular | 0-0.2 |
| Leve | Triangular | 0.15-0.5 |
| Moderado | Triangular | 0.45-0.75 |
| Grave | Triangular | 0.7-0.95 |
| Muy grave | Trapezoidal | 0.9-1.0 |

### Análisis de Infarto Agudo de Miocardio

Se basa también sobre la amplitud de la onda T y el segmento ST. Ambas variables adoptan una forma triangular en todos los niveles, excepto en el caso de "muy elevado" que es trapezoidal.

La variable de salida presenta una función de pertenencia similar a la definida para las demás variables de entrada:

| Término difuso | Tipo | Intervalo |
|----------------|------|-----------|
| Muy leve | Triangular | 0-0.15 |
| Leve | Triangular | 0.15-0.45 |
| Moderado | Triangular | 0.4-0.75 |
| Grave | Triangular | 0.70-0.95 |
| Muy grave | Trapezoidal | 0.65-1.0 |

### Análisis de Hipocalcemia

La variable de entrada se basa en la duración del intervalo QT, cuya función de pertenencia presenta una morfología ligeramente irregular. En los niveles representados mediante funciones trapezoidales, el objetivo es abarcar un rango más amplio de valores, mientras que las funciones triangulares se emplean para destacar un punto central con transiciones descendentes a ambos lados. La morfología resultante se describe de la siguiente manera:

| Término | Tipo | Rango (ms) |
|---------|------|------------|
| Normal | Trapezoidal | 200-440 |
| Leve | Triangular | 439-460 |
| Moderado | Triangular | 455-495 |
| Grave | Triangular | 490-540 |
| Muy grave | Trapezoidal | 535-700 |

La variable de salida "probabilidad de hipocalcemia" sigue una distribución equivalente a la de las demás variables de salida. En este caso, la función de pertenencia adopta una morfología triangular en todos los niveles, excepto en el nivel "muy alta", que se representa mediante una función trapezoidal.

### Análisis de Contracción Ventricular Prematura

Son necesarias las variables de entrada correspondientes a la duración del complejo QRS y a la ausencia de onda P. En el primer caso, la función de pertenencia adopta una morfología trapezoidal en los niveles extremos, es decir, en "muy corto" y "ancho", mientras que en los demás niveles se representa mediante funciones triangulares. En el segundo caso, la función de pertenencia es triangular en todos los niveles, excepto en el nivel "ausente", que se modela con una función trapezoidal.

La variable de salida "probabilidad de PVC" es análoga a las demás variables de salida. En este caso, la función de pertenencia se representa mediante morfologías triangulares en todos los niveles, excepto en el nivel "muy alta", que adopta una forma trapezoidal:

| Término difuso | Tipo | Intervalo |
|----------------|------|-----------|
| Muy leve | Triangular | 0-0.2 |
| Leve | Triangular | 0.15-0.5 |
| Moderado | Triangular | 0.45-0.75 |
| Grave | Triangular | 0.70-0.95 |
| Muy grave | Trapezoidal | 0.9-1.0 |

---

## MANUAL DE USO DEL SISTEMA FUZZY

El sistema `AnalizadorECG_Fuzzy` implementa un motor de inferencia difusa para el análisis de señales electrocardiográficas (ECG). Su finalidad es transformar parámetros fisiológicos en diagnósticos cualitativos acompañados de un nivel de severidad, siguiendo criterios médicos previamente definidos.

### Flujo de Procesamiento

El sistema sigue el siguiente flujo de procesamiento:

1. **Entrada (ECG)**
   - Se recibe la señal electrocardiográfica en formato digital.

2. **Parser**
   - Interpreta la señal y prepara los datos para la extracción de características.

3. **Extracción de ondas**
   - Identifica las ondas P, QRS, T y segmentos relevantes (ST, QT, etc.).

4. **jFuzzy Engine**
   - Inicializa el motor difuso encargado de gestionar funciones de pertenencia y reglas.

5. **Fuzzificación**
   - Convierte los valores numéricos en grados de pertenencia a conjuntos difusos (ej. "QRS ancho", "onda T invertida").

6. **Reglas Fuzzy**
   - Aplica la base de conocimiento mediante operadores lógicos (and, or, etc.).

7. **Defuzzificación**
   - Obtiene un valor real que representa el diagnóstico y su severidad.

8. **Diagnóstico + Severidad**
   - Determina la patología probable (ej. hipopotasemia, hipocalcemia, PVC) junto con un nivel de severidad.

9. **Salida**
   - Presenta el resultado final al usuario.

---

## CONCLUSIÓN

La introducción del sistema difuso en el análisis de señales ECG permite establecer criterios diagnósticos flexibles y adaptativos, capaces de representar la progresión fisiológica de manera más realista que los métodos estrictamente deterministas. El uso de funciones de pertenencia triangulares y trapezoidales facilita la modelización de variables clínicas, mientras que la aplicación de reglas difusas y el proceso de defuzzificación garantizan la obtención de diagnósticos con un nivel de severidad asociado. En conjunto, el sistema ofrece una herramienta eficaz de apoyo al diagnóstico, integrando simplicidad computacional con coherencia clínica.

---

## REFERENCIAS

Estas son algunas referencias que hemos utilizado a lo largo del proyecto:

- Ciclo Cardiaco
- 19. The Basis of ECG Diagnosis
- Hipopotasemia en el Electrocardiograma
- ❤▷ ECG del infarto de miocardio. Explicación con Imágenes
- Manejo-del-infarto-final.pdf (página 12)
- Electrocardiography - Wikipedia (hipocalcemia)
- Isquemia, Lesión y Necrosis en la Cardiopatía Isquémica
- Intervalo QT
- jFuzzyLogic
- PROYECTO-DSI-2025-26.V1.2.pdf
- Uso de herramientas de apoyo: ChatGPT y Copilot
