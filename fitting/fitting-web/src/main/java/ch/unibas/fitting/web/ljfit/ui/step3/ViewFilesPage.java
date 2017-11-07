package ch.unibas.fitting.web.ljfit.ui.step3;


import ch.unibas.fitting.web.ljfit.ui.step2.LjSessionPage;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ViewFilesPage extends HeaderPage {

    private File selected;

    public ViewFilesPage(){

        List<File> resultFileList = Arrays.asList(new File("D:/UNIBASEL/dummyText.txt")
                                                  {
                                                      @Override
                                                      public String toString() {
                                                          return this.getName();
                                                      }
                                                  },
                new File("D:/UNIBASEL/dummyText2.txt"),
                new File("D:/UNIBASEL/dummyText3.txt"),
                new File("D:/UNIBASEL/dummyText4.txt"),
                new File("D:/UNIBASEL/dummyText5.txt"),
                new File("D:/UNIBASEL/dummyText6.txt"));

        selected = resultFileList.get(0);

        add(new AjaxLink("goBack") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(LjSessionPage.class);
            }

        });

        DropDownChoice<File> dropDownChoice = new DropDownChoice<File>("resultFile",new PropertyModel<>(this, "selected"), resultFileList) {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(File newSelection) {
                updateResultPanel(newSelection);
            }
        };

        add(dropDownChoice);

        add(new ShowFileContentPanel("resultPanel", selected));
    }

    private void updateResultPanel(File file) {
        replace(new ShowFileContentPanel("resultPanel", file));
    }
}
