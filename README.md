# biblioteca-3
# parking-desktop

Proyecto JavaFX (Maven) - Sistema de Parqueadero Comunitario (mock API: json-server)

## Requisitos
- Java 11+
- Maven
- Node/npm (para json-server)

## Ejecutar backend mock
1. Instala json-server:
   npm install -g json-server
2. En la carpeta del proyecto:
   json-server --watch db.json --port 3000

## Ejecutar la aplicación
mvn clean javafx:run

## Estructura
- pom.xml
- src/main/java/com/parqueadero/...
- src/main/resources/fxml/parking_view.fxml
- db.json
- git_history.txt (ejemplo de comandos)
