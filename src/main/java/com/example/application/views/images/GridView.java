package com.example.application.views.images;

import com.example.application.data.AnalyzedContent;
import com.example.application.data.service.ContentService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;

import java.io.ByteArrayInputStream;
import java.util.Base64;

@Route(value="Grid", layout = MainLayout.class)
@PermitAll
public class GridView extends VerticalLayout {
    public GridView(ContentService contentService){
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Button clear = new Button("Clear DB");
        clear.addClickListener(e->contentService.deleteAllItems());

        Image img = new Image();
        img.setHeight("80%");

        Grid<AnalyzedContent> grid = new Grid<>(AnalyzedContent.class);
        grid.removeAllColumns();
        grid.addColumn(AnalyzedContent::getName).setAutoWidth(true).setHeader("Name");
        grid.addColumn(AnalyzedContent::getAttributes).setAutoWidth(true).setHeader("Identified Objects");
        grid.setItems(contentService.findAll());
        grid.addSelectionListener(e -> img.setSrc(e.getFirstSelectedItem().get().getImg()));
        add(clear, grid, img);
    }
}
