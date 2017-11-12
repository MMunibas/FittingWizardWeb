package ch.unibas.fitting.web.ljfit.ui.step3;


import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.directories.LjFitSessionDir;
import ch.unibas.fitting.web.ljfit.ui.step2.LjSessionPage;
import ch.unibas.fitting.web.web.HeaderPage;
import io.vavr.control.Option;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewFilesPage extends HeaderPage {

    @Inject
    private IUserDirectory userDirectory;

    private IModel<List<FileViewModel>> files = Model.ofList(new ArrayList<>());

    private FileViewModel selected;

    public ViewFilesPage(PageParameters pp){

        String runDirName = pp.get("run_dir").toString();

        Option<LjFitSessionDir> dir = userDirectory.getLjFitSessionDir(getCurrentUsername());
        if (dir.isDefined()) {
            List<FileViewModel> resultFileList = dir.get()
                    .listRunFiles(runDirName)
                    .map(file -> new FileViewModel(file))
                    .sortBy(model -> model.toString())
                    .toJavaList();
            files.setObject(resultFileList);
            selected = resultFileList.get(0);
        }

        add(new AjaxLink("goBack") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(LjSessionPage.class);
            }
        });

        DropDownChoice<FileViewModel> dropDownChoice = new DropDownChoice<FileViewModel>("resultFile",
                new PropertyModel<>(this, "selected"), files) {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(FileViewModel newSelection) {
                update(newSelection);
            }
        };

        add(dropDownChoice);

        if (selected != null) {
            add(new ShowFileContentPanel("resultPanel", selected.getFile()));
        }
    }

    private void update(FileViewModel model) {
        replace(new ShowFileContentPanel("resultPanel", model.getFile()));
    }
}
