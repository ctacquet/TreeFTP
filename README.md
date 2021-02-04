# TreeFTP

Charles TACQUET - Master 1 E-Services (04/02/2021)

## TP Systèmes répartis #1 :

## Introduction

L'objectif du projet est de mettre en œuvre une commande shell permettant d'afficher sur la sortie standard d'un terminal l'arborescence d'un répertoire distant accessible via le protocole applicatif File Transfer Protocol (FTP). 

Le rendu de l'arborescence distante est inspiré du formalisme utilisé la commande tree de Linux.

Cette nouvelle commande tree-ftp prend un argument obligatoire en paramètre: l'adresse du serveur FTP distant. 

Pour tester l'application on fait :

> java -jar TreeFtp.jar ftp.ubuntu.com

(On peut utiliser n'importe quelle adresse IP FTP dans le main mais pour éviter d'attendre trop longtemps avec tous les appels récursifs j'ai fait en sorte que l'on se connecte à un dossier particulier de ftp.ubuntu.com. Actuellement le dossier de commencement est /maas-images/ephemeral)


## Vidéo de présentation de l'application

[![Vidéo youtube](https://1.bp.blogspot.com/-OziQiugEGd0/XVun1YB0p9I/AAAAAAAAzPA/yb-qiHeDyn8Y9HZd3xUNJK8JGAn9pVR1wCLcBGAs/w680/chrome-supprime-ftp-1.jpg)](https://youtu.be/fDvNUvcRGqg)


## Architecture

Le projet contient une classe primaire qui est TreeFTP comportant toutes les commandes liées au serveur FTP.

Les classes Fichier, Dossier et Raccourci permettent de représenter les 3 types de fichiers que le serveur FTP va récupérer.

Pour chaque problème que l'on peut rencontrer on enverra une exception du type IOException avec un message expliquant quelle est le problème et la réponse récupérée.


## Code samples

La méthode d'affichage principal du listing des fichiers est la méthode 'toString' de la classe Dossier.

La classe la plus importante en terme de gestion de sockets et transactions FTP est la classe TreeFTP. Toutes les méthodes ont le même nom que les commandes Linux pour simplifier la lecture.

La méthode ListAll de TreeFTP montre une optimisation du listing de tous les fichiers de façon récursive.


## Diagramme de classes

![Diagramme](https://i.imgur.com/J67ubMA.png)
