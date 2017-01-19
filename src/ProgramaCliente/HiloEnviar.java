package ProgramaCliente;

import Encriptamiento.Cifrado;
import Encriptamiento.StringEncrypt;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HiloEnviar extends Thread {

    private final PrincipalCliente ventanaCliente;
    private ObjectOutputStream salida;
    private String mensaje;
    private Socket conexion;
//Constructor    

    public HiloEnviar(Socket conexion, final PrincipalCliente ventana) {
        this.conexion = conexion;
        this.ventanaCliente = ventana;

//Evento que ocurre al escribir en el areaTexto
        ventanaCliente.ingresoMensaje.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                mensaje = event.getActionCommand();
                String key = "92AE31A79FEEB2A3"; //llave
                String iv = "0123456789ABCDEF"; // vector de inicialización
                String mensajeCifrado = null;
                try {
                    mensajeCifrado = StringEncrypt.encrypt(key, iv, mensaje);
                } catch (Exception ex) {
                    Logger.getLogger(HiloEnviar.class.getName()).log(Level.SEVERE, null, ex);
                }
                mensaje = mensajeCifrado;
                enviarMensaje(mensaje); //se envia el mensaje
                ventanaCliente.ingresoMensaje.setText(""); //el area donde se ingresa el texto se lo borra para poder ingresar el nuevo texto
            }
        });
    }

//enviar objeto a cliente 
    private void enviarMensaje(String mensaje) {
        try {
            //Cifrado msg = new Cifrado();
            salida.writeObject(ventanaCliente.usuario + " dice: " + mensaje);
            salida.flush(); //flush salida a cliente //borra el buffer
            ventanaCliente.mostrarMensaje("YO: " + mensaje);
        } catch (IOException ioException) {
            ventanaCliente.mostrarMensaje("Servidor Perdido");
        }
    }

//manipula areaPantalla en el hilo despachador de eventos
    public void mostrarMensaje(String mensaje) {
        ventanaCliente.pantallaChat.append(mensaje);
    }

    public void run() {
        try {
            salida = new ObjectOutputStream(conexion.getOutputStream());
            salida.flush();
        } catch (SocketException ex) {
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (NullPointerException ex) {
        }
    }
}
