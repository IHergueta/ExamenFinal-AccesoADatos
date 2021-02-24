/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package BeansExamen;

import java.beans.*;
import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 *
 * @author Ignacio
 */
public class ClienteDelMes implements Serializable,PropertyChangeListener {
    
    private String dni;
    private GregorianCalendar fechaLogro;
    private int unidadesTotales;
    private boolean esClienteDelMes;
    
    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";
    
    private PropertyChangeSupport propertySupport;
    
    public ClienteDelMes() {
        propertySupport = new PropertyChangeSupport(this);
    }
    

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("Valor anterior: " + evt.getOldValue());
        System.out.println("Valor nuevo: " + evt.getNewValue());
        setEsClienteDelMes(true);
        }

    
    public ClienteDelMes(String dni) {
        this.dni = dni;
        this.esClienteDelMes = false;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public GregorianCalendar getFechaLogro() {
        return fechaLogro;
    }

    public void setFechaLogro(GregorianCalendar fechaLogro) {
        this.fechaLogro = fechaLogro;
    }

    public int getUnidadesTotales() {
        return unidadesTotales;
    }

    public void setUnidadesTotales(int unidadesTotales) {
        this.unidadesTotales = unidadesTotales;
    }

    public boolean isEsClienteDelMes() {
        return esClienteDelMes;
    }

    public void setEsClienteDelMes(boolean esClienteDelMes) {
        this.esClienteDelMes = esClienteDelMes;
    }
    
    
    
    
}
