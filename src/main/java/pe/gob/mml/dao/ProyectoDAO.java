package pe.gob.mml.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pe.gob.mml.modelo.Proyecto;
import pe.gob.mml.util.Conexion;

/** Acceso a datos de la tabla "proyecto". */
public class ProyectoDAO {

    public List<Proyecto> listar() throws SQLException {
        List<Proyecto> lista = new ArrayList<>();
        String sql = "SELECT * FROM proyecto ORDER BY id";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Proyecto buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM proyecto WHERE id = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public boolean registrar(Proyecto p) throws SQLException {
        String sql = "INSERT INTO proyecto (nombre, descripcion, fecha_inicio, fecha_fin, "
                   + "asesor_tecnico, presupuesto) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            llenarParametros(ps, p);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Proyecto p) throws SQLException {
        String sql = "UPDATE proyecto SET nombre = ?, descripcion = ?, fecha_inicio = ?, "
                   + "fecha_fin = ?, asesor_tecnico = ?, presupuesto = ? WHERE id = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            llenarParametros(ps, p);
            ps.setInt(7, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM proyecto WHERE id = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private void llenarParametros(PreparedStatement ps, Proyecto p) throws SQLException {
        ps.setString(1, p.getNombre());
        ps.setString(2, p.getDescripcion());
        ps.setDate(3, Date.valueOf(p.getFechaInicio()));
        ps.setDate(4, Date.valueOf(p.getFechaFin()));
        ps.setString(5, p.getAsesorTecnico());
        ps.setBigDecimal(6, p.getPresupuesto());
    }

    private Proyecto mapear(ResultSet rs) throws SQLException {
        return new Proyecto(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getDate("fecha_inicio").toLocalDate(),
                rs.getDate("fecha_fin").toLocalDate(),
                rs.getString("asesor_tecnico"),
                rs.getBigDecimal("presupuesto"));
    }
}
