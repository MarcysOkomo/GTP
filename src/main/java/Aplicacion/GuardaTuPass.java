/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Aplicacion;

import Clases.*;
import Modelo.*;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author perge
 */
public class GuardaTuPass extends javax.swing.JFrame {

    ManejaUsuario manejaUsuario = new ManejaUsuario();
    ManejaCredencales manejaCredencales = new ManejaCredencales();
    DefaultTableModel modelo;
    //ManejaCredenciales manejaCredenciales = new ManejaCredenciales();
    /**
     * Creates new form frmPrincipal
     */
    public GuardaTuPass() {
        initComponents();
        //Centrar la ventana
        this.setLocationRelativeTo(null);
        inicializar();
        
        modelo = (DefaultTableModel) tblLista.getModel();

    }

    //**************************************************************************
    //Código para cambiar el icono de la ventana
    @Override
    public Image getIconImage() {
        Image retValue = Toolkit.getDefaultToolkit().
                getImage(ClassLoader.getSystemResource("images/iconoAPP.png"));
        return retValue;
    }
    //**************************************************************************

    //**************************************************************************
    //Método para inicializar todas las variables
    private void inicializar() {
        
        if( !manejaUsuario.vericarUsuario() ){
            pnlRegistro.setVisible(false);
            pnlAcceso.setVisible(true);
        }else{
            pnlRegistro.setVisible(true);
            pnlAcceso.setVisible(false);
        }
        
        pnlLClaves.setVisible(false);
        pnlPrincipal.setVisible(false);
        pnlAgregarClave.setVisible(false);
        pnlCClave.setVisible(false);
        lblErrorAcceso.setVisible(false);
        lblErrorRegistro.setVisible(false);
        lblErrorCC.setVisible(false);
        lblErrorAC.setVisible(false);
        

    }
    //**************************************************************************

    //**************************************************************************
    //Método para verificar la iqualdad de contraseñas al registrarse
    private void Registrarse() {
        try {
            if (passRegistro1.getPassword().length > 0 && passRegistro2.getPassword().length > 0) {

                //Convertimos las contraseñas en string
                String pass1 = new String(passRegistro1.getPassword());
                String pass2 = new String(passRegistro2.getPassword());

                if (pass1.equals(pass2)) {
                    //Registramos la contraseña de acceso a la base de datos
                    Usuarioapp usuario = new Usuarioapp();
                    usuario.setId( 12345 ); //Solo se almacena una vez
                    pass2 = cifradoMD5.Encriptar(pass2);
                    usuario.setPassword(pass2);
                    manejaUsuario.Registrarse(usuario);

                    //Nos desplazamos a la vista principal
                    pnlRegistro.setVisible(false);
                    pnlPrincipal.setVisible(true);

                    lblErrorRegistro.setVisible(false);
                    passRegistro1.setText("");
                    passRegistro2.setText("");

                } else {
                    //Mostramos el mensaje de error
                    lblErrorRegistro.setText("Las claves no coinciden...");
                    lblErrorRegistro.setVisible(true);
                }
            } else {
                lblErrorRegistro.setText("Rellena el formulario...");
                lblErrorRegistro.setVisible(true);
            }
        } catch (Exception e) {
            // ERROR EN EL REGISTRO
            JOptionPane.showMessageDialog(this, "Error en el REGISTRO", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }
    //**************************************************************************

    //**************************************************************************
    //Método para el inicio de sesión
    private void Acceder() {

        try {
            //Convertimos el password a string
            String pass = new String(passAcceso.getPassword());
            //Encriptamos la clave de acceso
            pass = cifradoMD5.Encriptar(pass);
            //si existe accedemos a la vista inicial
            if ( manejaUsuario.Acceder(pass) == 1) {
                pnlAcceso.setVisible(false);
                pnlPrincipal.setVisible(true);

                lblErrorAcceso.setVisible(false);
                passAcceso.setText("");
            } else {
                //en caso contrario, mostramos el mensaje de error
                lblErrorAcceso.setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al ACCEDER", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //**************************************************************************

    //**************************************************************************
    private void CambiarPassword() {
        
        try{
            String pass1 = new String(passCC1.getPassword());
            String pass2 = new String(passCC2.getPassword());
            
            if( pass1.equals(pass2) ){
                //Cambiamos la clave del usuario
               manejaUsuario.CambiarPasswordAcceso(pass2);
               lblErrorCC.setVisible(false);
               JOptionPane.showMessageDialog(this, "Clave Cambiada Correctamente", "Cambiar Clave", JOptionPane.INFORMATION_MESSAGE); 
               passCC1.setText("");
               passCC2.setText("");
            }else{
                lblErrorCC.setVisible(true);
            }
            
        }catch( HeadlessException e ){
            JOptionPane.showMessageDialog(this, "Error al CAMBIAR CLAVE", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //**************************************************************************
    
    public void insertarClave(){
        try{
            String usuario = usuarioAC.getText();
            String pass = new String(passAC.getPassword());
            String descripcion = descipAC.getText();
            
            if( !usuario.equals("") && !pass.equals("") && !descripcion.equals("") ){
                
                //Encriptamos la clave
                pass = cifradoMD5.Encriptar(pass);
                
                CredencialesId crdID = new CredencialesId();
                crdID.setUsuario(usuario);
                crdID.setPassword(pass);
                crdID.setDescripcion(descripcion);
                
                Credenciales crd = new Credenciales( crdID );
                manejaCredencales.insertarClave(crd);
                
                lblErrorAC.setVisible(false);
                JOptionPane.showMessageDialog(this, "Clave almacenada con éxito", "Insertar Clave", JOptionPane.INFORMATION_MESSAGE); 
                usuarioAC.setText("");
                passAC.setText("");
                descipAC.setText("");
            }else{
                lblErrorAC.setVisible(true);
            }
            
        }catch( HeadlessException e ){
            JOptionPane.showMessageDialog(this, "Error al AÑADIR CLAVE", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void LimpiarLista(){
        int filas = tblLista.getRowCount();
        for (int i = 0; i < filas; i++) {
            modelo.removeRow(0);
        }
    }
    
    public void ListarClaves() throws Exception {
        LimpiarLista();
        String[] fila = new String[3];
        //Obtenemos las claves de la base de datos
        List<Credenciales> crds = manejaCredencales.listarCredenciales();
        
        for (Credenciales crd : crds) {
            fila[0] = crd.getId().getUsuario();
            fila[1] = cifradoMD5.Desencriptar(crd.getId().getPassword());
            fila[2] = crd.getId().getDescripcion();
            modelo.addRow(fila);
            tblLista.setModel(modelo);
        }
        
    }
    
    //**************************************************************************
    private void CerrarSesion() {
        //Volvemos a la vista de acceso
        pnlPrincipal.setVisible(false);
        pnlAcceso.setVisible(true);
    }
    //**************************************************************************

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlAgregarClave = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        btnVolverAC = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        btnAgregarClave = new javax.swing.JButton();
        passAC = new javax.swing.JPasswordField();
        jScrollPane1 = new javax.swing.JScrollPane();
        descipAC = new javax.swing.JTextArea();
        jLabel18 = new javax.swing.JLabel();
        usuarioAC = new javax.swing.JTextField();
        lblErrorAC = new javax.swing.JLabel();
        pnlRegistro = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        btnRegistro = new javax.swing.JButton();
        passRegistro2 = new javax.swing.JPasswordField();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        passRegistro1 = new javax.swing.JPasswordField();
        lblErrorRegistro = new javax.swing.JLabel();
        pnlPrincipal = new javax.swing.JPanel();
        btnConfig = new javax.swing.JButton();
        btnNClave = new javax.swing.JButton();
        btnListar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        btnCS = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        pnlAcceso = new javax.swing.JPanel();
        passAcceso = new javax.swing.JPasswordField();
        btnAcceso = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lblErrorAcceso = new javax.swing.JLabel();
        pnlCClave = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        btnVolverCC = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        btnCC = new javax.swing.JButton();
        passCC1 = new javax.swing.JPasswordField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        passCC2 = new javax.swing.JPasswordField();
        jSeparator9 = new javax.swing.JSeparator();
        lblErrorCC = new javax.swing.JLabel();
        pnlLClaves = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblLista = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        btnVolverLC = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GuardaTuPassword");
        setBackground(new java.awt.Color(255, 255, 255));
        setIconImage(getIconImage());
        setName("frmPrincial"); // NOI18N
        setResizable(false);

        pnlAgregarClave.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 23, 0));

        jLabel11.setFont(new java.awt.Font("Segoe UI Light", 1, 36)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Agregar Clave");

        btnVolverAC.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        btnVolverAC.setForeground(new java.awt.Color(255, 255, 255));
        btnVolverAC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/return.png"))); // NOI18N
        btnVolverAC.setText("Volver");
        btnVolverAC.setToolTipText("Volver al menú");
        btnVolverAC.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnVolverAC.setContentAreaFilled(false);
        btnVolverAC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnVolverAC.setName("bntAcceso"); // NOI18N
        btnVolverAC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverACActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 198, Short.MAX_VALUE)
                .addComponent(btnVolverAC, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVolverAC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jLabel14.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 51, 0));
        jLabel14.setText("Usuario");

        jLabel15.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 51, 0));
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/user.png"))); // NOI18N

        jSeparator5.setBackground(new java.awt.Color(204, 204, 204));

        jLabel16.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 51, 0));
        jLabel16.setText("Contraseña");

        jLabel17.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 51, 0));
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/key.png"))); // NOI18N

        jSeparator6.setBackground(new java.awt.Color(204, 204, 204));

        btnAgregarClave.setFont(new java.awt.Font("Segoe UI Light", 1, 24)); // NOI18N
        btnAgregarClave.setForeground(new java.awt.Color(255, 51, 0));
        btnAgregarClave.setText("Agregar");
        btnAgregarClave.setToolTipText("Acceso al sistema");
        btnAgregarClave.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnAgregarClave.setContentAreaFilled(false);
        btnAgregarClave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAgregarClave.setName("bntAcceso"); // NOI18N
        btnAgregarClave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarClaveActionPerformed(evt);
            }
        });

        passAC.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        passAC.setForeground(new java.awt.Color(255, 51, 0));
        passAC.setToolTipText("Introduce tu clave");
        passAC.setBorder(null);
        passAC.setMargin(new java.awt.Insets(2, 5, 2, 5));
        passAC.setName("passRegistro1"); // NOI18N

        descipAC.setColumns(15);
        descipAC.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        descipAC.setForeground(new java.awt.Color(255, 51, 0));
        descipAC.setRows(3);
        descipAC.setToolTipText("Inserte descripción");
        descipAC.setWrapStyleWord(true);
        jScrollPane1.setViewportView(descipAC);

        jLabel18.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 51, 0));
        jLabel18.setText("Descripción");

        usuarioAC.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        usuarioAC.setForeground(new java.awt.Color(255, 51, 0));
        usuarioAC.setToolTipText("Insertar usuario");
        usuarioAC.setBorder(null);

        lblErrorAC.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        lblErrorAC.setForeground(new java.awt.Color(255, 0, 0));
        lblErrorAC.setText("Rellenar todos los campos...");
        lblErrorAC.setName("lblErrorAcceso"); // NOI18N

        javax.swing.GroupLayout pnlAgregarClaveLayout = new javax.swing.GroupLayout(pnlAgregarClave);
        pnlAgregarClave.setLayout(pnlAgregarClaveLayout);
        pnlAgregarClaveLayout.setHorizontalGroup(
            pnlAgregarClaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAgregarClaveLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlAgregarClaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAgregarClaveLayout.createSequentialGroup()
                        .addGroup(pnlAgregarClaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jSeparator5)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlAgregarClaveLayout.createSequentialGroup()
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(usuarioAC, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(pnlAgregarClaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlAgregarClaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAgregarClaveLayout.createSequentialGroup()
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(72, 72, 72))
                                .addGroup(pnlAgregarClaveLayout.createSequentialGroup()
                                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(passAC, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(btnAgregarClave, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(88, 88, 88))
            .addGroup(pnlAgregarClaveLayout.createSequentialGroup()
                .addGap(221, 221, 221)
                .addComponent(lblErrorAC)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAgregarClaveLayout.setVerticalGroup(
            pnlAgregarClaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAgregarClaveLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnlAgregarClaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlAgregarClaveLayout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlAgregarClaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(usuarioAC, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlAgregarClaveLayout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlAgregarClaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(passAC, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20)
                .addGroup(pnlAgregarClaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlAgregarClaveLayout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnAgregarClave, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblErrorAC, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(60, Short.MAX_VALUE))
        );

        pnlRegistro.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Segoe UI Light", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 51, 0));
        jLabel4.setText("GuardaTuPass");

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/computerRegistro.png"))); // NOI18N

        jSeparator2.setBackground(new java.awt.Color(204, 204, 204));

        jLabel6.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 51, 0));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/key.png"))); // NOI18N

        btnRegistro.setFont(new java.awt.Font("Segoe UI Light", 1, 24)); // NOI18N
        btnRegistro.setForeground(new java.awt.Color(255, 51, 0));
        btnRegistro.setText("Registrarse");
        btnRegistro.setToolTipText("Acceso al sistema");
        btnRegistro.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnRegistro.setContentAreaFilled(false);
        btnRegistro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRegistro.setName("bntAcceso"); // NOI18N
        btnRegistro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistroActionPerformed(evt);
            }
        });

        passRegistro2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        passRegistro2.setForeground(new java.awt.Color(255, 51, 0));
        passRegistro2.setToolTipText("Introduce tu clave");
        passRegistro2.setBorder(null);
        passRegistro2.setMargin(new java.awt.Insets(2, 5, 2, 5));
        passRegistro2.setName("passRegistro2"); // NOI18N

        jSeparator3.setBackground(new java.awt.Color(204, 204, 204));

        jLabel7.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 51, 0));
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/key.png"))); // NOI18N

        jLabel8.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 51, 0));
        jLabel8.setText("Repetir contraseña");

        jLabel9.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 51, 0));
        jLabel9.setText("Contraseña");

        passRegistro1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        passRegistro1.setForeground(new java.awt.Color(255, 51, 0));
        passRegistro1.setToolTipText("Introduce tu clave");
        passRegistro1.setBorder(null);
        passRegistro1.setMargin(new java.awt.Insets(2, 5, 2, 5));
        passRegistro1.setName("passRegistro1"); // NOI18N

        lblErrorRegistro.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        lblErrorRegistro.setForeground(new java.awt.Color(255, 0, 0));
        lblErrorRegistro.setText("Las claves no coinciden...");
        lblErrorRegistro.setName("lblErrorAcceso"); // NOI18N

        javax.swing.GroupLayout pnlRegistroLayout = new javax.swing.GroupLayout(pnlRegistro);
        pnlRegistro.setLayout(pnlRegistroLayout);
        pnlRegistroLayout.setHorizontalGroup(
            pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRegistroLayout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addGroup(pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlRegistroLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel5))
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 131, Short.MAX_VALUE)
                .addGroup(pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblErrorRegistro)
                    .addGroup(pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRegistroLayout.createSequentialGroup()
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(passRegistro2, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(9, 9, 9))
                        .addComponent(btnRegistro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRegistroLayout.createSequentialGroup()
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(passRegistro1, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(9, 9, 9))
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(69, 69, 69))
        );
        pnlRegistroLayout.setVerticalGroup(
            pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRegistroLayout.createSequentialGroup()
                .addGroup(pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlRegistroLayout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(passRegistro1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlRegistroLayout.createSequentialGroup()
                                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(21, 21, 21)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(passRegistro2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlRegistroLayout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(pnlRegistroLayout.createSequentialGroup()
                        .addGap(163, 163, 163)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(btnRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(lblErrorRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(72, Short.MAX_VALUE))
        );

        passRegistro2.getAccessibleContext().setAccessibleDescription("Repetir clave");

        pnlPrincipal.setBackground(new java.awt.Color(255, 255, 255));

        btnConfig.setBackground(new java.awt.Color(255, 255, 255));
        btnConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/userSetting.png"))); // NOI18N
        btnConfig.setToolTipText("Configuración usuario");
        btnConfig.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 0)));
        btnConfig.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfigActionPerformed(evt);
            }
        });

        btnNClave.setBackground(new java.awt.Color(255, 255, 255));
        btnNClave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/newCredential.png"))); // NOI18N
        btnNClave.setToolTipText("Añadir nueva clave");
        btnNClave.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 0)));
        btnNClave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNClave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNClaveActionPerformed(evt);
            }
        });

        btnListar.setBackground(new java.awt.Color(255, 255, 255));
        btnListar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/list.png"))); // NOI18N
        btnListar.setToolTipText("Mostrar claves almacenadas");
        btnListar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 0)));
        btnListar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnListar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListarActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 23, 0));

        jLabel10.setFont(new java.awt.Font("Segoe UI Light", 1, 36)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("GuardaTuPass");

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Logo.png"))); // NOI18N

        btnCS.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        btnCS.setForeground(new java.awt.Color(255, 255, 255));
        btnCS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logout.png"))); // NOI18N
        btnCS.setText("Cerrar Sesión");
        btnCS.setToolTipText("Cerrar Sesión");
        btnCS.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnCS.setContentAreaFilled(false);
        btnCS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCS.setName("bntAcceso"); // NOI18N
        btnCS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCSActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCS, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(28, 28, 28)
                            .addComponent(btnCS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jLabel12)))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        jSeparator4.setBackground(new java.awt.Color(255, 51, 0));
        jSeparator4.setForeground(new java.awt.Color(255, 51, 0));

        javax.swing.GroupLayout pnlPrincipalLayout = new javax.swing.GroupLayout(pnlPrincipal);
        pnlPrincipal.setLayout(pnlPrincipalLayout);
        pnlPrincipalLayout.setHorizontalGroup(
            pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlPrincipalLayout.createSequentialGroup()
                .addGap(119, 119, 119)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jSeparator4)
                    .addGroup(pnlPrincipalLayout.createSequentialGroup()
                        .addComponent(btnNClave, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(btnListar, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(btnConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(154, Short.MAX_VALUE))
        );
        pnlPrincipalLayout.setVerticalGroup(
            pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPrincipalLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnNClave, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnListar, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(114, Short.MAX_VALUE))
        );

        pnlAcceso.setBackground(new java.awt.Color(255, 255, 255));

        passAcceso.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        passAcceso.setForeground(new java.awt.Color(255, 51, 0));
        passAcceso.setToolTipText("Introduce tu clave");
        passAcceso.setBorder(null);
        passAcceso.setMargin(new java.awt.Insets(2, 5, 2, 5));
        passAcceso.setName("passAcceso"); // NOI18N

        btnAcceso.setFont(new java.awt.Font("Segoe UI Light", 1, 24)); // NOI18N
        btnAcceso.setForeground(new java.awt.Color(255, 51, 0));
        btnAcceso.setText("Acceder");
        btnAcceso.setToolTipText("Acceso al sistema");
        btnAcceso.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnAcceso.setContentAreaFilled(false);
        btnAcceso.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAcceso.setName("bntAcceso"); // NOI18N
        btnAcceso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAccesoActionPerformed(evt);
            }
        });

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/computerPass.png"))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 51, 0));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/key.png"))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Segoe UI Light", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 51, 0));
        jLabel3.setText("GuardaTuPass");

        jSeparator1.setBackground(new java.awt.Color(204, 204, 204));

        lblErrorAcceso.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        lblErrorAcceso.setForeground(new java.awt.Color(255, 0, 0));
        lblErrorAcceso.setText("Clave de acceso incorrecto...");
        lblErrorAcceso.setName("lblErrorAcceso"); // NOI18N

        javax.swing.GroupLayout pnlAccesoLayout = new javax.swing.GroupLayout(pnlAcceso);
        pnlAcceso.setLayout(pnlAccesoLayout);
        pnlAccesoLayout.setHorizontalGroup(
            pnlAccesoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAccesoLayout.createSequentialGroup()
                .addGap(223, 223, 223)
                .addGroup(pnlAccesoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAccesoLayout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(pnlAccesoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAccesoLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2)
                                .addGap(45, 45, 45))
                            .addComponent(jLabel3)))
                    .addGroup(pnlAccesoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAccesoLayout.createSequentialGroup()
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(passAcceso, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(15, 15, 15))
                        .addComponent(btnAcceso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblErrorAcceso)))
                .addContainerGap(223, Short.MAX_VALUE))
        );
        pnlAccesoLayout.setVerticalGroup(
            pnlAccesoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAccesoLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(pnlAccesoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(passAcceso, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(btnAcceso, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblErrorAcceso, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(73, Short.MAX_VALUE))
        );

        pnlCClave.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 23, 0));

        jLabel13.setFont(new java.awt.Font("Segoe UI Light", 1, 36)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Cambiar Clave de acceso");

        btnVolverCC.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        btnVolverCC.setForeground(new java.awt.Color(255, 255, 255));
        btnVolverCC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/return.png"))); // NOI18N
        btnVolverCC.setText("Volver");
        btnVolverCC.setToolTipText("Volver al menú");
        btnVolverCC.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnVolverCC.setContentAreaFilled(false);
        btnVolverCC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnVolverCC.setName("bntAcceso"); // NOI18N
        btnVolverCC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverCCActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 75, Short.MAX_VALUE)
                .addComponent(btnVolverCC, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVolverCC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        jLabel21.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 51, 0));
        jLabel21.setText("Nueva contraseña");

        jLabel22.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 51, 0));
        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/key.png"))); // NOI18N

        jSeparator8.setBackground(new java.awt.Color(204, 204, 204));

        btnCC.setFont(new java.awt.Font("Segoe UI Light", 1, 24)); // NOI18N
        btnCC.setForeground(new java.awt.Color(255, 51, 0));
        btnCC.setText("Cambiar");
        btnCC.setToolTipText("Acceso al sistema");
        btnCC.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnCC.setContentAreaFilled(false);
        btnCC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCC.setName("bntAcceso"); // NOI18N
        btnCC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCCActionPerformed(evt);
            }
        });

        passCC1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        passCC1.setForeground(new java.awt.Color(255, 51, 0));
        passCC1.setToolTipText("Introduce tu clave");
        passCC1.setBorder(null);
        passCC1.setMargin(new java.awt.Insets(2, 5, 2, 5));
        passCC1.setName("passRegistro1"); // NOI18N

        jLabel24.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 51, 0));
        jLabel24.setText("Repetir contraseña");

        jLabel25.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 51, 0));
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/key.png"))); // NOI18N

        passCC2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        passCC2.setForeground(new java.awt.Color(255, 51, 0));
        passCC2.setToolTipText("Introduce tu clave");
        passCC2.setBorder(null);
        passCC2.setMargin(new java.awt.Insets(2, 5, 2, 5));
        passCC2.setName("passRegistro1"); // NOI18N

        jSeparator9.setBackground(new java.awt.Color(204, 204, 204));

        lblErrorCC.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        lblErrorCC.setForeground(new java.awt.Color(255, 0, 0));
        lblErrorCC.setText("Las Claves no coinciden...");
        lblErrorCC.setName("lblErrorAcceso"); // NOI18N

        javax.swing.GroupLayout pnlCClaveLayout = new javax.swing.GroupLayout(pnlCClave);
        pnlCClave.setLayout(pnlCClaveLayout);
        pnlCClaveLayout.setHorizontalGroup(
            pnlCClaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCClaveLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlCClaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblErrorCC)
                    .addComponent(btnCC, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlCClaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCClaveLayout.createSequentialGroup()
                            .addGroup(pnlCClaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(pnlCClaveLayout.createSequentialGroup()
                                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(passCC2, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(17, 17, 17)))
                    .addGroup(pnlCClaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCClaveLayout.createSequentialGroup()
                            .addGroup(pnlCClaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(pnlCClaveLayout.createSequentialGroup()
                                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(passCC1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(17, 17, 17))))
                .addGap(214, 214, 214))
        );
        pnlCClaveLayout.setVerticalGroup(
            pnlCClaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCClaveLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCClaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(passCC1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCClaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(passCC2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCC, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblErrorCC, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        pnlLClaves.setBackground(new java.awt.Color(255, 255, 255));

        tblLista.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tblLista.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Usuario", "Contraseña", "Descripción"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblLista.setGridColor(new java.awt.Color(51, 51, 51));
        jScrollPane2.setViewportView(tblLista);

        jPanel4.setBackground(new java.awt.Color(255, 23, 0));

        jLabel19.setFont(new java.awt.Font("Segoe UI Light", 1, 36)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Claves almacenadas");

        btnVolverLC.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        btnVolverLC.setForeground(new java.awt.Color(255, 255, 255));
        btnVolverLC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/return.png"))); // NOI18N
        btnVolverLC.setText("Volver");
        btnVolverLC.setToolTipText("Volver al menú");
        btnVolverLC.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnVolverLC.setContentAreaFilled(false);
        btnVolverLC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnVolverLC.setName("bntAcceso"); // NOI18N
        btnVolverLC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverLCActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnVolverLC, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(btnVolverLC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(35, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSeparator7.setBackground(new java.awt.Color(255, 51, 0));
        jSeparator7.setForeground(new java.awt.Color(255, 51, 0));

        javax.swing.GroupLayout pnlLClavesLayout = new javax.swing.GroupLayout(pnlLClaves);
        pnlLClaves.setLayout(pnlLClavesLayout);
        pnlLClavesLayout.setHorizontalGroup(
            pnlLClavesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlLClavesLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlLClavesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jSeparator7)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        pnlLClavesLayout.setVerticalGroup(
            pnlLClavesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLClavesLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(65, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 689, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlRegistro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlAcceso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlAgregarClave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlCClave, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlLClaves, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 422, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlRegistro, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlAcceso, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlAgregarClave, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlCClave, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlLClaves, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAccesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAccesoActionPerformed
        Acceder();
    }//GEN-LAST:event_btnAccesoActionPerformed

    private void btnRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistroActionPerformed
        Registrarse();
    }//GEN-LAST:event_btnRegistroActionPerformed

    private void btnCSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCSActionPerformed
        CerrarSesion();
    }//GEN-LAST:event_btnCSActionPerformed

    private void btnVolverCCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverCCActionPerformed
        pnlCClave.setVisible(false);
        pnlPrincipal.setVisible(true);
    }//GEN-LAST:event_btnVolverCCActionPerformed

    private void btnCCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCCActionPerformed
        // TODO add your handling code here:
        CambiarPassword();
    }//GEN-LAST:event_btnCCActionPerformed

    private void btnAgregarClaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarClaveActionPerformed
        // TODO add your handling code here:
        insertarClave();
    }//GEN-LAST:event_btnAgregarClaveActionPerformed

    private void btnVolverACActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverACActionPerformed
        pnlAgregarClave.setVisible(false);
        pnlPrincipal.setVisible(true);
    }//GEN-LAST:event_btnVolverACActionPerformed

    private void btnNClaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNClaveActionPerformed
        pnlPrincipal.setVisible(false);
        pnlAgregarClave.setVisible(true);
    }//GEN-LAST:event_btnNClaveActionPerformed

    private void btnListarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListarActionPerformed
        pnlPrincipal.setVisible(false);
        pnlLClaves.setVisible(true);
        try {
            ListarClaves();
        } catch (Exception ex) {
            Logger.getLogger(GuardaTuPass.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnListarActionPerformed

    private void btnConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfigActionPerformed
        pnlPrincipal.setVisible(false);
        pnlCClave.setVisible(true);
    }//GEN-LAST:event_btnConfigActionPerformed

    private void btnVolverLCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverLCActionPerformed
        pnlLClaves.setVisible(false);
        pnlPrincipal.setVisible(true);
    }//GEN-LAST:event_btnVolverLCActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GuardaTuPass.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GuardaTuPass.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GuardaTuPass.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GuardaTuPass.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GuardaTuPass().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAcceso;
    private javax.swing.JButton btnAgregarClave;
    private javax.swing.JButton btnCC;
    private javax.swing.JButton btnCS;
    private javax.swing.JButton btnConfig;
    private javax.swing.JButton btnListar;
    private javax.swing.JButton btnNClave;
    private javax.swing.JButton btnRegistro;
    private javax.swing.JButton btnVolverAC;
    private javax.swing.JButton btnVolverCC;
    private javax.swing.JButton btnVolverLC;
    private javax.swing.JTextArea descipAC;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JLabel lblErrorAC;
    private javax.swing.JLabel lblErrorAcceso;
    private javax.swing.JLabel lblErrorCC;
    private javax.swing.JLabel lblErrorRegistro;
    private javax.swing.JPasswordField passAC;
    private javax.swing.JPasswordField passAcceso;
    private javax.swing.JPasswordField passCC1;
    private javax.swing.JPasswordField passCC2;
    private javax.swing.JPasswordField passRegistro1;
    private javax.swing.JPasswordField passRegistro2;
    private javax.swing.JPanel pnlAcceso;
    private javax.swing.JPanel pnlAgregarClave;
    private javax.swing.JPanel pnlCClave;
    private javax.swing.JPanel pnlLClaves;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JPanel pnlRegistro;
    private javax.swing.JTable tblLista;
    private javax.swing.JTextField usuarioAC;
    // End of variables declaration//GEN-END:variables
}
