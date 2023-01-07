package com.example.application.views.biodata;

import com.example.application.data.entity.Biodata;
import com.example.application.data.service.BiodataService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Biodata")
@Route(value = "biodata/:biodataID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
public class BiodataView extends Div implements BeforeEnterObserver {

    private final String BIODATA_ID = "biodataID";
    private final String BIODATA_EDIT_ROUTE_TEMPLATE = "biodata/%s/edit";

    private final Grid<Biodata> grid = new Grid<>(Biodata.class, false);

    private TextField nik;
    private TextField nama;
    private TextField puskemas;
    private DatePicker tanggalLahir;
    private TextField pendidikan;
    private TextField noHp;
    private TextField alamat;
    private Checkbox important;
    

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private final BeanValidationBinder<Biodata> binder;

    private Biodata biodata;

    private final BiodataService biodataService;

    public BiodataView(BiodataService biodataService) {
        this.biodataService = biodataService;
        addClassNames("biodata-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("nik").setAutoWidth(true);
        grid.addColumn("nama").setAutoWidth(true);
        grid.addColumn("puskemas").setAutoWidth(true);
        grid.addColumn("tanggalLahir").setAutoWidth(true);
        grid.addColumn("pendidikan").setAutoWidth(true);
        grid.addColumn("noHp").setAutoWidth(true);
        grid.addColumn("alamat").setAutoWidth(true);
        LitRenderer<Biodata> importantRenderer = LitRenderer.<Biodata>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", important -> important.isImportant() ? "check" : "minus").withProperty("color",
                        important -> important.isImportant()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        grid.addColumn(importantRenderer).setHeader("Important").setAutoWidth(true);

        grid.setItems(query -> biodataService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(BIODATA_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(BiodataView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Biodata.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });
        delete.addClickListener(e -> {
            try {
                if (this.biodata == null) {
                    Notification.show("No biodata selected!");
                } else {
                    binder.writeBean(this.biodata);
    
                    biodataService.delete(this.biodata);
                    clearForm();
                    refreshGrid();
                    Notification.show("biodata details stored.");
                    UI.getCurrent().navigate(BiodataView.class);
                }
            } catch(ValidationException validationException){
                Notification.show("An exception happened while trying to store the Biodata detail");
            }
            }   
        );

        save.addClickListener(e -> {
            try {
                if (this.biodata == null) {
                    this.biodata = new Biodata();
                }
                binder.writeBean(this.biodata);
                biodataService.update(this.biodata);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(BiodataView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> biodataId = event.getRouteParameters().get(BIODATA_ID).map(Long::parseLong);
        if (biodataId.isPresent()) {
            Optional<Biodata> biodataFromBackend = biodataService.get(biodataId.get());
            if (biodataFromBackend.isPresent()) {
                populateForm(biodataFromBackend.get());
            } else {
                Notification.show(String.format("The requested biodata was not found, ID = %s", biodataId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(BiodataView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        nik = new TextField("Nik");
        nama = new TextField("Nama");
        puskemas = new TextField("Puskemas");
        tanggalLahir = new DatePicker("Tanggal Lahir");
        pendidikan = new TextField("Pendidikan");
        noHp = new TextField("No Hp");
        alamat = new TextField("Alamat");
        important = new Checkbox("Important");
        formLayout.add(nik, nama, puskemas, tanggalLahir, pendidikan, noHp, alamat, important);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel, delete);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Biodata value) {
        this.biodata = value;
        binder.readBean(this.biodata);

    }
}
