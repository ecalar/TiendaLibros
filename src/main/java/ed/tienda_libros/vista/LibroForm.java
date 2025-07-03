package ed.tienda_libros.vista;

import ed.tienda_libros.modelo.Libro;
import ed.tienda_libros.servicio.LibroServicio;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Component
public class LibroForm extends JFrame {
    LibroServicio libroServicio;
    private JPanel panel;
    private JTable tablaLibros;
    private JTextField idTexto;
    private JTextField libroTexto;
    private JTextField autorTexto;
    private JTextField precioTexto;
    private JTextField existenciasTexto;
    private JButton agregarButton;
    private JButton modificarButton;
    private JButton eliminarButton;
    private DefaultTableModel tablaModeloLibros;


    @Autowired
    public LibroForm(LibroServicio libroServicio){
        this.libroServicio=libroServicio;
        iniciarForma();
        agregarButton.addActionListener(e -> agregarLibro());
        tablaLibros.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                cargarLibroSeleccionado();
            }
        });
        modificarButton.addActionListener(e ->modificarLibro());
        eliminarButton.addActionListener(e ->eliminarLibro());
    }

    private void iniciarForma(){
        setContentPane(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(900,700);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension tamanioPantalla = toolkit.getScreenSize();
        int x = (tamanioPantalla.width - getWidth()/ 2);
        int y = (tamanioPantalla.width - getWidth()/ 2);
        setLocation(x, y);
    }
    private void agregarLibro(){
        //Leer valores del formulario
        if(libroTexto.getText().equals("")){
            mostrarMensaje("Proporciona el nombre del libro");
            libroTexto.requestFocusInWindow();
            return;
        }
        var nombreLibro = libroTexto.getText();
        var autor = autorTexto.getText();
        var precio = Double.parseDouble(precioTexto.getText());
        var existencias= Integer.parseInt(existenciasTexto.getText());
        var libro = new Libro(null, nombreLibro, autor, precio, existencias);
//        libro.setNombreLibro(nombreLibro);
//        libro.setAutor(autor);
//        libro.setPrecio(precio);
//        libro.setExistencias(existencias);
        this.libroServicio.guardarLibro(libro);
        mostrarMensaje("Se agrego el libro...");
        limpiarFormulario();
        listarLibros();
    }
    private void cargarLibroSeleccionado(){
        //Los indices de las columnas de nuestra tabla inicia en 0
        var renglon = tablaLibros.getSelectedRow();
        if(renglon != -1) {//Regresa -1 si no se selecciono ningun registro
        String idLibro = tablaLibros.getModel().getValueAt(renglon, 0).toString();
        idTexto.setText(idLibro);
        String nombreLibro = tablaLibros.getModel().getValueAt(renglon, 1).toString();
        libroTexto.setText(nombreLibro);
        String autor = tablaLibros.getModel().getValueAt(renglon, 2).toString();
        autorTexto.setText(autor);
        String precio = tablaLibros.getModel().getValueAt(renglon, 3).toString();
        precioTexto.setText(precio);
        String existencias = tablaLibros.getModel().getValueAt(renglon, 4).toString();
        existenciasTexto.setText(existencias);
        }
    }
    private void modificarLibro(){
        if(this.idTexto.getText().equals("")){
            mostrarMensaje("Debe seleccionar un registro...");
        }else{
            //Verificar que el nombre del libro no sea nulo
            if(libroTexto.getText().equals("")){
                mostrarMensaje("Selecciona un libro...");
                libroTexto.requestFocusInWindow();
                return;
            }
            //Llenamos el objeto del libro a actualizar
            int idLibro = Integer.parseInt(idTexto.getText());
            var nombreLibro = libroTexto.getText();
            var autor = autorTexto.getText();
            var precio =Double.parseDouble(precioTexto.getText());
            var existencias = Integer.parseInt(existenciasTexto.getText());
            var libro = new Libro(idLibro, nombreLibro, autor, precio, existencias);
            libroServicio.guardarLibro(libro);
            mostrarMensaje("Se modifico el libro...");
            limpiarFormulario();
            listarLibros();
        }
    }
    private void eliminarLibro(){
     var renglon = tablaLibros.getSelectedRow();
     if(renglon != -1){
         String idLibro = tablaLibros.getModel().getValueAt(renglon, 0).toString();
         var libro = new Libro();
         libro.setIdLibro(Integer.parseInt(idLibro));
         libroServicio.eliminarLibro(libro);
         mostrarMensaje("Libro " + idLibro + "eliminado...");
         limpiarFormulario();
         listarLibros();
     }else{
         mostrarMensaje("No se ha seleccionado ningun libro a eliminar...");
     }
    }

    private void limpiarFormulario(){
        libroTexto.setText("");
        autorTexto.setText("");
        precioTexto.setText("");
        existenciasTexto.setText("");
    }

    private void mostrarMensaje(String mensaje){
        JOptionPane.showMessageDialog(this, mensaje);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        //Creamos el elemento idTexto oculto
        idTexto = new JTextField("");
        idTexto.setVisible(false);

        this.tablaModeloLibros = new DefaultTableModel(0,5){
            @Override
            public boolean isCellEditable(int row, int column){return false;}
        };
        String[] cabeceros = {"Id", "Libro", "Autor", "Precio", "Existencias"};
        this.tablaModeloLibros.setColumnIdentifiers(cabeceros);
        //Instanciar el objeto JTable
        this.tablaLibros = new JTable(tablaModeloLibros);
        listarLibros();
    }

    private void listarLibros(){
        //Limpiar tabla
        tablaModeloLibros.setRowCount(0);
        //Obtener los libros
        var libros = libroServicio.listarLibros();
        libros.forEach((libro) ->{
            Object[] renglonLibro = {
                    libro.getIdLibro(),
                    libro.getNombreLibro(),
                    libro.getAutor(),
                    libro.getPrecio(),
                    libro.getExistencias()
            };
            this.tablaModeloLibros.addRow(renglonLibro);
        });
    }

}
