# TreeFTP
## TP Systèmes répartis #1 / Master 1 E-Services :

L'objectif du projet est de mettre en œuvre une commande shell permettant d'afficher sur la sortie standard d'un terminal l'arborescence d'un répertoire distant accessible via le protocole applicatif File Transfer Protocol (FTP). 

Le rendu de l'arborescence distante s'inspirera du formalisme utilisé la commande tree de Linux.

Cette nouvelle commande tree-ftp prend un argument obligatoire en paramètre: l'adresse du serveur FTP distant. 

Le 2e argument—optionnel—permet d'indiquer le nom d'utilisateur à utiliser, le 3e—optionnel également—correspond au mot de passe. Il n'y a aucune autre interaction de l'utilisateur avec la commande qui est nécessaire au delà de l'exécution avec les paramètres décrits ci-dessus. Typiquement, l'utilisateur doit uniquement indiquer :

> java -jar TreeFtp.jar ftp.ubuntu.com
