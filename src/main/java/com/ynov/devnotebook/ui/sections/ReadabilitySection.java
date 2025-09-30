package com.ynov.devnotebook.ui.sections;

import com.ynov.devnotebook.ContentService;
import com.ynov.devnotebook.ui.SectionPanel;

public class ReadabilitySection extends SectionPanel {
	public ReadabilitySection(ContentService contentService) {
		super(contentService, ContentService.KEY_READABILITY);
	}
}
