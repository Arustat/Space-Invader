# Space Invader
# V1 

# Modification du Jeu JAVA

## **Partie Vue**

  

### **EntreeJeu**

  

Mayssa : 

J'ai modifié la classe EntreeJeu pour améliorer l'interface d'entrée dans le jeu. J'ai ajouté un arrière-plan en image via un JLayeredPane qui gère plusieurs couches. L'image de fond est positionnée en bas (niveau 0), et les éléments graphiques (panneau de contenu, boutons) se trouvent au-dessus (niveau 1). La taille de la fenêtre a été définie à 800 x 700 pixels et est automatiquement centrée sur l'écran. Le mode plein écran a été désactivé pour une gestion plus facile de l'interface graphique.

###   

### **Layer**

  

Mayssa: 

J'ai créé la classe Layer pour gérer le défilement d'un fond ou d'un élément graphique à l'écran. Cette classe permet de déplacer une image verticalement à une certaine vitesse. L'image se déplace vers le bas et lorsqu'elle atteint la fin de l'écran, elle est réinitialisée en haut pour créer un effet de défilement continu. La méthode update met à jour la position de l'image, tandis que la méthode draw dessine l'image à sa position actuelle et à celle de l'image suivante pour un défilement fluide.

  

**ParallaxPanel**

  

Mayssa : 

J'ai ajouté la classe ParallaxPanel pour gérer le défilement parallax dans l'interface graphique du jeu. Cette classe utilise plusieurs couches (Layer) qui défilent à des vitesses différentes pour créer un effet de profondeur visuelle. Les couches sont stockées dans un ArrayList et sont dessinées par la méthode paintComponent. Le panneau permet de rendre visible les éléments derrière, grâce à l'appel de setOpaque(false).

  

**Background**

  

Mayssa : 

J'ai ajouté la classe Background pour gérer l'affichage d'une image en tant que fond d'écran dans l'interface du jeu. L'image de fond est chargée à partir d'un fichier via ImageIO.read, et est dessinée dans la méthode paintComponent, ajustée pour s'adapter à la taille du panneau. La disposition du panneau est définie manuellement avec setLayout(null) pour un contrôle précis de l'affichage.

  

**Classe Arene :**

  

Mayssa : 

J'ai ajouté un système de Parallax Background dans l'arène. Trois couches d'arrière-plan ont été créées avec des vitesses de défilement différentes pour donner un effet de profondeur. Ces couches sont chargées via des images (0.png, 1.png, 2.png) et affichées grâce à la classe ParallaxPanel. Un Timer met à jour les couches toutes les 20 millisecondes pour un défilement fluide. Le panneau ParallaxPanel a été ajouté au contentPane pour afficher ces couches parallaxes derrière les autres éléments de l'interface comme les murs, les joueurs et le chat.

  

Problème la méthode ajoutModifJoueur -> supprimer un composant à une position num dans un panneau (JpnJeu) seulement si le composant n’existe pas encore à cette position on obtient une erreur. _Mais en Java,_ **_toutes les modifications d’interface graphique (Swing)_** _doivent être faites dans le_ **_thread graphique principal_** _(appelé Event Dispatch Thread, ou EDT). Sinon, tu as des comportements bizarres ou des crashs._

  

J’ai enveloppé le code dans un SwingUtilities.invokeLater afin que les modifications soient exécutées dans le thread de l’interface graphique (ce qui est obligatoire en Swing). J’ai aussi ajouté une condition pour vérifier que l’index existe avant de tenter de retirer un composant, évitant ainsi une erreur ArrayIndexOutOfBoundsException. Enfin, j’ai ajouté revalidate() et repaint() pour forcer la mise à jour visuelle du panneau après les changements.

  

J'ai amélioré la gestion des barres de vie dans mon interface graphique. Le problème initial était que les barres de vie n'étaient pas correctement affichées côté client. Dans ma méthode ajoutModifJoueur, les barres de vie étaient simplement ajoutées au panel sans tenir compte de leur position relative au joueur. J'ai donc modifié cette méthode pour rechercher si une barre de vie existe déjà pour le joueur concerné, et la remplacer si nécessaire plutôt que d'en créer une nouvelle à chaque fois. Cette optimisation me permet d'éviter la duplication des barres de vie et assure qu'elles restent bien attachées à leur joueur.

  

J'ai modifié la méthode ajoutEnemy() pour améliorer la gestion des composants graphiques. J'ai conservé la vérification des doublons mais j'ai séparé explicitement les opérations de dimensionnement et de positionnement. Au lieu d'utiliser setBounds() qui pouvait causer des problèmes, j'utilise maintenant setSize() pour fixer les dimensions puis setLocation() pour le positionnement, ce qui évite l'effet d'étalement observé précédemment.

  

Antonin :

J’ai ajouté le JPanel Enemy dans le constructeur de Arène afin de  set les dimensions et l’affichage des sprites qui serviront dans les vagues par la suite. 

  

j’ai aussi créé la fonction ajoutEnemy() qui vérifie si un ennemi a été ajouté dans le panel et si non alors il ajoute l'ennemi. j’ai aussi enveloppé cette partie dans un SwingUtilities.invokeLater pour qu’il s'exécute sur threat graphique principale ( comme l’a fait mayssa plus haut. J’ai aussi créé un nouveau JPanel pour Ennemi afin qu’il puisse s’afficher mais aussi, comme on avait pu le faire pour mur j’ai fait une fonction AjoutPanelEnemy() pour ajouter tous les ennemis d’un seul coup. 

  

j’ai aussi créer le JPanel pour le score ainsi que la fonction updateScore qui récupère le score et qui l’affiche sur la scène. 

  

### **Enemy.java** 

  

Mayssa:

J'ai amélioré la méthode updateSprite() pour qu'elle gère mieux les images et leur affichage. J'ai remplacé la simple création d'une ImageIcon par une approche plus complète où je définis explicitement la description de l'image pour faciliter son identification. J'ai également ajouté des appels à setBounds() et setSize() pour s'assurer que le label conserve toujours les bonnes dimensions (L\_PERSO, H\_PERSO) pendant l'animation. Dans la méthode move(), j'ai aussi modifié la gestion de la position pour utiliser des dimensions constantes au lieu des dimensions variables du label, ce qui contribue à éliminer l'effet d'étalement visuel lorsque les ennemis se déplacent.

  

Ajout d'attributs JeuServeur et Explosion

  

Modification du constructeur pour accepter une référence au JeuServeur

  

Mise à jour de takeDamage() pour créer et afficher une explosion à la mort

  

Antonin : 

J'ai créé la classe Enemy avec dans le constructeur un switch case pour gérer les 3 différents types d’ennemis ( un ennemi rapide, un normal et un avec plus de point de vie ) mais aussi la création du label de l’ennemi ( comme on avait pu le faire avec les murs ). 

Pour ce qui est de l’animation je me suis renseigné et j’ai utilisé Timer et la méthode scheduleAtFixedRate pour que l’animation ne soit pas dépendante du temps de rafraîchissement de l’écran. J’ai couplé ça avec une fonction updateSprite() qui si le label existe et qui gère ensuite l’emplacement des sprites et qui les applique au jlabel.

  

j’ai écrit la fonction move qui gère le déplacement des ennemis de façon à ce qu’il avance en ligne tout en récupérant la valeur speed qui est attribuée à l'ennemi qui est instancié. 

  

j’ai créé la fonction takeDamage() pour gérer la vie des ennemies et passe une variable isAlive à false pour dire que notre ennemi est mort si sa vie passe a 0. Cela va de pair avec la fonction isAlive qui vérifie si la variable isAlive est a false, si oui alors la fonction retourne false. elle regarde aussi si le label et si le jlabel sont différents de nulle alors on regarde si les éléments graphique sont visibles avec label.getjLabel().isVisible(); et à la fin si on est rentré dans aucune structure de contrôle on retourne isAlive.

  

j’ai aussi créé deux getteur, un pour les points données par les ennemies et un pour le type d’ennemi.  

###   

### **EnemyData.java :** 

  

Mayssa:

J'ai créé cette nouvelle classe sérialisable qui contient uniquement les données essentielles d'un ennemi: position X et Y, type d'ennemi, frame d'animation actuelle et un identifiant unique. Cette classe sert de conteneur léger et fiable pour la transmission réseau, évitant les problèmes de sérialisation des composants graphiques. J'ai implémenté un constructeur qui prend un objet Enemy et en extrait les données, ainsi que des getters pour accéder à ces données côté client.

###   

### **WaveManager**

###   

### Mayssa: 

Ajout d'un attribut JeuServeur

  

Modification du constructeur pour accepter une référence au JeuServeur

  

Mise à jour de la méthode update() pour passer la référence JeuServeur aux nouveaux ennemis

  

Antonin : 

j’ai créé la classe WaveManager avec dans le constructeur toutes les caractéristiques des waves d’ennemies  avec en plus une variable jeuServeur pour ensuite pouvoir faire le lien et les afficher en jeu. Avec après ça une fonction pour démarrer une nouvelle wave ( startNewWave()) qui augmente de 2 a chaque fois le nombre d’ennemies. 

  

j’ai créer la fonction update() qui va regarde si taille de la wave en cours est inférieur au nombre d’ennemie par wave et si c’est le cas alors on choisi aléatoirement un nombre entre 1 et 3 et on instancie un ennemie de de type Enemy avec en paramètre le type et sa position. Après ça on actualise les ennemis de la vague avec une boucle for qui va regarder chaque instance de la wave et qui va vérifier si l’ennemi est vivant puis qui va mettre à jour sa position et appeler la fonction checkPlayerCollision(). Pour finir on va supprimer les ennemies qui sortent du cadre de l’arène pour éviter de consommer des performances. 

  

la fonction checkPlayerCollision() quant à elle, vérifie d’abord si l’ennemi est en vie puis avec une boucle for on vérifie pour tout les joueurs dans l’arène que si la collision est détecté et que les le joueur n’est pas mort alors on lui retire de la vie ( 1 point de vie pour les petit ennemies et 2 pour le type avec le plus de vie ). On fait perdre des points de vie à l'ennemi aussi, on joue le son qui indique que le joueur est blessé puis on vérifie s’il est mort. Si oui alors on lance l’animation d’explosion et on lance l’écran de Game Over. 

  

Pour finir j’ai implémenter des guetteurs pour récupérer la wave en cours, le nombre d’ennemi dans la wave en cours, et elle récupère le nombre d’ennemi qui sont apparus à partir du délai d’apparition de ceux- ci. 

  

## **Partie Asset**

  

Mayssa : 

J'ai intégré un fond personnalisé pour la fenêtre du jeu. L'image est affichée en arrière-plan, offrant ainsi un design visuel plus attrayant et immersif. Ainsi que de nouveaux sprites joueurs et ennemis. 

  

## **Partie Controle:**

### **Global**

Mayssa : 

J'ai ajusté les propriétés globales pour gérer la nouvelle taille de fenêtre et l'ajout de l'arrière-plan. Les boutons sont désormais centrés et l'espace entre les éléments a été redéfini pour un alignement optimal.

  

### **Controle**

Mayssa:

  

Dans ma classe centrale de contrôle, j'ai ajouté une méthode isFull() qui délègue la vérification à JeuServeur. J'ai également corrigé un problème où les événements étaient envoyés à frmArene avant que celle-ci ne soit initialisée, en ajoutant une vérification null dans la méthode evenementJeuClient. Enfin, pour permettre exactement deux joueurs, j'ai modifié l'appel à isFull() pour utiliser isFull(false) plutôt que isFull(true), ce qui permet au deuxième joueur de se connecter tout en bloquant le troisième.

  

Antonin : 

j’ai rajouté une boucle elseif dans evenementJeuServeur() pour vérifier l’envoi d’un ordre “ajout enemy” afin d’ajouter les ennemies dans l’arène côté serveur. j’ai fait la même chose avec la fonction evenementJeuClient(). 

  

j’ai aussi rajouté une boucle elseif dans evenementJeuServeur() etevenementJeuClient() pour  mettre a jour le score en récupérants le score qu’on lui envoie et en l’envoyant à frmArene avec updateScore().

  

  

  

  

## **Partie Outils.connexion:**

  

### **Classe Connection**

  

Mayssa :

J'ai ajouté une méthode getSocket() dans la classe Connection. Cette méthode permet de récupérer l'objet Socket associé à une connexion. Pour ce faire, j'ai d'abord stocké le Socket passé au constructeur dans une variable d'instance. Ensuite, j'ai créé la méthode getSocket() qui retourne cet objet. Cela permet d'accéder facilement au Socket depuis d'autres parties du code.

  

Pour résoudre des problèmes de NullPointerException, j'ai sécurisé ma classe Connection en ajoutant des vérifications sur le récepteur null. Dans le constructeur, je vérifie désormais si leRecepteur est null avant d'appeler setConnection. De même, dans la méthode run(), j'ai ajouté une vérification au début pour éviter d'exécuter la boucle d'écoute si leRecepteur est null. Ces modifications me permettent d'utiliser temporairement une connexion avec un récepteur null pour envoyer des messages comme "Server is full" sans provoquer d'erreurs.

  

### **Classe ServeurSocket**

  

Mayssa:

Pour la gestion des connexions serveur, j'ai corrigé plusieurs problèmes. D'abord, j'ai résolu une erreur où la méthode deconnexion() n'existait pas en utilisant à la place la fermeture directe du socket avec getSocket().close(). Ensuite, pour éviter l'avertissement "New instance ignored", j'ai stocké l'instance de Connection dans une variable locale avec l'annotation @SuppressWarnings("unused"). J'ai également implémenté une vérification si le serveur est plein avant d'accepter une nouvelle connexion, en utilisant la méthode isFull() du contrôleur.

  

## **Partie Font:**

  

Mayssa : J’ai ajouté des font spéciales pour un rendu unique et rétro.

  

## **Partie Modele:**

###   

### **Classe Joueur**

  

Mayssa: 

Pour la classe joueur, j’ai modifié l’implémentation du joueur, la variable global MARCHE n’existe plus puisque nous n’avons pas assez de sprites pour la mort (à voir pour après). Pour cela, j’ai remodelé la fonction affiche qui prenait anciennement en paramètre la variable etat qui fait référence à l’état d’un sprite.

Au niveau de l’animation, j’ai créé une méthode public qui me permet d’animer mon sprite dans une durée de 200 ms pour un jolie rendue. Ensuite, j’appelle la méthode public dans l’initialisation du Joueur mais à la fin de la méthode.

  

J'ai apporté plusieurs améliorations à la gestion des barres de vie dans ma classe Joueur. Initialement, une nouvelle instance de Label contenant la barre de vie était créée à chaque mise à jour, ce qui causait des problèmes de synchronisation. J'ai ajouté une propriété healthBarLabel pour stocker la référence au Label de la barre de vie, et modifié les méthodes initPerso et affiche pour utiliser cette référence unique. De plus, j'ai mis à jour la méthode departJoueur pour utiliser cette même référence lors de la déconnexion. Ces modifications garantissent que la barre de vie reste associée au joueur et se déplace correctement avec lui.

  

Mise à jour de departJoueur() pour utiliser le nouveau constructeur d'Explosion avec JeuServeur

Suppression de l'envoi redondant de l'objet explosion (maintenant géré dans la classe Explosion)

  

  

**Classe JeuServeur**

  

Mayssa :

J'ai modifié la classe JeuServeur pour limiter le nombre de joueurs connectés à deux. J'ai ajouté une constante MAX\_PLAYERS pour définir cette limite. Ensuite, j'ai modifié la méthode setConnection pour vérifier si le nombre de joueurs actifs est inférieur à MAX\_PLAYERS avant d'ajouter un nouveau joueur. Si la limite est atteinte, la connexion est refusée et fermée, et un message est envoyé au client pour l'informer que le serveur est plein. J'ai également mis à jour la méthode deconnection pour retirer le joueur de la liste lesJoueursDansLordre lorsqu'il se déconnecte.

  

Pour limiter le nombre de joueurs à deux, j'ai implémenté plusieurs mécanismes dans JeuServeur. Ma classe disposait déjà d'une constante MAX\_PLAYERS = 2, mais la vérification n'était pas complète. J'ai ajouté une méthode isFull() simple et une version plus élaborée isFull(boolean) qui peut prendre en compte un joueur supplémentaire potentiel. J'ai également modifié la méthode setConnection pour refuser les connexions si le serveur est plein, en envoyant un message explicite avant de fermer la connexion. Enfin, dans la méthode réception, j'ai ajouté une vérification pour s'assurer que le joueur existe dans la collection avant de traiter les messages.

  

J'ai ajouté un compteur nextEnemyId et une HashMap enemiesById pour suivre les ennemis actifs. Dans la méthode d'update, je vérifie si un ennemi possède déjà un ID, sinon je lui en attribue un nouveau. J'ai remplacé l'envoi direct des labels par la création d'objets EnemyData contenant les informations nécessaires. J'ai également ajouté un système de nettoyage qui supprime les ennemis morts ou disparus de la HashMap pour éviter les fuites de mémoire.

  

Modification de l'initialisation du WaveManager pour lui passer une référence this

  

Antonin : 

J’ai modifié la classe en initialisant les waves dans le constructeur et en initialisant le rang du dernier label mémoriser avec Label.setNbLabel(0). J’ai réutilisé Timer pour gérer l’apparition de mes waves et géré le rafraîchissement. Ici la fonction Timer s'exécute toutes les 16 millisecondes soit 60 fois par seconde (60 fps).  Ensuite on récupère tous les ennemis de la vague et on les envoie à contrôle avec evenementModele() et on les envoie au client avec une boucle for qui, pour tous les joueurs, envoie les ennemis.

  

### **Classe Boule**

  

Mayssa:

J'ai résolu un problème où une nouvelle instance d'Attaque était créée mais immédiatement ignorée par le garbage collector, ce qui empêchait l'attaque de fonctionner correctement. Dans ma méthode tireBoule, j'ai modifié le code pour stocker l'instance de la classe Attaque dans une variable membre attaque plutôt que de la créer sans référence. Cette modification me permet de maintenir l'instance en vie pendant toute la durée de l'attaque.

  

### **Classe Attaque**

###   

### Mayssa:

### Mise à jour de la méthode run() pour utiliser le nouveau constructeur d'Explosion avec JeuServeur

###   

### **Classe JeuClient**

  

Mayssa :

J'ai modifié la méthode reception dans la classe JeuClient pour gérer le cas où le serveur est plein. Si le message reçu indique que le serveur est plein, une boîte de dialogue s'affiche pour informer l'utilisateur qu'il ne peut pas se connecter. Ensuite, l'application se ferme automatiquement. Cela permet de gérer les tentatives de connexion lorsque le serveur a atteint sa capacité maximale de joueurs.

  

Dans ma classe de gestion du jeu côté client, j'ai amélioré la reconnaissance des différents types de labels. Initialement, tous les labels étaient traités de la même manière, ce qui causait des problèmes avec les barres de vie. J'ai modifié la méthode reception pour identifier spécifiquement les labels de type HealthBar et leur appliquer un traitement approprié avec l'événement "ajout healthbar" plutôt que "ajout joueur".

  

J'ai ajouté une HashMap enemyLabels pour conserver les JLabels créés pour chaque ennemi. Dans la méthode reception(), j'ai implémenté une nouvelle logique qui détecte les objets EnemyData, vérifie si un label existe déjà pour cet ID, et le met à jour ou en crée un nouveau si nécessaire. Je m'assure que chaque label a la bonne position, taille et image avant de l'envoyer à l'arène pour affichage, en utilisant le chemin d'image correctement construit.

  

Antonin : 

j’ai rajouter une boucle qui reçoit le message avec le score et avec ça il va le separé pour isolé le nombre du score et l’envoyer à evenementModèle().

  

### **Explosion**

  

Ajout d'un constructeur supplémentaire prenant une référence au JeuServeur

Ajout de l'incrémentation du compteur de labels (Label.setNbLabel)

Intégration d'envois réseau dans startAnimation() pour synchroniser l'animation sur tous les clients