package org.remast.baralga.model.filter;

import java.util.Date;

import org.apache.commons.collections.Predicate;
import org.remast.baralga.model.ProjectActivity;
import org.remast.util.DateUtils;
import org.remast.util.TextResourceBundle;

/**
 * Holds for all project activities of one year.
 * @author remast
 */
public class YearPredicate implements Predicate {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(YearPredicate.class);

    /**
     * The year to check for.
     */
    private final Date dateInYear;

    /**
     * Constructor for a new predicate.
     * @param dateInYear the year of the predicate
     */
    public YearPredicate(final Date dateInYear) {
        this.dateInYear = dateInYear;
    }

    /**
     * Checks if this predicate holds for the given object.
     * @param object the object to check
     * @return <code>true</code> if the given object is a project activity
     * of that year else <code>false</code>
     */
    public final boolean evaluate(final Object object) {
        if (object == null) {
            return false;
        }

        if (!(object instanceof ProjectActivity)) {
            return false;
        }

        final ProjectActivity activity = (ProjectActivity) object;
        return DateUtils.isSameYear(activity.getStart(), this.dateInYear);
    }

}