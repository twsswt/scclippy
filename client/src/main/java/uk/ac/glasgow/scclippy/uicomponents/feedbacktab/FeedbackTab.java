package uk.ac.glasgow.scclippy.uicomponents.feedbacktab;

import com.intellij.ui.components.JBScrollPane;

import uk.ac.glasgow.scclippy.plugin.editor.IntellijFacade;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Represents a class that contains components in the search panel/tab
 */
public class FeedbackTab {

    private JComponent panel;
    private JScrollPane scroll;

    private final static String FEEDBACK_URL = "http://www.smartsurvey.co.uk/s/BNLCA/";

    public FeedbackTab() {

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        scroll = new JBScrollPane(panel);

        JEditorPane participantConsentForm = new JEditorPane("text/html", "");
        participantConsentForm.setEditable(false);
        participantConsentForm.setFocusable(false);
        HTMLEditorKit kit = new HTMLEditorKit();
        kit.getStyleSheet().addRule("body {color: #C0C0C0}");
        participantConsentForm.setEditorKit(kit);
        participantConsentForm.setText(generateParticipantConsentFormInHTML());

        JButton surveyButton = new JButton("Provide feedback");
        surveyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        URI uri = new URI(FEEDBACK_URL);
                        Desktop.getDesktop().browse(uri);
                    } catch (IOException | URISyntaxException e1) {
                    	IntellijFacade.createErrorNotification(e1.getMessage());
                    }
                }
            }
        });

        // add components to info panel
        panel.add(participantConsentForm);
        panel.add(surveyButton);
    }

    private String generateParticipantConsentFormInHTML() {
        return
                "<h2><b>Participant Consent Form: Source Code Clippy IntelliJ Recommender Plugin</b><h2>" +
                "<p>" +
                "SCClippy is part of an experiment to investigate the efficacy of code snippet recommendations for" +
                "<br/>micro programming tasks. The aim of this experiment is to evaluate the usability of the Source Code Clippy plugin." +
                "<br/>" +
                "<br/>The experiment will take time defined by the participant – feedback is provided when they are " +
                "<br/>ready to do so." +
                "<br/>" +
                "<br/>At the start of the experiment, the participant should click the ‘Info’ tab provided by the plugin " +
                "<br/>which will introduce them how the plugin works. After that, the user is free to use whichever " +
                "<br/>functionalities." +
                "<br/>" +
                "<br/>Finally, the participant can give feedback by filling a questionnaire." +
                "<br/>" +
                "<br/>All results will be held in confidence, ensuring the privacy of all participants." +
                "<br/>" +
                "<br/>Only the answers to the questionnaire will be stored. The data will be stored on a password " +
                "<br/>protected machine." +
                "<br/>" +
                "<br/>A feedback email message containing analyzed data will be sent to participants who make " +
                "<br/>such request by email." +
                "<br/>" +
                "<br/>Your participation in this experiment will have no effect on your marks for any subject at this, or " +
                "<br/>any other university." +
                "<br/>" +
                "<br/>Please note that it is the diagrams, not you, that are being evaluated. You may withdraw from " +
                "<br/>the experiment at any time without prejudice, and any data already recorded will be discarded." +
                "<br/>" +
                "<br/>If you have any further questions regarding this experiment, please contact:" +
                "<br/>" +
                "<br/>Boris Nikolov" +
                "<br/>boris.ts.nikolov@gmail.com | 2020389n@student.gla.ac.uk" +
                "<br/>" +
                "<br/><i>This study adheres to the BPS ethical guidelines, and has been approved by the DCS ethics " +
                "<br/>committee of The University of Glasgow. Whilst you are free to discuss your participation in this " +
                "<br/>study with the experimenter, if you would like to speak to someone not involved in the study, " +
                "<br/>you may contact the chair of the DCS Ethics Committee: Prof Stephen Brewster " +
                "<br/>&lt;stephen@dcs.gla.ac.uk&gt;.</i>" +
                "</p>";
    }

    public JScrollPane getScroll() {
        return scroll;
    }

}