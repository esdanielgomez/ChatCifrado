package ProgrmaSerivdor;

import ProgramaArchivos.EnviarArchivo;
import ProgramaArchivos.RecibirArchivo;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.logging.*;
import javax.swing.*;

public class PrincipalServidor extends JFrame {

    public JTextField ingresoMensaje;
    public JTextArea pantallaChat;
    public JMenuItem adjuntar;
    public JMenuItem salir;
    private static ServerSocket servidor;
    private static Socket cliente;
    private static String ipCliente;// = "10.0.0.4";
    public static String usuario;
    public static PrincipalServidor ventanaServidor;

    //Creamos la ventana del chat del servidor
    public PrincipalServidor() {
        super("Chat Kevin Gino");
        //Campo de Texto en la parte inferior
        ingresoMensaje = new JTextField();
        ingresoMensaje.setEditable(false);
        add(ingresoMensaje, BorderLayout.SOUTH);

        //Hoja del chat centrado
        pantallaChat = new JTextArea();
        pantallaChat.setEditable(false);
        add(new JScrollPane(pantallaChat), BorderLayout.CENTER);
        pantallaChat.setBackground(Color.white);
        pantallaChat.setForeground(Color.black);
        ingresoMensaje.setForeground(Color.gray);

        //Crea opciones de Salir y Adjuntar Archivos
        salir = new JMenuItem("Descencriptar");
        adjuntar = new JMenuItem("Adjuntar Archivo");
        adjuntar.setEnabled(false);
        JMenuBar barra = new JMenuBar();
        setJMenuBar(barra);
        barra.add(salir);
        barra.add(adjuntar);

       

        //Accion que se realiza Adjuntar Archivo
        adjuntar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                javax.swing.JFileChooser ventanaEscojer = new javax.swing.JFileChooser();
                int seleccion = ventanaEscojer.showOpenDialog(ventanaEscojer);// Se abre el cuadro para escoger el archivo
                String path = ventanaEscojer.getSelectedFile().getAbsolutePath();//Se obtiene la direccion completa del archivo

                //bucle para realizar la comparacion del archivo y poderlo enviar
                if (seleccion == JFileChooser.APPROVE_OPTION) {
                    ventanaServidor.mostrarMensaje("Enviando Archivo...");
                    RecibirArchivo recibirArchivo = new RecibirArchivo(path, usuario, 35557, "localhost");
                    recibirArchivo.start();
                    EnviarArchivo enviarArchivo = new EnviarArchivo(ipCliente, path);
                    enviarArchivo.start();
                    ventanaServidor.mostrarMensaje("Archivo Enviado Existosamente");
//                    PrincipalCliente.ventanaCliente.recibirArchivo(path,11112);
                }
            }
        });
        ipCliente = JOptionPane.showInputDialog(null, "Introduzca numero IP del Cliente: ");
        setSize(320, 500);//tamano de la ventana del chat
        setVisible(true); //hace visible a la ventana

    }

    public static void main(String[] args) {
        ventanaServidor = new PrincipalServidor();
        ventanaServidor.setLocationRelativeTo(null);
        ventanaServidor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        usuario = JOptionPane.showInputDialog(null, "Introduzca su nombre: ");// intrduce el nombre del usuario o el nick

        try {
            //Crear el socket Servidor
            servidor = new ServerSocket(11111, 100);
            
            ventanaServidor.mostrarMensaje("Esperando Cliente ...");
            //Bucle infinito para esperar conexiones de los clientes
            while (true) {
                try {
                    //Coneccion con el cliente
                    cliente = servidor.accept();
                    ventanaServidor.mostrarMensaje("Conectado a : " + cliente.getInetAddress().getHostName());
                    ventanaServidor.habilitar(true);
                    //Correr los hilos de enviar y recibir
                    HiloEnviar hiloEnviarServidor = new HiloEnviar(cliente, ventanaServidor);
                    hiloEnviarServidor.start();
                    HiloRecibir hiloRecibirServidor = new HiloRecibir(cliente, ventanaServidor);
                    hiloRecibirServidor.start();
                } catch (IOException ex) {
                    Logger.getLogger(PrincipalServidor.class.getName()).log(Level.SEVERE, null, ex);
                    ventanaServidor.mostrarMensaje("No se puede conectar con el cliente");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PrincipalServidor.class.getName()).log(Level.SEVERE, null, ex);
            ventanaServidor.mostrarMensaje("No se encuentra IP del Servidor");
        }
    }

    public void mostrarMensaje(String mensaje) {
        pantallaChat.append(mensaje + "\n");
    }

    public void habilitar(boolean editable) {
        ingresoMensaje.setEditable(editable);
        adjuntar.setEnabled(editable);
        salir.setEnabled(editable);
    }
}
