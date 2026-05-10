package com.se4347.database_system_project.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.textfield.TextArea;

import java.util.ArrayList;
import java.util.List;

@Route("") // root URL
public class MainView extends VerticalLayout {
	
	private final MVQueries mvq;
	
    public MainView(MVQueries mvq) {

    	this.mvq = mvq;
    	
    	List<String> userInput = new ArrayList<String>();
    	
        Button goButton = new Button("Go");
        Button checkSeatButton = new Button("Check Seat Availability?");
        
        Select<String> functSelect = new Select<>();
        functSelect.setLabel("Execute Command");
        functSelect.setItems("From Two Destinations Flight Search", "From One Destination Flight Search",
                "Infrastructure Report", "Book a Seat", "Itinerary Retrieval");
        functSelect.setValue("Itinerary Retrieval");
        
        TextField parameter1 = new TextField();
        TextField parameter2 = new TextField();
        TextField parameter3 = new TextField();
        TextField parameter4 = new TextField();
        TextField parameter5 = new TextField();
        TextField seatParameter = new TextField();
        TextField seatParameter2 = new TextField();
        
        TextArea info = new TextArea();
        TextArea seatInfo = new TextArea();
        info.setReadOnly(true);        
        seatInfo.setReadOnly(true);
        
        info.setWidth("400px");							//Main info box width
        info.setHeight("250px");						//Main info box height
        info.setMinRows(5);
        info.setMaxRows(10);
        
        seatInfo.setWidth("200px");						//Seat info box width
        seatInfo.setHeight("200px");					//Seat info box height
        seatInfo.setMinRows(5);
        seatInfo.setMaxRows(10);

        //parameter1.setVisible(false);
        parameter2.setVisible(false);
        parameter3.setVisible(false);
        parameter4.setVisible(false);
        parameter5.setVisible(false);
        seatParameter.setVisible(false);
        seatParameter2.setVisible(false);
        checkSeatButton.setVisible(false);
        seatInfo.setVisible(false);

        //when drop down changes, reveal number of needed parameter boxes
        functSelect.addValueChangeListener(event -> {
            seatParameter.setVisible(false);
            seatParameter2.setVisible(false);
            checkSeatButton.setVisible(false);
            seatInfo.setVisible(false);
            parameter2.setVisible(false);
            parameter3.setVisible(false);
            parameter4.setVisible(false);
            parameter5.setVisible(false);
            
        	String value = event.getValue();
        	if(value.equals("From One Destination Flight Search"))
        		parameter2.setVisible(true);
        	else if(value.equals("From Two Destinations Flight Search") || value.equals("Infrastructure Report")) {
        		parameter2.setVisible(true);
        		parameter3.setVisible(true);
        	}
        	else if(value.equals("Book a Seat")) 
        	{
        		parameter2.setVisible(true);
        		parameter3.setVisible(true);
        		parameter4.setVisible(true);
        		parameter5.setVisible(true);
        	}
        });
        
        
        //When Go is clicked, execute function chosen from functSelect
        goButton.addClickListener(event -> {
        	String funct = functSelect.getValue();
        	
        	if (funct == null) info.setValue("Set desired Function\n");
        	else if(funct.equals("From Two Destinations Flight Search")) /*function call*/{
        		userInput.add(parameter1.getValue());
            	userInput.add(parameter2.getValue());
            	userInput.add(parameter3.getValue());
            	
            	List<String> results = mvq.airportFlightSearch(userInput);
            	
            	info.setValue(results.get(0).concat(results.get(1)));
                 	
            	userInput.clear();
        	}
        	else if(funct.equals("From One Destination Flight Search")) {
        		userInput.add(parameter1.getValue());
            	userInput.add(parameter2.getValue());
            	
            	info.setValue(mvq.numFlightSearch(userInput));
            	
            	userInput.clear();
        	}
        	else if(funct.equals("Infrastructure Report")) {
        		userInput.add(parameter1.getValue());
            	userInput.add(parameter2.getValue());
            	userInput.add(parameter3.getValue());
            	
            	info.setValue(mvq.aircraftUtilizationReport(userInput));
            	
            	userInput.clear();
        	}
        	else if(funct.equals("Book a Seat")) {
        		userInput.add(parameter1.getValue());
            	userInput.add(parameter2.getValue());
            	userInput.add(parameter3.getValue());
            	userInput.add(parameter4.getValue());
            	userInput.add(parameter5.getValue());
            	
        		info.setValue(mvq.bookASeat(userInput));
        		
        		userInput.clear();
        	}
        	else if(funct.equals("Itinerary Retrieval")) {
                info.setValue(mvq.passengerItineraryRetrieval(parameter1.getValue()));
        	}

        	if(funct.equals("From One Destination Flight Search") || funct.equals("From Two Destinations Flight Search")) {
                seatParameter.setVisible(true);
                seatParameter2.setVisible(true);
                checkSeatButton.setVisible(true);
                seatInfo.setVisible(true);
        	}
        });
        
        checkSeatButton.addClickListener(event -> {
        	if(seatParameter.getValue() == "" || seatParameter2.getValue() == "")
        	{
        		seatInfo.setValue("Missing Information");
        	}
        	else
        	{
        		userInput.add(seatParameter.getValue());
            	userInput.add(seatParameter2.getValue());
            	
            	seatInfo.setValue(mvq.seatAvailability(userInput));
        	}
        	
        	userInput.clear();
        });
        
        
        
        add(
        	new H1("Airplane Database Interface"),
        	new HorizontalLayout(
        			functSelect, goButton
        			),
        	new HorizontalLayout(
        			parameter1, parameter2, parameter3, parameter4, parameter5
        			),
        	info, 
        	new HorizontalLayout(
        			checkSeatButton, seatParameter, seatParameter2
        			),
			seatInfo
        		);
    }
}
