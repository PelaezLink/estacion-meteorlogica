/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demoestacion;
//enrique

import model.Model;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import net.sf.marineapi.nmea.event.AbstractSentenceListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.sentence.HDGSentence;
import net.sf.marineapi.nmea.sentence.MDASentence;
import net.sf.marineapi.nmea.sentence.MWVSentence;
import net.sf.marineapi.nmea.util.Position;

/**
 *
 * @author jsoler
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private Label ficheroLabel;
    @FXML
    private Label twdLabel;
    @FXML
    private Label twsLabel;
    @FXML
    private Label presionLabel;
    @FXML
    private Label tempLabel;

    private Model model;
    @FXML
    private Button cargar;
    @FXML
    private Button graficas;
    @FXML
    private Button puerto;
    @FXML
    private Button modo;
    @FXML
    private Button apagar;
    @FXML
    private Label fecha;
    @FXML
    private Label hora;
    @FXML
    private BorderPane escenario;
    @FXML
    private VBox centro;
    private boolean modoGrafica;
    private Node hijosCentroUno;
    private Node hijosCentroDos;
    @FXML
    private ImageView imagenBoton;
    private boolean modoDia;
    @FXML
    private ImageView imagenLuz;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        model = Model.getInstance();

        //==================================================================
        // anyadimos listener para que cuando cambie el valor en el modelo 
        //se actualice su valor en su correspondiente representacion grafica
        model.barometricPressureProperty().addListener((a, b, c) -> {
            String dat = String.valueOf(c) + " " + model.getBarometricUnit();
            Platform.runLater(() -> {
                presionLabel.setText(dat);
            });
        });
        model.TEMPProperty().addListener((a, b, c) -> {
            String dat = String.valueOf(c) + " ºC";
            Platform.runLater(() -> {
                tempLabel.setText(dat);
            });
        });

        model.TWDProperty().addListener((a, b, c) -> {
            String dat = String.valueOf(c) + "º";
            Platform.runLater(() -> {
                twdLabel.setText(dat);
            });
        });
        model.TWSProperty().addListener((a, b, c) -> {
            String dat = String.valueOf(c) + "Kn";
            Platform.runLater(() -> {
                twsLabel.setText(dat);
            });
        });

        fecha.setText(LocalDate.now().toString());
        escenario.getStylesheets().add("/demoestacion/estilosDia.css");
        modoGrafica = false;
        modoDia = true;

        //Codigo para hacer el reloj que se actualiza:
        //Fuente: https://stackoverflow.com/questions/38566638/javafx-displaying-time-and-refresh-in-every-second/38567319
        Thread timerThread = new Thread(() -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final String time = simpleDateFormat.format(new Date());
                Platform.runLater(() -> {
                    hora.setText(time);
                });
            }
        });
        timerThread.start();

    }

    @FXML
    private void cargarFichero(ActionEvent event) throws FileNotFoundException {
        FileChooser ficheroChooser = new FileChooser();
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        ficheroChooser.setInitialDirectory(new File(currentPath));
        ficheroChooser.getExtensionFilters().add(new ExtensionFilter("ficheros NMEA", "*.NMEA"));

//        ficheroChooser.setSelectedExtensionFilter(new ExtensionFilter("ficheros NMEA", "*.NMEA"));
        ficheroChooser.setTitle("fichero datos NMEA");

        File ficheroNMEA = ficheroChooser.showOpenDialog(ficheroLabel.getScene().getWindow());
        if (ficheroNMEA != null) {
            // ========================================================
            // NO se comprueba que se trata de un fichero de datos NMEA
            // esto es una demos
            ficheroLabel.setText(ficheroNMEA.getName());
            // ========================================================
            // se pone en marcha el proceso para recibir tramas
            model.addSentenceReader(ficheroNMEA);
        }
    }

    @FXML
    private void apagarApp(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void conectarEstacion(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Estación.");
        alert.setHeaderText("Conecte una estación.");
        alert.setContentText("Debe conectar una estación al puerto primero para acceder a esta funcionalidad");
        alert.showAndWait();

    }

    @FXML
    private void abrirGraficas(ActionEvent event) throws IOException {
        if(!modoGrafica){
        hijosCentroUno = centro.getChildren().get(0);
        hijosCentroDos = centro.getChildren().get(1);
        centro.getChildren().clear();
        FXMLLoader FXMLGraficas = new FXMLLoader(getClass().getResource("/demoestacion/FXMLGraficas.fxml"));
        centro.getChildren().add(FXMLGraficas.load());
        FXMLGraficasController controladorGraficas = FXMLGraficas.getController();
        controladorGraficas.setControladorPrincipal(this);
        Image imagen = new Image("/imagenes/volver.png");
        imagenBoton.setImage(imagen);
        }
        
        else{
        centro.getChildren().clear();
        centro.getChildren().add(hijosCentroUno);
        centro.getChildren().add(hijosCentroDos);
        puerto.setDisable(false);
        cargar.setDisable(false);
        modoGrafica = false;
        Image imagen = new Image("/imagenes/graficaN.png");
        imagenBoton.setImage(imagen);
        
        }
    }
    
    public void botonesGrafica(){
        puerto.setDisable(true);
        cargar.setDisable(true);
        modoGrafica = true;
    }

    @FXML
    private void cambiarModo(ActionEvent event) {

        if (modoDia) {
            escenario.getStylesheets().clear();
            escenario.getStylesheets().add("/demoestacion/estilosNoche.css");
            Image imagen = new Image("/imagenes/sol.png");
            imagenLuz.setImage(imagen);
        }
        else{
            escenario.getStylesheets().clear();
            escenario.getStylesheets().add("/demoestacion/estilosDia.css");
            Image imagen = new Image("/imagenes/luna.png");
            imagenLuz.setImage(imagen);
        }   
        modoDia = !modoDia;

    }



}
