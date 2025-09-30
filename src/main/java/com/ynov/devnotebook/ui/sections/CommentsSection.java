package com.ynov.devnotebook.ui.sections;

import com.ynov.devnotebook.ContentService;
import com.ynov.devnotebook.ui.SectionPanel;

public class CommentsSection extends SectionPanel {
	public CommentsSection(ContentService contentService) {
		super(contentService, ContentService.KEY_COMMENTS);
	}
}
