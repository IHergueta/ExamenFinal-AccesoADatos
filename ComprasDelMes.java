package BeansExamen;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.beans.*;
import java.io.Serializable;

/**
 *
 * @author Ignacio
 */
public class ComprasDelMes implements Serializable {
        
    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";
    
    private String dni;
    private int unidades;

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public int getUnidades() {
        return unidades;
    }

    public void setUnidades(int unidades) {
        int antiguo = this.unidades;
        this.unidades=unidades;
        
        if(unidades>10){
            
            propertySupport.firePropertyChange("stockActual", antiguo, unidades);
        }
        
    }
    
    private PropertyChangeSupport propertySupport;
    
    public ComprasDelMes() {
        propertySupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    public ComprasDelMes(String dni, int unidades) {
        this.dni = dni;
        this.unidades = unidades;
        propertySupport = new PropertyChangeSupport(this);
    }
    
    
}
