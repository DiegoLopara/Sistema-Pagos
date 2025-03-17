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
  Asegúrese de tener su base de datos (PostgreSQL, PGadmin4, mySQL...) instalado y ejecutándose.
  Crea una base de datos llamada 'payment_db' e inyecte el siguiente script de creación de tabla
  CREATE DATABASE payment_db;
  CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `card_number` varchar(255) NOT NULL,
  `amount` decimal(19,2) NOT NULL,
  `payment_date` date NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
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

    Para probar la inyección de datos se puede usar postman, insomnia, swagger..., se tiene que introducir este json para el post
    {
    "cardNumber": "1245678123456777",
    "amount": 200.00,
    "paymentDate": "2024-10-01",
    "description": "Payment for order #12345"
variando los datos según se quiera,
el endpoint es:
http://localhost:8080/api/payments
y el tipo de solicitud POST
y en cuanto a recoger todos los números de tarjeta, la solicitud es GET y el endpoint:
http://localhost:8080/api/payments

    
