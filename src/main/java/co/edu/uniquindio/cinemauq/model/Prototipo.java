package co.edu.uniquindio.cinemauq.model;

/** Patrón Prototype: un objeto que sabe crear una copia de sí mismo. */
public interface Prototipo<T> {
    T clonar();
}
