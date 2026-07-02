/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 24 de junio del 2026
 */
package mx.uv.spp.modelo;

/**
 * Representa la autoevaluación que realiza el Estudiante al concluir
 * su práctica profesional (sección 8). Contiene 10 afirmaciones con
 * escala Likert del 1 al 5. La calificación se obtiene con la fórmula:
 * {@code (puntuacionTotal / 50.0) * 10}, lo que produce un valor en
 * el rango 2.0–10.0. La entrega de la autoevaluación es irreversible.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class Autoevaluacion {

    private int idAutoevaluacion;
    private int idDocumento;
    private int afirmacion1;
    private int afirmacion2;
    private int afirmacion3;
    private int afirmacion4;
    private int afirmacion5;
    private int afirmacion6;
    private int afirmacion7;
    private int afirmacion8;
    private int afirmacion9;
    private int afirmacion10;
    private int puntuacionTotal;
    private double calificacion;

    /**
     * Constructor sin argumentos requerido para instanciación
     * desde {@code ResultSet} en la capa de persistencia.
     */
    public Autoevaluacion() {
    }

    /**
     * Constructor completo para inicializar una Autoevaluacion con
     * todos sus datos.
     *
     * @param idAutoevaluacion Identificador del registro en la BD.
     * @param idDocumento FK hacia el documento de tipo Autoevaluacion.
     * @param afirmacion1 Escala 1–5: "Mi participación en la OV fue
     * productiva."
     * @param afirmacion2 Escala 1–5: "Logré la aplicación de conocimientos
     * teórico-prácticos."
     * @param afirmacion3 Escala 1–5: "Me sentí seguro al realizar las
     * actividades."
     * @param afirmacion4 Escala 1–5: "Las actividades despertaron mi
     * interés."
     * @param afirmacion5 Escala 1–5: "La OV me proporcionó información y
     * facilidades adecuadas."
     * @param afirmacion6 Escala 1–5: "La OV me dio a conocer las reglas
     * internas."
     * @param afirmacion7 Escala 1–5: "El Responsable del Proyecto me
     * orientó correctamente."
     * @param afirmacion8 Escala 1–5: "El Responsable realizó seguimiento
     * efectivo."
     * @param afirmacion9 Escala 1–5: "El proyecto es congruente con la
     * formación de mi carrera."
     * @param afirmacion10 Escala 1–5: "Considero que las prácticas son
     * importantes para mi formación."
     * @param puntuacionTotal Suma de afirmacion1 a afirmacion10. Rango: 10–50.
     * @param calificacion Resultado de (puntuacionTotal / 50.0) * 10.
     * Rango: 2.0–10.0.
     */
    public Autoevaluacion(int idAutoevaluacion, int idDocumento,
            int afirmacion1, int afirmacion2, int afirmacion3,
            int afirmacion4, int afirmacion5, int afirmacion6,
            int afirmacion7, int afirmacion8, int afirmacion9,
            int afirmacion10, int puntuacionTotal, double calificacion) {
        this.idAutoevaluacion = idAutoevaluacion;
        this.idDocumento = idDocumento;
        this.afirmacion1 = afirmacion1;
        this.afirmacion2 = afirmacion2;
        this.afirmacion3 = afirmacion3;
        this.afirmacion4 = afirmacion4;
        this.afirmacion5 = afirmacion5;
        this.afirmacion6 = afirmacion6;
        this.afirmacion7 = afirmacion7;
        this.afirmacion8 = afirmacion8;
        this.afirmacion9 = afirmacion9;
        this.afirmacion10 = afirmacion10;
        this.puntuacionTotal = puntuacionTotal;
        this.calificacion = calificacion;
    }

    /**
     * Retorna el identificador único de la Autoevaluacion en la BD.
     *
     * @return identificador del registro.
     */
    public int getIdAutoevaluacion() {
        return idAutoevaluacion;
    }

    /**
     * Establece el identificador único de la Autoevaluacion.
     *
     * @param idAutoevaluacion Identificador asignado por la base de datos.
     */
    public void setIdAutoevaluacion(int idAutoevaluacion) {
        this.idAutoevaluacion = idAutoevaluacion;
    }

    /**
     * Retorna el identificador del documento asociado.
     *
     * @return FK hacia el documento de tipo Autoevaluacion.
     */
    public int getIdDocumento() {
        return idDocumento;
    }

    /**
     * Establece el documento asociado a esta autoevaluación.
     *
     * @param idDocumento FK hacia {@link Documento}.
     */
    public void setIdDocumento(int idDocumento) {
        this.idDocumento = idDocumento;
    }

    /**
     * Retorna el valor de la afirmación 1 (escala 1–5).
     *
     * @return valor entre 1 y 5 inclusive.
     */
    public int getAfirmacion1() {
        return afirmacion1;
    }

    /**
     * Establece el valor de la afirmación 1.
     *
     * @param afirmacion1 Valor en escala 1–5.
     */
    public void setAfirmacion1(int afirmacion1) {
        this.afirmacion1 = afirmacion1;
    }

    /**
     * Retorna el valor de la afirmación 2 (escala 1–5).
     *
     * @return valor entre 1 y 5 inclusive.
     */
    public int getAfirmacion2() {
        return afirmacion2;
    }

    /**
     * Establece el valor de la afirmación 2.
     *
     * @param afirmacion2 Valor en escala 1–5.
     */
    public void setAfirmacion2(int afirmacion2) {
        this.afirmacion2 = afirmacion2;
    }

    /**
     * Retorna el valor de la afirmación 3 (escala 1–5).
     *
     * @return valor entre 1 y 5 inclusive.
     */
    public int getAfirmacion3() {
        return afirmacion3;
    }

    /**
     * Establece el valor de la afirmación 3.
     *
     * @param afirmacion3 Valor en escala 1–5.
     */
    public void setAfirmacion3(int afirmacion3) {
        this.afirmacion3 = afirmacion3;
    }

    /**
     * Retorna el valor de la afirmación 4 (escala 1–5).
     *
     * @return valor entre 1 y 5 inclusive.
     */
    public int getAfirmacion4() {
        return afirmacion4;
    }

    /**
     * Establece el valor de la afirmación 4.
     *
     * @param afirmacion4 Valor en escala 1–5.
     */
    public void setAfirmacion4(int afirmacion4) {
        this.afirmacion4 = afirmacion4;
    }

    /**
     * Retorna el valor de la afirmación 5 (escala 1–5).
     *
     * @return valor entre 1 y 5 inclusive.
     */
    public int getAfirmacion5() {
        return afirmacion5;
    }

    /**
     * Establece el valor de la afirmación 5.
     *
     * @param afirmacion5 Valor en escala 1–5.
     */
    public void setAfirmacion5(int afirmacion5) {
        this.afirmacion5 = afirmacion5;
    }

    /**
     * Retorna el valor de la afirmación 6 (escala 1–5).
     *
     * @return valor entre 1 y 5 inclusive.
     */
    public int getAfirmacion6() {
        return afirmacion6;
    }

    /**
     * Establece el valor de la afirmación 6.
     *
     * @param afirmacion6 Valor en escala 1–5.
     */
    public void setAfirmacion6(int afirmacion6) {
        this.afirmacion6 = afirmacion6;
    }

    /**
     * Retorna el valor de la afirmación 7 (escala 1–5).
     *
     * @return valor entre 1 y 5 inclusive.
     */
    public int getAfirmacion7() {
        return afirmacion7;
    }

    /**
     * Establece el valor de la afirmación 7.
     *
     * @param afirmacion7 Valor en escala 1–5.
     */
    public void setAfirmacion7(int afirmacion7) {
        this.afirmacion7 = afirmacion7;
    }

    /**
     * Retorna el valor de la afirmación 8 (escala 1–5).
     *
     * @return valor entre 1 y 5 inclusive.
     */
    public int getAfirmacion8() {
        return afirmacion8;
    }

    /**
     * Establece el valor de la afirmación 8.
     *
     * @param afirmacion8 Valor en escala 1–5.
     */
    public void setAfirmacion8(int afirmacion8) {
        this.afirmacion8 = afirmacion8;
    }

    /**
     * Retorna el valor de la afirmación 9 (escala 1–5).
     *
     * @return valor entre 1 y 5 inclusive.
     */
    public int getAfirmacion9() {
        return afirmacion9;
    }

    /**
     * Establece el valor de la afirmación 9.
     *
     * @param afirmacion9 Valor en escala 1–5.
     */
    public void setAfirmacion9(int afirmacion9) {
        this.afirmacion9 = afirmacion9;
    }

    /**
     * Retorna el valor de la afirmación 10 (escala 1–5).
     *
     * @return valor entre 1 y 5 inclusive.
     */
    public int getAfirmacion10() {
        return afirmacion10;
    }

    /**
     * Establece el valor de la afirmación 10.
     *
     * @param afirmacion10 Valor en escala 1–5.
     */
    public void setAfirmacion10(int afirmacion10) {
        this.afirmacion10 = afirmacion10;
    }

    /**
     * Retorna la suma de las 10 afirmaciones.
     *
     * @return puntuación total en el rango 10–50.
     */
    public int getPuntuacionTotal() {
        return puntuacionTotal;
    }

    /**
     * Establece la puntuación total de la autoevaluación.
     *
     * @param puntuacionTotal Suma de afirmacion1 a afirmacion10; rango 10–50.
     */
    public void setPuntuacionTotal(int puntuacionTotal) {
        this.puntuacionTotal = puntuacionTotal;
    }

    /**
     * Retorna la calificación calculada como
     * {@code (puntuacionTotal / 50.0) * 10}.
     *
     * @return calificación en el rango 2.0–10.0.
     */
    public double getCalificacion() {
        return calificacion;
    }

    /**
     * Establece la calificación de la autoevaluación.
     *
     * @param calificacion Resultado de (puntuacionTotal / 50.0) * 10;
     * rango 2.0–10.0.
     */
    public void setCalificacion(double calificacion) {
        this.calificacion = calificacion;
    }

}
