package com.aempactice.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import com.day.cq.wcm.api.Page;

@Model(adaptables = {Resource.class})
public class ChildPageOperationsImpl {
	
	@ScriptVariable
	Page currentPage;
	
	private List<String> childPageTitles = new ArrayList<String>();
	
	public List<String> getChildPageTitles() {
		return childPageTitles;
	}

	@PostConstruct
	public void getTitle() {
		Iterator<Page> childPages = currentPage.listChildren();
		while(childPages.hasNext()) {
			Page childPage = childPages.next();
			String title = childPage.getTitle();
			childPageTitles.add(title);
		}
	}
}
