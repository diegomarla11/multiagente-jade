# Servicio multiagente con JADE

¿Cómo vuelve una respuesta al cliente cuando dos agentes trabajan sobre la misma solicitud en paralelo? Este proyecto en Java lo muestra con un cuestionario ficticio posterior a un terremoto. Un agente procesa el recuento y otro devuelve un recurso simulado para un código postal. JADE se encarga del descubrimiento del servicio y del intercambio de mensajes FIPA ACL.

Proyecto universitario desarrollado en equipo. Diego Martínez Lafuente publica esta adaptación en su portfolio personal.

## Probar la demo

Necesitas un JDK 11 o superior con `java` y `javac` en el PATH y Python 3.8 o superior. Desde la raíz del repositorio, ejecuta los dos comandos en orden. En Windows puedes sustituir `python3` por `py -3`.

```sh
python3 dependencias.py
python3 demo.py
```

El primer comando descarga y verifica las dependencias. El segundo compila Java, inicia un contenedor JADE local y ejecuta un caso de ejemplo con tres respuestas afirmativas y el código postal 37001. Entre los mensajes de arranque y cierre aparecerán estas dos líneas.

```text
Affirmative answers: 3/10 (academic simulation).
Simulated resource: Centro Demo (postal code 37001).
```

Para contestar en la terminal, ejecuta `python3 demo.py --interactive`. Introduce un código postal de ejemplo y diez respuestas ficticias (`y` o `n`). Para comprobar solo la compilación, usa `python3 demo.py --compile-only`.

## Qué ocurre durante una solicitud

| Agente | Responsabilidad |
| --- | --- |
| `Client` | Busca el servicio en el directorio de JADE y envía un mensaje `REQUEST` con el recuento y el código postal. |
| `PsycometricService` | Registra el servicio, crea un gestor con un identificador único por solicitud y conserva la respuesta dirigida al cliente. |
| `Manager` | Lanza los dos agentes de trabajo y espera sus mensajes `INFORM`, que pueden llegar en cualquier orden. |
| `PsycometricAgent` | Devuelve el número de respuestas afirmativas sobre diez. |
| `GptAgent` | Devuelve un recurso ficticio asociado al código postal. Por defecto lo genera localmente. |

Cuando llegan ambos resultados, el gestor los combina y el servicio los devuelve al cliente que inició la solicitud. Los cinco agentes se ejecutan dentro de una JVM. El transporte de la demo admite únicamente agentes del contenedor local.

## Dependencias

`dependencias.py` comprueba el SHA-256 de ambos JAR con los valores de la entrega. Si prefieres instalarlos a mano, crea `lib/`, coloca estos dos archivos y ejecuta de nuevo el comando de verificación.

| Archivo en el proyecto | Distribución |
| --- | --- |
| `lib/jade.jar` | Extrae `jade/lib/jade.jar` de **JADE-bin-4.5.0.zip**. [Distribución JADE 4.5](https://jade-project.gitlab.io/page/download/) |
| `lib/json-simple-1.1.1.jar` | [JSON.simple 1.1.1 en Maven Central](https://repo.maven.apache.org/maven2/com/googlecode/json-simple/json-simple/1.1.1/json-simple-1.1.1.jar) |

## Integración opcional con OpenAI

La demo funciona sin cuenta ni llamadas externas una vez instaladas las dependencias. Para probar la variante en línea, define en tu entorno `JADE_ENABLE_OPENAI=1`, `OPENAI_API_KEY` y `OPENAI_MODEL` con un modelo compatible con Chat Completions. El agente envía el código postal de ejemplo para pedir un nombre de hospital inventado. Las respuestas del cuestionario se procesan localmente. El uso de la API puede generar coste.

## Sobre esta versión

La entrega universitaria lanzaba los agentes en consolas de Windows. Esta edición utiliza el contenedor de JADE para crearlos directamente e incorpora un caso reproducible sin credenciales. El recuento y el recurso ficticio sirven para observar los mensajes y la coordinación entre agentes. El proyecto tiene fines docentes y carece de validación clínica.
