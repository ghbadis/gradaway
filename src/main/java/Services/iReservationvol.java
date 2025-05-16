package Services;

import java.sql.SQLException;
import java.util.List;

public interface iReservationvol <T> {

    void ajouter(T t) throws SQLException;

    void modifier(T t) throws SQLException;

    void supprimer(T t) throws SQLException;

    List<T> recuperer() throws SQLException;
}