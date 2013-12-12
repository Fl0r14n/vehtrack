package com.rhcloud.application.vehtrack.client.widgets;

import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.tab.Tab;

public class ContentWidget extends Tab {

    public ContentWidget(String title) {
        super(title);
        setCanClose(true);
        SectionStack sectionStack = new SectionStack();
        {
            sectionStack.setVisibilityMode(VisibilityMode.MULTIPLE);
            sectionStack.setAnimateSections(true);
            sectionStack.setOverflow(Overflow.HIDDEN);
            summarySection = new SectionStackSection();
            {
                summarySection.setTitle("Summary");
                summarySection.setExpanded(true);                
            }
            detailsSection = new SectionStackSection();
            {
                detailsSection.setTitle("Options");
                detailsSection.setExpanded(false);
            }
            sectionStack.setSections(summarySection, detailsSection);
        }
        setPane(sectionStack);
    }
    private SectionStackSection summarySection;
    private SectionStackSection detailsSection;

    public void addSummary(Canvas item) {
        if (item != null) {
            summarySection.addItem(item);                     
        }
    }

    public void addDetails(Canvas item) {
        if (item != null) {
            detailsSection.addItem(item);
        }
    }
}
