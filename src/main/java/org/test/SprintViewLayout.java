package org.test;

import java.util.List;

import org.test.dto.Subtask;
import org.test.dto.SubtaskState;
import org.test.service.ITicketService;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.VerticalLayout;

public class SprintViewLayout extends VerticalLayout {

	// Services
	private ITicketService ticketService;// = MockTicketService.getInstance();
	
	// UI Components
	private Table todo = new Table("To Do");
	private Table inProgress = new Table("In Progress");
	private Table done = new Table("Done");
	
	public SprintViewLayout(MyUI myUI, ITicketService ticketService) {
		this.ticketService = ticketService;
		Button backlogBtn = new Button("Backlog");
		backlogBtn.addClickListener(e -> {
			myUI.getNavigator().navigateTo("Backlog");
		});
		initGrid(todo, ticketService.getSubtasksByState(SubtaskState.TODO));
		initGrid(inProgress, ticketService.getSubtasksByState(SubtaskState.IN_PROGRESS));
		initGrid(done, ticketService.getSubtasksByState(SubtaskState.DONE));
		
		HorizontalLayout sprintLayout = new HorizontalLayout();
		sprintLayout.setWidth(100, Unit.PERCENTAGE);
		sprintLayout.setSpacing(true);
		sprintLayout.addComponents(todo, inProgress, done);
		addComponents(backlogBtn, sprintLayout);
		setSpacing(true);
		
	}
	
	private void initGrid(Table grid, List<Subtask> subtasks) {
		grid.setWidth(100, Unit.PERCENTAGE);
		
		// Custom cell layout so we can display the subtask title with the parent ticket
		grid.addGeneratedColumn("custom", new Table.ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				return "";
			}
		});
		
		// Custom cell layout so we can display the subtask title with the parent ticket
		grid.addGeneratedColumn("custom2", new Table.ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				Subtask subtask = (Subtask) itemId;
				VerticalLayout cell = new VerticalLayout();
				cell.addComponent(new Label(subtask.getParent().getTitle()));
				cell.addComponent(new Label(subtask.getTitle()));
				return cell;
			}
		});
		
		configureColumns(grid, subtasks);
		
		grid.setDragMode(TableDragMode.ROW);
		grid.setDropHandler(new DropHandler() {
			@Override
			public AcceptCriterion getAcceptCriterion() {
				return AcceptAll.get();
			}
			
			@Override
			public void drop(DragAndDropEvent event) {
				// Wrapper for the object that is dragged
				DataBoundTransferable t = (DataBoundTransferable) event.getTransferable();
				 
				 // Get the drop location
				AbstractSelectTargetDetails target = (AbstractSelectTargetDetails) event.getTargetDetails();
		        		        
		        // Item moved
				Subtask itemMoved = (Subtask)t.getItemId();
				
				// Dropped at
				SubtaskState state = null;
				if (target.getTarget() == todo) {
					state = SubtaskState.TODO;
				} else if (target.getTarget() == inProgress) {
					state = SubtaskState.IN_PROGRESS;
				} else if (target.getTarget() == done) {
					state = SubtaskState.DONE;
				}
		        
		        // Update the Subtask state and ensure the change is reflected in the grid
		        ticketService.updateSubtaskState(itemMoved, state);		        
		        updateView();
			}
		});
	}
	
	private void configureColumns(Table grid, List<Subtask> subtasks) {
		grid.setContainerDataSource(new BeanItemContainer<>(Subtask.class, subtasks));
		grid.setVisibleColumns( new Object[] {"custom", "custom2"} );
		grid.setColumnHeaders( new String[] {"", ""} );
	}
	
	public void updateView() {
		configureColumns(todo, ticketService.getSubtasksByState(SubtaskState.TODO));
		configureColumns(inProgress, ticketService.getSubtasksByState(SubtaskState.IN_PROGRESS));
		configureColumns(done, ticketService.getSubtasksByState(SubtaskState.DONE));
	}
}
