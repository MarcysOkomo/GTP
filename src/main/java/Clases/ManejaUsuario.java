
package Clases;

import Hibernate.HibernateUtil;
import Modelo.Usuarioapp;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author gonza
 */
public class ManejaUsuario {
    private Session sesion;
    private Transaction tx;

    public ManejaUsuario() {
    }
    
    public void iniciaOperacion() throws HibernateException {
        System.out.println("Comenzando con Hibernate");
        sesion = HibernateUtil.getSessionFactory().openSession();
        tx = sesion.beginTransaction();
    }
    
    public void manejaExcepcion(HibernateException he) throws HibernateException{
        tx.rollback();
        System.out.println("Ocurrió un error en el acceso a datos" + he.getMessage());
        System.exit(0);
    }
    
    public void finalizaOperacion() throws HibernateException{
        tx.commit();
        sesion.close();
        System.out.println("Finalizando Hibernate");
    }
    
    public void Registrarse(Usuarioapp usuario){
        try {
            iniciaOperacion();
            sesion.save(usuario);
            System.out.println("Usuario guardado");
        } catch (HibernateException he) {
            manejaExcepcion(he);
            throw he;
        }
        finally{
            finalizaOperacion();
        }
    }
    
    public int Acceder( String password ){
        int result = -1;
        int ID = 12345;
        try {
            iniciaOperacion();
            Usuarioapp usuario = (Usuarioapp) sesion.get(Usuarioapp.class, ID);
            if( usuario.getPassword().equals(password) ){
                result = 1;
                System.out.println("Usuario logueado con exito");
            }
        } catch (HibernateException he) {
            manejaExcepcion(he);
            throw he;
        }
        finally{
            finalizaOperacion();
        }
        return result;
    }
    
    public void CambiarPasswordAcceso( String pass ){
        //En falta de implementación
        try {
            iniciaOperacion();
            Query query = sesion.createQuery("FROM Usuarioapp as experto");
            List<Usuarioapp> results = query.list();
            results.get(0).setPassword(pass);
            sesion.save(results.get(0));
            System.out.println("Clave Modificada");
        } catch (HibernateException he) {
            manejaExcepcion(he);
            throw he;
        }
        finally{
            finalizaOperacion();
        }
    }
    
    public boolean vericarUsuario(){
        boolean isEmpty;
        try {
            iniciaOperacion();
            Query query = sesion.createQuery("FROM Usuarioapp as us");
            List<Object[]> results = query.list();
            isEmpty = results.isEmpty();
        } catch (HibernateException he) {
            manejaExcepcion(he);
            throw he;
        }
        finally{
            finalizaOperacion();
        }
        
        return isEmpty;
    }
}
