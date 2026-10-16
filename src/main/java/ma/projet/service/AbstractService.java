package ma.projet.service;

import java.io.PrintStream;

import ma.projet.dao.AbstractDao;

/**
 * Base commune des services : elle ajoute a {@link AbstractDao} le flux de
 * sortie utilise par les methodes d'affichage.
 *
 * <p>Le flux est injectable ({@link #setOut(PrintStream)}) plutot que lie a
 * {@code System.out}, ce qui permet aux tests de capturer le rapport imprime
 * dans un {@code ByteArrayOutputStream} sans dependre de {@code System.setOut}.</p>
 *
 * @param <T> le type de l'entite geree
 */
public abstract class AbstractService<T> extends AbstractDao<T> {

    private PrintStream out = System.out;

    protected AbstractService(Class<T> entityClass) {
        super(entityClass);
    }

    public PrintStream getOut() {
        return out;
    }

    public void setOut(PrintStream out) {
        this.out = out == null ? System.out : out;
    }

    protected void println(String ligne) {
        out.println(ligne);
    }

    protected void println() {
        out.println();
    }

    protected void printf(String format, Object... arguments) {
        out.printf(format, arguments);
    }
}
