package com.aempactice.core.models;

import java.util.List;

import com.aempactice.core.beans.NewsArticle;

public interface NewsArticleComponent {
	
	List<NewsArticle> getNewsArticle();

	List<String> getRetrievePage();
}
