/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import Hibernate.HibernateUtil;
import Modelo.Credenciales;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author perge
 */
public class ManejaCredencales {
    private Session sesion;
    private Transaction tx;

    public ManejaCredencales() {
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
    
    public void insertarClave( Credenciales credenciales ){
        try {
            iniciaOperacion();
            sesion.save(credenciales);
            System.out.println("Clave almacenada con éxito");
        } catch (HibernateException he) {
            manejaExcepcion(he);
            throw he;
        }
        finally{
            finalizaOperacion();
        }
    }
    
    public List<Credenciales> listarCredenciales(){
        List<Credenciales> results;
        try {
            iniciaOperacion();
            Query query = sesion.createQuery("FROM Credenciales as cr");
            results = query.list();
            System.out.println("Lista de claves");
        } catch (HibernateException he) {
            manejaExcepcion(he);
            throw he;
        }
        finally{
            finalizaOperacion();
        }
        return results;
    }
}
