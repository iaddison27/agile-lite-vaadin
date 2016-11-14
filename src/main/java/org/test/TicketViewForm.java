package org.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.test.dto.Attachment;
import org.test.dto.Subtask;
import org.test.dto.Ticket;
import org.test.dto.UserStory;
import org.test.service.ITicketService;
import org.test.util.ApplicationUtils;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.FooterRow;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.renderers.ImageRenderer;
import com.vaadin.ui.themes.ValoTheme;

// Alias for Spring's @Component
@SpringComponent
public class TicketViewForm extends VerticalLayout {
	
	// Services
	@Autowired
	private ITicketService ticketService;
	
	@Autowired
	private ApplicationUtils applicationUtils;

	BeanFieldGroup<Ticket> b = new BeanFieldGroup<>(Ticket.class);
	private Ticket ticket;

	private TextField storyPoints = new TextField("Story Points");
	private TextField title = new TextField("Title");
	private TextArea description = new TextArea("Description");
	private Table attachments = new Table("Attachments");
	private Grid acceptanceCriteriaGrid = new Grid("Acceptance Criteria");
	private Grid grid = new Grid("Sub Tasks");
	
	public TicketViewForm() {
		title.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
		description.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
		storyPoints.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
		
	    title.setWidth(100f, Unit.PERCENTAGE);
	    description.setWidth(100f, Unit.PERCENTAGE);
	    
	    setMargin(true);
	    setSpacing(true);
	    
	    initAttachmentGrid();
	    
	    // Bind the form fields to the bean properties
	    b.setItemDataSource(ticket);
	    b.bind(storyPoints, "storyPoints");
	    b.bind(title, "Title");
	    b.bind(description, "Description");
	    
	    addComponents(title, storyPoints, description, acceptanceCriteriaGrid, attachments, grid);
	}
	
	public void setTicket(Long id) {
		// Load the ticket so we have the most recent version, and also we can access Lazy properties
		ticket = ticketService.getById(id);
	    b.setItemDataSource(ticket);
	    
	    initAcceptanceCriteriaGrid();
	    initSubtaskGrid();
	    
	    List<Attachment> files = ticket.getAttachments();
		attachments.setContainerDataSource(new BeanItemContainer<>(Attachment.class, files));
		attachments.setPageLength(files.size());
		attachments.setVisibleColumns( new Object[] {"filename", "download"} );
		//attachments.setColumnHeaders( new String[] {"File", ""} );
		attachments.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);

	    setVisible(true);
	    
	    storyPoints.setReadOnly(true);
	    title.setReadOnly(true);
	    description.setReadOnly(true);
	}
	
	private void initAttachmentGrid() {
		// Using a Table over Grid as you need a reference to the Button so the FileDownloader can extend it:
		// http://stackoverflow.com/questions/36773203/how-to-download-file-on-button-click-in-grid-vaadin
		// It's also easier to render a FontAwesome icon button in a Table than a Grid		
		
		// Generated column to display a "Edit" button        
		attachments.addGeneratedColumn("download", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
            	// Button icon
            	Button editBtn = new Button();
            	editBtn.setIcon(FontAwesome.DOWNLOAD);
            	editBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
            	editBtn.setDescription("Download");
            	// Do not wrap this code in an addClickListener
        		StreamResource sr = getFileStream((Attachment)itemId);
        		FileDownloader fileDownloader = new FileDownloader(sr);
        		fileDownloader.extend(editBtn);
            	return editBtn;
            }
		});
	}
	
	private StreamResource getFileStream(Attachment attachment) {
		StreamResource.StreamSource source = new StreamResource.StreamSource() {
			public InputStream getStream() {
				InputStream input = new ByteArrayInputStream(attachment.getFile());
				return input;
			}
		};

		StreamResource resource = new StreamResource(source, attachment.getFilename());
		return resource;
	}
	
	private void initAcceptanceCriteriaGrid() {
		if (ticket instanceof UserStory) {
			acceptanceCriteriaGrid.setVisible(true);
			
			List<String> acceptanceCriteria = ((UserStory) ticket).getAcceptanceCriteria();
			GeneratedPropertyContainer gpContainer = new GeneratedPropertyContainer(new BeanItemContainer<>(String.class, acceptanceCriteria));
		    // Generated column to display the acceptance criteria text (as it is a String property)
			// This feels a bit of a "hacky" way to do this, but struggling to come up with something better
		    gpContainer.addGeneratedProperty("acceptanceCriteria", new PropertyValueGenerator<String>() {
				public String getValue(Item item, Object itemId, Object propertyId) {
					return (String) itemId;
				}
				
				public Class<String> getType() {
					return String.class;
				}
	 	    });
			
		    acceptanceCriteriaGrid.setContainerDataSource(gpContainer);
		    acceptanceCriteriaGrid.setHeightMode(HeightMode.ROW);
		    acceptanceCriteriaGrid.setHeightByRows(acceptanceCriteria.size());
		    acceptanceCriteriaGrid.setColumns("acceptanceCriteria");
		    acceptanceCriteriaGrid.setHeaderVisible(false);
		} else {
			acceptanceCriteriaGrid.setVisible(false);
		}
	}
	
	private void initSubtaskGrid() {
		List<Subtask> subtasks = ticket.getSubtasks();
		GeneratedPropertyContainer gpContainer = new GeneratedPropertyContainer(new BeanItemContainer<>(Subtask.class, subtasks));
	    // Generated column to display if a ticket is done
	    gpContainer.addGeneratedProperty("complete", new PropertyValueGenerator<Resource>() {
			public Resource getValue(Item item, Object itemId, Object propertyId) {
				return new ThemeResource("images/subtask-state/" + ((Subtask)itemId).getState().toString().toLowerCase() + ".png");
			}
			
			public Class<Resource> getType() {
				return Resource.class;
			}
 	    });
	    
	    grid.setContainerDataSource(gpContainer);
	    grid.setHeightMode(HeightMode.ROW);
	    grid.setHeightByRows(subtasks.size());
	    grid.setColumns("complete", "title", "estimate");
	    grid.getColumn("complete").setRenderer(new ImageRenderer());
	    // Footer
	    if (grid.getFooterRowCount() > 0) {
	    	grid.removeFooterRow(0);
	    }
	    FooterRow footer = grid.appendFooterRow();
	    footer.getCell("estimate").setText("Total: " + applicationUtils.getFormattedDaysAndHours(ticket.getSubtasksTotalEstimate()));
	}
}
