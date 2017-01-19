package ProgrmaSerivdor;

import Encriptamiento.Cifrado;
import Encriptamiento.StringEncrypt;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HiloEnviar extends Thread {

    private final PrincipalServidor ventanaServidor;
    private ObjectOutputStream salida;
    private String mensaje;
    private Socket conexion;

//Constructor 
    public HiloEnviar(Socket conexion, final PrincipalServidor ventana) {
        this.conexion = conexion;
        this.ventanaServidor = ventana;

//Evento que ocurre al escribir en el areaTexto
        ventanaServidor.ingresoMensaje.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                mensaje = event.getActionCommand();
                String key = "92AE31A79FEEB2A3"; //llave
                String iv = "0123456789ABCDEF"; // vector de inicialización
                String mensajeCifrado = null;
                try {
                    mensajeCifrado = StringEncrypt.encrypt(key, iv, mensaje);
                } catch (Exception ex) {
                    Logger.getLogger(ProgramaCliente.HiloEnviar.class.getName()).log(Level.SEVERE, null, ex);
                }
                mensaje = mensajeCifrado;
                enviarMensaje(mensaje); //se envia el mensaje
                ventanaServidor.ingresoMensaje.setText(""); //el area donde se ingresa el texto se lo borra para poder ingresar el nuevo texto
            }
        });
    }

//enviar objeto a cliente 
    private void enviarMensaje(String mensaje) {
        try {
            salida.writeObject(ventanaServidor.usuario + " dice: " + mensaje);
            salida.flush(); //flush salida a cliente //borra el buffer
            ventanaServidor.mostrarMensaje("YO: " + mensaje);
        } catch (IOException ioException) {
            ventanaServidor.mostrarMensaje("Cliente perdido");
        }
    }

//manipula areaPantalla en el hilo despachador de eventos
    public void mostrarMensaje(String mensaje) {
        ventanaServidor.pantallaChat.append(mensaje);
    }

    public void run() {
        try {
            salida = new ObjectOutputStream(conexion.getOutputStream());
            salida.flush(); //flush salida a cliente //borra el buffer
        } catch (SocketException ex) {
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (NullPointerException ex) {
        }
    }
}
