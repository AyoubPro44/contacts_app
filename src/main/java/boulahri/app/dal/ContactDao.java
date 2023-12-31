package boulahri.app.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import boulahri.app.models.Contact;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ContactDao implements IDao <Contact>{
    private Connection cnx = DatabaseConnection.getConnection();
    private Statement stm;
    private PreparedStatement ps;
    private ResultSet rs;

    @Override
    public void delete(Contact contact) {
        try {
            ps = cnx.prepareStatement("DELETE FROM contact where email=?");
            ps.setString(1, contact.email().get());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void add(Contact contact) {
        try {
            if(!search(contact)){
                ps = cnx.prepareStatement("insert into contact values(?,?,?,?)");
                ps.setString(1, contact.email().get());
                ps.setString(2, contact.name().get());
                ps.setString(3, contact.telephone().get());
                ps.setString(4, contact.address().get());
                ps.executeUpdate();
            }
            else {
                update(contact, contact);
            }
        } catch (SQLException e) {}
    }

    public boolean search(Contact contact) {
        try {
            ps = cnx.prepareStatement("select * from contact where email = ?");
            ps.setString(1, contact.email().get());
            rs = ps.executeQuery();
            if(rs.next())
                return true;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }
    
    @Override
    public void update(Contact contact, Contact newContact) {
        try {
            ps = cnx.prepareStatement("update contact set name = ?, telephone = ?, address = ? where email = ?");
            ps.setString(1, newContact.name().get());
            ps.setString(2, newContact.telephone().get());
            ps.setString(3, newContact.address().get());
            ps.setString(4, contact.email().get());
            ps.executeUpdate();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public Vector<Contact> getAll() {
        Vector<Contact> vContacts = new Vector<Contact>();
        try {
            stm = cnx.createStatement();
            rs = stm.executeQuery("SELECT * FROM contact");
            while (rs.next()) {
                vContacts.add(new Contact(new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleStringProperty(rs.getString(1)), new SimpleStringProperty(rs.getString(4))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vContacts;
    }
}
