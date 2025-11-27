package com.parqueadero.ui;

import com.parqueadero.model.ParkingSpot;
import com.parqueadero.service.ApiClient;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.Optional;

public class ParkingController {

    @FXML private TableView<ParkingSpot> table;
    @FXML private TableColumn<ParkingSpot, String> colCode;
    @FXML private TableColumn<ParkingSpot, Boolean> colOccupied;
    @FXML private TextField txtCode;
    @FXML private TextField txtPlate;
    @FXML private TextField txtOwner;
    @FXML private Button btnCreate;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Label lblStatus;

    private final ApiClient api = new ApiClient();
    private final ObservableList<ParkingSpot> spots = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colCode.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCode()));
        colOccupied.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().isOccupied()));
        table.setItems(spots);
        loadSpots();
        table.setOnMouseClicked(this::onTableClick);
    }

    private void loadSpots() {
        lblStatus.setText("Cargando...");
        new Thread(() -> {
            try {
                List<ParkingSpot> list = api.getSpots();
                Platform.runLater(() -> {
                    spots.setAll(list);
                    lblStatus.setText("Cargado: " + list.size() + " espacios");
                });
            } catch (Exception e) {
                Platform.runLater(() -> lblStatus.setText("Error: " + e.getMessage()));
            }
        }).start();
    }

    private void onTableClick(MouseEvent e) {
        ParkingSpot selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            txtCode.setText(selected.getCode());
            txtOwner.setText(Optional.ofNullable(selected.getOwnerName()).orElse(""));
            txtPlate.setText(Optional.ofNullable(selected.getVehiclePlate()).orElse(""));
            btnCreate.setDisable(true);
            btnUpdate.setDisable(false);
            btnDelete.setDisable(false);
        }
    }

    @FXML
    public void createSpot() {
        if (!validateInputs()) return;
        ParkingSpot p = new ParkingSpot();
        p.setCode(txtCode.getText().trim());
        p.setOwnerName(txtOwner.getText().trim());
        p.setVehiclePlate(txtPlate.getText().trim());
        p.setOccupied(!p.getVehiclePlate().isEmpty());

        lblStatus.setText("Creando...");
        new Thread(() -> {
            try {
                ParkingSpot created = api.createSpot(p);
                Platform.runLater(() -> {
                    spots.add(created);
                    clearForm();
                    lblStatus.setText("Creado con id " + created.getId());
                });
            } catch (Exception ex) {
                Platform.runLater(() -> lblStatus.setText("Error: " + ex.getMessage()));
            }
        }).start();
    }

    @FXML
    public void updateSpot() {
        ParkingSpot selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { lblStatus.setText("Seleccione un registro"); return; }
        if (!validateInputs()) return;

        selected.setCode(txtCode.getText().trim());
        selected.setOwnerName(txtOwner.getText().trim());
        selected.setVehiclePlate(txtPlate.getText().trim());
        selected.setOccupied(!selected.getVehiclePlate().isEmpty());

        lblStatus.setText("Actualizando...");
        new Thread(() -> {
            try {
                ParkingSpot updated = api.updateSpot(selected);
                Platform.runLater(() -> {
                    int idx = spots.indexOf(selected);
                    spots.set(idx, updated);
                    clearForm();
                    lblStatus.setText("Actualizado");
                });
            } catch (Exception ex) {
                Platform.runLater(() -> lblStatus.setText("Error: " + ex.getMessage()));
            }
        }).start();
    }

    @FXML
    public void deleteSpot() {
        ParkingSpot selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { lblStatus.setText("Seleccione un registro"); return; }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Eliminar spot " + selected.getCode() + "?", ButtonType.YES, ButtonType.NO);
        a.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                lblStatus.setText("Eliminando...");
                new Thread(() -> {
                    try {
                        boolean ok = api.deleteSpot(selected.getId());
                        Platform.runLater(() -> {
                            if (ok) {
                                spots.remove(selected);
                                clearForm();
                                lblStatus.setText("Eliminado");
                            } else {
                                lblStatus.setText("No se pudo eliminar");
                            }
                        });
                    } catch (Exception ex) {
                        Platform.runLater(() -> lblStatus.setText("Error: " + ex.getMessage()));
                    }
                }).start();
            }
        });
    }

    private boolean validateInputs() {
        String code = txtCode.getText().trim();
        String plate = txtPlate.getText().trim();
        String owner = txtOwner.getText().trim();

        if (code.isEmpty()) { lblStatus.setText("Código obligatorio"); return false; }
        if (code.length() < 2 || code.length() > 10) { lblStatus.setText("Código inválido"); return false; }
        if (!plate.isEmpty() && plate.length() < 5) { lblStatus.setText("Placa inválida"); return false; }
        if (!owner.isEmpty() && owner.length() < 3) { lblStatus.setText("Nombre propietario muy corto"); return false; }
        return true;
    }

    private void clearForm() {
        txtCode.clear();
        txtOwner.clear();
        txtPlate.clear();
        btnCreate.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
    }
}
