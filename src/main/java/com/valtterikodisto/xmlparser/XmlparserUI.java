package com.valtterikodisto.xmlparser;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;


/* Creates a simple Web UI for file uploading and downloading*/

@SpringUI
public class XmlparserUI extends UI implements Upload.Receiver, Upload.SucceededListener {

    private VerticalLayout root;
    private Button downloadButton;

    private File xmlFile;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        setUpLayout();
        addHeading();
        addUploadButton();
        addDownloadButton();
    }

    private void setUpLayout() {

        root = new VerticalLayout();
        root.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        setContent(root);
    }

    private void addHeading() {
        Label heading = new Label("XML Converter");
        Label smallHeading = new Label("Convert Finvoice XML to Procountor CSV");
        heading.addStyleName(ValoTheme.LABEL_H1);
        smallHeading.addStyleName(ValoTheme.LABEL_H3);
        root.addComponents(heading, smallHeading);
    }

    private void addUploadButton() {

        Upload upload = new Upload();
        upload.setReceiver(this);
        upload.addSucceededListener(this);

        root.addComponent(upload);
    }

    private void addDownloadButton() {
        downloadButton = new Button("Download file");
        downloadButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        downloadButton.setEnabled(false); // At first the download button is disabled since there is no file to download
        root.addComponent(downloadButton);
    }



    // Create and return a file output stream

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        downloadButton.setEnabled(false);

        try {
            xmlFile = File.createTempFile("temp", ".xml");
            return new FileOutputStream(xmlFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // When the file is successfully uploaded this method gets all the resources
    // CsvBuilder needs which are: Uploaded file and CSV Template files
    // And finally makes the button enabled so that the user can download the csv file

    @Override
    public void uploadSucceeded(Upload.SucceededEvent succeededEvent) {

        // Simple way to make sure we dont return many files when user clicks download
        root.removeComponent(downloadButton);
        addDownloadButton();

        try {

            File invoiceRecordTemplate = new File(this.getClass().getResource("/templates/invoiceRecordTemplate.txt").toURI());
            File invoiceRowRecordTemplate = new File(this.getClass().getResource("/templates/invoiceRowRecordTemplate.txt").toURI());

            String csv = CsvBuilder.build(xmlFile, invoiceRecordTemplate, invoiceRowRecordTemplate);
            StreamResource myResource = createResource(csv);
            //myResource.setCacheTime(0);
            FileDownloader fileDownloader = new FileDownloader(myResource);
            fileDownloader.extend(downloadButton);

            Notification.show("File was converted successfully");

            downloadButton.setEnabled(true);

        } catch (Exception e) {
                Notification error = new Notification("Error<br />",
                        "File conversion was not successful",
                        Notification.Type.ERROR_MESSAGE, true);
                error.setPosition(Position.TOP_CENTER);
                error.show(getCurrent().getPage());
        }
    }

    // Creates StreamResource out CsvBuilders CSV String

    private StreamResource createResource(String csv) {
        return new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {

                return new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

            }
        },  "procountor-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".csv");
    }
}
