package org.test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.test.dto.Ticket;
import org.test.dto.TicketType;
import org.test.factory.TicketFactory;
import org.test.service.ITicketService;
import org.test.service.ITicketTypeService;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroupFieldFactory;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.FieldEvents.TextChangeNotifier;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ErrorMessage;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringView(name = CreateTicketView.NAME)
public class CreateTicketView extends VerticalLayout implements View {

	public static final String NAME = "CreateTicket";
	
	@Autowired
	private MyUI myUI;
	
	// Services
	@Autowired
	private ITicketService ticketService;

	@Autowired
	private ITicketTypeService ticketTypeService;
	
	@Autowired
	private TicketFactory ticketFactory;
	
	// UI components
	private HorizontalLayout mainContent = new HorizontalLayout();
	private VerticalLayout lhs = new VerticalLayout();
	private VerticalLayout rhs = new VerticalLayout();
	
	private Panel detailsPanel = new Panel();
	private Panel acceptanceCriteriaPanel = new Panel("Acceptance Criteria");
	private Panel subtasksPanel = new Panel("Sub Tasks");
	private Panel uploadsPanel = new Panel("Attachments");
	
	private Grid attachmentsGrid = new Grid();
	private Grid acceptanceCriteriaGrid = new Grid();
	private Button addAcceptanceCriteria = new Button("Add");
	private Grid subtasksGrid = new Grid();
	
	private BeanFieldGroup<CreateTicketBackingBean> form;
	private SubtaskForm rhsSubtaskForm;
	
	private Button save = new Button("Save");
	private Button cancel = new Button("Cancel");
	
	// Backing bean
	private CreateTicketBackingBean ticket = new CreateTicketBackingBean();
	
	@PostConstruct
    void init() {
		Label title = new Label("Create Ticket");
		title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H2);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		
		
		// Construct layout
		initLhs();
		initRhs();
		
		//addComponents(lhs, rhs);
		mainContent.addComponents(lhs, rhs);
		mainContent.setSpacing(true);
		mainContent.setWidth(100, Unit.PERCENTAGE);
		
		setMargin(true);
		setSpacing(true);
		addComponents(title, mainContent);
    }
	
	private void setTicket(CreateTicketBackingBean ticket) {
		this.ticket = ticket;
		form.setItemDataSource(ticket);
		refreshSubtaskDatasource();
	}

	private void addFormChangeListeners() {
		TextChangeListener textListener = new TextChangeListener() {
			
			 @Override
		    public void textChange(TextChangeEvent event) {
				 validateForm();
		    }
		};

		ValueChangeListener valueListener = new ValueChangeListener() {
		    @Override
		    public void valueChange(ValueChangeEvent event) {
		    	validateForm();
		    }
		};

		for (Field<?> field : form.getFields()) {
		    if (field instanceof TextChangeNotifier) {
		        ((TextChangeNotifier) field).addTextChangeListener(textListener);
		    } else {
		    	field.addValueChangeListener(valueListener);
		    }
		}
	}
	
	private void validateForm() {
		save.setEnabled(form.isValid());
	}
	
	private void initLhs() {
		lhs.setSpacing(true);
		lhs.setWidth(100, Unit.PERCENTAGE);
		
		form = new BeanFieldGroup<CreateTicketBackingBean>(CreateTicketBackingBean.class);
		form.setItemDataSource(ticket);
		// Ensure the value displayed for null is the empty string ("")
		form.setFieldFactory(new FieldGroupFieldFactory() {
			@Override
			public <T extends Field> T createField(Class<?> dataType, Class<T> fieldType) {
				if (fieldType.equals(TextArea.class)) {
					TextArea field = new TextArea();
					field.setNullRepresentation("");
					return fieldType.cast(field);
				} else {
					TextField field = new TextField();
					field.setNullRepresentation("");
					return fieldType.cast(field);
				}
			}
		});
		
		// https://vaadin.com/wiki/-/wiki/Main/Using+Bean+Validation+to+validate+input
		addTicketTypesComboBox();
		
		TextField titleField = (TextField) (form.buildAndBind("Title", "title"));
		TextArea descriptionField = (TextArea) (form.buildAndBind("Description", "description", TextArea.class));
		TextField storyPointsField = (TextField) form.buildAndBind("Story Points", "storyPoints");
		
		
		titleField.setWidth(100, Unit.PERCENTAGE);
		descriptionField.setWidth(100, Unit.PERCENTAGE);
		
		VerticalLayout content = new VerticalLayout();
		content.setMargin(true);
		content.setSpacing(true);
		content.addComponent(titleField);
		content.addComponent(descriptionField);
		content.addComponent(storyPointsField);
		detailsPanel.setContent(content);
		lhs.addComponent(detailsPanel);
		
		addFileUploads();
		addAcceptanceCriteriaGrid();
		
		// Add listeners so we can enable/disable the Submit button depending on if the form is valid or not
		addFormChangeListeners();
		// Ensure Save button is set to false initially
		validateForm();
	}
	
	private void initRhs() {
		rhsSubtaskForm = new SubtaskForm(this);
		rhsSubtaskForm.setVisible(false);
		
		addSubtaskGrid();
		
		save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		save.addClickListener(e-> save());
		
		cancel.addClickListener(e -> discard());
		
		HorizontalLayout buttons = new HorizontalLayout(save, cancel);
	    buttons.setSpacing(true);
	    rhs.addComponent(buttons);
	    
		rhs.setSpacing(true);
		rhs.setWidth(100, Unit.PERCENTAGE);
	}
	
	private void addFileUploads() {
		// File uploads
		VerticalLayout content = new VerticalLayout();
		content.setMargin(true);
		content.setSpacing(true);
		
		ImageUploader receiver = new ImageUploader();
		Upload upload = new Upload("Upload it here", receiver);
		upload.addSucceededListener(receiver);
		content.addComponent(upload);
		
		attachmentsGrid.setContainerDataSource(new BeanItemContainer<>(FileUpload.class));
		attachmentsGrid.setHeightMode(HeightMode.ROW);
		attachmentsGrid.setHeightByRows(5.0);
		attachmentsGrid.setColumns("filename");
		attachmentsGrid.setHeaderVisible(false);
		content.addComponent(attachmentsGrid);
		
		uploadsPanel.setContent(content);
		lhs.addComponent(uploadsPanel);
	}
	
	private void addTicketTypesComboBox() {
		ComboBox ticketTypes = new ComboBox("Type");
		BeanItemContainer<TicketType> ticketTypeOptions = new BeanItemContainer<>(TicketType.class);
		ticketTypeOptions.addAll(ticketTypeService.getTicketTypes());
		ticketTypes.setContainerDataSource(ticketTypeOptions);
		ticketTypes.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		ticketTypes.setItemCaptionPropertyId("type");
		// Display icons in the combo box
		ticketTypes.setItemIconPropertyId("iconResource");
		
		// Ticket type changed, decide whether to show/hide acceptance criteria
		ticketTypes.addBlurListener(e -> {
			// TODO: Define whether acceptanceCriteria is permitted on TicketType
			if (ticketTypes.getValue() != null && "User Story".equals(((TicketType)ticketTypes.getValue()).getType())) {
				acceptanceCriteriaPanel.setVisible(true);
			} else {
				acceptanceCriteriaPanel.setVisible(false);
			}
		});
		
		form.bind(ticketTypes, "ticketType");
		lhs.addComponent(ticketTypes);
	}
	
	private void addAcceptanceCriteriaGrid() {
		VerticalLayout content = new VerticalLayout();
		content.setMargin(true);
		content.setSpacing(true);
		
		// Unable to find a way to bind a Grid to List<String> so need the AcceptanceCriteria class as an intermediatory
		acceptanceCriteriaGrid.setContainerDataSource(new BeanItemContainer<AcceptanceCriteria>(AcceptanceCriteria.class, ticket.getAcceptanceCriteria()));
		acceptanceCriteriaGrid.setEditorEnabled(true);
		acceptanceCriteriaGrid.setHeightMode(HeightMode.ROW);
		acceptanceCriteriaGrid.setHeightByRows(5.0);
		acceptanceCriteriaGrid.setColumns("acceptanceCriteria");
		acceptanceCriteriaGrid.setHeaderVisible(false);
		
		// https://vaadin.com/forum#!/thread/9781195
		addAcceptanceCriteria.addClickListener(e -> {
			AcceptanceCriteria newCriteria = new AcceptanceCriteria("");
			ticket.getAcceptanceCriteria().add(newCriteria);
			acceptanceCriteriaGrid.setContainerDataSource(new BeanItemContainer<AcceptanceCriteria>(AcceptanceCriteria.class, ticket.getAcceptanceCriteria()));
			acceptanceCriteriaGrid.editItem(newCriteria);
		});
		
		content.addComponents(acceptanceCriteriaGrid, addAcceptanceCriteria);
		acceptanceCriteriaPanel.setContent(content);
		lhs.addComponent(acceptanceCriteriaPanel);
	}
	
	private void addSubtaskGrid() {
		VerticalLayout content = new VerticalLayout();
		content.setMargin(true);
		content.setSpacing(true);
		
		refreshSubtaskDatasource();
		subtasksGrid.setHeightMode(HeightMode.ROW);
		subtasksGrid.setHeightByRows(5.0);
		subtasksGrid.setColumns("title", "estimate");
		// Allocate all extra space to the "title" column
		subtasksGrid.getColumn("title").setExpandRatio(1);
		
		subtasksGrid.addSelectionListener(e -> {
			if (e.getSelected().isEmpty()) {
		        rhsSubtaskForm.setVisible(false);
		    } else {
		    	SubtaskBackingBean subtask = (SubtaskBackingBean) e.getSelected().iterator().next();
		    	rhsSubtaskForm.setSubtaskBackingBean(subtask);
		    	rhsSubtaskForm.setVisible(true);
		    }
		});
		
		Button addSubtaskBtn = new Button("New Subtask");
		addSubtaskBtn.addClickListener(e -> {
			rhsSubtaskForm.setSubtaskBackingBean(new SubtaskBackingBean());
			rhsSubtaskForm.setVisible(true);
		});
		
		content.addComponents(subtasksGrid, addSubtaskBtn, rhsSubtaskForm);
		subtasksPanel.setContent(content);
		rhs.addComponent(subtasksPanel);
	}
	
	private void refreshSubtaskDatasource() {
		subtasksGrid.setContainerDataSource(new BeanItemContainer<>(SubtaskBackingBean.class, ticket.getSubtasks()));
	}
	
	public void addToSubtaskGrid(SubtaskBackingBean subtask) {
		rhsSubtaskForm.setVisible(false);
		ticket.getSubtasks().add(subtask);
		refreshSubtaskDatasource();
		subtasksGrid.setColumns("title", "estimate");
	}
	
	private void save() {
		try {
			// Save the data
            form.commit();
            if (ticket.getId() == null) {
            	ticketService.createOrUpdateTicket(ticket);
            } else {
            	ticketService.createOrUpdateTicket(ticket);
            }
            // Reset the form
            form.discard();
            
            // Navigate back to the backlog and display a notification
            myUI.getNavigator().navigateTo("Backlog");
            Notification.show("Ticket saved successfully");
        } catch (CommitException e) {
            for (Field<?> field: form.getFields()) {
                ErrorMessage errMsg = ((AbstractField<?>)field).getErrorMessage();
                if (errMsg != null) {
                    System.out.println("Error in " +
                            field.getCaption() + ": " +
                            errMsg.getFormattedHtmlMessage());
                    break;
                }
            }
        }
	}
	
	private void discard() {
		form.discard();
		myUI.getNavigator().navigateTo("Backlog");
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		if (event.getParameters() != null && !event.getParameters().isEmpty()) {
			Long ticketId = Long.valueOf(event.getParameters());
			Ticket ticket = ticketService.getById(ticketId);
			if (ticket != null) {
				// Convert ticket to CreateTicketBackingBean
				CreateTicketBackingBean newTicket = ticketFactory.createTicketBackingBean(ticket);
				setTicket(newTicket);
			}
		} else {
			setTicket(new CreateTicketBackingBean());
		}
	}
	
	// Implement both receiver that saves upload in a file and listener for successful upload
	private class ImageUploader implements Receiver, SucceededListener {
	    
	    public ByteArrayOutputStream byteArray;

	    public OutputStream receiveUpload(String filename, String mimeType) {	    	
	    	// Create upload stream
	    	byteArray = new ByteArrayOutputStream(); // Stream to write to
	        return byteArray; // Return the output stream to write to
	    }

	    public void uploadSucceeded(SucceededEvent event) {
	        // Add the upload to the UI
			FileUpload upload = new FileUpload(event.getFilename(), byteArray);
			attachmentsGrid.getContainerDataSource().addItem(upload);
			
			// Add the upload to the CreateTicketBackingBean
			if (ticket.getAttachments() == null) {
				ticket.setAttachment(new ArrayList<>());
			}
			ticket.getAttachments().add(upload);
		}
	};

}
