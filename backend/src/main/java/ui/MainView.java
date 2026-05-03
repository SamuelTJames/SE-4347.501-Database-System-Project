package com.se4347.database_system_project.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("") // root URL
public class MainView extends VerticalLayout {

    public MainView() {
        Button button = new Button("Click");
        button.addClickListener(e -> button.setText("Complete"));
        add(button);
    }
}