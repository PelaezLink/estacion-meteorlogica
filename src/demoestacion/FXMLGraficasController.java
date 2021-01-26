/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demoestacion;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Slider;
import model.Model;

/**
 * FXML Controller class
 *
 * @author Luis
 */
public class FXMLGraficasController implements Initializable {
    private FXMLDocumentController controladorPrincipal;
    private Model model;
    @FXML
    private LineChart<String, Number> TWDGrafica;
    @FXML
    private LineChart<String, Number> TWSGrafica;
    @FXML
    private Slider slider;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        model = Model.getInstance();
        
        model.setSizeWindChart((int) (slider.getValue() * 60));
        
        //Listener para que al mover el slider se cambie el tamaño del intervalo.
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                model.setSizeWindChart(new_val.intValue() * 60);
                
            }
        });
        
        TWDGrafica.getData().add(model.getTWDSerie());
        TWSGrafica.getData().add(model.getTWSSerie());
        
    }

    public void setControladorPrincipal(FXMLDocumentController c){
        controladorPrincipal = c;
        controladorPrincipal.botonesGrafica();
    }
    
}
