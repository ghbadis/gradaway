package controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Custom TextField with autocomplete functionality
 */
public class AutocompleteTextField extends TextField {
    
    private final ObjectProperty<Consumer<String>> onSuggestionRequestProperty = new SimpleObjectProperty<>();
    private final ContextMenu suggestionsPopup = new ContextMenu();
    private final int maxSuggestions = 5;
    
    public AutocompleteTextField() {
        super();
        configureTextField();
    }
    
    /**
     * Configure the text field settings and behavior
     */
    private void configureTextField() {
        // Set the suggestions popup to be displayed below the text field
        suggestionsPopup.setAutoHide(true);
        
        // Add focus listener to hide popup when focus is lost
        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                suggestionsPopup.hide();
            }
        });
        
        // Listen for text changes to trigger suggestion requests
        this.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty() && newValue.length() >= 3) {
                Consumer<String> suggestionRequestHandler = onSuggestionRequestProperty.get();
                if (suggestionRequestHandler != null) {
                    suggestionRequestHandler.accept(newValue);
                }
            } else {
                suggestionsPopup.hide();
            }
        });
        
        // Handle key events (especially for navigation in the popup)
        this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (suggestionsPopup.isShowing()) {
                if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.UP) {
                    // Let the popup's default key handler deal with it
                    return;
                } else if (event.getCode() == KeyCode.ESCAPE) {
                    suggestionsPopup.hide();
                    event.consume();
                }
            }
        });
    }
    
    /**
     * Display a list of suggestions in the popup
     * @param suggestions List of strings to display as suggestions
     */
    public void showSuggestions(List<String> suggestions) {
        if (suggestions == null || suggestions.isEmpty()) {
            suggestionsPopup.hide();
            return;
        }
        
        // Limit the number of suggestions
        List<String> limitedSuggestions = suggestions.stream()
                .limit(maxSuggestions)
                .collect(Collectors.toList());
        
        // Create menu items for each suggestion
        List<CustomMenuItem> menuItems = new LinkedList<>();
        for (String suggestion : limitedSuggestions) {
            Label label = new Label(suggestion);
            label.setPrefWidth(this.getPrefWidth() * 1.5);
            CustomMenuItem item = new CustomMenuItem(label, true);
            
            // When an item is clicked, fill in the text and hide the popup
            item.setOnAction(event -> {
                setText(suggestion);
                suggestionsPopup.hide();
                positionCaret(getText().length());
            });
            
            menuItems.add(item);
        }
        
        // Add all items to the popup menu
        suggestionsPopup.getItems().clear();
        suggestionsPopup.getItems().addAll(menuItems);
        
        // Show the popup below the text field
        if (!suggestionsPopup.isShowing()) {
            suggestionsPopup.show(this, Side.BOTTOM, 0, 0);
        }
    }
    
    /**
     * Set a handler for when suggestion requests should be made
     * @param handler Consumer that accepts the current text and should fetch suggestions
     */
    public void setOnSuggestionRequest(Consumer<String> handler) {
        onSuggestionRequestProperty.set(handler);
    }
    
    /**
     * Hide the suggestions popup
     */
    public void hideSuggestions() {
        suggestionsPopup.hide();
    }
} 