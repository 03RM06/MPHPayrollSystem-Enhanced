package DAO;
 
import java.sql.SQLException;
import java.util.List;

public interface DataAccessObject<T> {
 
    List<T> findAll()                                   throws SQLException;
    T       findById(String id)                         throws SQLException;
    boolean create(T entity)                            throws SQLException;
    boolean update(T entity)                            throws SQLException;
    boolean delete(int id)                              throws SQLException;
 
    /** Optional: override in DAOs that need employee-scoped queries. */
    default List<T> findByEmployee(String employeeId)   throws SQLException {
        return null;
    }
}
