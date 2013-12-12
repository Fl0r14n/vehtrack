package com.rhcloud.application.vehtrack.client.widgets;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.TabSet;

public class RootWidget extends VLayout {

    public RootWidget() {
        setWidth100();
        setHeight100();
        Label header = new Label();
        {
            header.setContents("Header");
            header.setAlign(Alignment.CENTER);
            header.setOverflow(Overflow.HIDDEN);
            header.setHeight("20%");
            header.setShowResizeBar(true);
        }
        addMember(header);
        HLayout footer;
        addMember(footer = new HLayout());
        {
            footer.setHeight("80%");            
            Label menu;
            footer.addMember(menu = new Label());
            {
                menu.setContents("Menu");
                menu.setAlign(Alignment.CENTER);
                menu.setOverflow(Overflow.HIDDEN);
                menu.setWidth("20%");
                menu.setShowResizeBar(true);
            }
            
            footer.addMember(contents = new TabSet());
            {
//                contents.setContents("Contents");
                contents.setAlign(Alignment.CENTER);
                contents.setOverflow(Overflow.HIDDEN);
                contents.setWidth("80%");
            }
        }
    }
    private TabSet contents;
    
    public void addContentWidget(ContentWidget content) {
        contents.addTab(content);
    }
}
