# MEMORIA FASE 2  
## DESARROLLO DEL SISTEMA INTELIGENTE AVANZADO

**Integrantes, subgrupo y DNI:**
- Mohammed Amrou Labied Nasser, 1.1, 49857930W
- Sergio García García, 1.1, 49308323A
- José Antonio Nova Robles, 1.2, 49248919P

**Fecha de entrega:** 23/11/2025

---

## 1. ARQUITECTURA DEL SISTEMA

A continuación, se presenta la arquitectura que seguirá nuestro proyecto, organizada en tres capas principales:

### Capas del Sistema

- **Capa Parser:** Encargada de la gestión de los archivos de entrada y salida. Este paquete se responsabiliza de la lectura, interpretación y escritura de datos en formatos compatibles con el sistema.

- **Capa del Sistema Inteligente:** Contiene la lógica principal del sistema, incluyendo el motor de inferencia y la base de conocimiento (Knowledge Base). Esta capa se encarga del proceso de inferencia y la toma de decisiones.

- **Capa Main:** Responsable de la inicialización y ejecución del sistema de inferencia.

### Descripción del Proceso del Sistema Basado en Reglas

En este apartado describiremos qué realiza cada paso del sistema.\
#### PASO 1: Inicialización de la Base de Hechos

El sistema comienza leyendo un archivo de entrada con extensión `.ecg`, que contiene un conjunto de hechos iniciales representando el conocimiento base del dominio a tratar. Esta información se carga en la base de hechos del motor de inferencia, siendo el conjunto de hechos inicial o *facts*. Este paso permite que el sistema disponga de todos los datos necesarios antes de aplicar las reglas definidas.\
#### PASO 2: Activación del Motor de Reglas (DROOLS)

Una vez que la base de hechos está lista, se pone en marcha el motor de reglas **DROOLS**, encargado de evaluar las reglas existentes en función de los hechos disponibles. Cada regla se compone de una condición y una acción. Cuando los hechos actuales cumplen con las condiciones especificadas, la regla se activa y ejecuta las acciones correspondientes, produciendo cambios en la base de hechos (con la inserción de nuevos *facts*).

#### PASO 3: Modificación e Inferencia Continua

Cada vez que se dispara una regla, el sistema puede generar nuevos hechos o actualizar los existentes. Estos cambios se incorporan a la base de hechos, lo que puede hacer que nuevas reglas se vuelvan aplicables. Este proceso se repite hasta que ocurre alguno de los siguientes eventos:

- No quede ninguna regla que pueda ejecutarse.
- Se haya alcanzado una conclusión.

#### PASO 4: Escritura y Almacenamiento de los Resultados

Una vez finalizado el proceso de inferencia, el sistema accede a la memoria de trabajo (*working memory*) para obtener los diagnósticos y generar los archivos de salida. Estos archivos son:

- **`nombre.salida.txt`:** Archivo individual generado por paciente.
- **`todo.salida.txt`:** Archivo consolidado con todos los diagnósticos.

---

## 2. CONTEXTO PARA EL ANÁLISIS ECG

Tras revisar la arquitectura de nuestro sistema, en este apartado vamos a describir el objetivo del proyecto. Como mencionamos anteriormente, los ficheros de entrada contendrán los hechos iniciales, concretamente las distintas ondas caracterizadas por su instante inicial, instante final, amplitud y tipo de onda. A partir de estos datos, se iniciará el proceso de inferencia mediante las reglas definidas en la base de conocimiento, con el fin de alcanzar un diagnóstico determinado. Dicho diagnóstico incluirá la patología detectada y una breve descripción de su causa.\
### 2.1. Diseño de las Reglas para la Inferencia de las Patologías

Teniendo la base de hechos inicializada con las ondas de la señal ECG, es necesario agrupar dicho conjunto de ondas en distintos segmentos, intervalos y complejos para facilitar la detección de las enfermedades descritas en el siguiente apartado. En particular, será necesario detectar los ciclos cardíacos (la posible secuencia de las ondas P-Q-R-S-T), el ritmo cardíaco, el complejo QRS, el intervalo QT, segmento ST y ondas específicas como la T. Para ello, mostraremos el proceso seguido en la construcción de las reglas, junto con algunas decisiones tomadas durante su diseño.\
#### Regla: Detección del Ciclo Cardíaco con Onda T

Analizamos la secuencia completa de ondas P-Q-R-S-T. Guardamos la duración entre el instante inicial de la onda P y el instante final de la onda T.

**Pasos:**
1. Selección de la primera onda P.
2. Selección de la primera onda Q posterior a la P.
3. Selección de la primera onda R posterior a Q.
4. Selección de la primera onda S posterior a R.
5. Selección de la primera onda T posterior a S.
6. Se genera un `Ciclo_Cardiaco` usando inicio (Onda Q) y fin (Onda T).\
#### Regla: Detección del Ciclo Cardíaco sin Onda T

Analizamos la secuencia de ondas P-Q-R-S y la ausencia de onda T (para el último ciclo cardíaco). Guardamos la duración entre el instante inicial de la onda P y el instante final de la onda S.

**Pasos:**
1. Selección de onda P.
2. Selección de la primera onda Q posterior.
3. Selección de la primera onda R posterior.
4. Selección de la primera onda S posterior.
5. Confirmación de que no existe ninguna onda T posterior.
6. Se genera un `Ciclo_Cardiaco` usando inicio (onda Q) y fin (onda S).\
#### Regla: Número de Ciclos y Ritmo Cardíaco

Derivamos la cantidad de ciclos y el ritmo cardíaco por minuto. Almacenamos el número de ciclos y el ritmo cardiaco inferido.

**Pasos:**
1. Recolección de todos los `Ciclo_Cardiaco`.
2. Recolección de todas las ondas Q.
3. Cálculo de la duración total entre la primera y la última Q.
4. Cálculo del ritmo cardíaco: `(60000 × ciclos) / duración`.
5. Inserción de un `Analisis_Señal`.\
#### Regla: Creación del Complejo QRS

Detectamos la secuencia de ondas Q-R-S para construir un complejo QRS. Almacenamos la duración siendo el instante entre el inicio de la onda Q y el final de la onda S, y también se guarda la amplitud total del complejo cuyo cálculo se realiza con la siguiente fórmula:

```
AmplitudQRS = AmplitudR - min(AmplitudQ, AmplitudS)
```

**Pasos:**
1. Selección de onda Q.
2. Selección de la primera onda R posterior.
3. Selección de la primera onda S posterior.
4. Verificar que no exista un complejo duplicado.
5. Crear `Complejo_QRS` con duración y amplitud estimada.\
#### Regla: Creación del Intervalo QT

Creamos el intervalo de ondas Q a T. No es necesario preservar la amplitud de todo el intervalo; en lugar de ello, conservamos la duración entre el instante inicial de la onda Q y el instante final de la onda T.

**Pasos:**
1. Seleccionar onda Q.
2. Seleccionar la primera onda T siguiente de la onda Q.
3. Comprobar duplicados.
4. Crear `Intervalo_QT`.

#### Regla: Creación del Segmento ST\
Creamos el conjunto de ondas S y T dentro de un mismo ciclo cardíaco. La amplitud del segmento no es necesario calcularla realmente, aunque nosotros haremos la media entre la amplitud de la onda S y la onda T. Y calculamos la duración del segmento.

**Pasos:**
1. Seleccionar onda S.
2. Seleccionar la primera onda T posterior.
3. Evitar duplicados.
4. Crear `Segmento_ST` con amplitud media de ambas ondas y duración.\
### Decisiones Tomadas

1. La principal razón para definir dos reglas que generen ciclos cardíacos es la posible presencia de **ciclos parciales**. Estos ciclos parciales se caracterizan por la ausencia de la onda T, siendo la onda S (la inmediatamente anterior) el final del ciclo. Es fundamental considerar los ciclos parciales para calcular correctamente el número de ciclos y el ritmo cardíaco. Además, en los ficheros de entrada suelen detectarse al final de la señal ECG. Por tanto, si no se detectan ciclos parciales, siempre se ejecutará la primera regla y la segunda no llegará a aplicarse; ambas nunca se ejecutarán a la vez.

2. Para la creación del complejo QRS, intervalo QT y el segmento ST no hemos establecido ningún parámetro para la duración fisiológica, puesto que nuestro sistema coge la onda correspondiente inmediatamente posterior, evitando duplicidad mediante la cláusula `not`.

3. Todo este conjunto de reglas está agrupado en la **agenda de inferencia** y constituye el primer bloque que se ejecuta, ya que estos elementos son fundamentales para el diagnóstico.\
---

### 2.2. Diseño de las Reglas para la Detección de las Patologías

Nuestro sistema de inferencia será capaz de detectar **siete tipos de enfermedades** a partir de la señal ECG, además de un nuevo estado denominado **normal**. Este estado se definirá al comprobar la ausencia de cualquier diagnóstico relacionado con las enfermedades consideradas. El conjunto de patologías que abordamos se detalla a continuación, indicando qué propiedades se verifican (ondas, intervalos, ritmo cardíaco, etc.), las condiciones necesarias y decisiones tomadas en base a las distintas fuentes de información consultadas.

#### Enfermedad: Taquicardia Sinusal\
Para detectar la taquicardia solo debemos comprobar que el ritmo cardíaco sea mayor a 100 pulsaciones por minuto.

**Pasos:**
1. Obtener el atributo ritmo de `Analisis_Señal`.
2. Si ritmo > 100 bpm, insertar diagnóstico.\
#### Enfermedad: Bradicardia Sinusal

De forma análoga al proceso que se realiza para detectar la taquicardia, debemos comprobar que el ritmo cardíaco sea menor que 60 pulsaciones por minuto para detectar la bradicardia.

**Pasos:**
1. Obtener el atributo ritmo de `Analisis_Señal`.
2. Si ritmo < 60 bpm, insertar diagnóstico.\
#### Enfermedad: Hipopotasemia

En el caso de la patología de hipopotasemia, se debe comprobar el valor de la amplitud del segmento ST descendido y la morfología de la onda T, que suele presentarse aplanada o ligeramente invertida. En particular, hemos definido una cota específica.

**Pasos:**
1. Verificar amplitud del segmento ST < -0.5.
2. Verificar pico de onda T < -12.
3. Insertar diagnóstico si no existe.\
#### Enfermedad: Infarto Agudo de Miocardio Temprano

La detección del infarto agudo de miocardio temprano utiliza los mismos elementos que la hipopotasemia; la diferencia radica en que el segmento ST presenta una morfología convexa y la onda T es ascendente.

**Pasos:**
1. Amplitud del segmento ST > 0.1.
2. Pico de la onda T > 0.6.
3. Comprobar continuidad entre ST y T.
4. Insertar diagnóstico si no existe.\
#### Enfermedad: Hipocalcemia

Para detectar la hipocalcemia solamente debemos verificar si el intervalo QT es prolongado, exactamente que la duración sea mayor a 440 ms.

**Pasos:**
1. Detectar intervalo QT cuya duración > 440 ms.
2. Insertar diagnóstico si no existe.\
#### Enfermedad: Isquemia Coronaria

De forma similar al proceso seguido para detectar la hipopotasemia, se emplean los mismos elementos para identificar la isquemia coronaria. En este caso, la onda T debe ser invertida y aguda, mientras que el segmento ST se encuentra predominantemente descendido. Siendo las cotas más restrictivas.

**Pasos:**
1. Pico de la onda T entre -5 y -12.
2. Amplitud de Intervalo ST entre -1 y -6.
3. Insertar diagnóstico si no existe.\
#### Enfermedad: Contracción Ventricular Prematura (PVC)

En la contracción ventricular prematura (precisamente la supraventricular) se debe detectar si el complejo QRS es menor a 90 ms.

**Pasos:**
1. Detectar QRS con duración < 90 ms.
2. Insertar diagnóstico si no existe.\
#### Diagnóstico: Normal

Como mencionamos anteriormente, el diagnóstico normal se obtiene en el caso de que ninguna de las patologías definidas haya sido detectada. En cierto sentido, puede interpretarse como la salida por defecto, asociada a un paciente sano, aunque no necesariamente implica que el usuario realmente esté sano.

**Pasos:**
1. Verificar que no exista ningún diagnóstico previo.
2. Insertar diagnóstico sano.\
### Decisiones Tomadas

1. Una de las decisiones más importantes en nuestro conjunto de reglas para inferir diagnósticos es la **introducción de cotas** que permitan diferenciar patologías. Un ejemplo se da entre la hipopotasemia y la isquemia coronaria, ya que comparten los mismos elementos para la inferencia diagnóstica. Estas cotas se han definido a partir de una combinación de las fuentes de información consultadas y los ficheros de entrada. En particular, se observa que la amplitud de la onda T en la hipopotasemia suele ser más negativa que en la isquemia coronaria, estableciéndose la cota en -10 como límite entre ambas. No obstante, nosotros hemos fijado la cota en **-12** para mantener un margen adicional de seguridad ante futuros casos de prueba.

2. Para la patología de infarto agudo de miocardio temprano sí que se usan las cotas presentes en las fuentes de información, ya que se adaptan bien a los datos de entrada.

3. Un caso similar al descrito en el punto anterior corresponde a la patología de contracción ventricular prematura. En la fuente de información consultada, la cota se establece en 100 ms; sin embargo, hemos ajustado el umbral a **90 ms** en función de los casos de prueba, ya que con el valor original se producirían falsos positivos incluso cuando el diagnóstico esperado fuese normal.

4. Nuevamente, todo este conjunto de reglas está agrupado en la **agenda de diagnósticos** y constituye el segundo bloque que se ejecuta, insertando metas en la base de hechos. Un aspecto importante dentro de nuestro sistema es que siempre va a existir una solución gracias al diseño que hemos implementado del estado normal.

---

### 2.3. Diseño de las Reglas para Finalizar la Inferencia

La última regla definida en nuestro sistema tiene como objetivo indicar que el proceso de inferencia ha concluido y que se ha obtenido un diagnóstico. Esta regla pertenece al grupo de **agenda report** y se activa cuando existe un objeto de tipo `Diagnostico`, constituyendo el último bloque que se ejecuta. Una vez disparada, la regla ejecuta la acción de mostrar el diagnóstico final. De esta manera, el sistema no solo determina la patología (o el estado normal) a partir de las reglas anteriores, sino que también asegura que el resultado quede registrado y comunicado como salida del proceso de inferencia.

---

## 3. CONCLUSIÓN

El sistema desarrollado permite analizar señales ECG a partir de ondas detectadas y, mediante un conjunto de reglas bien definidas, identificar automáticamente diferentes patologías cardíacas. La construcción de ciclos, intervalos y segmentos ha sido esencial para extraer los parámetros necesarios y aplicar criterios clínicos que posibilitan el diagnóstico de taquicardia, bradicardia, alteraciones del segmento ST, anomalías en la onda T, cambios en el intervalo QT y otras condiciones relevantes.

El uso de umbrales ajustados y reglas diferenciadas garantiza diagnósticos coherentes con la información de entrada, reduciendo falsos positivos y permitiendo distinguir patologías con características similares. Asimismo, la inclusión de un estado normal asegura que el sistema siempre proporcione una salida interpretada, aunque todo esto depende de la distribución de los ficheros de entrada.

En conjunto, el proyecto demuestra que un enfoque basado en reglas puede ofrecer resultados fiables y comprensibles para el análisis de ECG, sentando las bases para futuras mejoras y la incorporación de nuevas patologías.

---

## 4. REFERENCIAS

Estas son algunas referencias que hemos utilizado a lo largo del proyecto:

- **Ciclo Cardiaco**
- **19. The Basis of ECG Diagnosis**
- **Hipopotasemia en el Electrocardiograma**
- **❤▷ ECG del infarto de miocardio. Explicación con Imágenes**
- **Manejo-del-infarto-final.pdf** (página 12)
- **Electrocardiography - Wikipedia** (hipocalcemia)
- **Isquemia, Lesión y Necrosis en la Cardiopatía Isquémica**
- **Intervalo QT**
- **PROYECTO-DSI-2025-26.V1.2.pdf**
- **Uso de herramientas de apoyo:** ChatGPT y Copilot
