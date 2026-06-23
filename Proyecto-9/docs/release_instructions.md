Instrucciones para la Release final-v1.0
======================================

Pasos para crear la etiqueta (tag) y preparar la release:

1. Asegúrese de que todos los cambios deseados están commiteados en la rama principal (por ejemplo `main` o `master`).

```powershell
cd D:\Programacion2\Proyecto-9\Proyecto-9
git status
git add .
git commit -m "Preparando release final-v1.0"
```

2. Crear el tag firmado (opcionalmente firmado con GPG):

```powershell
git tag -a final-v1.0 -m "Release final v1.0 - Sprint 4 completo"
git push origin final-v1.0
```

3. Empaquetar el proyecto (JAR ejecutable) usando Maven:

```powershell
mvn clean package
```

Si desea generar un paquete nativo con JavaFX use el `javafx-maven-plugin` o herramientas como `jpackage`.

4. Adjuntar documentación y artefactos: subir `docs/` al repositorio o a un artefact repository.

Checklist previo al tag
-----------------------
- [ ] Todos los tests pasan (`mvn test`).
- [ ] Manual de Usuario completo y con capturas.
- [ ] Manual Técnico y UML incluidos.
- [ ] CSV/PDF de ejemplo generados.

Fin de las instrucciones de release.

