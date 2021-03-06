package booknext;

import Classes.CBook;
import Pages.DiscoverPage;
import Pages.LandingPage;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.fontawesome.Icon;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
/**
 *
 * @author jcdur
 */
public class BookNext extends Application {
    
    private Pane showBooksMatrix(List<CBook> booksToShow)
    {        
        try {

                JFXTreeTableColumn<Book, String> isbnColumn = new JFXTreeTableColumn<>("ISBN");
                isbnColumn.setPrefWidth(150);
                isbnColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Book, String> param) ->{
                        if(isbnColumn.validateValue(param)) return param.getValue().getValue().isbn;
                        else return isbnColumn.getComputedValue(param);
                });

                JFXTreeTableColumn<Book, String> nameColumn = new JFXTreeTableColumn<>("Book name");
                nameColumn.setPrefWidth(150);
                nameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Book, String> param) ->{
                        if(nameColumn.validateValue(param)) return param.getValue().getValue().book_name;
                        else return nameColumn.getComputedValue(param);
                });

                JFXTreeTableColumn<Book, String> authorsColumn = new JFXTreeTableColumn<>("Authors");
                authorsColumn.setPrefWidth(150);
                authorsColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Book, String> param) ->{
                        if(authorsColumn.validateValue(param)) return param.getValue().getValue().authors;
                        else return authorsColumn.getComputedValue(param);
                });

                JFXTreeTableColumn<Book, String> genreColumn = new JFXTreeTableColumn<>("Genre");
                genreColumn.setPrefWidth(200);
                genreColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Book, String> param) ->{
                        if(authorsColumn.validateValue(param)) return param.getValue().getValue().genres;
                        else return authorsColumn.getComputedValue(param);
                });

                JFXTreeTableColumn<Book, String> avgColumn = new JFXTreeTableColumn<>("Rating");
                avgColumn.setPrefWidth(100);
                avgColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Book, String> param) ->{
                        if(avgColumn.validateValue(param)) return param.getValue().getValue().average;
                        else return avgColumn.getComputedValue(param);
                });       

                isbnColumn.setCellFactory((TreeTableColumn<Book, String> param) -> new GenericEditableTreeTableCell<Book, String>(new TextFieldEditorBuilder()));
                isbnColumn.setOnEditCommit((CellEditEvent<Book, String> t)->{
                        ((Book) t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue()).isbn.set(t.getNewValue());
                });

                nameColumn.setCellFactory((TreeTableColumn<Book, String> param) -> new GenericEditableTreeTableCell<Book, String>(new TextFieldEditorBuilder()));
                nameColumn.setOnEditCommit((CellEditEvent<Book, String> t)->{
                        ((Book) t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue()).book_name.set(t.getNewValue());
                });

                authorsColumn.setCellFactory((TreeTableColumn<Book, String> param) -> new GenericEditableTreeTableCell<Book, String>(new TextFieldEditorBuilder()));
                authorsColumn.setOnEditCommit((CellEditEvent<Book, String> t)->{
                        ((Book) t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue()).authors.set(t.getNewValue());
                });

                genreColumn.setCellFactory((TreeTableColumn<Book, String> param) -> new GenericEditableTreeTableCell<Book, String>(new TextFieldEditorBuilder()));
                genreColumn.setOnEditCommit((CellEditEvent<Book, String> t)->{
                        ((Book) t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue()).genres.set(t.getNewValue());
                });

                avgColumn.setCellFactory((TreeTableColumn<Book, String> param) -> new GenericEditableTreeTableCell<Book, String>(new TextFieldEditorBuilder()));
                avgColumn.setOnEditCommit((CellEditEvent<Book, String> t)->{
                        ((Book) t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue()).average.set(t.getNewValue());
                });



                // data
                ObservableList<Book> books = FXCollections.observableArrayList();
                for (CBook book : booksToShow) {
                    books.add(new Book(book));
                }


                // build tree
                final TreeItem<Book> root = new RecursiveTreeItem<Book>(books, RecursiveTreeObject::getChildren);

                JFXTreeTableView<Book> treeView = new JFXTreeTableView<Book>(root, books);
                treeView.setShowRoot(false);
                treeView.setEditable(true);
                treeView.getColumns().setAll(isbnColumn, nameColumn, authorsColumn, genreColumn, avgColumn);
                treeView.relocate(0, 0);
                
                Pane main = new Pane();
                main.setPadding(new Insets(10));
                main.getChildren().add(treeView);


                Icon searchIcon = new Icon("SEARCH", "2em");
                searchIcon.relocate(200, 450);
                JFXTextField filterField = new JFXTextField();
                filterField.setPrefWidth(200);
                filterField.relocate(-10, 450);
                filterField.setStyle("-fx-font-size:15px; -fx-background-color: TRANSPARENT;");
                filterField.setPromptText("Book name");
                main.getChildren().addAll(filterField, searchIcon);

                Label size = new Label();
                size.setStyle("-fx-font-size: 20px;");
                size.relocate(0, 420);
                filterField.textProperty().addListener((o,oldVal,newVal)->{
                        treeView.setPredicate(book -> book.getValue().genres.get().contains(newVal));
                });

                size.textProperty().bind(Bindings.createStringBinding(()->"Book count: " + treeView.getCurrentItemsCount()+"", treeView.currentItemsCountProperty()));
                main.getChildren().add(size);

                return main;

//						ScenicView.show(scene);
        } catch (Exception e) {
                e.printStackTrace();
        }
        return new Pane();
    }
    
    @Override
    public void start(Stage primaryStage) {
        
        LandingPage logPage = new LandingPage();
        Stage newStage = logPage.getStage();
        newStage.show();
        primaryStage.close();
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

   class Book extends RecursiveTreeObject<Book>{
		
                SimpleStringProperty isbn, book_name, authors, genres, average;

		public Book(CBook newBook) {
			this.isbn = new SimpleStringProperty(newBook.isbn) ;
			this.book_name = new SimpleStringProperty(newBook.getBook_name());
			this.authors = new SimpleStringProperty(newBook.getBook_authorsStr());
                        this.genres = new SimpleStringProperty(newBook.getBook_genre());
                        this.average = new SimpleStringProperty(newBook.getBook_StrRating());
		}

	}

}
