package com.javafortesters.pulp.html.gui.entitycrud.updatePages;

import com.javafortesters.pulp.domain.objects.PulpPublisher;
import com.javafortesters.pulp.html.gui.snippets.AppPageBuilder;
import com.javafortesters.pulp.html.gui.snippets.PageSnippets;
import com.javafortesters.pulp.html.templates.MyTemplate;
import com.javafortesters.pulp.reader.ResourceReader;

public class AmendPublisherPage {
    private final PulpPublisher publisher;

    public AmendPublisherPage(final PulpPublisher aPublisher) {
        this.publisher = aPublisher;
    }

    private String output="";

    public void setOutput(final String output) {
        this.output=output;
    }

    public String asHTMLString() {

        AppPageBuilder page = new AppPageBuilder("Amend Publisher");

        String pageToRender = new ResourceReader().asString("/web/apps/pulp/v001/page-template/entity-crud/update/edit-book-publisher-content.html");
        MyTemplate template = new MyTemplate(pageToRender);

        template.replace("!!PUBLISHERID!!", publisher.getId());
        template.replace("!!PUBLISHERNAME!!", publisher.getName());
        template.replace("<!-- OUTPUT GOES HERE -->", output);

        page.appendToBody(template.toString());
        return page.toString();
    }
}
