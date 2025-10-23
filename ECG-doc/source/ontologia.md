# INTRODUCCIÓN Y OBJETIVOS

El informe describe el desarrollo de una ontología del dominio clínico del electrocardiograma (ECG), siguiendo los siete pasos del método 101.  
El objetivo principal es representar formalmente los conceptos, relaciones y propiedades asociadas a las señales ECG para poder realizar diagnósticos automáticos.  
La ontología tomará como referencia las ondas, segmentos e intervalos característicos del trazado electrocardiográfico.

---

# DESARROLLO

Según el método de desarrollo de ontologías 101, el primer paso consiste en determinar el dominio y el alcance de la ontología.  

En nuestro caso, el dominio se define por el electrocardiograma (ECG) desde una perspectiva clínica, centrado en la representación formal de sus componentes morfológicos: ondas, segmentos, parámetros fisiológicos derivados, como la frecuencia cardiaca o el número de ciclos.  
Además, incluye los diagnósticos simples y complejos que pueden inferirse a partir de la señal, tales como taquicardia, bradicardia, etc.  

Por el contrario, quedan fuera del dominio aspectos relacionados con:
- El procesamiento de la señal bruta (ya que se asume un preprocesamiento previo).
- Los elementos anatómicos del corazón.
- Los electrodos del dispositivo electrocardiógrafo.
- Los informes médicos completos.

El alcance de la ontología está delimitado por el objetivo del proyecto, que consiste en desarrollar un sistema inteligente capaz de interpretar la señal electrocardiográfica y emitir diagnósticos automáticos.

---

## Segundo paso: revisión de 0ntologías existentes

Durante esta revisión se identificaron algunas ontologías relacionadas con el electrocardiograma (ver referencias en la bibliografía).  
Sin embargo, ninguna de ellas cumple de manera concreta con los objetivos del proyecto, ya que no contemplan todos los intervalos, segmentos y parámetros fisiológicos necesarios.  
Además, su estructura resulta poco adaptable al enfoque propuesto, por lo que se opta por desarrollar una ontología propia.

---

## Tercer paso: enumeración de términos importantes

Estos términos representan los elementos, propiedades y relaciones esenciales.  
En el siguiente esquema se integran el tercer, quinto, sexto y séptimo paso del método.

### **ONDA**
Representa cualquier componente ondulatorio del ECG (clase abstracta).

**Slots:**
- **Start (Integer):** Instante del comienzo de la onda  
- **End (Integer):** Instante de finalización de la onda  
- **Peak (Float):** Amplitud de la onda (en S y Q debe ser negativo)  

**Heredan:** Onda P, Onda Q, Onda R, Onda S y Onda T (instancias).  
**Restricciones:** Una onda inicia en P y termina en T, comenzando la siguiente onda por P. `End > Start`

---

### **SEGMENTO**
La unión de una onda con el complejo QRS.

**Slots:**
- **Inicio (Integer):** Instante del comienzo del segmento  
- **Fin (Integer):** Instante de finalización del segmento  
- **Amplitud (Float):** Amplitud de la onda  

**Heredan:** Segmento PR, Segmento ST  
**Restricciones:** `Fin > Inicio`, `Duración >= 0`

---

### **INTERVALO**
Representa un segmento temporal entre dos puntos característicos del ECG con el complejo QRS.

**Slots:**
- **Inicio (Integer):** Instante del comienzo de la onda  
- **Fin (Integer):** Instante de finalización de la onda  
- **Amplitud (Float):** Amplitud de la onda  

**Heredan:** Intervalo PQ, Intervalo QT  
**Restricciones:** `Fin > Inicio`, `Duración >= 0`, correcta situación de cada onda

---

### **COMPLEJO QRS**
Representa agrupaciones de ondas. En esta versión se incluye el complejo formado por las ondas Q, R y S, que representa la despolarización ventricular.

**Slots:**
- **Inicio (Integer):** Instante del comienzo de la onda  
- **Fin (Integer):** Instante de finalización de la onda  
- **Amplitud (Float):** Amplitud de la onda  

**Restricciones:** `Fin > Inicio`, `Duración >= 0`, correcta situación de cada onda

---

### **DIAGNÓSTICO_INFERIDO**
Es el resultado obtenido a partir del análisis del ECG.

**Slots:**
- **Resultado (Enumerado)**  

**Heredan:**
- Patrones anormales simples del ECG:
  - Hipopotasemia  
  - Hipocalcemia  
  - Infarto Agudo de Miocardio temprano  
  - Isquemia Coronaria  
- Patrones anormales complejos del ECG:
  - Bradicardia Sinusal  
  - Taquicardia Sinusal  
  - Atrial Flutter  
  - Premature Ventricular Contraction  
  - Estimación del ritmo cardíaco  
- Sano  

---

### **ANÁLISIS_SEÑAL**
Clase que calcula todos los atributos derivados a partir de la señal ECG.

**Slots:**
- **Ritmo cardíaco (Integer)**  
- **Número de ciclos (Integer)**

---

## Cuarto paso: definición de clases y jerarquía

Nuestra estructura jerárquica se organiza en 4 niveles:  
- Componentes de la señal ECG  
- Análisis de señal  
- Resultado del diagnóstico  

![Esquema completo de la ontología ECG](ECG-doc/img/schema-ont.png)

---

# DECISIONES TOMADAS

1. La clase **Análisis señal** se creó para calcular los atributos derivados de la señal ECG (como número de ciclos o frecuencia cardiaca) mediante reglas de la base de conocimiento y no en el preprocesamiento.  
2. Se creó una **clase padre** de la cual heredan todos los elementos relacionados con la señal ECG.  
3. Se subdividió el tipo de diagnóstico en tres clases:
   - **Anormalidad simple:** enfermedad común o leve.  
   - **Anormalidad compleja:** enfermedades más dañinas o graves.  
   - **Sano:** si el sistema de inferencia no ejecuta ninguna regla, se asume que el paciente está sano.  
4. Inicialmente, la clase **Onda** iba a ser concreta con un atributo tipo, pero se descartó por generar muchos casos especiales. Se optó por la jerarquía descrita.  
5. Para **Segmento** e **Intervalo**, se incluyeron solo las clases concretas previamente descritas, por ser más expresivas y fáciles de comprender.

---

# ESQUEMA DE LA ONTOLOGÍA

A continuación, se muestra un grafo que describe de forma completa nuestra ontología.  
Las clases **Anormalidad compleja** y **Anormalidad simple** no incluyen sus subclases, con el fin de mantener la representación lo más sencilla posible.

![Grafo completo de la ontología ECG](ECG-doc/img/graph-ont.png)


---

# BIBLIOGRAFÍA

**Información utilizada para definir la ontología propia:**
- [Wikipedia: Electrocardiography](https://en.wikipedia.org/w/index.php?title=Electrocardiography&oldid=513556137#Waves_and_intervals)

**Ontologías de ECG:**
- *Using an ECG reference ontology for semantic interoperability of ECG data*  
- *Electrocardiography Ontology | NCBO BioPortal*  
- *Bernardo-WOMSDE'07-v4.pdf*
