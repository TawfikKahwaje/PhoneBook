package ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.sun.awt.SecurityWarning;

import db.*;
public class PhoneBookAppRunner extends Application {
    
    public static String USER_ID ;
    Stage window;
    
    // Login Fields    
    Label msgLabel ;
    GridPane loginRoot;
    TextField tNameField;
    Label tNameLabel;
    PasswordField tPasswordField;
    Label tPasswordLabel;
    Button loginButton;
    Button signinButton;
    
    VBox root2 ;
    HBox hBox;
    // Table Fields 
    TableColumn<Contact, String> nameColumn ;
    TableColumn<Contact, String> phoneNumberColumn ;
    TableColumn<Contact, String> emailColumn ;
    
    // Add Or Delete Contact
    TableView<Contact> table;
    TextField nameInput ;
    TextField phoneNumberInput ;
    TextField emailInput ;
    Button addButton;
    
    // Filter for Search
    TextField filterField;
    FilteredList<Contact> filteredData;
    SortedList<Contact> sortedData;
    Label validationLabel = new Label();
    
    
    VBox contactDetails;
    Stage contactDetailsStage ;
    
    ArrayList<Contact> contactsList;
    
    public static void main(String[] args) throws UnknownHostException{        
     Application.launch(args);
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub

		window = primaryStage;
		window.setTitle("Phone Book");
		loginRoot = createLoginRoot();
		
		window.setScene(new Scene(loginRoot, 300, 250));
		window.show();
		
	}

	private GridPane createLoginRoot() {
		// TODO Auto-generated method stub
		GridPane loginRoot = GridPaneBuilder.create()
                .hgap(5)
                .vgap(7)
                .build();
		
        msgLabel = new Label("SomeThing Wrong Try Again");
        msgLabel.setVisible(false);
        
        tNameField = new TextField();
        tNameField.setPromptText("username");
        
        tNameLabel = new Label("UserName :");
        
        tPasswordField = new PasswordField();
        tPasswordField.setPromptText("Password");
        
        tPasswordLabel = new Label("Password :");
        
        loginButton = new Button("Login");
        loginButton.setOnAction(e -> loginCheck());
        
        signinButton = new Button("SignIn");
        signinButton.setOnAction(e ->{
        	if(tNameField.getText().trim().isEmpty() || tPasswordField.getText().trim().isEmpty()){
        		msgLabel.setText("You should fill the fields");
        		msgLabel.setVisible(true);
        	}
        	else {
        		root2 = createTableViewRoot();
        		try {
    				User newUser = UserDB.saveUser(new User(tNameField.getText(),tPasswordField.getText()));
    				if(newUser == null){
    					msgLabel.setVisible(true);
    					msgLabel.setText("User Name alredy taken");
    				}
    				else
    				{
    					USER_ID = newUser.getUserID();
    					showHomePage();
    				}
    			} catch (UnknownHostException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			}
        	}
        });
        
        
        loginRoot.add(msgLabel,1,2,2,1);
        loginRoot.add(tNameLabel,1,3);
        loginRoot.add(tNameField,2,3);
        loginRoot.add(tPasswordLabel,1,4);
        loginRoot.add(tPasswordField,2,4);
        HBox buttonsBox = new HBox(10);
        buttonsBox.getChildren().addAll(loginButton,signinButton);
        loginRoot.add(buttonsBox,2,6);
        
        return loginRoot;
        
	}

	private void loginCheck() {
		if(tNameField.getText().trim().isEmpty() || tPasswordField.getText().trim().isEmpty()){
    		msgLabel.setText("You should fill the fields");
    		msgLabel.setVisible(true);
    	}
		else {
	    	User user = new User(tNameField.textProperty().getValue(), tPasswordField.textProperty().getValue());
	    	msgLabel.setText("Wrong Username Or PassWord :(");
	    	msgLabel.setVisible(false);
	    	try {
	    		root2 = createTableViewRoot();
	    		DBObject dbObject = UserDB.getUser(user);
	    		if(dbObject != null){
	    			USER_ID= dbObject.get("_id").toString();
	    			showHomePage();
	    		}
	    		else {
	    			msgLabel.setVisible(true);
	    		}
			} catch (UnknownHostException ee) {
				// TODO Auto-generated catch block
				ee.printStackTrace();
			}
		}
    }

	private void showHomePage() throws UnknownHostException {
		// TODO Auto-generated method stub
		// MenuBar for Profile and Import
		MenuBar menuBar = new MenuBar();
	    menuBar.prefWidthProperty().bind(window.widthProperty());
	    Menu fileMenu = new Menu("File");
	    MenuItem ProfileMenuItem = new MenuItem("Profile");
	    MenuItem importContact = new MenuItem("Import");
	    fileMenu.getItems().addAll(ProfileMenuItem,importContact);
	    importContact.setOnAction(e -> importContact());
	    ProfileMenuItem.setOnAction(e -> showProfile());
	    menuBar.getMenus().addAll(fileMenu);
	    
	    // Filter for Search
		filterField = new TextField();
		filterField.setPromptText("Search");
		
		contactsList= UserDB.getAllContactsByUserId(USER_ID);
		table = new TableView<>();
		if(contactsList !=null){
	        filteredData = new FilteredList<>(FXCollections.observableList(contactsList), p -> true);
	        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
	            filteredData.setPredicate(contact -> {
	                if (newValue == null || newValue.isEmpty()) {
	                    return true;
	                }
	                String lowerCaseFilter = newValue.toLowerCase();
	                if (contact.getName().toLowerCase().contains(lowerCaseFilter)) {
	                    return true; 
	                } else if (contact.getEmail().toLowerCase().contains(lowerCaseFilter)) {
	                    return true; 
	                } else if (contact.getPhoneNumber().toLowerCase().contains(lowerCaseFilter)) {
	                    return true; 
	                }
	                return false;
	            });
	        });
	        sortedData = new SortedList<>(filteredData);
	        sortedData.comparatorProperty().bind(table.comparatorProperty());
	        table.setItems(sortedData);
		}
		
		// Action for select contact
		table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
		    if (newSelection != null ) {
		    	Contact contact = table.getSelectionModel().getSelectedItem();
		    	contactDetails = createContactDetailsRoot(contact);
		    	contactDetailsStage = new Stage();
		    	contactDetailsStage.setScene(new Scene(contactDetails, 300, 200));
		    	contactDetailsStage.show();
		    	contactDetailsStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
					
					@Override
					public void handle(WindowEvent arg0) {
						// TODO Auto-generated method stub
						table.getSelectionModel().clearSelection();
					}
				});
		    }
		});
		table.getColumns().addAll(nameColumn, phoneNumberColumn, emailColumn);
		
		root2.getChildren().addAll(menuBar,filterField,table,hBox);
		window.setScene(new Scene(root2, 610, 400));
		window.show();
	}

	private void showProfile() {
		// TODO Auto-generated method stub
    	VBox profileRoot = new VBox(10);
    	HBox hBoxProfile = new HBox();
    	Stage progileStage = new Stage();
    	progileStage.setScene(new Scene(profileRoot, 200, 200));
    	User user;
    	try {
			user = UserDB.getUserById(USER_ID);
			
			Label usernameLabel = new Label("User Name :");
			Label usernameValue = new Label(user.getUsername());
			hBoxProfile.getChildren().addAll(usernameLabel,usernameValue);
			
			Button deleteUserButton = new Button("Delete User");
			deleteUserButton.setOnAction(event ->{
				try {
					UserDB.deleteUserById(USER_ID);
					System.out.println("User Deleted");
					loginRoot = createLoginRoot();
					window.setScene(new Scene(loginRoot, 300, 250));
					progileStage.close();
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
			
			profileRoot.getChildren().addAll(hBoxProfile,deleteUserButton);
			progileStage.show();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void importContact() {
		// TODO Auto-generated method stub
	    	FileChooser fileChooser = new FileChooser();
	    	fileChooser.setTitle("Open Resource File");
	    	
	    	FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.cvs)","*.cvs");
	    	fileChooser.getExtensionFilters().add(extFilter);
             
             
	    	File file = fileChooser.showOpenDialog(window);
	    	String line = "";
	    	if(file != null){
	    		ArrayList<Contact> contacts = new ArrayList<>();
	    		try ( BufferedReader br = new BufferedReader(new FileReader(file))) {

	                while ((line = br.readLine()) != null) {

	                    // use comma as separator
	                    String[] contactsString = line.split(",");
	                    Contact contact = new Contact(contactsString[0].replace("\"", ""),contactsString[1].replace("\"", ""),contactsString[2].replace("\"", ""));
	                    contactsList.add(contact);
	                    filteredData = new FilteredList<>(FXCollections.observableList(contactsList), p -> true);
						sortedData = new SortedList<>(filteredData);
						sortedData.comparatorProperty().bind(table.comparatorProperty());
				        table.setItems(sortedData);
	                    
	                    UserDB.addContact(contact, USER_ID);
	                }
	            } catch (IOException ee) {
	                ee.printStackTrace();
	            }
            }
	    	else {
	    		System.out.println("Not CVS");
	    	}
	}

	private VBox createTableViewRoot() {
		// TODO Auto-generated method stub
		VBox root2 = new VBox(10);
        
        nameColumn = new TableColumn<>("Name");
		nameColumn.setMinWidth(200);
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		phoneNumberColumn = new TableColumn<>("phoneNumber");
		phoneNumberColumn.setMinWidth(200);
		phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		
		emailColumn = new TableColumn<>("email");
		emailColumn.setMinWidth(200);
		emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
		
        
        nameInput = new TextField();
        nameInput.setPromptText("Name");
        nameInput.setMinWidth(100);
        
        phoneNumberInput = new TextField();
        phoneNumberInput.setPromptText("Phone_Number");
        phoneNumberInput.setMinWidth(100);
        
        emailInput = new TextField();
        emailInput.setPromptText("Email");
        emailInput.setMinWidth(100);
        
        addButton = new Button("Add");
        addButton.setOnAction(e -> addContact());
        addButton.setMinWidth(100);
        
        hBox = new HBox();
        hBox.setPadding(new Insets(10,10,10,10));
        hBox.setSpacing(10);
        hBox.getChildren().addAll(nameInput, phoneNumberInput, emailInput, addButton);
        
        return root2;
	}

	private void addContact() {
		// TODO Auto-generated method stub
		{
			root2.getChildren().remove(validationLabel);
			Contact contact = new Contact(nameInput.getText(),phoneNumberInput.getText(),emailInput.getText());
			if(validtionData(contact) == null)
			{
				try {
	        		nameInput.clear();
	        		phoneNumberInput.clear();
	        		emailInput.clear();
	        		String iD= UserDB.addContact(contact, USER_ID);
	        		contact.setID(iD);
					contactsList.add(contact);
					filteredData = new FilteredList<>(FXCollections.observableList(contactsList), p -> true);
					sortedData = new SortedList<>(filteredData);
					sortedData.comparatorProperty().bind(table.comparatorProperty());
			        table.setItems(sortedData);
					
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else
			{
				validationLabel.setText(validtionData(contact));
				root2.getChildren().addAll(validationLabel);
			}
        	
        }
	}

	private String validtionData(Contact contact) {
		// TODO Auto-generated method stub
		if(contact.getName().length()<3){
			return "The username should be at lest 3 charecters ";
		}
		String phoneNumberStr = "^[0-9]*$";
		if ( !(contact.getPhoneNumber().matches(phoneNumberStr)) || contact.getPhoneNumber().length()==0 ) {
			return "Its Not Phone Number :)";
		}
		final Pattern VALID_EMAIL_ADDRESS_REGEX =  Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
		if ( !(VALID_EMAIL_ADDRESS_REGEX .matcher(contact.getEmail()).find())) {
			return "Its Not Email :)";
		}
			return null;
	}

	private VBox createContactDetailsRoot(Contact contact) {
		// TODO Auto-generated method stub
		VBox newBox = new VBox(10);
    	TextField name = new TextField();
    	name.setText(contact.getName());
    	name.setMinWidth(100);
    	
    	TextField phoneNumber = new TextField();
    	phoneNumber.setText(contact.getPhoneNumber());
    	phoneNumber.setMinWidth(100);
    	
    	TextField email = new TextField();
    	email.setText(contact.getEmail());
    	email.setMinWidth(100);
    	
    	
    	Button editeButton = new Button("Edit");
    	editeButton.setOnAction(e ->{
    		try {
    			contact.setName(name.getText());
    			contact.setPhoneNumber(phoneNumber.getText());
    			contact.setEmail(email.getText());
				ContactDB.editContactById(contact);
				
				try {
					contactsList= UserDB.getAllContactsByUserId(USER_ID);
					filteredData = new FilteredList<>(FXCollections.observableList(contactsList), p -> true);
					sortedData = new SortedList<>(filteredData);
					sortedData.comparatorProperty().bind(table.comparatorProperty());
			        table.setItems(sortedData);
				} catch (UnknownHostException ee) {
					// TODO Auto-generated catch block
					ee.printStackTrace();
				}
				table.getSelectionModel().clearSelection();
				contactDetailsStage.close(); 
				
				
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	});
    	Button dleteButton = new Button("Delete");
    	dleteButton.setOnAction(e ->{
    		contactDetailsStage.close();
    		String contactId =contact.getID();
    		ObservableList<Contact> contactSelected;
        	contactSelected = table.getSelectionModel().getSelectedItems();
        	try {
        		
				ContactDB.deleteContactById(contactId,USER_ID);
				contactsList.remove(contact);
				filteredData = new FilteredList<>(FXCollections.observableList(contactsList), p -> true);
				sortedData = new SortedList<>(filteredData);
				sortedData.comparatorProperty().bind(table.comparatorProperty());
		        table.setItems(sortedData);
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	table.getSelectionModel().clearSelection();
			contactDetailsStage.close();
    		
    	});
    	newBox.getChildren().addAll(name,phoneNumber,email,editeButton,dleteButton);
    	return newBox;
	}
    
    
}