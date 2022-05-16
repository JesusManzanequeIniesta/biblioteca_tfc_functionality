package com.jmi.bibliotecatfc.dao;

import com.jmi.bibliotecatfc.entities.Autor;
import java.io.Serializable;
import com.jmi.bibliotecatfc.entities.Libro;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 *
 * @author yisus
 */
@ApplicationScoped
public class DaoAutor implements Serializable {
    
    @PersistenceContext(unitName = "bibliotecatfc")
    private EntityManager em;

    ///CREATE

    /**
     *
     * @param autor
     */
    @Transactional
    public void inserta(Autor autor) {
        try {
            em.persist(autor);
            em.flush();
        } catch (Exception ex) {
            throw ex;
        }

    }
    ///GET
    public Autor findByNombre(String nombre) {
        List<Autor> listaAutor = em.createNamedQuery("Autor.findByNombre", Autor.class)
                .setParameter("nombre", nombre)
                .getResultList();
        if(listaAutor.isEmpty()){
            return null;
        }
        else{
            return listaAutor.get(0);
        }
    }
    
    public List<Autor> listAllAuthors (){
        return em.createNamedQuery("Autor.findAll", Autor.class).getResultList();
    }

    ///UPDATE
    @Transactional
    public Autor updateAutor(int id, String nombreNuevo, Libro libro) {

        Autor autor = em.find(Autor.class, id);
        if (nombreNuevo != null) {
            autor.setNombre(nombreNuevo);

        }
        if (libro != null) {
            List<Libro> librosActuales = autor.getLibroList();
            librosActuales.add(libro);
            autor.setLibroList(librosActuales);
            libro.getAutorList().add(autor);
        }

        em.persist(autor);
        em.flush();
        return autor;
    }
    ///DELETE
    public void deleteAutor(int id){
        em.remove(em.find(Autor.class, id));
    }
    
}
