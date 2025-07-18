package com.aempactice.core.models.impl;

import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.adobe.cq.export.json.ExporterConstants;
import com.aempactice.core.beans.NewsArticle;
import com.aempactice.core.models.NewsArticleComponent;
import com.day.cq.commons.RangeIterator;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {
		NewsArticleComponent.class }, resourceType = OfficeComponentImpl.RESOURCE_TYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class NewsArticleComponentImpl implements NewsArticleComponent {

	public static final String RESOURCE_TYPE = "aempractice/components/newsarticle";
	
	@ScriptVariable
	Page currentPage;
	
	private List<NewsArticle> newsArticleList = new ArrayList<>();
	
	@SlingObject
	ResourceResolver resolver;
	
	List<String> retrievePage = new ArrayList<>();
	
	@Override
	public List<String> getRetrievePage() {
		return retrievePage;
	}

	@PostConstruct
	private void init() {
		Iterator<Page> childPage = currentPage.listChildren();
		if(null!= childPage) {
			while(childPage.hasNext()) {
				NewsArticle newsArticle = new NewsArticle();
				Page newsPage = childPage.next();
				String description = newsPage.getDescription();
				String title = newsPage.getTitle();
				String link = newsPage.getPath();
				Resource jcrResource = newsPage.getContentResource();
				Resource imageResource = jcrResource.getChild("cq:featuredimage");
				if(null != imageResource) {
					String image = imageResource.getValueMap().get("fileReference", String.class);
					newsArticle.setImage(image);
				}
				newsArticle.setDescription(description);
				newsArticle.setTitle(title);
				newsArticle.setLink(link);
				
				newsArticleList.add(newsArticle);
			}
		}
		
		try {
			TagManager manager =  resolver.adaptTo(TagManager.class);
			Tag newTag = manager.createTagByTitle("car22");
			
			Tag[] currentTags = currentPage.getTags();
			
			List<String> updatedTags = new ArrayList<>();
			for(Tag ct: currentTags) {
				updatedTags.add(ct.getTagID());
			}

			updatedTags.add(newTag.getTagID());
			
			Resource resource = currentPage.getContentResource();
			ModifiableValueMap mvp=  resource.adaptTo(ModifiableValueMap.class);
			mvp.put("cq:tags", updatedTags.toArray(new String[0]));
			resolver.commit();
			
			RangeIterator<Resource> iterator = manager.find("car22");
			while(iterator.hasNext()) {
				Resource pageResource = iterator.next();
				Page taggedPage = pageResource.getParent().adaptTo(Page.class);
				if(null!=taggedPage) {
					retrievePage.add(taggedPage.getTitle());
				}
			}
			
		} catch (AccessControlException |InvalidTagFormatException | PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<NewsArticle> getNewsArticle() {		
		return newsArticleList;
	}
}
