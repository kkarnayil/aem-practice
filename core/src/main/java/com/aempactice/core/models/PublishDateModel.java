package com.aempactice.core.models;

import com.day.cq.wcm.api.Page;
import lombok.Getter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;

@Model(adaptables = SlingHttpServletRequest.class, resourceType = PublishDateModel.RESOURCE_TYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PublishDateModel {

        public static final String RESOURCE_TYPE = "aempractice/components/publishDate";

        private static final Logger LOGGER = LoggerFactory.getLogger(PublishDateModel.class);

        private static final String PN_PUBLISH_DATE = "originalPublishDate";

        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);
        
        @ScriptVariable
        private Page currentPage;

        @Getter
        private String publishDate;

        @PostConstruct
        void init() {

                Optional<Calendar> calendarOptional = Optional.ofNullable(currentPage)
                                .map(Page::getProperties)
                                .map(vm -> vm.get(PN_PUBLISH_DATE, Calendar.class));

                publishDate = calendarOptional
                                .map(Calendar::toInstant)
                                .map(instant -> instant.atZone(ZoneId.systemDefault())
                                .toLocalDate())
                                .map(FORMATTER::format)
                                .orElse(null);

                LOGGER.debug("Publish Date is :{}", publishDate);
        }

}
