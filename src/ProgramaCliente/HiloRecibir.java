package ProgramaCliente;

import Encriptamiento.Cifrado;
import Encriptamiento.StringEncrypt;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.logging.*;

public class HiloRecibir extends Thread {

    private final PrincipalCliente ventanaCliente;
    private String mensaje;
    private ObjectInputStream entrada;
    private Socket cliente;

//Constructor del Hilo
    public HiloRecibir(Socket cliente, PrincipalCliente ventana) {
        this.cliente = cliente;
        this.ventanaCliente = ventana;
         //Accion que se realiza Salir
        ventanaCliente.desencriptar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String key = "92AE31A79FEEB2A3"; //llave
                    String iv = "0123456789ABCDEF"; // vector de inicialización
                    System.out.println(mensaje);
                    ventanaCliente.pantallaChat.append(StringEncrypt.decrypt(key, iv, mensaje) + '\n');
                    //System.exit(0); //Sale de la aplicacion
                    //ventanaCliente.pantallaChat.append(mensaje);
                } catch (Exception ex) {
                    Logger.getLogger(HiloRecibir.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
       
    }
//metodo para mostrar el mensaje

    public void mostrarMensaje(final String mensaje) {
         //Accion que se realiza Salir
        ventanaCliente.desencriptar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Cifrado descifrado = new Cifrado();
                descifrado.addKey("programacion");
                ventanaCliente.pantallaChat.append(descifrado.desencriptar(mensaje));
                //System.exit(0); //Sale de la aplicacion
            }
        });
        ventanaCliente.pantallaChat.append(mensaje);
    }

    public void run() {
        try {
            entrada = new ObjectInputStream(cliente.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(HiloRecibir.class.getName()).log(Level.SEVERE, null, ex);
        }

        do {
//leer el mensaje y mostrarlo
            try {
                mensaje = (String) entrada.readObject();
                mensaje = mensaje.substring(mensaje.indexOf(" dice: ") + " dice: ".length(), mensaje.length());
                ventanaCliente.mostrarMensaje(mensaje);
            } catch (SocketException ex) {
            } catch (EOFException eofException) {
                ventanaCliente.mostrarMensaje("Conexion Servidor Perdida");
                mensaje ="xxxx";
            } catch (IOException ex) {
                Logger.getLogger(HiloRecibir.class.getName()).log(Level.SEVERE, null, ex);
                ventanaCliente.mostrarMensaje("Conexion Servidor Perdida");
                mensaje ="xxxx";
            } catch (ClassNotFoundException classNotFoundException) {
                ventanaCliente.mostrarMensaje("Objeto desconocido");
                mensaje ="xxxx";
            }

        } while (!mensaje.equals("xxxx")); //Ejecuta hasta que el server escriba TERMINATE

        try {
            entrada.close();//cierra la entrada
            cliente.close();//cierra el socket
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        ventanaCliente.mostrarMensaje("Fin de la conexion");
    }
}
