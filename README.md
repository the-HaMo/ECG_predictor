#  ECG_predictor
Sistema completo para el análisis automático de señales ECG mediante:

- **Fase 1:** Ontología  
- **Fase 2:** Sistema basado en reglas (Drools)  
- **Fase 3:** Sistema basado en lógica difusa (Fuzzy Logic)

---

#  Estructura del Repositorio

```
ECG_predictor/
│
├── inputs/               # Archivos .ecg de entrada
├── salidas/              # Salidas generadas (.txt)
│
├── SIcompleto.jar        # Sistema basado en reglas (Drools)
├── SFuzzy.jar            # Sistema de análisis difuso (JFuzzyLogic)
│
├── ECG-doc/
│   ├── source/
│   │    ├── ontologia.md
│   │    ├── DSINT_fase2.md
│   │    └── DSINT_fase3.md
│   └── ...
│
└── README.md
```

---

#  Requisitos

###  **Java 1.8 (OBLIGATORIO)**  
Drools y Fuzzy **NO funcionan en Java 17/18/21**, puede usar `Docker` para ello.

Verifica tu versión:

```bash
java -version
```

Debe mostrar:

```
java version "1.8.x"
```

Si no, instala JDK 8  
https://adoptium.net/

---

# Preparación de ficheros de entrada (.ecg)

Los ficheros deben estar en la carpeta `inputs/`.

Cada línea debe tener:

```
tipoOnda instanteInicio instanteFinal amplitud
```

Ejemplo:

```
P(12 34 0.25)
Q(35 40 -0.12)
R(41 50 0.92)
S(51 60 -0.35)
T(61 90 0.45)
```

---

# Ejecución del sistema basado en reglas (Drools)

### **Comando general:**

```bash
java -jar /jar/SIcompleto.jar ruta_inputs ruta_salidas
```

Ejemplo:

```bash
java -jar /jar/SIcompleto.jar "inputs" "salidas"
```

Genera:

```
salidas/
 ├── paciente.salida.txt
 ├── otroPaciente.salida.txt
 └── todo.salida.txt
```

Diagnósticos detectados:

- Taquicardia  
- Bradicardia  
- Hipopotasemia  
- Infarto Agudo de Miocardio Temprano  
- Hipocalcemia  
- Isquemia Coronaria  
- PVC  
- Normal

---

# Ejecución del sistema Fuzzy

### **Comando general:**

```bash
java -jar /jar/SFuzzy.jar ruta_inputs ruta_salidas
```

Ejemplo:

```bash
java -jar /jar/SFuzzy.jar "inputs" "salidas_fuzzy"
```

Devuelve:

- Diagnóstico inferido  
- Nivel de severidad (0–1)
- gráfica de la funcion de pertenencia. 

---

#  Ejemplo completo de uso

```
java -jar SIcompleto.jar C:\ECG\inputs C:\ECG\salidas
java -jar SFuzzy.jar C:\ECG\inputs C:\ECG\salidas_fuzzy
```

#  Documentación de las fases

- [Fase 1 – Ontología](ECG-doc/source/ontologia.md)
- [Fase 2 – Sistema basado en reglas](ECG-doc/source/DSINT_fase2.md)
- [Fase 3 – Sistema difuso](ECG-doc/source/DSINT_fase3.md)

---

# 🙌 Autores

- Mohammed Amrou Labied Nasser  
- Sergio García García  
- José Antonio Nova Robles  
