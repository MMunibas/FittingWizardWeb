package ch.unibas.fitting.web.web;

import ch.unibas.fitting.web.application.Version;
import ch.unibas.fitting.web.WebApp;
import ch.unibas.fitting.web.welcome.WelcomePage;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.ImmutableNavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;

import java.io.Serializable;

/**
 * Created by martin on 04.06.2016.
 */
public abstract class HeaderPage extends WizardPage {

    protected HeaderPage() {
        super();
        Navbar navbar = new Navbar("navbar");
        add(navbar);

        navbar.setBrandName(Model.of("Fitting Web"));

        navbar.setPosition(Navbar.Position.STATIC_TOP);

        navbar.addComponents(new ImmutableNavbarComponent(
                new NavbarButton<WelcomePage>(WelcomePage.class, Model.of("Home")), Navbar.ComponentPosition.LEFT));
        navbar.addComponents(new ImmutableNavbarComponent(
                new NavbarButton<AdminPage>(AdminPage.class, Model.of("Sessions")), Navbar.ComponentPosition.LEFT));

        Label lbl = new Label(Navbar.componentId(), new Model() {
            @Override
            public Serializable getObject() {
                return session().getUsername() + " ["+ session().getId() + "]";
            }
        }) {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("style", "margin-top: 15px;color:white;");
            }
        };
        navbar.addComponents(new ImmutableNavbarComponent(lbl, Navbar.ComponentPosition.RIGHT));
        AjaxLink lnk = new AjaxLink(Navbar.componentId()) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                session().invalidate();
                setResponsePage(WelcomePage.class);
            }

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("style", "margin-top: 15px;margin-left: 15px;font-weight:bold;color:white;");
            }
        };
        lnk.setBody(Model.of("Logout"));
        //navbar.addComponents(new ImmutableNavbarComponent(lnk, Navbar.ComponentPosition.RIGHT));

        add(new Label("footer", new Model<>("Fitting Wizard Web " + Version.getManifestVersion())));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssReferenceHeaderItem.forReference(new CssResourceReference(WebApp.class, "styles.css")));
    }
}
