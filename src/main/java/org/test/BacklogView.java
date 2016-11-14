package org.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.test.dto.Ticket;
import org.test.dto.TicketLocation;
import org.test.service.ITicketService;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;


@SpringView(name = BacklogView.NAME)
public class BacklogView extends HorizontalSplitPanel implements View {

	public static final String NAME = "Backlog";
	
	@Autowired
	private MyUI myUI;
	
	// Services
	@Autowired
	private ITicketService ticketService;
	
	// UI Components
	private VerticalLayout lhs = new VerticalLayout();
	
	@Autowired
	private TicketViewForm rhs;
	private Table sprintGrid = new Table("Current Sprint");
	private Table backlogGrid = new Table("Backlog");
	private Button moveToSprint = new Button("To Sprint");
	private Button moveToBacklog = new Button("To Backlog");
	
	
	@PostConstruct
    void init() {
		// Construct layout
		initLhs(myUI);
		initRhs();
		addComponents(lhs, rhs);
		
		// 65/35 split
		setSplitPosition(65, Sizeable.Unit.PERCENTAGE);
    }
	
	private void initLhs(MyUI myUI) {
		Button createTicketBtn = new Button("Create Ticket");
		createTicketBtn.addClickListener(e -> {
			myUI.getNavigator().navigateTo("CreateTicket");
		});
		
		Button sprintViewBtn = new Button("Start Sprint");
		sprintViewBtn.addClickListener(e -> {
			myUI.getNavigator().navigateTo("Sprint");
		});
		
		initGrid(sprintGrid, TicketLocation.CURRENT_SPRINT);
		initGrid(backlogGrid, TicketLocation.BACKLOG);
		
		initGridMovement(backlogGrid, sprintGrid, moveToSprint, TicketLocation.CURRENT_SPRINT);
		initGridMovement(sprintGrid, backlogGrid, moveToBacklog, TicketLocation.BACKLOG);
		
		HorizontalLayout moveButtons = new HorizontalLayout(moveToSprint, moveToBacklog);
		
		lhs.setWidth(100, Sizeable.Unit.PERCENTAGE);
		lhs.setSpacing(true);
		lhs.addComponents(createTicketBtn, sprintViewBtn, sprintGrid, moveButtons, backlogGrid);
	}
	
	private void initRhs() {
		//rhs = new TicketViewForm();
		rhs.setWidth(100, Sizeable.Unit.PERCENTAGE);
	}
	
	private void initGrid(Table grid, TicketLocation location) {
		// Generated column to display an icon corresponding to the type of Ticket
		grid.addGeneratedColumn("icon", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                    return new Embedded("",new ThemeResource("images/" + ((Ticket) itemId).getIconName() + ".png"));
            } 
		});
		
		// Generated column to display if a ticket is done
		grid.addGeneratedColumn("complete", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
            	if (((Ticket) itemId).getDone() == Boolean.TRUE) {
            		return new Embedded("",new ThemeResource("images/subtask-state/done.png"));
            	}
                    return new Embedded("");
            } 
		});
		
		// Generated column to display a "Edit" button        
		grid.addGeneratedColumn("edit", new Table.ColumnGenerator() {
			
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
            	// Button icon
            	Button editBtn = new Button();
            	editBtn.setIcon(FontAwesome.EDIT);
            	editBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
            	editBtn.setDescription("Edit Ticket");
            	editBtn.addClickListener(e -> {
            		myUI.getNavigator().navigateTo("CreateTicket/" + ((Ticket) itemId).getId());
            	});
            	return editBtn;
            } 
		});
		
		
		
		//updateList(grid, location);
		
		// Width and column widths
		grid.setWidth(100, Sizeable.Unit.PERCENTAGE);
		grid.setColumnExpandRatio("icon",0.1f);
		grid.setColumnExpandRatio("title",2f);
		grid.setColumnExpandRatio("storyPoints",0.7f);
		grid.setColumnExpandRatio("complete",0.3f);
		grid.setColumnExpandRatio("edit",0.25f);
		
		// Drag and drop config
		grid.setDragMode(TableDragMode.MULTIROW);
		grid.setDropHandler(new DropHandler() {
			@Override
			public AcceptCriterion getAcceptCriterion() {
				return AcceptAll.get();
			}
			
			@Override
			public void drop(DragAndDropEvent event) {
				// Wrapper for the object that is dragged
				DataBoundTransferable t = (DataBoundTransferable) event.getTransferable();
				
				// Source of this DragAndDropEvent is not the backlog grid, so return
				if (t.getSourceComponent() != grid)
					return;
				
				TicketLocation ticketLocation = null;
				if (t.getSourceComponent() == backlogGrid) {
					ticketLocation = TicketLocation.BACKLOG;
				} else if (t.getSourceComponent() == sprintGrid) {
					ticketLocation = TicketLocation.CURRENT_SPRINT;
				}
				 
				 // Get the drop location
				AbstractSelectTargetDetails target = (AbstractSelectTargetDetails) event.getTargetDetails();
				VerticalDropLocation location = target.getDropLocation();
		        
		        String beforeAfter = "before";
		        if (location == VerticalDropLocation.TOP) {
		        	System.out.println("dropped at top - so put above");
		        } else if (location == VerticalDropLocation.MIDDLE) {
		        	System.out.println("dropped at middle - so put above");
		        } else if (location == VerticalDropLocation.BOTTOM) {
		        	System.out.println("dropped at bottom - so put below");
		        	beforeAfter = "after";
		        }
		        
		        // Item moved
		        Ticket itemMoved = (Ticket)t.getItemId();
		        // Dropped at
		        Ticket targetItemId = (Ticket) target.getItemIdOver();
		        
		        // Move the ticket and ensure the change is reflected in the grid
		        ticketService.moveTicket(itemMoved, targetItemId, beforeAfter);
		        updateList(grid, ticketLocation);
			}
		});
	}
	
	/**
	 * Updates the UI from the service so the grid displays the most recent data
	 */
	private void updateList(Table grid, TicketLocation location) {
		List<Ticket> tickets = ticketService.getAllTicketsByLocation(location);
		// Need to set the visible columns otherwise columns not displayed (e.g. subtasks, attachments) will
		// be accessed and therefore Hibernate will perform irrelevent queries
		Collection<String> visibleColumns = Arrays.asList(new String[] {"icon", "title", "storyPoints", "complete", "edit"});
		grid.setContainerDataSource(new BeanItemContainer<>(Ticket.class, tickets), visibleColumns);
		grid.setPageLength(tickets.size());
		//grid.setVisibleColumns( new Object[] {"icon", "title", "storyPoints", "complete", "edit"} );
		grid.setColumnHeaders( new String[] {"", "Title", "Story Points", "", ""} );
		// Footer to display total number of story points
		grid.setFooterVisible(true);
		grid.setColumnFooter("title", "Total Story Points");
		grid.setColumnFooter("storyPoints", Integer.toString(ticketService.sumStoryPoints(tickets)));
	}
	
	private void initGridMovement(Table grid, Table otherGrid, Button button, TicketLocation location) {
		// Move ticket from backlog to current sprint
		grid.addValueChangeListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		    	if (grid.getValue() == null) {
		    		button.setEnabled(false);
		    		rhs.setVisible(false);
			    } else {
			    	otherGrid.setValue(null);
			    	button.setEnabled(true);
			    	Ticket ticket = (Ticket) grid.getValue();
			    	rhs.setTicket(ticket.getId());
			    }
		    }
		});
		
		button.setEnabled(false);
		button.addClickListener(e -> {
			ticketService.updateLocation((Ticket) grid.getValue(), location);
			
			updateList(sprintGrid, TicketLocation.CURRENT_SPRINT);
			updateList(backlogGrid, TicketLocation.BACKLOG);
		});
	}
	
	@Override
    public void enter(ViewChangeEvent event) {
		// Ensure both grids display the most recent data
		updateList(sprintGrid, TicketLocation.CURRENT_SPRINT);
		updateList(backlogGrid, TicketLocation.BACKLOG);
		
		// TODO: When launching the application this method receives a ViewChangeEvent where "old" is null
		// it then receives another where old is the BacklogView
		// Is it this bug? https://vaadin.com/forum/#!/thread/3395652/3395651
    }

}
