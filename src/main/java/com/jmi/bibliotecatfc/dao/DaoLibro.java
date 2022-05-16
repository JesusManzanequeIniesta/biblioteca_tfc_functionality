/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jmi.bibliotecatfc.dao;

import com.jmi.bibliotecatfc.dao.exceptions.IllegalOrphanException;
import com.jmi.bibliotecatfc.dao.exceptions.NonexistentEntityException;
import com.jmi.bibliotecatfc.dao.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.jmi.bibliotecatfc.entities.Autor;
import java.util.ArrayList;
import java.util.List;
import com.jmi.bibliotecatfc.entities.Ejemplar;
import com.jmi.bibliotecatfc.entities.Libro;
import com.jmi.bibliotecatfc.entities.Peticion;
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
public class DaoLibro implements Serializable {

    @PersistenceContext(unitName = "bibliotecatfc")
    EntityManager em;
    
    /**
     *
     * @param libro
     */
    //Algo pasa aqui, después de persistir y comitear se va a la mierda el EntityManager.
    @Transactional
    public void inserta(Libro libro) {
        em.persist(libro);
   }

    @Transactional
    public void edit(Libro libro) throws IllegalOrphanException, NonexistentEntityException, Exception {
        try {
            Libro persistentLibro = em.find(Libro.class, libro.getId());
            List<Autor> autorListOld = persistentLibro.getAutorList();
            List<Autor> autorListNew = libro.getAutorList();
            List<Ejemplar> ejemplarListOld = persistentLibro.getEjemplarList();
            List<Ejemplar> ejemplarListNew = libro.getEjemplarList();
            List<Peticion> peticionListOld = persistentLibro.getPeticionList();
            List<Peticion> peticionListNew = libro.getPeticionList();
            List<String> illegalOrphanMessages = null;
            for (Ejemplar ejemplarListOldEjemplar : ejemplarListOld) {
                if (!ejemplarListNew.contains(ejemplarListOldEjemplar)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ejemplar " + ejemplarListOldEjemplar + " since its libroId field is not nullable.");
                }
            }
            for (Peticion peticionListOldPeticion : peticionListOld) {
                if (!peticionListNew.contains(peticionListOldPeticion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Peticion " + peticionListOldPeticion + " since its libroId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Autor> attachedAutorListNew = new ArrayList<Autor>();
            for (Autor autorListNewAutorToAttach : autorListNew) {
                autorListNewAutorToAttach = em.getReference(autorListNewAutorToAttach.getClass(), autorListNewAutorToAttach.getId());
                attachedAutorListNew.add(autorListNewAutorToAttach);
            }
            autorListNew = attachedAutorListNew;
            libro.setAutorList(autorListNew);
            List<Ejemplar> attachedEjemplarListNew = new ArrayList<Ejemplar>();
            for (Ejemplar ejemplarListNewEjemplarToAttach : ejemplarListNew) {
                ejemplarListNewEjemplarToAttach = em.getReference(ejemplarListNewEjemplarToAttach.getClass(), ejemplarListNewEjemplarToAttach.getId());
                attachedEjemplarListNew.add(ejemplarListNewEjemplarToAttach);
            }
            ejemplarListNew = attachedEjemplarListNew;
            libro.setEjemplarList(ejemplarListNew);
            List<Peticion> attachedPeticionListNew = new ArrayList<Peticion>();
            for (Peticion peticionListNewPeticionToAttach : peticionListNew) {
                peticionListNewPeticionToAttach = em.getReference(peticionListNewPeticionToAttach.getClass(), peticionListNewPeticionToAttach.getId());
                attachedPeticionListNew.add(peticionListNewPeticionToAttach);
            }
            peticionListNew = attachedPeticionListNew;
            libro.setPeticionList(peticionListNew);
            libro = em.merge(libro);
            for (Autor autorListOldAutor : autorListOld) {
                if (!autorListNew.contains(autorListOldAutor)) {
                    autorListOldAutor.getLibroList().remove(libro);
                    autorListOldAutor = em.merge(autorListOldAutor);
                }
            }
            for (Autor autorListNewAutor : autorListNew) {
                if (!autorListOld.contains(autorListNewAutor)) {
                    autorListNewAutor.getLibroList().add(libro);
                    autorListNewAutor = em.merge(autorListNewAutor);
                }
            }
            for (Ejemplar ejemplarListNewEjemplar : ejemplarListNew) {
                if (!ejemplarListOld.contains(ejemplarListNewEjemplar)) {
                    Libro oldLibroIdOfEjemplarListNewEjemplar = ejemplarListNewEjemplar.getLibroId();
                    ejemplarListNewEjemplar.setLibroId(libro);
                    ejemplarListNewEjemplar = em.merge(ejemplarListNewEjemplar);
                    if (oldLibroIdOfEjemplarListNewEjemplar != null && !oldLibroIdOfEjemplarListNewEjemplar.equals(libro)) {
                        oldLibroIdOfEjemplarListNewEjemplar.getEjemplarList().remove(ejemplarListNewEjemplar);
                        oldLibroIdOfEjemplarListNewEjemplar = em.merge(oldLibroIdOfEjemplarListNewEjemplar);
                    }
                }
            }
            for (Peticion peticionListNewPeticion : peticionListNew) {
                if (!peticionListOld.contains(peticionListNewPeticion)) {
                    Libro oldLibroIdOfPeticionListNewPeticion = peticionListNewPeticion.getLibroId();
                    peticionListNewPeticion.setLibroId(libro);
                    peticionListNewPeticion = em.merge(peticionListNewPeticion);
                    if (oldLibroIdOfPeticionListNewPeticion != null && !oldLibroIdOfPeticionListNewPeticion.equals(libro)) {
                        oldLibroIdOfPeticionListNewPeticion.getPeticionList().remove(peticionListNewPeticion);
                        oldLibroIdOfPeticionListNewPeticion = em.merge(oldLibroIdOfPeticionListNewPeticion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = libro.getId();
                if (findLibro(id) == null) {
                    throw new NonexistentEntityException("The libro with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    @Transactional
    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        Libro libro;
        try {
            libro = em.getReference(Libro.class, id);
            libro.getId();
        } catch (EntityNotFoundException enfe) {
            throw new NonexistentEntityException("The libro with id " + id + " no longer exists.", enfe);
        }
        List<String> illegalOrphanMessages = null;
        List<Ejemplar> ejemplarListOrphanCheck = libro.getEjemplarList();
        for (Ejemplar ejemplarListOrphanCheckEjemplar : ejemplarListOrphanCheck) {
            if (illegalOrphanMessages == null) {
                illegalOrphanMessages = new ArrayList<String>();
            }
            illegalOrphanMessages.add("This Libro (" + libro + ") cannot be destroyed since the Ejemplar " + ejemplarListOrphanCheckEjemplar + " in its ejemplarList field has a non-nullable libroId field.");
        }
        List<Peticion> peticionListOrphanCheck = libro.getPeticionList();
        for (Peticion peticionListOrphanCheckPeticion : peticionListOrphanCheck) {
            if (illegalOrphanMessages == null) {
                illegalOrphanMessages = new ArrayList<String>();
            }
            illegalOrphanMessages.add("This Libro (" + libro + ") cannot be destroyed since the Peticion " + peticionListOrphanCheckPeticion + " in its peticionList field has a non-nullable libroId field.");
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        List<Autor> autorList = libro.getAutorList();
        for (Autor autorListAutor : autorList) {
            autorListAutor.getLibroList().remove(libro);
            autorListAutor = em.merge(autorListAutor);
        }
        em.remove(libro);

    }

    public List<Libro> findLibroEntities() {
        return findLibroEntities(true, -1, -1);
    }

    public List<Libro> findLibroEntities(int maxResults, int firstResult) {
        return findLibroEntities(false, maxResults, firstResult);
    }

    private List<Libro> findLibroEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Libro.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Libro findLibro(Integer id) {
        return em.find(Libro.class, id);
    }

    public int getLibroCount() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Libro> rt = cq.from(Libro.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();

    }

    public Libro findByISBN(String isbn) {
        List<Libro> listaLibros = em.createNamedQuery("Libro.findByIsbn", Libro.class).setParameter("isbn", isbn)
                .getResultList();
        if (listaLibros.isEmpty()) {
            return null;
        } else {
            return listaLibros.get(0);
        }
    }

}
