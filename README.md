# Sistema-Pagos
 Aplicación de gestión de pagos construida con Spring Boot. Permite a los usuarios registrar pagos y gestionar sus transacciones.


Requisitos Previos
- Java JDK 17 o superior
- Maven 3.x
- Cualquier base de datos

Instalación
- Clonar el repositorio:
  git clone https://github.com/tu_usuario/Sistema-Pagos
  cd Sistema-Pagos
- Construir el proyecto:
  Se debe ejecutar el siguiente comando para compilar el proyecto y descargar dependencias.
  mvn clean package.

  
Configuración
- Base de datos:
  Asegúrese de tener su base de datos (PostgreSQL, PGadmin4,...) instalado y ejecutándose.
  Crea una base de datos llamada 'payment_db'
  CREATE DATABASE payment_db;
- Actualiza el archivo 'src/main/resources/application.properties' con la configuración de la base de datos:
  spring.datasource.url=jdbc:postgresql://localhost:5432/payment_db
  spring.datasource.username=tu_usuario
  spring.datasource.password=tu_contraseña
  spring.jpa.hibernate.ddl-auto=update
  spring.jpa.show-sql=true

  
Dependencias
- Asegurese de que las dependencias necesarias estén incluidas e su archivo 'pom.xml', como Spring Web, Spring Data JPA, y el conector de la base de datos correspondiente.

  
Ejecución de la aplicación
- Para ejecutar la aplicación, utilice uno de los siguientes comandos:
  - Ejecutar con un archivo .JAR:
    java -jar target/SistemaPagos-0.0.1-SNAPSHOT.jar
  - O directamente desde Maven:
    mvn spring-boot:run

    
