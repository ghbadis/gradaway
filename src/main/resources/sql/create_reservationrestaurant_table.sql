-- Script pour créer la table reservationrestaurant dans la base de données gradaway

-- Suppression de la table si elle existe déjà pour éviter les conflits
DROP TABLE IF EXISTS `reservationrestaurant`;

CREATE TABLE IF NOT EXISTS `reservationrestaurant` (
  `idReservation` int(11) NOT NULL AUTO_INCREMENT,
  `idRestaurant` int(11) NOT NULL,
  `IdEtudient` int(11) NOT NULL,
  `dateReservation` date NOT NULL,
  `nombrePersonne` int(11) NOT NULL,
  PRIMARY KEY (`idReservation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Note: Nous avons supprimé les clés étrangères pour éviter les erreurs de contrainte
-- Vous pourrez les ajouter plus tard si nécessaire
