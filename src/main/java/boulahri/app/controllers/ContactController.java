package boulahri.app.controllers;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.stream.Collectors;

import boulahri.app.App;
import boulahri.app.dal.ContactDao;
import boulahri.app.models.Contact;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class ContactController {
    @FXML
    private Button addBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button clearBtn;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtTele;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtSearch;
    @FXML
    private TextField txtAddress;
    private int index = 0;

    @FXML
    private TableView contactsTable;
    @FXML
    private TableColumn<Contact, String> nameColumn;
    @FXML
    private TableColumn<Contact, String> emailColumn;
    @FXML
    private TableColumn<Contact, String> phoneColumn;
    @FXML
    private TableColumn<Contact, String> adressColumn;

    private ContactDao contactDao = new ContactDao();
    private Vector<Contact> vContacts = new Vector<Contact>();

    public void importFile() {
        File f = new File(App.class.getResource("contacts.csv").getFile());
        try (FileReader fr = new FileReader(f); BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                vContacts.add(new Contact(new SimpleStringProperty(data[0]), new SimpleStringProperty(data[1]),
                        new SimpleStringProperty(data[2]), new SimpleStringProperty(data[3])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        fillTable(vContacts);
        contactsTable.getSelectionModel().select(index);
        txtEmail.setText(vContacts.getFirst().email().get());
        txtName.setText(vContacts.getFirst().name().get());
        txtAddress.setText(vContacts.getFirst().address().get());
        txtTele.setText(vContacts.getFirst().telephone().get());
    }

    public void fillTable(Vector<Contact> vContacts) {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().name());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().email());
        adressColumn.setCellValueFactory(cellData -> cellData.getValue().address());
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().telephone());

        contactsTable.setItems(FXCollections.observableArrayList(vContacts));
    }

    public void exportToBd() {
        if (!vContacts.isEmpty()) {
            vContacts.forEach(e -> contactDao.add(e));
            new Alert(AlertType.WARNING,"contacts saved in database successfully").show();
        }
        new Alert(AlertType.WARNING,"you don't have any contacts").show();
    }

    public void disableFields(boolean enabled) {
        txtName.setDisable(enabled);
        txtAddress.setDisable(enabled);
        txtEmail.setDisable(enabled);
        txtTele.setDisable(enabled);
    }

    public void handleAdd() {
        if (addBtn.getText().equals("Add")) {
            clear();
            addBtn.setText("Save");
            disableFields(false);
        } else {
            addBtn.setText("Add");
            disableFields(true);
            Contact contact = new Contact(new SimpleStringProperty(txtName.getText()),
                    new SimpleStringProperty(txtTele.getText()), new SimpleStringProperty(txtEmail.getText()),
                    new SimpleStringProperty(txtAddress.getText()));
            index = -1;
            if (!contact.isValidEmail()) {
                new Alert(AlertType.ERROR, "Email invalid").show();
            } else if (checkEmailExist(contact.email().get())) {
                new Alert(AlertType.ERROR, "Email already existe").show();
            } else {
                vContacts.add(contact);
                save(contact);
                contactDao.add(contact);
                fillTable(vContacts);
                new Alert(AlertType.INFORMATION, "Contact added Succefully").show();
                index = vContacts.size() - 2;
            }
            handleNext();
        }
    }

    public boolean checkEmailExist(String email) {
        for (Contact c : vContacts) {
            if (c.email().equals(email)) {
                return true;
            }
        }
        return false;
    }

    public void save(Contact contact) {
        File f = new File(".\\src\\main\\resources\\boulahri\\app\\contacts.csv");
        try (FileWriter fw = new FileWriter(f, true)) {
            fw.append(contact.toString() + System.lineSeparator());
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void clear() {
        txtName.clear();
        txtEmail.clear();
        txtTele.clear();
        txtAddress.clear();
    }

    @FXML
    public void handleDelete() {
        Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete this contact ?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.NO)
            return;
        boolean deleted = false;
        Contact c = new Contact(new SimpleStringProperty(txtName.getText()),
                new SimpleStringProperty(txtTele.getText()), new SimpleStringProperty(txtEmail.getText()),
                new SimpleStringProperty(txtAddress.getText()));
        for (int i = 0; i < vContacts.size(); i++) {
            if (vContacts.get(i).toString().equals(c.toString())) {
                vContacts.remove(i);
                deleted = true;
                break;
            }
        }
        if (deleted) {
            String filePath = ".\\src\\main\\resources\\boulahri\\app\\contacts.csv";
            try (FileWriter fw = new FileWriter(filePath)) {
                for (Contact contact : vContacts) {
                    fw.write(contact.toString() + System.lineSeparator());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            contactDao.delete(c);
            contactsTable.setItems(FXCollections.observableArrayList(vContacts));
        }
        index = vContacts.size() - 2;
        handleNext();
    }

    public void setTexts(int index) {
        txtEmail.setText(vContacts.get(index).email().get());
        txtName.setText(vContacts.get(index).name().get());
        txtAddress.setText(vContacts.get(index).address().get());
        txtTele.setText(vContacts.get(index).telephone().get());
    }

    @FXML
    public void handlePrev() {
        if (!vContacts.isEmpty()) {
            if (index == 0)
                index = vContacts.size() - 1;
            else
                index--;
            contactsTable.getSelectionModel().select(index);
            setTexts(index);
        }
    }

    @FXML
    public void handleNext() {
        if (!vContacts.isEmpty()) {
            if (index == vContacts.size() - 1)
                index = 0;
            else
                index++;
            contactsTable.getSelectionModel().select(index);
            setTexts(index);
        }
    }

    public void showItem(MouseEvent event) {
        Contact c = (Contact) contactsTable.getSelectionModel().getSelectedItem();
        txtEmail.setText(c.email().get());
        txtAddress.setText(c.address().get());
        txtName.setText(c.name().get());
        txtTele.setText(c.telephone().get());
        index = contactsTable.getSelectionModel().getSelectedIndex();
    }

    public void search() {
        Vector<Contact> filterVector = vContacts.stream()
                                                .filter(
                                                    e -> e.email().get().toLowerCase().startsWith(txtSearch.getText().toLowerCase()) || 
                                                    e.name().get().toLowerCase().startsWith(txtSearch.getText().toLowerCase()) ||
                                                    e.telephone().get().toLowerCase().startsWith(txtSearch.getText().toLowerCase()) ||
                                                    e.address().get().toLowerCase().startsWith(txtSearch.getText().toLowerCase()))
                                                .collect(Collectors.toCollection(Vector::new));
        fillTable(filterVector);
    }

}
