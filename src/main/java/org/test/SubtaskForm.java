package org.test;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroupFieldFactory;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.ErrorMessage;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

public class SubtaskForm extends FormLayout {
	
	private SubtaskBackingBean subtask;
	private CreateTicketView createTicketForm;
	
	private BeanFieldGroup<SubtaskBackingBean> form;
	
	private TextField title;
	private TextArea description;
	private TextField estimate;
	
	private Button save = new Button("Save");
	private Button delete = new Button("Delete");
	
	public SubtaskForm(CreateTicketView createTicketForm) {
		this.createTicketForm = createTicketForm;
		
		initForm();
		
		save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		save.addClickListener(e-> this.save());
		HorizontalLayout buttons = new HorizontalLayout(save, delete);
	    buttons.setSpacing(true);
	    addComponents(title, description, estimate, buttons);
	    
	    setWidth(100, Unit.PERCENTAGE);
	}
	
	private void initForm() {
		// Can't use "form = BeanFieldGroup.bindFieldsUnbuffered(subtask, this);" as we want to use a fieldFactory
	    form = new BeanFieldGroup<SubtaskBackingBean>(SubtaskBackingBean.class);
	    form.setItemDataSource(subtask);
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
 		
 		title = (TextField) form.buildAndBind("Title", "title");
 		description = (TextArea) form.buildAndBind("Description", "description", TextArea.class);
 		estimate = (TextField) form.buildAndBind("Estimate", "estimate");
 		
 		title.setWidth(100, Unit.PERCENTAGE);
 		description.setWidth(100, Unit.PERCENTAGE);
	}
	
	private void save() {
		try {
			// Save the data
	        form.commit();
	        
	        // Add to grid on LHS
			createTicketForm.addToSubtaskGrid(subtask);
			
			// Reset the form
			form.discard();
		} catch (CommitException e) {
			e.printStackTrace();
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

	public void setSubtaskBackingBean(SubtaskBackingBean subtask) {
	    this.subtask = subtask;
	    form.setItemDataSource(subtask);
	    title.selectAll();
	}

}
