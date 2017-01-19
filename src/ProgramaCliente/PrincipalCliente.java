package ProgramaCliente;

import ProgramaArchivos.EnviarArchivo;
import ProgramaArchivos.RecibirArchivo;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.logging.*;
import javax.swing.*;

public class PrincipalCliente extends JFrame {

    public JTextField ingresoMensaje;
    public JTextArea pantallaChat;
    public JMenuItem adjuntar;
    public JMenuItem salir;
    private static ServerSocket servidor;
    private static Socket cliente;
    private static String ipServidor;// = "127.0.0.1";
    public static PrincipalCliente ventanaCliente;
    public static String usuario;
    public boolean recibir;

//Creamos la ventana del chat del cliente
    public PrincipalCliente() {
        super("Chat");
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

//Crea opciones de Salir, Adjuntar Archivos y Aceptar Archivos
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
                System.out.println("Envio Archivo soy cliente");
//bucle para realizar la comparacion del archivo y poderlo enviar
                if (seleccion == JFileChooser.APPROVE_OPTION) {
                    //JOptionPane.showMessageDialog(null, path);
                    ventanaCliente.mostrarMensaje("Enviando Archivo...");
                    RecibirArchivo recibirArchivo = new RecibirArchivo(path, usuario, 35557, "localhost");
                    recibirArchivo.start();
                    EnviarArchivo enviarArchivo = new EnviarArchivo(ipServidor, path);
                    enviarArchivo.start();
                    ventanaCliente.mostrarMensaje("Archivo Enviado Existosamente");
                }
            }
        });
        ipServidor = JOptionPane.showInputDialog(null, "Introduzca IP del servidor: ");
        setSize(320, 500);//tamano de la ventana del chat
        setVisible(true);//hace visible a la ventana
    }

    public static void main(String[] args) {
        ventanaCliente = new PrincipalCliente();
        ventanaCliente.setLocationRelativeTo(null);
        ventanaCliente.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        usuario = JOptionPane.showInputDialog(null, "Introduzca su nombre: ");// intrduce el nombre del usuario o el nick

        try {
//Coneccion con el cliente
            ventanaCliente.mostrarMensaje("Buscando Servidor ...");
            cliente = new Socket(InetAddress.getByName(ipServidor), 11111);
            ventanaCliente.mostrarMensaje("Conectado a :" + cliente.getInetAddress().getHostName());
            ventanaCliente.habilitar(true);

//Correr los hilos de enviar y recibir
            HiloEnviar hiloEnviarCliente = new HiloEnviar(cliente, ventanaCliente);
            hiloEnviarCliente.start();
            HiloRecibir hiloRecibirCliente = new HiloRecibir(cliente, ventanaCliente);
            hiloRecibirCliente.start();
        } catch (IOException ex) {
            Logger.getLogger(PrincipalCliente.class.getName()).log(Level.SEVERE, null, ex);
            ventanaCliente.mostrarMensaje("No se encuentra al Servidor");
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
