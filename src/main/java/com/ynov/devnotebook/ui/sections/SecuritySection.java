package com.ynov.devnotebook.ui.sections;

import com.ynov.devnotebook.ContentService;
import com.ynov.devnotebook.ui.SectionPanel;

public class SecuritySection extends SectionPanel {
	public SecuritySection(ContentService contentService) {
		super(contentService, ContentService.KEY_SECURITY);
	}
}
