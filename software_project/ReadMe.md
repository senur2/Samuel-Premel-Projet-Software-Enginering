 Projet Bit Packing – Compression d'entiers

Samuel Premel  

---

## Description

Ce projet implémente plusieurs algorithmes de **compression d'entiers** en Java basés sur la technique du **Bit Packing**.  
L’objectif est de comparer différentes méthodes : `NO_CROSS`, `CROSS` et `OVERFLOW`.

---

## Prérequis

- Java 17 ou plus récent  
- Maven 3.8 ou plus récent  

---

## Compilation 

Depuis le dossier du projet (où se trouve `pom.xml`), exécuter :

```bash
mvn clean package
```

## Usage

Executer cette commande a la source du projet pour voir les different usage
```bash 
java -jar target/software_project-1.0-SNAPSHOT.jar 
```
exemple de commande pour tester le fichiet Test.txt: 

```bash
 java -jar target/software_project-1.0-SNAPSHOT.jar compress cross Test.txt
 ```
