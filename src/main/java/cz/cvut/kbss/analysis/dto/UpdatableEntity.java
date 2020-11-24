package cz.cvut.kbss.analysis.dto;


public interface UpdatableEntity<E> {

    void copyToEntity(E entity);

}
