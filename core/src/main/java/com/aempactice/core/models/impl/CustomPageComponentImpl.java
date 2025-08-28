package com.aempactice.core.models.impl;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.featureflags.Features;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ExporterConstants;
import com.aempactice.core.models.CustomPageComponent;

@Model(adaptables = Resource.class, adapters = {
		CustomPageComponent.class }, resourceType = CustomPageComponentImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class CustomPageComponentImpl implements CustomPageComponent {

	public static final String RESOURCE_TYPE = "aempractice/components/page";

	 @ValueMapValue(name = "customFeature", optional = true)
	 private String customFeature;

	 @OSGiService
     private Features features;

	 @Override
	 public boolean isCustomFeatureEnabled() {
		return features.isEnabled("customFeature");	
	 }

	 @Override
	 public String getCustomFeature() {
		 return customFeature;
	 }
	
}
