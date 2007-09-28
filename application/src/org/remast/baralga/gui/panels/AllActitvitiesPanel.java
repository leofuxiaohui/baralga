package org.remast.baralga.gui.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;
import org.remast.baralga.Messages;
import org.remast.baralga.gui.events.ProTrackEvent;
import org.remast.baralga.gui.model.AllActivitiesTableFormat;
import org.remast.baralga.gui.utils.GUISettings;
import org.remast.baralga.model.PresentationModel;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.EventComboBoxModel;
import ca.odell.glazedlists.swing.EventTableModel;

/**
 * @author Jan Stamer
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class AllActitvitiesPanel extends JXPanel implements Observer {

    private final PresentationModel model;

    private Filter filter;

    private EventList<ProjectActivity> filteredActivitiesList;

    public AllActitvitiesPanel(PresentationModel model) {
        this.model = model;
        this.filteredActivitiesList = new BasicEventList<ProjectActivity>();
        this.model.addObserver(this);
        this.filter = this.model.getFilter();
        
        this.setLayout(new BorderLayout());

        initialize();
    }

    private void applyFilter() {
        // clear filtered activities
        filteredActivitiesList.clear();

        if(filter != null) {
            List<ProjectActivity> filteredResult = filter.applyFilters(model.getActivitiesList());
            filteredActivitiesList.addAll(filteredResult);
        } else {
            filteredActivitiesList.addAll(model.getActivitiesList());
        }
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        applyFilter();

        EventTableModel<ProjectActivity> tableModel = new EventTableModel<ProjectActivity>(this.filteredActivitiesList,
                new AllActivitiesTableFormat(model));
        final JXTable table = new JXTable(tableModel);


        final JPopupMenu menu = new JPopupMenu();
        menu.add(new AbstractAction(Messages.getString("AllActitvitiesPanel.Delete")) { //$NON-NLS-1$

            public void actionPerformed(ActionEvent arg0) {
                // 1. Get selected activities
                int[] selectionIndices = table.getSelectedRows();
                
                // 2. Remove all selected activities
                for (int j = 0; j < selectionIndices.length; j++) {
                    model.removeActivity(filteredActivitiesList.get(selectionIndices[j]));
                }
            }
            
        });
        
        table.addMouseListener( new MouseAdapter()
        {
            public void mouseReleased(MouseEvent e)
            {
                if (e.isPopupTrigger())
 
                {
                    JTable source = (JTable)e.getSource();
                    int row = source.rowAtPoint( e.getPoint() );
                    int column = source.columnAtPoint( e.getPoint() );
                    source.changeSelection(row, column, false, false);
 
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        
        table.setHighlighters(GUISettings.HIGHLIGHTERS);
        table.setCellEditor(new JXTable.GenericEditor());
        
        TableColumn projectColumn = table.getColumn(0);
        projectColumn.setCellEditor(new ComboBoxCellEditor(new JComboBox(new EventComboBoxModel<Project>(model.getProjectList()))));

        JScrollPane table_scroll_pane = new JScrollPane(table);
        this.add(table_scroll_pane);
    }

    /**
     * @return the filter
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * Set and apply new filter.
     * @param filter the filter to set
     */
    public void setFilter(Filter filter) {
        this.filter = filter;        
        applyFilter();
    }

    public void update(Observable source, Object eventObject) {
        if (eventObject instanceof ProTrackEvent) {
            ProTrackEvent event = (ProTrackEvent) eventObject;

            switch (event.getType()) {

            case ProTrackEvent.PROJECT_ACTIVITY_ADDED:
                applyFilter();
                break;

            case ProTrackEvent.PROJECT_ACTIVITY_REMOVED:
                applyFilter();
                break;
            }
        }
    }

}
