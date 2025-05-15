# GRADAWAY - OpenCage Geocoder API Integration

Cette implémentation ajoute une fonctionnalité d'auto-complétion et de validation d'adresses à l'application GRADAWAY en utilisant l'API OpenCage Geocoder.

## Fonctionnalités

- **Auto-complétion d'adresses**: Suggestions dynamiques lors de la saisie de villes et adresses
- **Validation d'adresses**: Vérification qu'une adresse existe réellement
- **Carte interactive**: Sélection directe de l'emplacement sur une carte OpenStreetMap
- **Géocodage bidirectionnel**: Conversion d'adresses en coordonnées et inverse
- **Interface utilisateur intuitive**: Retours visuels et indicateurs de statut

## Configuration de l'API

Pour que l'intégration fonctionne, vous devez obtenir une clé API gratuite depuis OpenCage:

1. Créez un compte sur [OpenCage](https://opencagedata.com/users/sign_up)
2. Obtenez une clé API gratuite (2,500 requêtes/jour)
3. Remplacez la valeur de `API_KEY` dans `src/main/java/utils/OpenCageApiClient.java`

```java
private static final String API_KEY = "VOTRE_CLÉ_API"; // Remplacez par votre clé API
```

## Utilisation

### Méthode 1: Saisie manuelle avec autocomplétion
1. **Saisie d'adresse**: Commencez à taper dans les champs ville ou adresse pour voir apparaître des suggestions
2. **Vérification**: Cliquez sur "Vérifier" pour valider l'adresse complète
3. **Indication visuelle**: Un message vert apparaît si l'adresse est valide, rouge sinon

### Méthode 2: Sélection sur la carte
1. **Navigation**: Naviguez sur la carte en utilisant la souris (zoom avec la molette)
2. **Sélection**: Cliquez à l'endroit souhaité sur la carte
3. **Remplissage automatique**: Les champs ville et adresse se remplissent automatiquement
4. **Validation automatique**: L'adresse est automatiquement marquée comme valide

## Structure des fichiers

- `utils/OpenCageApiClient.java`: Client d'API pour communiquer avec OpenCage
- `utils/JavaConnector.java`: Interface entre JavaFX et JavaScript (WebView)
- `controls/AutocompleteTextField.java`: Champ de texte personnalisé avec auto-complétion
- `models/Universite.java`: Classe modèle pour stocker les données d'université
- `controllers/AjouterUniversiteController.java`: Contrôleur avec intégration de la carte
- `resources/map.html`: Page HTML contenant la carte Leaflet

## Avantages

- **Gratuit**: L'API OpenCage et OpenStreetMap sont gratuites pour une utilisation raisonnable
- **Facilité d'utilisation**: Deux méthodes complémentaires pour saisir des adresses
- **Validation des données**: Réduit les erreurs lors de la saisie d'adresses
- **Expérience utilisateur améliorée**: Interface intuitive avec carte interactive
- **Géocodage**: Stocke les coordonnées pour une utilisation future (cartographie, distance, etc.)

## Notes techniques

- L'application utilise WebView pour intégrer une carte Leaflet (bibliothèque JavaScript)
- La communication entre JavaFX et JavaScript est gérée par JavaConnector
- L'auto-complétion commence après la saisie de 3 caractères minimum
- Les coordonnées sont stockées avec l'université pour une utilisation future
- La carte utilise OpenStreetMap comme fond de carte (gratuit et open source) 